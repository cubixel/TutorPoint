package application.controller;

import application.controller.services.MainConnection;
import application.controller.services.WebcamService;
import application.view.ViewFactory;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
//TODO Remove these once done
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder.Exception;
import org.bytedeco.javacv.OpenCVFrameGrabber;

public class WebcamWindowController extends BaseController implements Initializable {

  @FXML
  private FlowPane fpBottomPane;

  @FXML
  private Button btnStartCamera;

  @FXML
  private Button btnStopCamera;

  @FXML
  private Button btnDisposeCamera;

  @FXML
  private BorderPane bpWebCamPaneHolder;

  @FXML
  private ImageView imgWebCamCapturedImage;

  @FXML
  private ComboBox<?> cbCameraOptions;

  private WebcamService service;
  private MediaPlayer IncomingPlayer;
  private MediaPlayer OutgoingPlayer;
  //TODO New constructor for joining existing
  //TODO Dropdowns for mic and camera
  //TODO Mute button
  //TODO StreamID standard

  /*
  Webcam controller class, when no StreamID is passed it will start a new stream and
  add it to the database with the streamID. When passed an ID will load that stream and
  send the host their streamID and load the video
   */
  public WebcamWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection) {
    super(viewFactory, fxmlName, mainConnection);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    startStream(this.getMainConnection());
  }

  private void startStream(MainConnection connection){
    try {
      this.service = new WebcamService(connection, java.util.UUID.randomUUID().toString());
      this.service.start();
    }catch (Exception | FrameGrabber.Exception e){
      e.printStackTrace();
    }
  }

  /*
  Connects to the incoming video stream with the given streamID.
   */
  private void playIncoming(String IncomingStreamID){
    Media media = new Media("http://cubixel.ddns.net:52677/hls/Upload/test/1080p.m3u8");
    this.IncomingPlayer = new MediaPlayer(media);
    //TODO pass this shit to a GUI player
  }

  @FXML
  void disposeCamera(ActionEvent event) {

  }

  /*
  Allows frames to be grabbed from the camera. 
   */
  @FXML
  void startCamera(ActionEvent event) {
    //TODO start and stop stream and maintain ID, this might fuck it on the receiver side
    //test this tho. If not overwrite captured frames with blanks.
  }

  @FXML
  void stopCamera(ActionEvent event) {

  }
}