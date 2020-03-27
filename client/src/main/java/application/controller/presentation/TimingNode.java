package application.controller.presentation;

/**
 * CLASS DESCRIPTION.
 *
 * @author CUBIXEL
 *
 */
public class TimingNode {
  private int id;
  private long time;
  private String type;

  TimingNode(int id, long time, String type) {
    this.id = id;
    this.time = time;
    this.type = type;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
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