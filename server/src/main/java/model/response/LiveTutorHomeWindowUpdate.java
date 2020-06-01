package model.response;

/**
 * Used to send an update from the Server to the Client
 * upon any change to the status of live tutors. Contains
 * the information on the tutor that has changed status.
 *
 * @author James Gardner
 * @see services.ClientNotifier
 */
public class LiveTutorHomeWindowUpdate {
  String tutorName;
  int tutorID;
  float rating;
  boolean isLive;

  /**
   * Constructor for the LiveTutorHomeWindowUpdate all information must
   * be supplied.
   *
   * @param tutorName
   *        A string with the unique name of the tutor
   *
   * @param tutorID
   *        A userID that is assigned to a user upon account creation
   *
   * @param rating
   *        The current average rating of the tutor
   *
   * @param isLive
   *        If the tutor is live or not
   */
  public LiveTutorHomeWindowUpdate(String tutorName, int tutorID, float rating, boolean isLive) {
    this.tutorName = tutorName;
    this.tutorID = tutorID;
    this.rating = rating;
    this.isLive = isLive;
  }
}