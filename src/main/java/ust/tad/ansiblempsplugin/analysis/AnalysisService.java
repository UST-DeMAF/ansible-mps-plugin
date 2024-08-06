package ust.tad.ansiblempsplugin.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;
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

@Service
public class AnalysisService<T> {

    private static final List<String> supportedModules = List.of("docker_container");
    @Autowired
    ModelsService modelsService;
    @Autowired
    AnalysisTaskResponseSender analysisTaskResponseSender;
    @Autowired
    private TransformationService transformationService;

    private static final Logger LOG =
            LoggerFactory.getLogger(AnalysisTaskResponseSender.class);

    private static final Set<String> supportedFileExtensions = Set.of("yaml", "yml");
    private TechnologySpecificDeploymentModel tsdm;
    private TechnologyAgnosticDeploymentModel tadm;
    private Set<Integer> newEmbeddedDeploymentModelIndexes = new HashSet<>();
    private Set<Play> plays = new HashSet<>();

    /**
     * Start the analysis of the deployment model.
     * 1. Retrieve internal deployment models from models service
     * 2. Parse in technology-specific deployment model from locations
     * 3. Update tsdm with new information
     * 4. Transform to EDMM entities and update tadm
     * 5. Send updated models to models service
     * 6. Send AnalysisTaskResponse or EmbeddedDeploymentModelAnalysisRequests if present
     *
     * @param taskId
     * @param transformationProcessId
     * @param commands
     * @param locations
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
            e.printStackTrace();
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
                if (location.getUrl().equals(content.getLocation())) {
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
     * @param locations
     * @throws InvalidNumberOfContentException
     * @throws InvalidAnnotationException
     * @throws InvalidNumberOfLinesException
     * @throws IOException
     * @throws URISyntaxException
     * @throws InvalidPropertyValueException
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
            Play parsed;
            if (supportedFileExtensions.contains(fileExtension)) {
                parseFile(locationURL);
                // TODO remove this debug statement
                System.out.println(plays);
            }
            /*}*/
        }

        this.tadm = transformationService.transformInternalToTADM(this.tadm,
                new AnsibleDeploymentModel(this.plays));
    }

    private void parseFile(URL url) throws IOException, InvalidNumberOfLinesException, InvalidAnnotationException {
        DeploymentModelContent deploymentModelContent = new DeploymentModelContent();
        deploymentModelContent.setLocation(url);

        // Parse main.yaml
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        Yaml yaml = new Yaml();
        ArrayList<Map<String, T>> parsedYaml = yaml.load(reader);
        reader.close();

        // Extract information from parsed yaml construct
        parsedYaml.forEach((playYaml) -> {
            HashSet<Host> hosts = new HashSet<>();
            boolean become = false;
            String name = "";
            HashSet<Task> preTasks = new HashSet<>();
            HashSet<Role> roles = new HashSet<>();
            HashSet<Task> postTasks = new HashSet<>();
            HashSet<Task> tasks = new HashSet<>();
            HashSet<Variable> vars = new HashSet<>();

            if (playYaml.get("name") != null) {
                name = playYaml.get("name").toString();
            }
            if (playYaml.get("vars") != null) {
                vars.addAll(parseVars(playYaml.get("vars")));
            }
            if (playYaml.get("hosts") != null) {
                try {
                    hosts.addAll(parseHosts(url, playYaml.get("hosts")));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (playYaml.get("become") != null) {
                become = Boolean.parseBoolean(playYaml.get("become").toString());
            }
            if (playYaml.get("pre_tasks") != null) {
                preTasks.addAll(parseTasks(playYaml.get("pre_tasks")));
            }
            if (playYaml.get("tasks") != null) {
                tasks.addAll(parseTasks(playYaml.get("tasks")));
            }
            if (playYaml.get("post_tasks") != null) {
                postTasks.addAll(parseTasks(playYaml.get("post_tasks")));
            }
            if (playYaml.get("roles") != null) {
                try {
                    roles.addAll(parseRoles(url, playYaml.get("roles"), vars));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            plays.add(new Play(name, hosts, preTasks, tasks, postTasks, roles, vars, become));
        });
    }

    private HashSet<Host> parseHosts(URL url, T mainHostYaml) throws IOException {
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
            } else if(hosts.stream().anyMatch(host -> host.getGroup().equals(mainHostString))) {
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

            mainHostList.forEach((name -> {
                hostIterator.forEach((host -> {
                    if (host.getHostName().equals(name) || host.getGroup().equals(name)) {
                        returnHosts.add(host);
                    }
                }));
            }));
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
        parsedYaml.forEach((group, hostsWrapper) -> {
            hostsWrapper.get("hosts").forEach((hostName, varMap) -> {
                HashSet<Variable> vars = new HashSet<>();
                varMap.forEach((varKey, varValue) -> {
                    vars.add(new Variable(varKey, varValue));
                });
                hosts.add(new Host(hostName, vars, group));
            });
        });
        return hosts;
    }


    private static String removeAfterLastSlash(String input) {
        int lastSlashIndex = input.lastIndexOf('/');
        if (lastSlashIndex != -1) {
            return input.substring(0, lastSlashIndex);
        } else {
            return input;  // No slash found, return the original string
        }
    }


    private HashSet<Task> parseTasks(T mainTaskYaml) {

        HashSet<Task> tasks = new HashSet<>();

        return tasks;
    }

    private HashSet<Role> parseRoles(URL url, T mainRoleYaml, HashSet<Variable> globalVars) throws IOException {

        // TODO read roles files based on role name with new BufferedReader

        HashSet<Role> roles = new HashSet<>();
        return roles;
    }

    private HashSet<Variable> parseVars(T mainVarsYaml) {

        HashSet<Variable> vars = new HashSet<>();
        return vars;
    }
}
