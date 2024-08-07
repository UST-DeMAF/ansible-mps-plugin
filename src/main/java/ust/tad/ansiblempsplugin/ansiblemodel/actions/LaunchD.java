package ust.tad.ansiblempsplugin.ansiblemodel.actions;

import ust.tad.ansiblempsplugin.ansiblemodel.Module;

public class LaunchD extends Module {
    private String name;
    private String state;

    public LaunchD(String name, String state) {
        this.name = name;
        this.state = state;
    }

    public LaunchD() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
