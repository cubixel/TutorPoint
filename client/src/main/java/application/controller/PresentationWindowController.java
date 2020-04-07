package application.controller;

import application.controller.presentation.ImageHandler;
import application.controller.presentation.PresentationObject;
import application.controller.presentation.TextHandler;
import application.controller.presentation.TimingManager;
import application.controller.presentation.VideoHandler;
import application.controller.presentation.XmlHandler;
import application.controller.presentation.exceptions.PresentationCreationException;
import application.controller.presentation.exceptions.XmlLoadingException;
import application.controller.services.ListenerThread;
import application.controller.services.MainConnection;
import application.model.PresentationRequest;
import application.view.ViewFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class PresentationWindowController extends BaseController implements Initializable {

  @FXML
  private Button prevSlideButton;

  @FXML
  private Button nextSlideButton;

  @FXML
  private Button loadPresentationButton;

  @FXML
  private TextField urlBox;

  @FXML
  private TextField messageBox;

  @FXML
  private StackPane pane;

  @FXML
  private Canvas canvas;

  TimingManager timingManager;

  MainConnection connection;

  ListenerThread listen;

  private static final Logger log = LoggerFactory.getLogger("PresentationWindowController Logger");

  /**
   * Uhh.
   * 
   * @param viewFactory a
   * @param fxmlName a
   * @param mainConnection a
   */
  public PresentationWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection) {
    super(viewFactory, fxmlName, mainConnection);
    this.connection = getMainConnection();
    this.listen = new ListenerThread(mainConnection);
    listen.start();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    resizePresentation(0, 0);
  }

  private void resizePresentation(double width, double height) {
    // TODO - all of these probably arent necessary
    pane.setMinSize(width, height);
    pane.setMaxSize(width, height);
    pane.setPrefSize(width, height);
    Rectangle clipRectangle = new Rectangle(width, height);
    pane.setClip(clipRectangle);
  }

  @FXML
  void loadPresentation(ActionEvent event) {
    messageBox.setText("Loading...");

    if (urlBox.getText().equals("server")) {
      log.info("Requesting File");
      try {
        connection.sendString(connection.packageClass(new PresentationRequest("SendXml")));
      } catch (IOException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }

      log.info("Listening for file...");
      File downloadedFile = null;
      try {
        downloadedFile = getMainConnection().listenForFile();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      if (downloadedFile == null) {
        log.error("Failed to get a file from server");
        return;
      } else {
        urlBox.setText(downloadedFile.getAbsolutePath());
      }
    }

    // Use a new thread to prevent locking up the JavaFX Application Thread while parsing
    Thread xmlParseThread = new Thread(new Runnable() {
      @Override
      public void run() {
        XmlHandler handler = new XmlHandler();
        try {
          Document xmlDoc = handler.makeXmlFromUrl(urlBox.getText());
          PresentationObject presentation = new PresentationObject(xmlDoc);
          TextHandler textHandler = new TextHandler(pane, presentation.getDfFont(), 
              presentation.getDfFontSize(), presentation.getDfFontColor());
          ImageHandler imageHandler = new ImageHandler(pane);
          VideoHandler videoHandler = new VideoHandler(pane);
          //set slide size
          resizePresentation(presentation.getDfSlideWidth(), presentation.getDfSlideHeight());

          timingManager = new TimingManager(presentation, pane, textHandler, imageHandler, 
              videoHandler);
          timingManager.start();
        } catch (XmlLoadingException e) {
          Platform.runLater(() -> {
            messageBox.setText(e.getMessage());
          });
          log.warn("Xml Loading Error: " + e.getMessage());
          return;
        } catch (PresentationCreationException e) {
          Platform.runLater(() -> {
            messageBox.setText(e.getMessage());
          });
          log.warn("Presentation Creation Error: " + e.getMessage());
          return;
        }

        Platform.runLater(() -> {
          messageBox.setText("Finished Loading");
        });
      }
    }, "XmlParseThread");
    xmlParseThread.start();
  }

  @FXML
  void nextSlide(ActionEvent event) {
    timingManager.setSlide(timingManager.getSlideNumber() + 1);
  }

  @FXML
  void prevSlide(ActionEvent event) {
    timingManager.setSlide(timingManager.getSlideNumber() - 1);
  }
}
