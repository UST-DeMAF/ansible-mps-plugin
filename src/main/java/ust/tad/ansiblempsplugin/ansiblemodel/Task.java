package ust.tad.ansiblempsplugin.ansiblemodel;

import java.util.HashSet;

public class Task {

  private String name;
  private HashSet<Variable> vars;
  private Boolean become;
  private Module action;
  private HashSet<String> loop;

  /**
   * Constructs a Task object with the specified name, variables, become, action, and loop.
   *
   * @param name The name of the task.
   * @param vars The variables of the task.
   * @param become The become of the task.
   * @param action The action of the task.
   * @param loop The loop of the task.
   */
  public Task(
      String name, HashSet<Variable> vars, Boolean become, Module action, HashSet<String> loop) {
    this.name = name;
    this.vars = vars;
    this.become = become;
    this.action = action;
    this.loop = loop;
  }

  // Getters and Setters
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public HashSet<Variable> getVars() {
    return vars;
  }

  public void setVars(HashSet<Variable> vars) {
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

  public HashSet<String> getLoop() {
    return loop;
  }

  public void setLoop(HashSet<String> loop) {
    this.loop = loop;
  }
}
