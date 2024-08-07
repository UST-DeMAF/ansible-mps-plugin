package ust.tad.ansiblempsplugin.ansiblemodel.actions;

import ust.tad.ansiblempsplugin.ansiblemodel.Module;

public class LaunchD extends Module {
    private String name;
    private String state;
    private boolean enabled;

    public LaunchD(String name, String state, boolean enabled) {
        this.name = name;
        this.state = state;
        this.enabled = enabled;
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

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
