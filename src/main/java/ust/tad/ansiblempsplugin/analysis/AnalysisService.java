package ust.tad.ansiblempsplugin.analysis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ust.tad.ansiblempsplugin.analysistask.AnalysisTaskResponseSender;
import ust.tad.ansiblempsplugin.analysistask.Location;
import ust.tad.ansiblempsplugin.models.ModelsService;
import ust.tad.ansiblempsplugin.models.tadm.InvalidPropertyValueException;
import ust.tad.ansiblempsplugin.models.tadm.InvalidRelationException;
import ust.tad.ansiblempsplugin.models.tadm.TechnologyAgnosticDeploymentModel;
import ust.tad.ansiblempsplugin.models.tsdm.*;
import ust.tad.ansiblempsplugin.ansiblemodel.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalysisService {

    private static final List<String> supportedModules = List.of("docker_container");
    @Autowired
    ModelsService modelsService;
    @Autowired
    AnalysisTaskResponseSender analysisTaskResponseSender;
    @Autowired
    private TransformationService transformationService;

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
        TechnologySpecificDeploymentModel completeTsdm =
                modelsService.getTechnologySpecificDeploymentModel(transformationProcessId);
        this.tsdm = getExistingTsdm(completeTsdm, locations);
        if (tsdm == null) {
            analysisTaskResponseSender.sendFailureResponse(taskId, "No technology-specific " +
                    "deployment model found!");
            return;
        }
        this.tadm = modelsService.getTechnologyAgnosticDeploymentModel(transformationProcessId);

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

        List<Line> lines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        boolean openPlayComponent = false;

        HashSet<Host> hosts = new HashSet<Host>();
        boolean become = false;
        String name = "";
        HashSet<Task> preTasks = new HashSet<Task>();
        HashSet<Role> roles = new HashSet<Role>();
        HashSet<Task> postTasks = new HashSet<Task>();
        HashSet<Task> tasks = new HashSet<Task>();
        HashSet<Variable> vars = new HashSet<Variable>();

        //First Play Object
        while (reader.lines() != null && reader.ready()) {
            String line = reader.readLine();
            if (line.trim().startsWith("hosts")) {
                hosts = parseHosts(reader, line, url);
            } else if (line.trim().startsWith("become")) {
                become = Boolean.parseBoolean(line.split(":")[1].trim());
            } else if (line.trim().startsWith("pre_tasks")) {
                preTasks = parseTasks(reader, line);
            } else if (line.trim().startsWith("roles")) {
                roles = parseRoles(reader, line, url);
            } else if (line.trim().startsWith("post_tasks")) {
                postTasks = parseTasks(reader, line);
            } else if (line.trim().startsWith("tasks")) {
                tasks = parseTasks(reader, line);
            } else if (line.trim().startsWith("vars")) {
                vars = parseVars(reader, line);
            } else if (line.startsWith("- name:")) { // No trim here, because we only want to find play names
                if (openPlayComponent) {
                    Play play = new Play(name, hosts, preTasks, tasks, postTasks, roles, vars, become);
                    //  abschließen und neues Play erzeugen
                    plays.add(play);
                    clean(hosts, preTasks, tasks, postTasks, roles, vars, become);
                    name = line.split(":")[1].trim();
                } else {
                    name = line.split(":")[1].trim();
                    openPlayComponent = true;
                }
            }
        }

        // letztes Play hinzufügen
        Play play = new Play(name, hosts, preTasks, tasks, postTasks, roles, vars, become);
        //  abschließen und neues Play erzeugen
        plays.add(play);


        reader.close();

        if (!lines.isEmpty()) {
            deploymentModelContent.setLines(lines);
            this.tsdm.addDeploymentModelContent(deploymentModelContent);
        }
    }

    private void clean(HashSet<Host> hosts, HashSet<Task> preTasks, HashSet<Task> tasks, HashSet<Task> postTasks, HashSet<Role> roles, HashSet<Variable> vars, boolean become) {
        hosts.clear();
        preTasks.clear();
        tasks.clear();
        postTasks.clear();
        roles.clear();
        vars.clear();
    }

    private HashSet<Host> parseHosts(BufferedReader reader, String line, URL url) {

        // TODO read file hosts.yml / hosts.yaml in same directory with new BufferedReader

        HashSet<Host> hosts = new HashSet<>();
        return hosts;
    }

    private HashSet<Task> parseTasks(BufferedReader reader, String line) {

        HashSet<Task> tasks = new HashSet<>();

        return tasks;
    }

    private HashSet<Role> parseRoles(BufferedReader reader, String line, URL url) {

        // TODO read roles files based on role name with new BufferedReader

        HashSet<Role> roles = new HashSet<>();
        return roles;
    }

    private HashSet<Variable> parseVars(BufferedReader reader, String line) {

        HashSet<Variable> vars = new HashSet<>();
        return vars;
    }
}
