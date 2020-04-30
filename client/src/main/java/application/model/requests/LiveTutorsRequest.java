package application.model.requests;

public class LiveTutorsRequest {
  int id;

  public LiveTutorsRequest(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
}
