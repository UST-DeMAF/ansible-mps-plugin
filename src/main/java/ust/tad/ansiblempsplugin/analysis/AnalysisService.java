package ust.tad.ansiblempsplugin.analysis;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;
import ust.tad.ansiblempsplugin.analysis.ansibleactions.ActionParser;
import ust.tad.ansiblempsplugin.analysistask.AnalysisTaskResponseSender;
import ust.tad.ansiblempsplugin.analysistask.Location;
import ust.tad.ansiblempsplugin.ansiblemodel.*;
import ust.tad.ansiblempsplugin.models.ModelsService;
import ust.tad.ansiblempsplugin.models.tadm.InvalidPropertyValueException;
import ust.tad.ansiblempsplugin.models.tadm.InvalidRelationException;
import ust.tad.ansiblempsplugin.models.tadm.TechnologyAgnosticDeploymentModel;
import ust.tad.ansiblempsplugin.models.tsdm.*;

@SuppressWarnings({"unchecked"})
@Service
public class AnalysisService {

  @Autowired ModelsService modelsService;
  @Autowired AnalysisTaskResponseSender analysisTaskResponseSender;
  @Autowired private TransformationService transformationService;
  @Autowired private ActionParser actionParser;

  private static final Logger LOG = LoggerFactory.getLogger(AnalysisTaskResponseSender.class);

  private static final Set<String> supportedFileExtensions = Set.of("yaml", "yml");
  private TechnologySpecificDeploymentModel tsdm;
  private TechnologyAgnosticDeploymentModel tadm;
  private Set<Integer> newEmbeddedDeploymentModelIndexes = new HashSet<>();
  private final Set<Play> plays = new HashSet<>();
  @Autowired private TaskExecutionAutoConfiguration taskExecutionAutoConfiguration;

  /**
   * Start the analysis of the deployment model. 1. Retrieve internal deployment models from models
   * service 2. Parse in technology-specific deployment model from locations 3. Update tsdm with new
   * information 4. Transform to EDMM entities and update tadm 5. Send updated models to models
   * service 6. Send AnalysisTaskResponse or EmbeddedDeploymentModelAnalysisRequests if present
   *
   * @param taskId the taskId as UUID
   * @param transformationProcessId the transformationProcessId as UUID
   * @param commands list of commands
   * @param locations list of locations
   */
  public void startAnalysis(
      UUID taskId, UUID transformationProcessId, List<String> commands, List<Location> locations) {
    /*TechnologySpecificDeploymentModel completeTsdm =
        modelsService.getTechnologySpecificDeploymentModel(transformationProcessId);
    this.tsdm = getExistingTsdm(completeTsdm, locations);
    if (tsdm == null) {
      analysisTaskResponseSender.sendFailureResponse(
          taskId, "No technology-specific " + "deployment model found!");
      return;
    }*/
    this.tsdm = modelsService.getTechnologySpecificDeploymentModel(transformationProcessId);
    this.tadm = modelsService.getTechnologyAgnosticDeploymentModel(transformationProcessId);

    try {
      runAnalysis(locations);
    } catch (InvalidNumberOfContentException
        | URISyntaxException
        | IOException
        | InvalidNumberOfLinesException
        | InvalidAnnotationException
        | InvalidPropertyValueException
        | InvalidRelationException e) {
      LOG.error(e.getMessage());
      analysisTaskResponseSender.sendFailureResponse(taskId, e.getClass() + ": " + e.getMessage());
      return;
    }

    try {
      updateDeploymentModels(this.tsdm, this.tadm);
    } catch (InvalidPropertyValueException | InvalidRelationException e) {
      LOG.info(e.getMessage());
    }

    if (!newEmbeddedDeploymentModelIndexes.isEmpty()) {
      for (int index : newEmbeddedDeploymentModelIndexes) {
        analysisTaskResponseSender.sendEmbeddedDeploymentModelAnalysisRequestFromModel(
            this.tsdm.getEmbeddedDeploymentModels().get(index), taskId);
      }
    }
    analysisTaskResponseSender.sendSuccessResponse(taskId);
  }

