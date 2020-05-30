package application.controller;

import application.controller.services.MainConnection;
import application.controller.services.WebcamService;
import application.view.ViewFactory;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder.Exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebcamWindowController extends BaseController implements Initializable {

  @FXML
  private AnchorPane anchorPane;

  @FXML
  private Button btnStartCamera;

  @FXML
  private ImageView imgWebCamCapturedImage;

  @FXML
  private MediaView mediaView;

  @FXML
  private StackPane stackPane;


  private WebcamService service;
  private MediaPlayer IncomingPlayer;
  private MediaPlayer OutgoingPlayer;
  private final String streamID;
  private final boolean isHost;

  private static final Logger log = LoggerFactory.getLogger("WebcamWindowController");

  //TODO New constructor for joining existing
  //TODO Dropdowns for mic and camera
  //TODO Mute button
  //TODO StreamID standard

  /*
  Webcam controller class, when no streamID is passed it will start a new stream and
  add it to the database with the streamID. When passed an ID will load that stream and
  send the host their streamID and load the video
   */
  public WebcamWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, String streamID, boolean isHost) {
    super(viewFactory, fxmlName, mainConnection);
    this.streamID = streamID;
    this.isHost = isHost;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    if (!isHost) {
      imgWebCamCapturedImage.setVisible(false);
      btnStartCamera.setVisible(false);
      log.info("Not Host");
      log.info("Attempting to join Stream: " + "http://cubixelservers.uksouth.cloudapp.azure.com/hls/Upload/" + streamID + ".m3u8");
      Media media = new Media("http://cubixelservers.uksouth.cloudapp.azure.com/hls/Upload/" + streamID + ".m3u8");
      //Media media = new Media("http://cubixel.ddns.net:52677/hls/Upload/test/1080p.m3u8");
      MediaPlayer mediaPlayer = new MediaPlayer(media);
      mediaView.setMediaPlayer(mediaPlayer);
      mediaPlayer.play();
      mediaView.setPreserveRatio(true);
      getMainConnection().getListener().setWebcamWindowController(this);
      // DoubleProperty width = mediaView.fitWidthProperty();
      // DoubleProperty height = mediaView.fitHeightProperty();
      // width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
      // height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
    }
  }

  private void startStream(MainConnection connection, String StreamID) {
    try {
      // Pass player into service?
      this.service = new WebcamService(connection,imgWebCamCapturedImage ,StreamID);
      this.service.start();
      System.out.println("Running!");
      //Grab other users stream if needed
    } catch (Exception | FrameGrabber.Exception e) {
      e.printStackTrace();
    }
  }

  /*
  Connects to the incoming video stream with the given streamID.
  //TODO Feature outside of development scope
   */
  private void playIncoming(String IncomingStreamID) {
    Media media = new Media("http://cubixel.ddns.net:52677/hls/Upload/test/1080p.m3u8");
    this.IncomingPlayer = new MediaPlayer(media);
    //TODO pass this shit to a GUI player
  }

  @FXML
  void disposeCamera(ActionEvent event) {

  }

  void endStream(){
    this.service.killService();
  }

  /*
  Allows frames to be grabbed from the camera. 
   */
  @FXML
  void startCamera(ActionEvent event) {
    //TODO start and stop stream and maintain ID, this might mess it up on the receiver side
    //test this tho. If not overwrite captured frames with blanks.
    log.info("Creating stream with StreamID: " + streamID);
    startStream(this.getMainConnection(), this.streamID);
    btnStartCamera.setVisible(false);
  }

  @FXML
  void stopCamera(ActionEvent event) {

  }
}