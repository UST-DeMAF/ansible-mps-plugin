package ust.tad.ansiblempsplugin.ansiblemodel;

import java.util.HashSet;

public class Role {

  public Role() {}

  /**
   * Constructs a Role object with the specified name, tasks, handlers, variables, defaults,
   * dependencies, and file.
   *
   * @param name The name of the role.
   * @param tasks The tasks of the role.
   * @param handlers The handlers of the role.
   * @param vars The variables of the role.
   * @param defaults The defaults of the role.
   * @param dependencies The dependencies of the role.
   * @param file The file of the role.
   */
  public Role(
      String name,
      HashSet<Task> tasks,
      HashSet<Task> handlers,
      HashSet<Variable> vars,
      HashSet<Variable> defaults,
      HashSet<String> dependencies,
      HashSet<File> file) {
    this.name = name;
    this.tasks = tasks;
    this.handlers = handlers;
    this.vars = vars;
    this.defaults = defaults;
    this.dependencies = dependencies;
    this.file = file;
  }

  private String name;

  private HashSet<Task> tasks;

  private HashSet<Task> handlers;

  private HashSet<Variable> vars;

  private HashSet<Variable> defaults;

  private HashSet<String> dependencies;

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

  public HashSet<String> getDependencies() {
    return dependencies;
  }

  public void setDependencies(HashSet<String> dependencies) {
    this.dependencies = dependencies;
  }

  public HashSet<File> getFile() {
    return file;
  }

  public void setFile(HashSet<File> file) {
    this.file = file;
  }
}
