package model.response;

public class SubjectHomeWindowResponse {
  private int id;
  private String name;
  private String category;
  private boolean isFollowed;

  public SubjectHomeWindowResponse(int id, String name, String category, boolean isFollowed) {
    this.id = id;
    this.name = name;
    this.category = category;
    this.isFollowed = isFollowed;
  }
}
