package ust.tad.ansiblempsplugin.ansiblemodel;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class AnsibleDeploymentModel {

    private Set<Resource> resources = new HashSet<>();

    private Set<Variable> variables = new HashSet<>();

    public AnsibleDeploymentModel() {
    }

    public AnsibleDeploymentModel(Set<Resource> resources, Set<Variable> variables) {
        this.resources = resources;
        this.variables = variables;
    }

    public Set<Resource> getResources() {
        return resources;
    }

    public void setResources(Set<Resource> resources) {
        this.resources = resources;
    }

    public Set<Variable> getVariables() {
        return variables;
    }

    public void setVariables(Set<Variable> variables) {
        this.variables = variables;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnsibleDeploymentModel that = (AnsibleDeploymentModel) o;
        return Objects.equals(resources, that.resources) && Objects.equals(variables, that.variables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resources, variables);
    }

    @Override
    public String toString() {
        return "AnsibleDeploymentModel{" +
                "resources=" + resources +
                ", variables=" + variables +
                '}';
    }
}
