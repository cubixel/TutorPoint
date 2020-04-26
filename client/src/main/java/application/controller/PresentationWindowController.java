package application.controller;

import application.controller.presentation.AudioHandler;
import application.controller.presentation.GraphicsHandler;
import application.controller.presentation.ImageHandler;
import application.controller.presentation.PresentationObject;
import application.controller.presentation.TextHandler;
import application.controller.presentation.TimingManager;
import application.controller.presentation.VideoHandler;
import application.controller.presentation.XmlHandler;
import application.controller.presentation.exceptions.PresentationCreationException;
import application.controller.presentation.exceptions.XmlLoadingException;
import application.controller.services.MainConnection;
import application.model.PresentationRequest;
import application.model.requests.SessionRequest;
import application.view.ViewFactory;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
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
  private Button uploadPresentationButton;

  @FXML
  private Button joinPresentationButton;

  @FXML
  private TextField urlBox;

  @FXML
  private TextField messageBox;

  @FXML
  private StackPane pane;

  TimingManager timingManager;
  MainConnection connection;
  Thread xmlParseThread;
  int sessionId;

  private static final Logger log = LoggerFactory.getLogger("PresentationWindowController");

  /**
   * .
   * @param viewFactory .
   * @param fxmlName .
   * @param mainConnection .
   */
  public PresentationWindowController(
        ViewFactory viewFactory, String fxmlName, MainConnection mainConnection) {
    super(viewFactory, fxmlName, mainConnection);
    this.connection = getMainConnection();
    mainConnection.getListener().setPresentationWindowController(this);
    log.info("Created");
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    resizePresentation(0, 0);
    log.info("Initialised");
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
    Platform.runLater(() -> {
      pane.getChildren().clear();
    });

    if (xmlParseThread != null) {
      xmlParseThread = null;
    }

    if (timingManager != null) {
      timingManager.stopManager();
      timingManager = null;
    }

    messageBox.setText("Loading...");

    /*if (urlBox.getText().equals("server")) {
      log.info("Requesting File");
      try {
        connection.sendString(connection.packageClass(new PresentationRequest("sendXml")));
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
    }*/
    String url;
    
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open Presentation File");
    fileChooser.getExtensionFilters().addAll(
            new ExtensionFilter("Text Files", "*.xml")
    );
    File selectedFile = fileChooser.showOpenDialog(
          (Stage) loadPresentationButton.getScene().getWindow());
    if (selectedFile != null) {
      url = selectedFile.getAbsolutePath();
      urlBox.setText(url);
    } else {
      return;
    }

    displayFile(selectedFile, 0);
    
  }

  @FXML
  void uploadPresentation(ActionEvent event) {

    File toSend = new File(urlBox.getText());

    try {
      connection.sendString(connection.packageClass(new PresentationRequest("uploadXml")));
      connection.getListener().sendFile(toSend);
    } catch (IOException e) {
      log.error("Failed to send presentation", e);
    }
  }

  @FXML
  void nextSlide(ActionEvent event) {
    timingManager.setSlide(timingManager.getSlideNumber() + 1);
    try {
      connection.sendString(connection.packageClass(new PresentationRequest("changeSlide", 
          timingManager.getSlideNumber())));
    } catch (IOException e) {
      log.error("Failed to send presentation", e);
    }

  }

  @FXML
  void prevSlide(ActionEvent event) {
    timingManager.setSlide(timingManager.getSlideNumber() - 1);
    try {
      connection.sendString(connection.packageClass(new PresentationRequest("changeSlide", 
          timingManager.getSlideNumber())));
    } catch (IOException e) {
      log.error("Failed to send presentation", e);
    }
  }

  @FXML
  void joinPresentation(ActionEvent event) {
    //TODO un-hardcode sessionId
    log.info("Joining stream session: [hardcoded] 1 (Admin) as user ID: " + (int)connection.getId());
    try {
      connection.sendString(
          connection.packageClass(new SessionRequest((int)connection.getId(), 1, false)));
    } catch (IOException e) {
      log.error("Failed to send stream join request", e);
    }
  }


  public void displayFile(File presentation, int slideNum) {
    xmlParseThread = new Thread(new Runnable() {
      @Override
      public void run() {
        XmlHandler handler = new XmlHandler();
        try {
          Document xmlDoc = handler.makeXmlFromUrl(presentation.getAbsolutePath());
          PresentationObject presentation = new PresentationObject(xmlDoc);
          //set slide size
          resizePresentation(presentation.getDfSlideWidth(), presentation.getDfSlideHeight());

          //Create Handlers
          TextHandler textHandler = new TextHandler(pane, presentation.getDfFont(),
              presentation.getDfFontSize(), presentation.getDfFontColor());
          ImageHandler imageHandler = new ImageHandler(pane);
          VideoHandler videoHandler = new VideoHandler(pane);
          GraphicsHandler graphicsHandler = new GraphicsHandler(pane);
          AudioHandler audioHandler = new AudioHandler();
          
          // Start timing Manager
          timingManager = new TimingManager(presentation, pane, textHandler, imageHandler,
              videoHandler, graphicsHandler, audioHandler);
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

    setSlideNum(slideNum);
  }

  public void setSlideNum(int slideNum) {
    timingManager.setSlide(slideNum);
  }
}
