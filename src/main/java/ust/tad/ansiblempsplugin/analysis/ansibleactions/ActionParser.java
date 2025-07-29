package ust.tad.ansiblempsplugin.analysis.ansibleactions;

import org.springframework.stereotype.Service;
import ust.tad.ansiblempsplugin.ansiblemodel.Module;
import ust.tad.ansiblempsplugin.ansiblemodel.Variable;
import ust.tad.ansiblempsplugin.ansiblemodel.actions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
@Service
public class ActionParser {
    /**
     * Parses the actions of an ansible task.
     *
     * @param taskYaml The yaml representation of the task.
     * @return The module that represents the task.
     */
    public Module parseActions(Map<String, Object> taskYaml) {
        if (taskYaml == null) {
            return new Module("default-fallback");
        }

        // Here we have the ontological vendor-specific modules in ansible.
        // If the ansible play uses a specific task-type it must have a dedicated parsing here.
        if (taskYaml.containsKey("community.general.launchd")) {
            return parseLaunchD(taskYaml);
        } else if (taskYaml.containsKey("docker_network")) {
            return parseDockerNetwork(taskYaml);
        } else if (taskYaml.containsKey("docker_image")) {
            return parseDockerImage(taskYaml);
        } else if (taskYaml.containsKey("docker_container")) {
            return parseDockerContainer(taskYaml);
        } else if (taskYaml.containsKey("apt")) {
            return parseApt(taskYaml);
        } else {
            return new Module("default-fallback");
        }
    }

    /**
     * Parses the apt module.
     *
     * @param taskYaml The yaml representation of the task.
     * @return The module that represents the task.
     */
    private Module parseApt(Map<String, Object> taskYaml) {
        Map<String, Object> aptYaml = (Map<String, Object>) taskYaml.get("apt");
        if (aptYaml.get("pkg") != null) {
            return new Apt(
                    aptYaml.get("name").toString(),
                    aptYaml.get("state").toString(),
                    (HashSet<String>) aptYaml.get("pkg"));
        } else {
            return new Apt(aptYaml.get("name").toString(), aptYaml.get("state").toString());
        }
    }

    /**
     * Parses the launchd module.
     *
     * @param taskYaml The yaml representation of the task.
     * @return The module that represents the task.
     */
    private Module parseLaunchD(Map<String, Object> taskYaml) {
        Map<String, Object> launchDYaml =
                (Map<String, Object>) taskYaml.get("community.general.launchd");
        List<String> loopValues = new ArrayList<>();
        if (taskYaml.containsKey("loop")) {
            loopValues = getListOfStringsFromYAMLList("loop", taskYaml);
        }
        return createLaunchD(launchDYaml, loopValues);
    }

    /**
     * Creates a new launchD object and resolves Ansible loop over the name field if used.
     *
     * @param launchDYaml the yaml containing the launchD task.
     * @param loopValues  the values of the loop.
     * @return a new launchD object.
     */
    private LaunchD createLaunchD(Map<String, Object> launchDYaml, List<String> loopValues) {
        String name = launchDYaml.get("name").toString();
        if (name.trim().equals("{{ item }}")) {
            name = loopValues.toString();
        }
        return new LaunchD(
                name,
                launchDYaml.getOrDefault("state", "").toString(),
                (boolean) launchDYaml.getOrDefault("enabled", false));
    }

    /**
     * Parses the docker_network module.
     *
     * @param taskYaml The yaml representation of the task.
     * @return The module that represents the task.
     */
    private Module parseDockerNetwork(Map<String, Object> taskYaml) {
        Map<String, String> dockerNetworkYaml = (Map<String, String>) taskYaml.get(
                "docker_network");
        return new DockerNetwork(
                dockerNetworkYaml.get("name"),
                dockerNetworkYaml.getOrDefault("driver", "bridge"));
    }

    /**
     * Parses the docker_image module.
     *
     * @param taskYaml The yaml representation of the task.
     * @return The module that represents the task.
     */
    private Module parseDockerImage(Map<String, Object> taskYaml) {
        Map<String, Object> dockerImageYaml = (Map<String, Object>) taskYaml.get("docker_image");
        return new DockerImage(
                getStringByKeyFromYAML("name", dockerImageYaml, ""),
                getStringByKeyFromYAML("source", dockerImageYaml, ""));
    }

