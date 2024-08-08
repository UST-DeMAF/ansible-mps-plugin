package ust.tad.ansiblempsplugin.ansiblemodel;

public class Module {

  private String type;

  public Module() {}

  public Module(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
