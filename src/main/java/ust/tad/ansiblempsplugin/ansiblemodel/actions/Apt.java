package ust.tad.ansiblempsplugin.ansiblemodel.actions;

import java.util.HashSet;
import ust.tad.ansiblempsplugin.ansiblemodel.Module;

public class Apt extends Module {
  private String name;
  private String state;
  private HashSet<String> pkg;

  public Apt() {}

  /**
   * Constructs an Apt object with the specified name, state, and package list.
   *
   * @param name The name of the package.
   * @param state The state of the package.
   * @param pkg The list of packages.
   */
  public Apt(String name, String state, HashSet<String> pkg) {
    super.setType("apt");
    this.name = name;
    this.state = state;
    this.pkg = pkg;
  }

  /**
   * Constructs an Apt object with the specified name and state.
   *
   * @param name The name of the package.
   * @param state The state of the package.
   */
  public Apt(String name, String state) {
    super.setType("apt");
    this.name = name;
    this.state = state;
  }

  public HashSet<String> getPkg() {
    return pkg;
  }

  public void setPkg(HashSet<String> pkg) {
    this.pkg = pkg;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