    /**
     * Parses the docker_container module.
     *
     * @param taskYaml The yaml representation of the task.
     * @return The module that represents the task.
     */
    private DockerContainer parseDockerContainer(Map<String, Object> taskYaml) {
        Map<String, Object> dockerContainerYaml =
                (Map<String, Object>) taskYaml.get("docker_container");
        return new DockerContainer(
                getStringByKeyFromYAML("name", dockerContainerYaml, ""),
                getStringByKeyFromYAML("image", dockerContainerYaml, ""),
                getStringByKeyFromYAML("restart_policy", dockerContainerYaml, "no"),
                getStringByKeyFromYAML("memory", dockerContainerYaml, "no memory limit"),
                getStringByKeyFromYAML("state", dockerContainerYaml, "started"),
                getStringByKeyFromYAML("network_mode", dockerContainerYaml, "default"),
                getStringByKeyFromYAML("log_driver", dockerContainerYaml, "json-file"),
                getSetOfVariablesFromYAMLDictionary("env", dockerContainerYaml),
                getSetOfVariablesFromYAMLList("networks", dockerContainerYaml),
                getSetOfVariablesFromYAMLDictionary("log_options", dockerContainerYaml));
    }

    /**
     * Generic function to extract a single value by key from a yaml.
     * If the yaml does not contain the key, the defaultValue is used.
     *
     * @param key          the key to search for in the yaml.
     * @param yaml         the yaml to search in.
     * @param defaultValue fallback value if key is not contained.
     * @return the value of the key or the defaultValue.
     */
    private String getStringByKeyFromYAML(String key, Map<String, Object> yaml,
                                          String defaultValue) {
        if (yaml.containsKey(key)) {
            return yaml.get(key).toString();
        } else {
            return defaultValue;
        }
    }

    /**
     * Generic function to extract a set of variables by key from a dictionary in a yaml.
     * If the yaml does not contain the dictionary with the given key, returns an empty set.
     *
     * @param dictKey the key of the dictionary to search for in the yaml.
     * @param yaml    the yaml to search in.
     * @return the extracted variables or an empty set.
     */
    private HashSet<Variable> getSetOfVariablesFromYAMLDictionary(String dictKey, Map<String,
            Object> yaml) {
        HashSet<Variable> variables = new HashSet<>();
        if (yaml.containsKey(dictKey)) {
            Map<String, String> yamlDict = (Map<String, String>) yaml.get(dictKey);
            yamlDict.forEach((key, value) -> variables.add(new Variable(key, value)));
        }
        return variables;
    }

    /**
     * Generic function to extract a set of variables by key from a list in a yaml.
     * If the yaml does not contain the list with the given key, returns an empty set.
     *
     * @param listKey the key of the list to search for in the yaml.
     * @param yaml    the yaml to search in.
     * @return the extracted variables or an empty set.
     */
    private HashSet<Variable> getSetOfVariablesFromYAMLList(String listKey,
                                                            Map<String, Object> yaml) {
        HashSet<Variable> variables = new HashSet<>();
        if (yaml.containsKey(listKey)) {
            ArrayList<Map<String, String>> listYaml =
                    (ArrayList<Map<String, String>>) yaml.get(listKey);
            variables.addAll(
                    listYaml.stream()
                            .map(
                                    map ->
                                            new Variable(
                                                    map.keySet().stream().findFirst().get(),
                                                    map.values().stream().findFirst().get()))
                            .collect(Collectors.toCollection(HashSet<Variable>::new)));
        }
        return variables;
    }

    /**
     * Generic function to extract a list of strings by key from a list in a yaml.
     * If the yaml does not contain the list with the given key, returns an empty list.
     *
     * @param listKey the key of the list to search for in the yaml.
     * @param yaml    the yaml to search in.
     * @return the extracted list of strings or an empty list.
     */
    private List<String> getListOfStringsFromYAMLList(String listKey, Map<String, Object> yaml) {
        if (yaml.containsKey(listKey)) {
            return (ArrayList<String>) yaml.get(listKey);
        } else {
            return List.of();
        }
    }
}
