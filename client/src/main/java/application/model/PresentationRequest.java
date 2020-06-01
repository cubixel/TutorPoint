package application.model;

/**
 * This Class is used to contain the information used for
 * updating the server with a presentation action
 * It is packaged as a Json and sent to the server.
 *
 * @author Daniel Bishop
 * @author Eric Walker
 * @see application.controller.PresentationWindowController
 */
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