  /**
   * Get the existing technology-specific deployment model from the complete technology-specific
   * deployment model.
   *
   * @param tsdm the complete technology-specific deployment model
   * @param locations the list of locations
   * @return the existing technology-specific deployment model
   */
  private TechnologySpecificDeploymentModel getExistingTsdm(
      TechnologySpecificDeploymentModel tsdm, List<Location> locations) {
    for (DeploymentModelContent content : tsdm.getContent()) {
      for (Location location : locations) {
        if (location.getUrl() == content.getLocation()) {
          return tsdm;
        }
      }
    }
    for (TechnologySpecificDeploymentModel embeddedDeploymentModel :
        tsdm.getEmbeddedDeploymentModels()) {
      TechnologySpecificDeploymentModel foundModel =
          getExistingTsdm(embeddedDeploymentModel, locations);
      if (foundModel != null) {
        return foundModel;
      }
    }
    return null;
  }

  /**
   * Update the deployment models with the new technology-specific deployment model and the new
   * technology-agnostic deployment model.
   *
   * @param tsdm the technology-specific deployment model
   * @param tadm the technology-agnostic deployment model
   */
  private void updateDeploymentModels(
      TechnologySpecificDeploymentModel tsdm, TechnologyAgnosticDeploymentModel tadm) {
    modelsService.updateTechnologySpecificDeploymentModel(tsdm);
    modelsService.updateTechnologyAgnosticDeploymentModel(tadm);
  }

  /**
   * Iterate over the locations and parse in all files that can be found. If the URL ends with a
   * ".", remove it. The file has to have a fileextension contained in the supported fileextension
   * Set, otherwise it will be ignored. If the given location is a directory, iterate over all
   * contained files. Removes the deployment model content associated with the old directory
   * locations because it has been resolved to the contained files.
   *
   * @param locations list of locations
   * @throws InvalidNumberOfContentException tbd
   * @throws InvalidAnnotationException tbd
   * @throws InvalidNumberOfLinesException tbd
   * @throws IOException tbd
   * @throws URISyntaxException tbd
   * @throws InvalidPropertyValueException tbd
   */
  private void runAnalysis(List<Location> locations)
      throws URISyntaxException,
          IOException,
          InvalidNumberOfLinesException,
          InvalidAnnotationException,
          InvalidNumberOfContentException,
          InvalidPropertyValueException,
          InvalidRelationException {
    for (Location location : locations) {
      String locationURLString = location.getUrl().toString().trim().replaceAll("\\.$", "");
      URL locationURL = new URL(locationURLString);

      // TODO think about what to do with directories
      /* if ("file".equals(locationURL.getProtocol()) && new File(locationURL.toURI()).isDirectory()) {
          File directory = new File(locationURL.toURI());
          for (File file : Objects.requireNonNull(directory.listFiles())) {
              String fileExtension = StringUtils.getFilenameExtension(file.toURI().toURL().toString());
              if (fileExtension != null && supportedFileExtensions.contains(fileExtension)) {
                  parseFile(file.toURI().toURL());
              }
          }
          DeploymentModelContent contentToRemove = new DeploymentModelContent();
          for (DeploymentModelContent content : this.tsdm.getContent()) {
              if (content.getLocation().equals(location.getUrl())) {
                  contentToRemove = content;
              }
          }
          this.tsdm.removeDeploymentModelContent(contentToRemove);
      } else {*/
      String fileExtension = StringUtils.getFilenameExtension(locationURLString);
      if (supportedFileExtensions.contains(fileExtension)) {
        parseFile(locationURL);
      }
      /*}*/
    }

    this.tadm =
        transformationService.transformInternalToTADM(
            this.tadm, new AnsibleDeploymentModel(this.plays));
  }

