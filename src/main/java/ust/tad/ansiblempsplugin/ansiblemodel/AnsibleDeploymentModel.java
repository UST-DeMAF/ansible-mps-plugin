package ust.tad.ansiblempsplugin.ansiblemodel;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class AnsibleDeploymentModel {

  private Set<Play> plays = new HashSet<>();

  public AnsibleDeploymentModel() {}

  /**
   * Constructs an AnsibleDeploymentModel object with the specified set of plays.
   *
   * @param plays The set of plays.
   */
  public AnsibleDeploymentModel(Set<Play> plays) {
    this.plays = plays;
  }

  public Set<Play> getPlays() {
    return plays;
  }

  public void setPlays(Set<Play> resources) {
    this.plays = plays;
  }

  /**
   * Checks if the AnsibleDeploymentModel object is equal to the specified object.
   *
   * @param o The object to compare this AnsibleDeploymentModel object against.
   * @return {@code true} if the specified object is equal to this AnsibleDeploymentModel object,
   *     {@code false} otherwise.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AnsibleDeploymentModel that = (AnsibleDeploymentModel) o;
    return Objects.equals(plays, that.plays);
  }

  /**
   * Returns the hash code value for this AnsibleDeploymentModel object.
   *
   * @return The hash code value for this AnsibleDeploymentModel object.
   */
  @Override
  public int hashCode() {
    return Objects.hash(plays);
  }

  /**
   * Returns a string representation of this AnsibleDeploymentModel object.
   *
   * @return A string representation of this AnsibleDeploymentModel object.
   */
  @Override
  public String toString() {
    return "AnsibleDeploymentModel{" + "plays=" + plays + '}';
  }
}
