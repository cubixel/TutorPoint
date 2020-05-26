package application.controller.presentation;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

/**
 * Module to play video files onto a StackPane by adding extra MediaView.
 * Also handles removing videos via an ID assigned when drawn.
 *
 * @author Daniel Bishop/Eric Walker
 *
 */
public class VideoHandler {

  private StackPane pane;
  private Map<String, MediaView> videos = new ConcurrentHashMap<>();

  /**
   * Creates an instance of VideoHandler that draws to a specified StackPane.
   * @param targetPane The StackPane to draw to.
   */
  public VideoHandler(StackPane targetPane) {
    this.pane = targetPane;
  }

  /**
   * Registers an video from a URL onto a MediaView, using the provided ID.
   * @param url A URL linking to the desired video file.
   * @param id An ID assigned to the video element to allow retrieval.
   * @param floatX The X position of the video on the slide, measured in percentage of slide width. 
   * @param floatY The Y position of the video on the slide, measured in percentage of slide 
   *      height. 
   * @param loop A Boolean of whether to loop the video when it ends (true) or not (false).
   * @return true if video was successfully registered or false if not.
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
   * @param id The ID of the video to play.
   * @return Boolean whether the video was successfully played or not.
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
   * @param id The ID of the video to stop.
   * @return Boolean whether the video was successfully stopped or not.
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
   * @param id The ID of the video to deregister.
   * @return Boolean whether the video was successfully deregistered or not.
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
   * Validate provided URL.
   */
  public static boolean validateUrl(String url) {
    try {
      new Media(url);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * getter for testing purposes.
   */
  Map<String, MediaView> getVideosMap() {
    return videos;
  }
}
