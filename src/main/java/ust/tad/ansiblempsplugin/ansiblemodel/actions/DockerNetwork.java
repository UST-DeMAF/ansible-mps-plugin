package ust.tad.ansiblempsplugin.ansiblemodel.actions;

import ust.tad.ansiblempsplugin.ansiblemodel.Module;

public class DockerNetwork extends Module {

  private String name;
  private String driver;

  public DockerNetwork(String name, String driver) {
    this.name = name;
    this.driver = driver;
  }

  public DockerNetwork() {}

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDriver() {
    return driver;
  }

  public void setDriver(String driver) {
    this.driver = driver;
  }
}
