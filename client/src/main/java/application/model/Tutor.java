package application.model;

public class Tutor extends Account {
  boolean isFollowed;
  boolean isLive;

  public Tutor(String username, int userID, float rating, boolean isFollowed) {
    super(username, userID, rating);
    this.isFollowed = isFollowed;
  }

  public boolean isFollowed() {
    return isFollowed;
  }

  public void setFollowed(boolean followed) {
    isFollowed = followed;
  }

  public boolean isLive() {
    return isLive;
  }

  public void setLive(boolean live) {
    isLive = live;
  }
}
