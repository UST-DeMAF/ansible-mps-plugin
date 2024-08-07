package ust.tad.ansiblempsplugin.analysis;

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
import ust.tad.ansiblempsplugin.models.ModelsService;
import ust.tad.ansiblempsplugin.models.tadm.InvalidPropertyValueException;
import ust.tad.ansiblempsplugin.models.tadm.InvalidRelationException;
import ust.tad.ansiblempsplugin.models.tadm.TechnologyAgnosticDeploymentModel;
import ust.tad.ansiblempsplugin.models.tsdm.*;
import ust.tad.ansiblempsplugin.ansiblemodel.*;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked"})
@Service
public class AnalysisService {

    private static final List<String> supportedModules = List.of("docker_container", "docker_images", "docker_network", "apt", "community.general.launchd");
    @Autowired
    ModelsService modelsService;
    @Autowired
    AnalysisTaskResponseSender analysisTaskResponseSender;
    @Autowired
    private TransformationService transformationService;
    @Autowired
    private ActionParser actionParser;

    private static final Logger LOG =
            LoggerFactory.getLogger(AnalysisTaskResponseSender.class);

    private static final Set<String> supportedFileExtensions = Set.of("yaml", "yml");
    private TechnologySpecificDeploymentModel tsdm;
    private TechnologyAgnosticDeploymentModel tadm;
    private Set<Integer> newEmbeddedDeploymentModelIndexes = new HashSet<>();
    private final Set<Play> plays = new HashSet<>();
    @Autowired
    private TaskExecutionAutoConfiguration taskExecutionAutoConfiguration;

    /**
     * Start the analysis of the deployment model.
     * 1. Retrieve internal deployment models from models service
     * 2. Parse in technology-specific deployment model from locations
     * 3. Update tsdm with new information
     * 4. Transform to EDMM entities and update tadm
     * 5. Send updated models to models service
     * 6. Send AnalysisTaskResponse or EmbeddedDeploymentModelAnalysisRequests if present
     *
     * @param taskId                  the taskId as UUID
     * @param transformationProcessId the transformationProcessId as UUID
     * @param commands                list of commands
     * @param locations               list of locations
     */
    public void startAnalysis(UUID taskId, UUID transformationProcessId, List<String> commands,
                              List<Location> locations) {
        //TechnologySpecificDeploymentModel completeTsdm =
        //            modelsService.getTechnologySpecificDeploymentModel(transformationProcessId);
        //  this.tsdm = getExistingTsdm(completeTsdm, locations);
        //if (tsdm == null) {
        //   analysisTaskResponseSender.sendFailureResponse(taskId, "No technology-specific " +
        //          "deployment model found!");
        // return;
        // }
        //this.tadm = modelsService.getTechnologyAgnosticDeploymentModel(transformationProcessId);

        try {
            runAnalysis(locations);
        } catch (InvalidNumberOfContentException | URISyntaxException | IOException |
                 InvalidNumberOfLinesException | InvalidAnnotationException |
                 InvalidPropertyValueException | InvalidRelationException e) {
            LOG.error(e.getMessage());
            analysisTaskResponseSender.sendFailureResponse(taskId,
                    e.getClass() + ": " + e.getMessage());
            return;
        }

        updateDeploymentModels(this.tsdm, this.tadm);

        if (!newEmbeddedDeploymentModelIndexes.isEmpty()) {
            for (int index : newEmbeddedDeploymentModelIndexes) {
                analysisTaskResponseSender.sendEmbeddedDeploymentModelAnalysisRequestFromModel(
                        this.tsdm.getEmbeddedDeploymentModels().get(index), taskId);
            }
        }
        analysisTaskResponseSender.sendSuccessResponse(taskId);
    }

