package ust.tad.ansiblempsplugin.ansiblemodel.actions;

import ust.tad.ansiblempsplugin.ansiblemodel.Module;
import ust.tad.ansiblempsplugin.ansiblemodel.Variable;

import java.util.HashSet;

public class DockerContainer extends Module {
    private String containerName;
    private String image;
    private String restartPolicy;
    private String memory;

    public DockerContainer(String containerName, String image, String restartPolicy, String memory, String state, String network_mode, String log_driver, HashSet<Variable> env, HashSet<Variable> networks, HashSet<Variable> log_options) {
        this.containerName = containerName;
        this.image = image;
        this.restartPolicy = restartPolicy;
        this.memory = memory;
        this.state = state;
        this.network_mode = network_mode;
        this.log_driver = log_driver;
        this.env = env;
        this.networks = networks;
        this.log_options = log_options;
    }

    private String state;
    private String network_mode;
    private String log_driver;
    private HashSet<Variable> env;
    private HashSet<Variable> networks;
    private HashSet<Variable> log_options;

    public DockerContainer() {}

}
