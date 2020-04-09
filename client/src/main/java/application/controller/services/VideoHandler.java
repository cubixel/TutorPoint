package application.controller.services;

import application.controller.enums.VideoReqResult;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class VideoHandler extends Service<VideoReqResult> {

  public MediaPlayer player = null;
  private String videoID = null;
  // private MainConnection connection = null;
  public MediaView mediaView;

  public VideoHandler(MediaView mediaView, MainConnection connection) {
    this.mediaView = mediaView;
    // this.connection = connection;
  }

  /**
   * .
   */
  public VideoReqResult fetchVideo() {

    //String MRL = connection.

    Media media = new Media(this.videoID);
    this.player = new MediaPlayer(media);
    this.mediaView.setMediaPlayer(player);
    this.player.setAutoPlay(true);
    DoubleProperty width = mediaView.fitWidthProperty();
    DoubleProperty height = mediaView.fitHeightProperty();
    width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
    height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
    return VideoReqResult.SUCCESS;

  }

  @Override
  protected Task<VideoReqResult> createTask() {
    return new Task<VideoReqResult>() {
      @Override
      protected VideoReqResult call() throws Exception {
          return fetchVideo();
      }
    };
  }
}
