package application.model.requests;

public class FollowTutorRequest {
  int tutorID;
  boolean isFollowing;

  public FollowTutorRequest(int tutorID, boolean isFollowing) {
    this.tutorID = tutorID;
    this.isFollowing = isFollowing;
  }
}
