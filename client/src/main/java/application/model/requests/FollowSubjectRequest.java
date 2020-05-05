package application.model.requests;

public class FollowSubjectRequest {
  int subjectID;
  boolean isFollowing;

  public FollowSubjectRequest(int subjectID, boolean isFollowing) {
    this.subjectID = subjectID;
    this.isFollowing = isFollowing;
  }
}
