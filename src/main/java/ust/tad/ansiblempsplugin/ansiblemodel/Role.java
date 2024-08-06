package ust.tad.ansiblempsplugin.ansiblemodel;

import java.util.HashSet;
import java.util.Objects;

public class Role {

    public Role() {}

    public Role(String name, HashSet<Task> tasks, HashSet<Task> handlers, HashSet<Variable> vars, HashSet<Variable> defaults, HashSet<Variable> meta, HashSet<File> file) {
        this.name = name;
        this.tasks = tasks;
        this.handlers = handlers;
        this.vars = vars;
        this.defaults = defaults;
        this.meta = meta;
        this.file = file;
    }

    private String name;

    private HashSet<Task> tasks;

    private HashSet<Task> handlers;

    private HashSet<Variable> vars;

    private HashSet<Variable> defaults;

    private HashSet<Variable> meta;

    private HashSet<File> file;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashSet<Task> getTasks() {
        return tasks;
    }

    public void setTasks(HashSet<Task> tasks) {
        this.tasks = tasks;
    }

    public HashSet<Task> getHandlers() {
        return handlers;
    }

    public void setHandlers(HashSet<Task> handlers) {
        this.handlers = handlers;
    }

    public HashSet<Variable> getVars() {
        return vars;
    }

    public void setVars(HashSet<Variable> vars) {
        this.vars = vars;
    }

    public HashSet<Variable> getDefaults() {
        return defaults;
    }

    public void setDefaults(HashSet<Variable> defaults) {
        this.defaults = defaults;
    }

    public HashSet<Variable> getMeta() {
        return meta;
    }

    public void setMeta(HashSet<Variable> meta) {
        this.meta = meta;
    }

    public HashSet<File> getFile() {
        return file;
    }

    public void setFile(HashSet<File> file) {
        this.file = file;
    }
}
