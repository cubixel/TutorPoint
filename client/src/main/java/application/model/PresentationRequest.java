package application.model;

public class PresentationRequest {

  String action = null;
  int slideNum = -1;

  public PresentationRequest(String action) {
    this.action = action;
  }

  public PresentationRequest(String action, int slideNum) {
    this.action = action;
    this.slideNum = slideNum;
  }

}
