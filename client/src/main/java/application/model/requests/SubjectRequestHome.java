package application.model.requests;

/**
 * This Class is used to contain the information used for
 * requesting a set of subjects. It is packaged as a Json
 * and sent to the server. It is used specifically by
 * the HomeWindowController to show a list of the top
 * subjects on the HomeWindow.
 *
 * @author James Gardner
 * @see application.controller.HomeWindowController
 */
public class SubjectRequestHome {
  int numberOfSubjectsRequested;
  int userID;

  /**
   * Constructor for the SubjectRequestHome.
   *
   * @param numberOfSubjectsRequested
   *        The number of subjects that has already been requested from the server
   *
   * @param userID
   *        A userID that is assigned to a user upon account creation
   */
  public SubjectRequestHome(int numberOfSubjectsRequested, int userID) {
    this.numberOfSubjectsRequested = numberOfSubjectsRequested;
    this.userID = userID;
  }
}