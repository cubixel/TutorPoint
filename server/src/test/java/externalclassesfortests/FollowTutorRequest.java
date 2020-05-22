package externalclassesfortests;

/**
 * CLASSES IN THE 'externalclassesfortests' PACKAGE ARE NOT USED FOR ANYTHING
 * OTHER THAN TESTS THAT NEED ACCESS TO A COPY OF THE CLASS.
 *
 * <p>This Class is used to contain the information used for
 * following a tutor. It is packaged as a Json and sent to
 * the server.
 *
 * @author James Gardner
 */
public class FollowTutorRequest {
  int tutorID;
  boolean isFollowing;

  /**
   * Constructor for the FollowTutorRequest.
   *
   * @param tutorID
   *        A userID of the tutor that is assigned to a user upon account creation
   *
   * @param isFollowing
   *        Is the User is currently following the subject or not
   */
  public FollowTutorRequest(int tutorID, boolean isFollowing) {
    this.tutorID = tutorID;
    this.isFollowing = isFollowing;
  }
}