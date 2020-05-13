package application.model;

import application.model.managers.TutorManager;

/**
 * Extension of the Account class used for holding extra
 * information regarding the current Users relationship with
 * the tutor and tutor specific information such as being live.
 *
 * @author James Gardner
 * @see TutorManager
 */
public class Tutor extends Account {
  boolean isFollowed;
  boolean isLive = false;

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
