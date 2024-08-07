package ust.tad.ansiblempsplugin.analysis.ansibleactions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import ust.tad.ansiblempsplugin.ansiblemodel.Module;
import ust.tad.ansiblempsplugin.ansiblemodel.Variable;
import ust.tad.ansiblempsplugin.ansiblemodel.actions.*;

@SuppressWarnings("unchecked")
@Service
public class ActionParser {
  public Module parseActions(Map<String, Object> taskYaml) {

    if (taskYaml != null){
      return new Module("default-fallback");
    }

    // Here we have the ontological vendor-specific modules in ansible.
    // If the ansible play uses a specific task-type it must have a dedicated parsing here.
    if (taskYaml.get("community.general.launchd") != null) {
      return parseLaunchD(taskYaml);
    } else if (taskYaml.get("docker_network") != null) {
      return parseDockerNetwork(taskYaml);
    } else if (taskYaml.get("docker_image") != null) {
      return parseDockerImage(taskYaml);
    } else if (taskYaml.get("docker_container") != null) {
      return parseDockerContainer(taskYaml);
    } else if (taskYaml.get("apt") != null) {
      return parseApt(taskYaml);
    } else {
      return new Module("default-fallback");
    }
  }

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

  private Module parseLaunchD(Map<String, Object> taskYaml) {
    Map<String, Object> launchDYaml =
        (Map<String, Object>) taskYaml.get("community.general.launchd");
    return new LaunchD(
        launchDYaml.get("name").toString(),
        launchDYaml.getOrDefault("state", "").toString(),
        (boolean) launchDYaml.getOrDefault("enabled", false));
  }

  private Module parseDockerNetwork(Map<String, Object> taskYaml) {
    Map<String, String> dockerNetworkYaml = (Map<String, String>) taskYaml.get("docker_network");
    return new DockerNetwork(dockerNetworkYaml.get("name"), dockerNetworkYaml.get("driver"));
  }

  private Module parseDockerImage(Map<String, Object> taskYaml) {
    Map<String, String> dockerImageYaml = (Map<String, String>) taskYaml.get("docker_image");
    return new DockerImage(dockerImageYaml.get("name"), dockerImageYaml.get("source"));
  }

  private DockerContainer parseDockerContainer(Map<String, Object> taskYaml) {

    Map<String, Object> dockerContainerYaml =
        (Map<String, Object>) taskYaml.get("docker_container");

    // Extract environment variables
    Map<String, String> envYaml = (Map<String, String>) dockerContainerYaml.get("env");
    HashSet<Variable> env = new HashSet<>();
    envYaml.forEach((key, value) -> env.add(new Variable(key, value)));

    // Extract networks
    ArrayList<Map<String, String>> networksYaml =
        (ArrayList<Map<String, String>>) dockerContainerYaml.get("networks");
    HashSet<Variable> networks =
        networksYaml.stream()
            .map(
                map ->
                    new Variable(
                        map.keySet().stream().findFirst().get(),
                        map.values().stream().findFirst().get()))
            .collect(Collectors.toCollection(HashSet<Variable>::new));

    // Extract Log options
    Map<String, String> logOptionsYaml =
        (Map<String, String>) dockerContainerYaml.get("log_options");
    HashSet<Variable> logOptions = new HashSet<>();
    logOptionsYaml.forEach((key, value) -> logOptions.add(new Variable(key, value)));

    return new DockerContainer(
        dockerContainerYaml.get("name").toString(),
        dockerContainerYaml.get("image").toString(),
        dockerContainerYaml.get("restart_policy").toString(),
        dockerContainerYaml.get("memory").toString(),
        dockerContainerYaml.get("state").toString(),
        dockerContainerYaml.get("network_mode").toString(),
        dockerContainerYaml.get("log_driver").toString(),
        env,
        networks,
        logOptions);
  }
}