  /**
   * Reads in all plays in a main.yaml file and calls the subsequent parsing methods.
   *
   * @param url the url of the main.yaml
   */
  private void parseFile(URL url) throws IOException {
    DeploymentModelContent deploymentModelContent = new DeploymentModelContent();
    deploymentModelContent.setLocation(url);

    // Parse main.yaml
    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
    Yaml yaml = new Yaml();
    ArrayList<Map<String, Object>> parsedYaml = yaml.load(reader);
    reader.close();

    // Extract information from parsed yaml construct
    parsedYaml.forEach(
        (playYaml) -> {
          HashSet<Host> hosts;
          HashSet<Role> roles;

          String name = playYaml.getOrDefault("name", "").toString();
          HashSet<Variable> vars = new HashSet<>(parseVars(playYaml.get("vars")));
          try {
            hosts = new HashSet<>(parseHosts(url, playYaml.get("hosts")));
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
          boolean become =
              Boolean.parseBoolean(playYaml.getOrDefault("become", "false").toString());
          HashSet<Task> preTasks = new HashSet<>(parseTasks(playYaml.get("pre_tasks")));
          HashSet<Task> tasks = new HashSet<>(parseTasks(playYaml.get("tasks")));
          HashSet<Task> postTasks = new HashSet<>(parseTasks(playYaml.get("post_tasks")));
          try {
            roles = new HashSet<>(parseRoles(url, playYaml.get("roles")));
          } catch (IOException e) {
            throw new RuntimeException(e);
          }

          Play play = new Play(name, hosts, preTasks, tasks, postTasks, roles, vars, become);
          Play variableResolvedPlay = resolveVariables(play);

          plays.add(variableResolvedPlay);
        });
  }

  /**
   * Reads in all used hosts and parses them accordingly.
   *
   * @param url the url of the main.yaml to find the hots file.
   * @param mainHostYaml the main.yaml's snippet containing the YAML stating which hosts are used in
   *     a play, parsed as LinkedHashMap
   * @return the HashSet of parsed hosts
   */
  private HashSet<Host> parseHosts(URL url, Object mainHostYaml) throws IOException {

    if (mainHostYaml == null) {
      return new HashSet<>();
    }

    String directoryPath = removeAfterLastSlash(url.toString());
    URL hostUrl1 = new URL(directoryPath + "/hosts.yaml");
    URL hostUrl2 = new URL(directoryPath + "/hosts.yml");

    HashSet<Host> hosts;
    try {
      hosts = parseHostYaml(hostUrl1);
    } catch (Exception e) {
      try {
        hosts = parseHostYaml(hostUrl2);
      } catch (Exception e2) {
        LOG.error("Could not parse hosts.yaml/yml: {}, {}", e.getMessage(), e2.getMessage());
        return new HashSet<>();
      }
    }

    // The hosts.yaml can contain more hosts than targeted in this deployment, thus we need to
    // filter them out.
    // In the global main.yaml hots can be defined as hostGroup (one or many) or as specific host
    // (one or many)

    // Either there is one host-group or all or host provided...
    try {
      String mainHostString = (String) mainHostYaml;
      if (mainHostString.equals("all")) {
        return hosts;
      } else if (hosts.stream().anyMatch(host -> host.getHostName().equals(mainHostString))) {
        hosts.removeIf(host -> !host.getHostName().equals(mainHostString));
        // as this is a set we assume there is only one host with this name.
        return hosts;
      } else if (hosts.stream().anyMatch(host -> host.getGroup().equals(mainHostString))) {
        hosts.removeIf(host -> !host.getGroup().equals(mainHostString));
        return hosts;
      } else {
        return hosts;
      }
    } catch (ClassCastException stringError) {
      LOG.debug("mainHostYaml is not a string");
    }

    // Or there is a list of host-groups or hosts provided.
    try {
      HashSet<Host> hostIterator = hosts;
      HashSet<Host> returnHosts = new HashSet<>();
      ArrayList<String> mainHostList = (ArrayList<String>) mainHostYaml;

      mainHostList.forEach(
          (name ->
              hostIterator.forEach(
                  (host -> {
                    if (host.getHostName().equals(name) || host.getGroup().equals(name)) {
                      returnHosts.add(host);
                    }
                  }))));
      if (!returnHosts.isEmpty()) {
        return returnHosts;
      } else {
        return hosts;
      }

    } catch (ClassCastException ListError) {
      LOG.error("Could not parse main host information, gonna add all hosts from host.yaml");
      return hosts;
    }
  }

  /**
   * Reads in all hosts from the hosts.yaml file and parses them accordingly.
   *
   * @param hostUrl the URL of the hosts.yaml file
   * @return the HashSet of parsed hosts
   */
  private static HashSet<Host> parseHostYaml(URL hostUrl) throws IOException {
    BufferedReader hostReader = new BufferedReader(new InputStreamReader(hostUrl.openStream()));
    Yaml yaml = new Yaml();
    Map<String, Map<String, Map<String, Map<String, String>>>> parsedYaml = yaml.load(hostReader);
    hostReader.close();

    HashSet<Host> hosts = new HashSet<>();
    parsedYaml.forEach(
        (group, hostsWrapper) ->
            hostsWrapper
                .get("hosts")
                .forEach(
                    (hostName, varMap) -> {
                      HashSet<Variable> vars = new HashSet<>();
                      varMap.forEach(
                          (varKey, varValue) -> vars.add(new Variable(varKey, varValue)));
                      hosts.add(new Host(hostName, vars, group));
                    }));
    return hosts;
  }

  /**
   * Reads in tasks and parses them accordingly.
   *
   * @param mainTaskYaml the snippet containing the YAML stating the tasks, parsed as LinkedHashMap
   * @return the HashSet of parsed tasks
   */
  private HashSet<Task> parseTasks(Object mainTaskYaml) {

    // TODO import_tasks is a statement that imports tasks at a place where other tasks would be.
    // TODO our parser should in the long term be able to parse this.

    if (mainTaskYaml == null) {
      return new HashSet<>();
    }

    ArrayList<Map<String, Object>> mainTasksList;
    try {
      mainTasksList = (ArrayList<Map<String, Object>>) mainTaskYaml;
    } catch (Exception e) {
      LOG.error("could not parse global tasks {}", e.getMessage());
      return new HashSet<>();
    }
    HashSet<Task> tasks = new HashSet<>();
    mainTasksList.forEach(
        taskYaml ->
            tasks.add(
                new Task(
                    taskYaml.getOrDefault("name", "").toString(),
                    parseVars(taskYaml.get("vars")),
                    Boolean.parseBoolean(taskYaml.getOrDefault("become", "false").toString()),
                    actionParser.parseActions(taskYaml),
                    new HashSet<>(
                        (ArrayList<String>)
                            taskYaml.getOrDefault("loop", new ArrayList<String>())))));
    return tasks;
  }

  /**
   * Reads in all used roles and parses them accordingly.
   *
   * @param url the url of the main.yaml to find the roles directories.
   * @param mainRoleYaml the main.yaml's snippet containing the YAML stating which roles are used in
   *     a play, parsed as LinkedHashMap
   * @return the HashSet of parsed roles
   */
  private HashSet<Role> parseRoles(URL url, Object mainRoleYaml) throws IOException {

    if (mainRoleYaml == null) {
      return new HashSet<>();
    }
    ArrayList<Map<String, Object>> roleList = (ArrayList<Map<String, Object>>) mainRoleYaml;
    final List<String> possibleDirectories =
        List.of("defaults", "vars", "meta", "tasks", "handlers");

    HashSet<Role> roles = new HashSet<>();
    roleList.forEach(
        role -> {
          String roleName = (String) role.get("role");

          ArrayList<String> directoryPaths = new ArrayList<>();
          possibleDirectories.forEach(
              directory ->
                  directoryPaths.add(
                      removeAfterLastSlash(url.toString())
                          + "/roles/"
                          + roleName
                          + "/"
                          + directory
                          + "/main.yaml"));

          HashSet<Variable> defaults = new HashSet<>();
          HashSet<Variable> vars = new HashSet<>();
          HashSet<String> dependencies = new HashSet<>();
          HashSet<Task> tasks = new HashSet<>();
          HashSet<Task> handlers = new HashSet<>();

          directoryPaths.forEach(
              directory -> {
                Object parsedYaml;
                try {
                  URL newUrl = new URL(directory);
                  BufferedReader reader =
                      new BufferedReader(new InputStreamReader(newUrl.openStream()));
                  Yaml yaml = new Yaml();
                  parsedYaml = yaml.load(reader);
                  reader.close();

                  if (directory.contains("tasks")) {
                    tasks.addAll(parseTasks(parsedYaml));
                  } else if (directory.contains("handlers")) {
                    handlers.addAll(parseTasks(parsedYaml));
                  } else if (directory.contains("defaults")) {
                    defaults.addAll(parseVars(parsedYaml));
                  } else if (directory.contains("vars")) {
                    vars.addAll(parseVars(parsedYaml));
                  } else if (directory.contains("meta")) {
                    Map<String, ArrayList<Map<String, String>>> mainMetaMap;
                    try {
                      mainMetaMap = (Map<String, ArrayList<Map<String, String>>>) parsedYaml;
                      dependencies.addAll(
                          mainMetaMap.get("dependencies").stream()
                              .flatMap(map -> map.values().stream())
                              .collect(Collectors.toCollection(ArrayList::new)));
                    } catch (Exception e) {
                      LOG.error("could not parse dependencies {}", e.getMessage());
                    }
                  }
                } catch (Exception e) {
                  LOG.debug("Could not parse role file {}, might not exist.", directory);
                }
              });
          roles.add(
              new Role(roleName, tasks, handlers, vars, defaults, dependencies, new HashSet<>()));
        });

    return roles;
  }

  /**
   * Parses variables from the YAML.
   *
   * @param mainVarsYaml the Variable Snippet containing the YAML, parsed as LinkedHashMap
   * @return the HashSet of parsed variables
   */
  private HashSet<Variable> parseVars(Object mainVarsYaml) {

    if (mainVarsYaml == null) {
      return new HashSet<>();
    }

    Map<String, Object> mainVarsList;
    try {
      mainVarsList = (Map<String, Object>) mainVarsYaml;
    } catch (Exception e) {
      LOG.error("could not parse variables {}", e.getMessage());
      return new HashSet<>();
    }
    HashSet<Variable> vars = new HashSet<>();
    mainVarsList.forEach(
        (key, value) -> {
          if (value instanceof String) {
            vars.add(new Variable(key, (String) value));
          } else if (value instanceof List) {
            ((ArrayList<Object>) value)
                .forEach(
                    innerValue -> {
                      if (innerValue instanceof String) {
                        vars.add(new Variable(key, (String) innerValue));
                      }
                    });
          }
        });
    return vars;
  }

  /**
   * String helper that cuts the filename after the last slash to return the directory.
   *
   * @param input the input file path
   * @return the shortened directory path
   */
  private static String removeAfterLastSlash(String input) {
    int lastSlashIndex = input.lastIndexOf('/');
    if (lastSlashIndex != -1) {
      return input.substring(0, lastSlashIndex);
    } else {
      return input; // No slash found, return the original string
    }
  }

  /**
   * Static function that iterates through all tasks to resolve variables. Variables have the
   * following precedence. A task can have vars which come before a play's vars. In a role a task
   * can also have task vars but also role vars which both come before role defaults and finally
   * play vars. In this order every String is checked via reflection in all tasks in the entire play
   * object. If a String matches to a variable name, the variable name is replaced with the variable
   * value. Variables in Ansible are annotated as {{ var }}. The helper function
   * iterateStringFields() is called to iterate recursively over all objects and collections in a
   * play. Note, that this might be error-prone in special cases, thus all exceptions are caught to
   * be safe.
   *
   * @param play The play where all strings should be checked and replaced.
   * @return The play with replaced variables
   */
  private static Play resolveVariables(Play play) {

    // TODO theoretically vars can be added by vars in outer scopes, this is not covered yet.

    for (Task task : play.getPre_tasks()) {
      iterateStringFields(task, task.getVars(), null, play.getVars());
    }
    for (Task task : play.getTasks()) {
      iterateStringFields(task, task.getVars(), null, play.getVars());
    }
    for (Task task : play.getPost_tasks()) {
      iterateStringFields(task, task.getVars(), null, play.getVars());
    }

    for (Role role : play.getRoles()) {
      for (Task task : role.getTasks()) {
        iterateStringFields(
            task,
            Stream.of(task.getVars(), role.getVars())
                .flatMap(Collection::stream)
                .collect(Collectors.toSet()),
            role.getDefaults(),
            play.getVars());
      }
    }
    return play;
  }

  // Set to prevent infinite recursion
  private static final Set<Object> visited = new HashSet<>();

  /**
   * The actual method that iterates recursively through an object to find all strings and check if
   * a variable can be replaced.
   *
   * @param obj The object to check and modify (e.g. a task or a list of tasks)
   * @param vars The highest precedence variables (e.g. from task, might be null)
   * @param defaults The default vars of a role (might be null)
   * @param globalVars The global vars from the play (might be null)
   */
  public static void iterateStringFields(
      Object obj, Set<Variable> vars, Set<Variable> defaults, Set<Variable> globalVars) {
    if (obj == null || visited.contains(obj)) {
      return;
    }

    // Add the object to the visited set to prevent infinite recursion
    visited.add(obj);

    // Get the class of the object
    Class<?> clazz = obj.getClass();

    // Get all declared fields of the class
    Field[] fields = clazz.getDeclaredFields();

    // Iterate over the fields
    for (Field field : fields) {
      // Make the field accessible if it's private
      field.setAccessible(true);
      try {
        Object value = field.get(obj);

        if (field.getType().equals(String.class)) {
          String string = (String) value;
          final Set<String> usedVars = new HashSet<>();

          string = checkAndReplaceString(vars, string, usedVars);
          string = checkAndReplaceString(defaults, string, usedVars);
          string = checkAndReplaceString(globalVars, string, usedVars);

          field.set(obj, string);
        } else if (Collection.class.isAssignableFrom(field.getType())) {
          // Handle collections
          Collection<?> procecessedCollection =
              processCollection((Collection<?>) value, vars, defaults, globalVars);
          field.set(obj, procecessedCollection);
        } else if (!field.getType().isPrimitive() && value != null) {
          // Recursively inspect non-primitive fields that are not null
          iterateStringFields(value, vars, defaults, globalVars);
        }
      } catch (Exception e) {
        LOG.debug("Failed to replace Variable for field {}{}", field.getName(), e.getMessage());
      }
    }
  }

  /**
   * Helper function for iterating through collections. Either the collection is of string itself,
   * then the strings are checked, or the collection contains other objects which themselves might
   * contain strings, thus in this case iterateStringFields() is called again recursively.
   *
   * @param collection The collection to check.
   * @param vars The highest precedence variables (e.g. from task, might be null)
   * @param defaults The default vars of a role (might be null)
   * @param globalVars The global vars from the play (might be null)
   * @return The collection (possibly modified)
   */
  private static Collection<?> processCollection(
      Collection<?> collection,
      Set<Variable> vars,
      Set<Variable> defaults,
      Set<Variable> globalVars) {
    if (collection == null || visited.contains(collection)) {
      return collection;
    }

    visited.add(collection);
    Collection<Object> returnCollection = new HashSet<>();

    for (Object element : collection) {
      // Handle the case when the collection itself contains only Strings.
      if (element instanceof String) {
        String string = (String) element;
        final Set<String> usedVars = new HashSet<>();

        string = checkAndReplaceString(vars, string, usedVars);
        string = checkAndReplaceString(defaults, string, usedVars);
        string = checkAndReplaceString(globalVars, string, usedVars);

        returnCollection.add(string);

      } else if (element != null && !element.getClass().isPrimitive()) {
        // Recursively inspect non-primitive elements that are not null
        iterateStringFields(element, vars, defaults, globalVars);
        returnCollection.add(element);
      } else {
        returnCollection.add(element);
      }
    }
    return returnCollection;
  }

  /**
   * Helper function to actually check and replace a variable with its value if applicable.
   *
   * @param vars the variables used for replacement
   * @param string the string that should possibly be replaced
   * @param usedVars container to help the to terminate the recursion, contains vars that have been
   *     checked already
   * @return the possibly adjusted string
   */
  private static String checkAndReplaceString(
      Set<Variable> vars, String string, Set<String> usedVars) {
    if (vars != null && !vars.isEmpty()) {
      for (Variable defaultVar : vars) {
        if (!usedVars.contains(defaultVar.getName()) && string.contains(defaultVar.getName())) {
          string = string.replace("{{ " + defaultVar.getName() + " }}", defaultVar.getValue());
        }
      }
    }
    return string;
  }
}
