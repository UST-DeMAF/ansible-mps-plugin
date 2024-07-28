package ust.tad.ansiblempsplugin.ansiblemodel;

import java.util.HashSet;
import java.util.Objects;

public class Role {

    public Role() {}

    public Role(String name, HashSet<Task> tasks, HashSet<Variable> vars, HashSet<Variable> defaults, HashSet<Variable> meta, HashSet<File> file) {
        this.name = name;
        this.tasks = tasks;
        this.vars = vars;
        this.defaults = defaults;
        this.meta = meta;
        this.file = file;
    }

    private String name;

    private HashSet<Task> tasks;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role role = (Role) o;
        return Objects.equals(name, role.name) && Objects.equals(tasks, role.tasks) && Objects.equals(vars, role.vars) && Objects.equals(defaults, role.defaults) && Objects.equals(meta, role.meta) && Objects.equals(file, role.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, tasks, vars, defaults, meta, file);
    }

    @Override
    public String toString() {
        return "Role{" +
                "name='" + name + '\'' +
                ", tasks=" + tasks +
                ", vars=" + vars +
                ", defaults=" + defaults +
                ", meta=" + meta +
                ", file=" + file +
                '}';
    }
}
