package externalclassesfortests;

/**
 * CLASSES IN THE 'externalclassesfortests' PACKAGE ARE NOT USED FOR ANYTHING
 * OTHER THAN TESTS THAT NEED ACCESS TO A COPY OF THE CLASS.
 *
 * <p>This Class is used to contain the information used for
 * requesting a set of subjects. It is packaged as a Json
 * and sent to the server. It is used specifically by
 * the SubscriptionsWindowController to show a list of the
 * related subjects on the SubscriptionsWindow.
 *
 * @author James Gardner
 */
public class SubjectRequestSubscription {
  int numberOfSubjectsRequested;
  int userID;
  String subject;

  /**
   * Constructor for the SubjectRequestHome.
   *
   * @param numberOfSubjectsRequested
   *        The number of subjects that has already been requested from the server
   *
   * @param userID
   *        A userID that is assigned to a user upon account creation
   *
   * @param subject
   *        The subject that is used as a reference to receive similar subjects from
   *        the server
   */
  public SubjectRequestSubscription(int numberOfSubjectsRequested, int userID, String subject) {
    this.numberOfSubjectsRequested = numberOfSubjectsRequested;
    this.userID = userID;
    this.subject = subject;
  }
}