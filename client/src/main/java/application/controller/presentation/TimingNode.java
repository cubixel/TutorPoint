package application.controller.presentation;

/**
 * Class to store data about when an element with type and ID should 
 * start/end.
 *
 * @author Eric Walker
 */
public class TimingNode {
  private String id;
  private long time;
  private String type;

  /**
   * Creates an instance of TimingNode with set parameters.
   * @param id The ID of the element.
   * @param time The time the element should start/end.
   * @param type The type of the element ('text', 'shape' etc.).
   */
  TimingNode(String id, long time, String type) {
    this.id = id;
    this.time = time;
    this.type = type;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public long getTime() {
    return time;
  }

  public void setTime(long time) {
    this.time = time;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}