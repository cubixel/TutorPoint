package application.controller.presentation;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

/**
 * Module to play video files onto a StackPlane by adding extra MediaView.
 * Also handles removing videos via an ID assigned when drawn.
 *
 * @author Daniel Bishop/Eric Walker
 *
 */
public class VideoHandler {

  private StackPane pane;
  private Map<String, MediaView> videos = new HashMap<>();

  public VideoHandler(StackPane targetPane) {
    this.pane = targetPane;
  }

  /**
   * Registers an video from a URL onto a MediaView, using the provided ID.
   */
  public String register(String url, String id, int w, int h, int x, int y, boolean loop) {
        
    Media video = new Media(url);
    MediaPlayer player = new MediaPlayer(video);
    MediaView view = new MediaView(player);
    player.setAutoPlay(false);
    if (loop) {
      player.setCycleCount(MediaPlayer.INDEFINITE);
    }
    
    view.setFitHeight(h);
    view.setFitWidth(w);
    view.setTranslateX(x);
    view.setTranslateY(y);

    if (videos.putIfAbsent(id, view) == null) {
      return id;
    } else {
      return null;
    }
  }

  /**
   * Make the specified video visible and play it.
   */
  public boolean play(String id) {
    if (videos.containsKey(id) && !pane.getChildren().contains(videos.get(id))) {
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
  public boolean stop(String id) {
    if (videos.containsKey(id) && pane.getChildren().contains(videos.get(id))) {
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
  public boolean deregister(String id) {
    if (videos.containsKey(id)) {
      pane.getChildren().remove(videos.get(id));
      videos.remove(id);
      return true;
    } else {
      return false;
    }
  }
}
