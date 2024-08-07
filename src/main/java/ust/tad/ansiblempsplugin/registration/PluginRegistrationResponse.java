package ust.tad.ansiblempsplugin.registration;

import java.util.Objects;

public class PluginRegistrationResponse {

  private String requestQueueName;

  private String responseExchangeName;

  public PluginRegistrationResponse() {}

  /**
   * Constructs a PluginRegistrationResponse object with the specified request queue name and
   * response exchange name.
   *
   * @param requestQueueName The name of the request queue.
   * @param responseExchangeName The name of the response exchange.
   */
  public PluginRegistrationResponse(String requestQueueName, String responseExchangeName) {
    this.requestQueueName = requestQueueName;
    this.responseExchangeName = responseExchangeName;
  }

  public String getRequestQueueName() {
    return this.requestQueueName;
  }

  public void setRequestQueueName(String requestQueueName) {
    this.requestQueueName = requestQueueName;
  }

  public String getResponseExchangeName() {
    return this.responseExchangeName;
  }

  public void setResponseExchangeName(String responseExchangeName) {
    this.responseExchangeName = responseExchangeName;
  }

  public PluginRegistrationResponse requestQueueName(String requestQueueName) {
    setRequestQueueName(requestQueueName);
    return this;
  }

  public PluginRegistrationResponse responseExchangeName(String responseExchangeName) {
    setResponseExchangeName(responseExchangeName);
    return this;
  }

  /**
   * Compares this PluginRegistrationResponse object to the specified object.
   *
   * @param o The object to compare this PluginRegistrationResponse object against.
   * @return {@code true} if the given object represents a PluginRegistrationResponse equivalent to
   *     this PluginRegistrationResponse object, {@code false} otherwise.
   */
  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof PluginRegistrationResponse)) {
      return false;
    }
    PluginRegistrationResponse pluginRegistrationResponse = (PluginRegistrationResponse) o;
    return Objects.equals(requestQueueName, pluginRegistrationResponse.requestQueueName)
        && Objects.equals(responseExchangeName, pluginRegistrationResponse.responseExchangeName);
  }

  /**
   * Returns the hash code value for this PluginRegistrationResponse object.
   *
   * @return The hash code value for this PluginRegistrationResponse object.
   */
  @Override
  public int hashCode() {
    return Objects.hash(requestQueueName, responseExchangeName);
  }

  /**
   * Returns a string representation of this PluginRegistrationResponse object.
   *
   * @return A string representation of this PluginRegistrationResponse object.
   */
  @Override
  public String toString() {
    return "{"
        + " requestQueueName='"
        + getRequestQueueName()
        + "'"
        + ", responseExchangeName='"
        + getResponseExchangeName()
        + "'"
        + "}";
  }
}
