package ust.tad.ansiblempsplugin.ansiblemodel;

import java.util.HashSet;
import java.util.Objects;

public class Play {

  private String name;

  private Boolean become;

  private HashSet<Host> hosts;

  private HashSet<Task> pre_tasks;

  private HashSet<Task> tasks;

  private HashSet<Task> post_tasks;

  private HashSet<Role> roles;

  private HashSet<Variable> vars;

  public Play() {}

  /**
   * Constructs a Play object with the specified name, hosts, pre-tasks, tasks, post-tasks, roles,
   * variables, and become.
   *
   * @param name The name of the play.
   * @param hosts The hosts of the play.
   * @param pre_tasks The pre-tasks of the play.
   * @param tasks The tasks of the play.
   * @param post_tasks The post-tasks of the play.
   * @param roles The roles of the play.
   * @param vars The variables of the play.
   * @param become The become of the play.
   */
  public Play(
      String name,
      HashSet<Host> hosts,
      HashSet<Task> pre_tasks,
      HashSet<Task> tasks,
      HashSet<Task> post_tasks,
      HashSet<Role> roles,
      HashSet<Variable> vars,
      Boolean become) {
    this.name = name;
    this.hosts = hosts;
    this.pre_tasks = pre_tasks;
    this.tasks = tasks;
    this.post_tasks = post_tasks;
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

  public HashSet<Task> getPre_tasks() {
    return pre_tasks;
  }

  public void setPre_tasks(HashSet<Task> pre_tasks) {
    this.pre_tasks = pre_tasks;
  }

  public HashSet<Task> getTasks() {
    return tasks;
  }

  public void setTasks(HashSet<Task> tasks) {
    this.tasks = tasks;
  }

  public HashSet<Task> getPost_tasks() {
    return post_tasks;
  }

  public void setPost_tasks(HashSet<Task> post_tasks) {
    this.post_tasks = post_tasks;
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
        && Objects.equals(pre_tasks, play.pre_tasks)
        && Objects.equals(tasks, play.tasks)
        && Objects.equals(post_tasks, play.post_tasks)
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
    return Objects.hash(name, hosts, pre_tasks, tasks, post_tasks, roles, vars, become);
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
        + getPre_tasks()
        + "'"
        + ", tasks='"
        + getTasks()
        + "'"
        + ", postTasks='"
        + getPost_tasks()
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
