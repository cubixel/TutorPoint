package application.controller;

import application.controller.services.MainConnection;
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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
import org.bytedeco.javacv.FrameRecorder.Exception;
import org.bytedeco.javacv.OpenCVFrameGrabber;
/**
 * This is awful for audio
 */
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


  public WebcamWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection) {
    super(viewFactory, fxmlName, mainConnection);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }


  @FXML
  void disposeCamera(ActionEvent event) {

  }

  @FXML
  void startCamera(ActionEvent event) {

  }

  @FXML
  void stopCamera(ActionEvent event) {

  }
}