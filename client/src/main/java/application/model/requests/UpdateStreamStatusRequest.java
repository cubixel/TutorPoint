package application.model.requests;

public class UpdateStreamStatusRequest {

  boolean isLive;

  public UpdateStreamStatusRequest(boolean isLive) {
    this.isLive = isLive;
  }
}
