package model.response;

/**
 * Used to send a subject from the Server to the Client
 * specifically to update the subscriptions window on the
 * Client side. Contains all the information on the subject
 * relevant to that user.
 *
 * @author James Gardner
 * @see services.ClientNotifier
 */
public class SubjectSubscriptionsWindowResponse {
  int id;
  String name;
  String category;
  String originalSubject;
  boolean isFollowed;

  /**
   * Constructor for the SubjectSubscriptionsWindowResponse all information must
   * be supplied.
   *
   * @param id
   *        A unique ID that is assigned to a subject upon creation
   *
   * @param name
   *        The unique String used to identify the subject
   *
   * @param category
   *        A unique string that is assigned to a category upon creation
   *
   * @param originalSubject
   *        The original subject sent by the client used to request similar subjects
   *
   * @param isFollowed
   *        If the user is following the subject or not
   */
  public SubjectSubscriptionsWindowResponse(int id, String name, String category,
      String originalSubject, boolean isFollowed) {
    this.id = id;
    this.name = name;
    this.category = category;
    this.originalSubject = originalSubject;
    this.isFollowed = isFollowed;
  }
}