package application.controller.presentation;

/**
 * CLASS DESCRIPTION.
 *
 * @author CUBIXEL
 *
 */
public class TimingNode {
  private String id;
  private long time;
  private String type;

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