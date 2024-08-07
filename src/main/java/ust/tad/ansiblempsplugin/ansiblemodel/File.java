package ust.tad.ansiblempsplugin.ansiblemodel;

import java.util.Objects;

public class File {

  private String path;

  public File() {}
  ;

  /**
   * Constructs a File object with the specified path.
   *
   * @param path The path of the file.
   */
  public File(String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  /**
   * Compares this File object to the specified object.
   *
   * @param o The object to compare this File object against.
   * @return {@code true} if the specified object is equal to this File object, {@code false}
   *     otherwise.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof File)) return false;
    File file = (File) o;
    return Objects.equals(path, file.path);
  }

  /**
   * Returns the hash code value for this File object.
   *
   * @return The hash code value for this File object.
   */
  @Override
  public int hashCode() {
    return Objects.hashCode(path);
  }

  /**
   * Returns a string representation of this File object.
   *
   * @return A string representation of this File object.
   */
  @Override
  public String toString() {
    return "File{" + "path='" + path + '\'' + '}';
  }
}
