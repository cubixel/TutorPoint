package application.controller;

import application.controller.services.MainConnection;
import application.view.ViewFactory;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

/**
 * This is used to play videos within a JavaFX MediaView.
 * No other functionality is currently in place and the
 * media player just points to a file hosted on the server.
 *
 * @author James Gardner
 * @author Che McKirgan
 */
public class MediaPlayerController extends BaseController implements Initializable {

  @FXML
  private MediaView mediaView;

  public MediaPlayerController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection) {
    super(viewFactory, fxmlName, mainConnection);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    Media media = new Media("http://clips.vorwaerts-gmbh.de/VfE_html5.mp4");
    MediaPlayer mediaPlayer = new MediaPlayer(media);
    mediaView.setMediaPlayer(mediaPlayer);
    mediaPlayer.setMute(true);
    mediaPlayer.setAutoPlay(true);
    DoubleProperty width = mediaView.fitWidthProperty();
    DoubleProperty height = mediaView.fitHeightProperty();
    width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
    height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
  }
}
