package model.response;

public class SubjectSubscriptionsWindowResponse {
  private int id;
  private String name;
  private String category;
  private String originalSubject;
  private boolean isFollowed;

  public SubjectSubscriptionsWindowResponse(int id, String name, String category,
      String originalSubject, boolean isFollowed) {
    this.id = id;
    this.name = name;
    this.category = category;
    this.originalSubject = originalSubject;
    this.isFollowed = isFollowed;
  }
}
