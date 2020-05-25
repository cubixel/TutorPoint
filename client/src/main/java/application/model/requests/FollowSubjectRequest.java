package application.model.requests;

import application.controller.SubjectWindowController;

/**
 * This Class is used to contain the information used for
 * following a subject. It is packaged as a Json and sent to
 * the server.
 *
 * @author James Gardner
 * @see application.controller.services.FollowSubjectRequestService
 * @see SubjectWindowController
 */
public class FollowSubjectRequest {
  int subjectID;
  boolean isFollowing;

  /**
   * Constructor for the FollowSubjectRequest.
   *
   * @param subjectID
   *        A unique subject ID that is assigned by the MySql Database
   *
   * @param isFollowing
   *        Is the User is currently following the subject or not
   */
  public FollowSubjectRequest(int subjectID, boolean isFollowing) {
    this.subjectID = subjectID;
    this.isFollowing = isFollowing;
  }
}