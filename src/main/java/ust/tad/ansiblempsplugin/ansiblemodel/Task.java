package ust.tad.ansiblempsplugin.ansiblemodel;

import java.util.HashSet;

public class Task {

    private String name;
    private HashSet<String> vars; // Assuming vars should be a set of Strings
    private Boolean become;
    private Module action;

    public Task(String name, HashSet<String> vars, Boolean become, Module action) {
        this.name = name;
        this.vars = vars;
        this.become = become;
        this.action = action;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashSet<String> getVars() {
        return vars;
    }

    public void setVars(HashSet<String> vars) {
        this.vars = vars;
    }

    public Boolean getBecome() {
        return become;
    }

    public void setBecome(Boolean become) {
        this.become = become;
    }

    public Module getAction() {
        return action;
    }

    public void setAction(Module action) {
        this.action = action;
    }
}
