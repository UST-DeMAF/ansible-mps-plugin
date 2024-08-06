package ust.tad.ansiblempsplugin.ansiblemodel.actions;

import ust.tad.ansiblempsplugin.ansiblemodel.Module;

public class DockerNetwork extends Module {
    private String name;
    private String driver;

    public DockerNetwork(String name, String driver) {
        this.name = name;
        this.driver = driver;
    }
}
