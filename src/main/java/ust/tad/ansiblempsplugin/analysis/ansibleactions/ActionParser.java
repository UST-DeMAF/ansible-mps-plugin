package ust.tad.ansiblempsplugin.analysis.ansibleactions;

import org.springframework.stereotype.Service;
import ust.tad.ansiblempsplugin.ansiblemodel.Module;
import ust.tad.ansiblempsplugin.ansiblemodel.actions.*;
import java.util.Map;

@Service
public class ActionParser {
    public Module parseActions(Map<String, Object> taskYaml) {
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

    // TODO finalize the unfinished methods below
    private Module parseApt(Map<String, Object> taskYaml) {
        return new Apt();
    }

    private Module parseLaunchD(Map<String, Object> taskYaml) {
        return new LaunchD();
    }

    private Module parseDockerNetwork(Map<String, Object> taskYaml) {
        Map<String, String> dockerNetworkYaml = (Map<String, String>) taskYaml.get("docker_network");
        return new DockerNetwork(
                dockerNetworkYaml.get("name"),
                dockerNetworkYaml.get("driver")
        );
    }

    private Module parseDockerImage(Map<String, Object> taskYaml) {
        return new DockerImage();
    }

    private DockerContainer parseDockerContainer(Map<String, Object> taskYaml) {
        return new DockerContainer();
    }

}
