package ust.tad.ansiblempsplugin.ansiblemodel.actions;

import java.util.HashSet;
import ust.tad.ansiblempsplugin.ansiblemodel.Module;
import ust.tad.ansiblempsplugin.ansiblemodel.Variable;

public class DockerContainer extends Module {

  private String name;
  private String image;
  private String restart_policy;
  private String memory;
  private String state;
  private String network_mode;
  private String log_driver;
  private HashSet<Variable> env;
  private HashSet<Variable> networks;
  private HashSet<Variable> log_options;

  /**
   * Constructs a DockerContainer object with the specified container name, image, restart policy,
   * memory, state, network mode, log driver, environment variables, networks, and log options.
   *
   * @param name The name of the container.
   * @param image The image of the container.
   * @param restart_policy The restart policy of the container.
   * @param memory The memory of the container.
   * @param state The state of the container.
   * @param network_mode The network mode of the container.
   * @param log_driver The log driver of the container.
   * @param env The environment variables of the container.
   * @param networks The networks of the container.
   * @param log_options The log options of the container.
   */
  public DockerContainer(
      String name,
      String image,
      String restart_policy,
      String memory,
      String state,
      String network_mode,
      String log_driver,
      HashSet<Variable> env,
      HashSet<Variable> networks,
      HashSet<Variable> log_options) {
    super.setType("docker_container");
    this.name = name;
    this.image = image;
    this.restart_policy = restart_policy;
    this.memory = memory;
    this.state = state;
    this.network_mode = network_mode;
    this.log_driver = log_driver;
    this.env = env;
    this.networks = networks;
    this.log_options = log_options;
  }

  public DockerContainer() {}

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getRestart_policy() {
    return restart_policy;
  }

  public void setRestart_policy(String restart_policy) {
    this.restart_policy = restart_policy;
  }

  public String getMemory() {
    return memory;
  }

  public void setMemory(String memory) {
    this.memory = memory;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getNetwork_mode() {
    return network_mode;
  }

  public void setNetwork_mode(String network_mode) {
    this.network_mode = network_mode;
  }

  public String getLog_driver() {
    return log_driver;
  }

  public void setLog_driver(String log_driver) {
    this.log_driver = log_driver;
  }

  public HashSet<Variable> getEnv() {
    return env;
  }

  public void setEnv(HashSet<Variable> env) {
    this.env = env;
  }

  public HashSet<Variable> getNetworks() {
    return networks;
  }

  public void setNetworks(HashSet<Variable> networks) {
    this.networks = networks;
  }

  public HashSet<Variable> getLog_options() {
    return log_options;
  }

  public void setLog_options(HashSet<Variable> log_options) {
    this.log_options = log_options;
  }
}
