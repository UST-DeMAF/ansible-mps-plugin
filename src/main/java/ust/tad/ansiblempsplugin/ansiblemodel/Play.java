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

  /**
   * Constructs a Play object with the specified name, hosts, pre-tasks, tasks, post-tasks, roles,
   * variables, and become.
   *
   * @param name The name of the play.
   * @param hosts The hosts of the play.
   * @param preTasks The pre-tasks of the play.
   * @param tasks The tasks of the play.
   * @param postTasks The post-tasks of the play.
   * @param roles The roles of the play.
   * @param vars The variables of the play.
   * @param become The become of the play.
   */
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

  /**
   * Checks if the Play object is equal to the specified object.
   *
   * @param o The object to compare this Play object against.
   * @return {@code true} if the specified object is equal to this Play object, {@code false}
   *     otherwise.
   */
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

  /**
   * Returns the hash code value for this Play object.
   *
   * @return The hash code value for this Play object.
   */
  @Override
  public int hashCode() {
    return Objects.hash(name, hosts, preTasks, tasks, postTasks, roles, vars, become);
  }

  /**
   * Returns a string representation of this Play object.
   *
   * @return A string representation of this Play object.
   */
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
