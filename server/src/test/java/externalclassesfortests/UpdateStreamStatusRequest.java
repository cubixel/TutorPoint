package externalclassesfortests;

/**
 * CLASSES IN THE 'externalclassesfortests' PACKAGE ARE NOT USED FOR ANYTHING
 * OTHER THAN TESTS THAT NEED ACCESS TO A COPY OF THE CLASS.
 *
 * <p>Used to start streaming on a session. This
 * means that other users will now see updates in
 * realtime to the presentations and whiteboards and
 * text-chat in the StreamWindow.
 *
 * @author James Gardner
 */
public class UpdateStreamStatusRequest {

  boolean isLive;

  public UpdateStreamStatusRequest(boolean isLive) {
    this.isLive = isLive;
  }
}
