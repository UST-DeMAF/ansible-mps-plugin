package ust.tad.ansiblempsplugin.ansiblemodel;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class AnsibleDeploymentModel {

  private Set<Play> plays = new HashSet<>();

  public AnsibleDeploymentModel() {}

  public AnsibleDeploymentModel(Set<Play> plays) {
    this.plays = plays;
  }

  public Set<Play> getPlays() {
    return plays;
  }

  public void setPlays(Set<Play> resources) {
    this.plays = plays;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AnsibleDeploymentModel that = (AnsibleDeploymentModel) o;
    return Objects.equals(plays, that.plays);
  }

  @Override
  public int hashCode() {
    return Objects.hash(plays);
  }

  @Override
  public String toString() {
    return "AnsibleDeploymentModel{" + "plays=" + plays + '}';
  }
}
