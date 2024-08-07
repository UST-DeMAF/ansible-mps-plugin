package ust.tad.ansiblempsplugin.ansiblemodel;

import java.util.HashSet;
import java.util.Objects;

public class Play {

  private String name;

  private Boolean become;

  private HashSet<Host> hosts;

  private HashSet<Task> preTasks;

  private HashSet<Task> tasks;

  private HashSet<Task> postTasks;

  private HashSet<Role> roles;

  private HashSet<Variable> vars;

  public Play() {}

  public Play(
      String name,
      HashSet<Host> hosts,
      HashSet<Task> preTasks,
      HashSet<Task> tasks,
      HashSet<Task> postTasks,
      HashSet<Role> roles,
      HashSet<Variable> vars,
      Boolean become) {
    this.name = name;
    this.hosts = hosts;
    this.preTasks = preTasks;
    this.tasks = tasks;
    this.postTasks = postTasks;
    this.roles = roles;
    this.vars = vars;
    this.become = become;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public HashSet<Host> getHosts() {
    return hosts;
  }

  public void setHosts(HashSet<Host> hosts) {
    this.hosts = hosts;
  }

  public HashSet<Task> getPreTasks() {
    return preTasks;
  }

  public void setPreTasks(HashSet<Task> preTasks) {
    this.preTasks = preTasks;
  }

  public HashSet<Task> getTasks() {
    return tasks;
  }

  public void setTasks(HashSet<Task> tasks) {
    this.tasks = tasks;
  }

  public HashSet<Task> getPostTasks() {
    return postTasks;
  }

  public void setPostTasks(HashSet<Task> postTasks) {
    this.postTasks = postTasks;
  }

  public HashSet<Role> getRoles() {
    return roles;
  }

  public void setRoles(HashSet<Role> roles) {
    this.roles = roles;
  }

  public HashSet<Variable> getVars() {
    return vars;
  }

  public void setVars(HashSet<Variable> vars) {
    this.vars = vars;
  }

  public Boolean getBecome() {
    return this.become;
  }

  public void setBecome(Boolean become) {
    this.become = become;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof Play)) {
      return false;
    }
    Play play = (Play) o;
    return Objects.equals(name, play.name)
        && Objects.equals(hosts, play.hosts)
        && Objects.equals(preTasks, play.preTasks)
        && Objects.equals(tasks, play.tasks)
        && Objects.equals(postTasks, play.postTasks)
        && Objects.equals(roles, play.roles)
        && Objects.equals(vars, play.vars)
        && Objects.equals(become, play.become);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, hosts, preTasks, tasks, postTasks, roles, vars, become);
  }

  @Override
  public String toString() {
    return "{"
        + " name='"
        + getName()
        + "'"
        + ", hosts='"
        + getHosts()
        + "'"
        + ", preTasks='"
        + getPreTasks()
        + "'"
        + ", tasks='"
        + getTasks()
        + "'"
        + ", postTasks='"
        + getPostTasks()
        + "'"
        + ", roles='"
        + getRoles()
        + "'"
        + ", vars='"
        + getVars()
        + "'"
        + ", become='"
        + getBecome()
        + "'"
        + "}";
  }
}
