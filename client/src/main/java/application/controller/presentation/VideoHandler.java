package application.controller.presentation;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

/**
 * Module to play video files onto a StackPlane by adding extra MediaView.
 * Also handles removing videos via an ID assigned when drawn.
 *
 * @author Daniel Bishop/Eric Walker
 *
 */
public class VideoHandler {

  private StackPane pane;
  private Map<String, MediaView> videos = new ConcurrentHashMap<>();

  public VideoHandler(StackPane targetPane) {
    this.pane = targetPane;
  }

  /**
   * Registers an video from a URL onto a MediaView, using the provided ID.
   */
  public String registerVideo(String url, String id, float floatX, float floatY, boolean loop) {
    //Setup 
    Media video = new Media(url);
    MediaPlayer player = new MediaPlayer(video);
    MediaView view = new MediaView(player);

    //prevent video from autoplaying and set loop property
    player.setAutoPlay(false);
    if (loop) {
      player.setCycleCount(MediaPlayer.INDEFINITE);
    }
    
    // Calculate pixel values for x, y, w and h
    int x = Math.toIntExact(Math.round((floatX / 100) * pane.getMaxWidth()));
    int y = Math.toIntExact(Math.round((floatY / 100) * pane.getMaxHeight()));

    //position
    view.setTranslateX(x);
    view.setTranslateY(y);

    //add to map
    if (videos.putIfAbsent(id, view) == null) {
      return id;
    } else {
      return null;
    }
  }

  /**
   * Make the specified video visible and play it.
   */
  public boolean startVideo(String id) {
    //if video id exists and is not already displayed
    if (videos.containsKey(id) && !pane.getChildren().contains(videos.get(id))) {
      //display and play video
      pane.getChildren().add(videos.get(id));
      videos.get(id).getMediaPlayer().play();
      return true;
    } else {
      return false;
    }
  }

  /**
   * Stops and hides the video with the provided ID.
   */
  public boolean stopVideo(String id) {
    //if video id exists and is displayed
    if (videos.containsKey(id) && pane.getChildren().contains(videos.get(id))) {
      //stop and hide video
      videos.get(id).getMediaPlayer().stop();
      pane.getChildren().remove(videos.get(id));
      return true;
    } else {
      return false;
    }
  }

  /**
   * Deregister the video with the provided ID.
   */
  public boolean deregisterVideo(String id) {
    //if video id exists
    if (videos.containsKey(id)) {
      //hide and deregister video
      pane.getChildren().remove(videos.get(id));
      videos.remove(id);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Validate provided ID.
   */
  public static boolean validateUrl(String url) {
    try {
      new Media(url);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
