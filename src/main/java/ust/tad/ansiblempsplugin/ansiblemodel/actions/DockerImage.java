package ust.tad.ansiblempsplugin.ansiblemodel.actions;

import ust.tad.ansiblempsplugin.ansiblemodel.Module;

public class DockerImage extends Module {

  private String name;
  private String source;

  public DockerImage() {}

  /**
   * Constructs a DockerImage object with the specified name and source.
   *
   * @param name The name of the image.
   * @param source The source of the image.
   */
  public DockerImage(String name, String source) {
    super.setType("docker_image");
    this.name = name;
    this.source = source;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }
}
