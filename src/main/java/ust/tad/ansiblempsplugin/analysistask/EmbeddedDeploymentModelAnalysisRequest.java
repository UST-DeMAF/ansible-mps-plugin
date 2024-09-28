package ust.tad.ansiblempsplugin.analysistask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class EmbeddedDeploymentModelAnalysisRequest {

  private UUID parentTaskId;

  private UUID transformationProcessId;

  private String technology;

  private List<String> commands = new ArrayList<>();

  private List<String> options = new ArrayList<>();

  private List<Location> locations = new ArrayList<>();

  public EmbeddedDeploymentModelAnalysisRequest() {}

  /**
   * Constructs an EmbeddedDeploymentModelAnalysisRequest object with the specified parent task ID,
   * transformation process ID, technology, list of commands, and list of locations.
   *
   * @param parentTaskId The unique identifier of the parent task.
   * @param transformationProcessId The unique identifier of the transformation process.
   * @param technology The technology used in the deployment model.
   * @param commands The list of commands to be executed.
   * @param locations The list of locations to be analyzed.
   */
  public EmbeddedDeploymentModelAnalysisRequest(
      UUID parentTaskId,
      UUID transformationProcessId,
      String technology,
      List<String> commands,
      List<String> options,
      List<Location> locations) {
    this.parentTaskId = parentTaskId;
    this.transformationProcessId = transformationProcessId;
    this.technology = technology;
    this.commands = commands;
    this.options = options;
    this.locations = locations;
  }

  public UUID getParentTaskId() {
    return this.parentTaskId;
  }

  public void setParentTaskId(UUID parentTaskId) {
    this.parentTaskId = parentTaskId;
  }

  public UUID getTransformationProcessId() {
    return this.transformationProcessId;
  }

  public void setTransformationProcessId(UUID transformationProcessId) {
    this.transformationProcessId = transformationProcessId;
  }

  public String getTechnology() {
    return this.technology;
  }

  public void setTechnology(String technology) {
    this.technology = technology;
  }

  public List<String> getCommands() {
    return this.commands;
  }

  public void setCommands(List<String> commands) {
    this.commands = commands;
  }

  public List<String> getOptions() {
    return this.options;
  }

  public void setOptions(List<String> options) {
    this.options = options;
  }

  public List<Location> getLocations() {
    return this.locations;
  }

  public void setLocations(List<Location> locations) {
    this.locations = locations;
  }

  /**
   * Sets the parent task ID and returns the current EmbeddedDeploymentModelAnalysisRequest object.
   *
   * @param parentTaskId The unique identifier of the parent task.
   * @return The current EmbeddedDeploymentModelAnalysisRequest object.
   */
  public EmbeddedDeploymentModelAnalysisRequest parentTaskId(UUID parentTaskId) {
    setParentTaskId(parentTaskId);
    return this;
  }

  /**
   * Sets the transformation process ID and returns the current
   * EmbeddedDeploymentModelAnalysisRequest object.
   *
   * @param transformationProcessId The unique identifier of the transformation process.
   * @return The current EmbeddedDeploymentModelAnalysisRequest object.
   */
  public EmbeddedDeploymentModelAnalysisRequest transformationProcessId(
      UUID transformationProcessId) {
    setTransformationProcessId(transformationProcessId);
    return this;
  }

  /**
   * Sets the technology and returns the current EmbeddedDeploymentModelAnalysisRequest object.
   *
   * @param technology The technology used in the deployment model.
   * @return The current EmbeddedDeploymentModelAnalysisRequest object.
   */
  public EmbeddedDeploymentModelAnalysisRequest technology(String technology) {
    setTechnology(technology);
    return this;
  }

  /**
   * Sets the list of commands and returns the current EmbeddedDeploymentModelAnalysisRequest
   * object.
   *
   * @param commands The list of commands to be executed.
   * @return The current EmbeddedDeploymentModelAnalysisRequest object.
   */
  public EmbeddedDeploymentModelAnalysisRequest commands(List<String> commands) {
    setCommands(commands);
    return this;
  }

  /**
   * Sets the list of options and returns the current EmbeddedDeploymentModelAnalysisRequest object.
   *
   * @param options The list of options to be used.
   * @return The current EmbeddedDeploymentModelAnalysisRequest object.
   */
  public EmbeddedDeploymentModelAnalysisRequest options(List<String> options) {
    setOptions(options);
    return this;
  }

  /**
   * Sets the list of locations and returns the current EmbeddedDeploymentModelAnalysisRequest
   * object.
   *
   * @param locations The list of locations to be analyzed.
   * @return The current EmbeddedDeploymentModelAnalysisRequest object.
   */
  public EmbeddedDeploymentModelAnalysisRequest locations(List<Location> locations) {
    setLocations(locations);
    return this;
  }

  /**
   * Adds a command to the list of commands and returns the current
   * EmbeddedDeploymentModelAnalysisRequest object.
   *
   * @param command The command to be added.
   * @return The current EmbeddedDeploymentModelAnalysisRequest object.
   */
  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof EmbeddedDeploymentModelAnalysisRequest)) {
      return false;
    }
    EmbeddedDeploymentModelAnalysisRequest embeddedDeploymentModelAnalysisRequest =
        (EmbeddedDeploymentModelAnalysisRequest) o;
    return Objects.equals(parentTaskId, embeddedDeploymentModelAnalysisRequest.parentTaskId)
        && Objects.equals(
            transformationProcessId, embeddedDeploymentModelAnalysisRequest.transformationProcessId)
        && Objects.equals(technology, embeddedDeploymentModelAnalysisRequest.technology)
        && Objects.equals(commands, embeddedDeploymentModelAnalysisRequest.commands)
        && Objects.equals(options, embeddedDeploymentModelAnalysisRequest.options)
        && Objects.equals(locations, embeddedDeploymentModelAnalysisRequest.locations);
  }

  /**
   * Returns the hash code of the EmbeddedDeploymentModelAnalysisRequest object.
   *
   * @return The hash code of the EmbeddedDeploymentModelAnalysisRequest object.
   */
  @Override
  public int hashCode() {
    return Objects.hash(
        parentTaskId, transformationProcessId, technology, commands, options, locations);
  }

  /**
   * Returns a string representation of the EmbeddedDeploymentModelAnalysisRequest object.
   *
   * @return A string representation of the EmbeddedDeploymentModelAnalysisRequest object.
   */
  @Override
  public String toString() {
    return "{"
        + " parentTaskId='"
        + getParentTaskId()
        + "'"
        + ", transformationProcessId='"
        + getTransformationProcessId()
        + "'"
        + ", technology='"
        + getTechnology()
        + "'"
        + ", commands='"
        + getCommands()
        + "'"
        + ", options='"
        + getOptions()
        + "'"
        + ", locations='"
        + getLocations()
        + "'"
        + "}";
  }
}
