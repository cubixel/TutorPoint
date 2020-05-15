package application.model.requests;

/**
 * Used to start streaming on a session. This
 * means that other users will now see updates in
 * realtime to the presentations and whiteboards and
 * text-chat in the StreamWindow.
 *
 * @author James Gardner
 * @see application.controller.StreamWindowController
 */
public class UpdateStreamStatusRequest {

  boolean isLive;

  public UpdateStreamStatusRequest(boolean isLive) {
    this.isLive = isLive;
  }
}
