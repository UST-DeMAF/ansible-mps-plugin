package ust.tad.ansiblempsplugin.ansiblemodel;

import java.util.Objects;

public class Variable {

  private String name;
  private String value;

  public Variable() {}

  /**
   * Constructs a Variable object with the specified name and value.
   *
   * @param name The name of the variable.
   * @param value The value of the variable.
   */
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

  /**
   * Compares this Variable object to the specified object.
   *
   * @param o The object to compare this Variable object against.
   * @return {@code true} if the given object represents a Variable equivalent to this Variable
   *     object, {@code false} otherwise.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Variable)) return false;
    Variable variable = (Variable) o;
    return Objects.equals(name, variable.name) && Objects.equals(value, variable.value);
  }

  /**
   * Returns the hash code value for this Variable object.
   *
   * @return The hash code value for this Variable object.
   */
  @Override
  public int hashCode() {
    return Objects.hash(name, value);
  }

  /**
   * Returns a string representation of this Variable object.
   *
   * @return A string representation of this Variable object.
   */
  @Override
  public String toString() {
    return "Variable{" + "name='" + name + '\'' + ", value='" + value + '\'' + '}';
  }
}
