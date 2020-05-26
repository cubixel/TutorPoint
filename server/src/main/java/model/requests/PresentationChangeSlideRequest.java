package model.requests;

/**
 * Used to update the Client with a change in slide number.
 * This is packages and sent as a Json to be received by
 * the ListenerThread on the client.
 *
 * @author Eric Walker
 */
public class PresentationChangeSlideRequest {
  int slideNum;

  public PresentationChangeSlideRequest(int slideNum) {
    this.slideNum = slideNum;
  }
}