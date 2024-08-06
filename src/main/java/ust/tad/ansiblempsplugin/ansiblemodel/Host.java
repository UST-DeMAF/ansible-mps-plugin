package ust.tad.ansiblempsplugin.ansiblemodel;

import java.util.HashSet;

public class Host {
    private String hostName;
    private HashSet<Variable> vars;
    private String group;

    public Host(String hostName, HashSet<Variable> vars, String group) {
        this.hostName = hostName;
        this.vars = vars;
        this.group = group;
    }
    public Host() {
    }
    public String getHostName() {
        return hostName;
    }
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
    public HashSet<Variable> getVars() {
        return vars;
    }
    public void setVars(HashSet<Variable> vars) {
        this.vars = vars;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
