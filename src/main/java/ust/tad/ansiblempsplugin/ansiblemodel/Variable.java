package ust.tad.ansiblempsplugin.ansiblemodel;

import java.util.Objects;

public class Variable {

  private String name;
  private String value;

  public Variable() {}

  public Variable(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Variable)) return false;
    Variable variable = (Variable) o;
    return Objects.equals(name, variable.name) && Objects.equals(value, variable.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, value);
  }

  @Override
  public String toString() {
    return "Variable{" + "name='" + name + '\'' + ", value='" + value + '\'' + '}';
  }
}
