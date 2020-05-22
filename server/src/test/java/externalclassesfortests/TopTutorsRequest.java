package externalclassesfortests;

/**
 * CLASSES IN THE 'externalclassesfortests' PACKAGE ARE NOT USED FOR ANYTHING
 * OTHER THAN TESTS THAT NEED ACCESS TO A COPY OF THE CLASS.
 *
 * <p>This Class is used to contain the information used for
 * requesting a set of top tutors. It is packaged as a Json
 * and sent to the server. It is used specifically by
 * the HomeWindowController to show a list of the top
 * tutors on the HomeWindow.
 *
 * @author James Gardner
 */
public class TopTutorsRequest {
  int numberOfTutorsRequested;
  int userID;

  /**
   * Constructor for the TopTutorsRequest.
   *
   * @param numberOfTutorsRequested
   *        The number of tutors that has already been requested from the server
   *
   * @param userID
   *        A userID that is assigned to a user upon account creation
   */
  public TopTutorsRequest(int numberOfTutorsRequested, int userID) {
    this.numberOfTutorsRequested = numberOfTutorsRequested;
    this.userID = userID;
  }
}
