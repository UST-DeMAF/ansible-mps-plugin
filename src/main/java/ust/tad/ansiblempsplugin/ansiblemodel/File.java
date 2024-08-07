package ust.tad.ansiblempsplugin.ansiblemodel;

import java.util.Objects;

public class File {

  private String path;

  public File() {}
  ;

  public File(String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof File)) return false;
    File file = (File) o;
    return Objects.equals(path, file.path);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(path);
  }

  @Override
  public String toString() {
    return "File{" + "path='" + path + '\'' + '}';
  }
}