    private TechnologySpecificDeploymentModel getExistingTsdm(TechnologySpecificDeploymentModel tsdm, List<Location> locations) {
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

    private void updateDeploymentModels(TechnologySpecificDeploymentModel tsdm,
                                        TechnologyAgnosticDeploymentModel tadm) {
        modelsService.updateTechnologySpecificDeploymentModel(tsdm);
        modelsService.updateTechnologyAgnosticDeploymentModel(tadm);
    }

    /**
     * Iterate over the locations and parse in all files that can be found.
     * If the URL ends with a ".", remove it.
     * The file has to have a fileextension contained in the supported fileextension Set, otherwise it will be ignored.
     * If the given location is a directory, iterate over all contained files.
     * Removes the deployment model content associated with the old directory locations
     * because it has been resolved to the contained files.
     *
     * @param locations list of locations
     * @throws InvalidNumberOfContentException tbd
     * @throws InvalidAnnotationException      tbd
     * @throws InvalidNumberOfLinesException   tbd
     * @throws IOException                     tbd
     * @throws URISyntaxException              tbd
     * @throws InvalidPropertyValueException   tbd
     */
    private void runAnalysis(List<Location> locations) throws URISyntaxException, IOException, InvalidNumberOfLinesException, InvalidAnnotationException, InvalidNumberOfContentException, InvalidPropertyValueException, InvalidRelationException {
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

        this.tadm = transformationService.transformInternalToTADM(this.tadm, new AnsibleDeploymentModel(this.plays));
    }

    private void parseFile(URL url) throws IOException {
        // DeploymentModelContent deploymentModelContent = new DeploymentModelContent();
        // deploymentModelContent.setLocation(url);

        // Parse main.yaml
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        Yaml yaml = new Yaml();
        ArrayList<Map<String, Object>> parsedYaml = yaml.load(reader);
        reader.close();

        // Extract information from parsed yaml construct
        parsedYaml.forEach((playYaml) -> {
            HashSet<Host> hosts;
            HashSet<Role> roles;

            String name = playYaml.getOrDefault("name", "").toString();
            HashSet<Variable> vars = new HashSet<>(parseVars(playYaml.get("vars")));
            try {
                hosts = new HashSet<>(parseHosts(url, playYaml.get("hosts")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            boolean become = Boolean.parseBoolean(playYaml.getOrDefault("become", "false").toString());
            HashSet<Task> preTasks = new HashSet<>(parseTasks(playYaml.get("pre_tasks")));
            HashSet<Task> tasks = new HashSet<>(parseTasks(playYaml.get("tasks")));
            HashSet<Task> postTasks = new HashSet<>(parseTasks(playYaml.get("post_tasks")));
            try {
                roles = new HashSet<>(parseRoles(url, playYaml.get("roles"), vars));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            plays.add(new Play(name, hosts, preTasks, tasks, postTasks, roles, vars, become));
        });
    }

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

        // The hosts.yaml can contain more hosts than targeted in this deployment, thus we need to filter them out.
        // In the global main.yaml hots can be defined as hostGroup (one or many) or as specific host (one or many)

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

            mainHostList.forEach((name -> hostIterator.forEach((host -> {
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

    private static HashSet<Host> parseHostYaml(URL hostUrl) throws IOException {
        BufferedReader hostReader = new BufferedReader(new InputStreamReader(hostUrl.openStream()));
        Yaml yaml = new Yaml();
        Map<String, Map<String, Map<String, Map<String, String>>>> parsedYaml = yaml.load(hostReader);
        hostReader.close();

        HashSet<Host> hosts = new HashSet<>();
        parsedYaml.forEach((group, hostsWrapper) -> hostsWrapper.get("hosts").forEach((hostName, varMap) -> {
            HashSet<Variable> vars = new HashSet<>();
            varMap.forEach((varKey, varValue) -> vars.add(new Variable(varKey, varValue)));
            hosts.add(new Host(hostName, vars, group));
        }));
        return hosts;
    }

    private HashSet<Task> parseTasks(Object mainTaskYaml) {

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
        mainTasksList.forEach(taskYaml -> tasks.add(new Task(
                taskYaml.get("name").toString(),
                parseVars(taskYaml.get("vars")),
                Boolean.parseBoolean(taskYaml.getOrDefault("become", "false").toString()),
                actionParser.parseActions(taskYaml),
                new HashSet<>((ArrayList<String>) taskYaml.getOrDefault("loop", new ArrayList<String>()))
        )));
        return tasks;
    }

    private HashSet<Role> parseRoles(URL url, Object mainRoleYaml, HashSet<Variable> globalVars) throws IOException {

        if (mainRoleYaml == null) {
            return new HashSet<>();
        }
        ArrayList<Map<String, Object>> roleList = (ArrayList<Map<String, Object>>) mainRoleYaml;
        final List<String> possibleDirectories = List.of("defaults", "vars", "meta", "tasks", "handlers");

        HashSet<Role> roles = new HashSet<>();
        roleList.forEach(role -> {

            String roleName = (String) role.get("role");

            ArrayList<String> directoryPaths = new ArrayList<>();
            possibleDirectories.forEach(directory -> directoryPaths.add(removeAfterLastSlash(url.toString()) + "/roles/" + roleName + "/" + directory + "/main.yaml"));


            HashSet<Variable> defaults = new HashSet<>();
            HashSet<Variable> vars = new HashSet<>();
            HashSet<String> dependencies = new HashSet<>();
            HashSet<Task> tasks = new HashSet<>();
            HashSet<Task> handlers = new HashSet<>();

            directoryPaths.forEach(directory -> {
                Object parsedYaml;
                try {
                    URL newUrl = new URL(directory);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(newUrl.openStream()));
                    Yaml yaml = new Yaml();
                    parsedYaml = yaml.load(reader);
                    reader.close();

                    // TODO think about how to do variable replacements
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
                            dependencies.addAll(mainMetaMap.get("dependencies").stream()
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
            roles.add(new Role(roleName, tasks, handlers, vars, defaults, dependencies, new HashSet<>()));
        });


        return roles;
    }

    private HashSet<Variable> parseVars(Object mainVarsYaml) {

        if (mainVarsYaml == null) {
            return new HashSet<>();
        }

        Map<String, String> mainVarsList;
        try {
            mainVarsList = (Map<String, String>) mainVarsYaml;
        } catch (Exception e) {
            LOG.error("could not parse global variables {}", e.getMessage());
            return new HashSet<>();
        }
        HashSet<Variable> vars = new HashSet<>();
        mainVarsList.forEach((key, value) -> vars.add(new Variable(key, value)));
        return vars;
    }

    private static String removeAfterLastSlash(String input) {
        int lastSlashIndex = input.lastIndexOf('/');
        if (lastSlashIndex != -1) {
            return input.substring(0, lastSlashIndex);
        } else {
            return input;  // No slash found, return the original string
        }
    }
}
