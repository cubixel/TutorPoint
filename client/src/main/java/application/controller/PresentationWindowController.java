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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
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
  private TextField urlBox;

  @FXML
  private TextField messageBox;

  @FXML
  private StackPane pane;

  @FXML
  private GridPane presentationGrid;

  @FXML
  private Pane controlPane;

  private volatile TimingManager timingManager;
  private MainConnection connection;
  private Thread xmlParseThread;
  private Boolean isHost;

  private static final Logger log = LoggerFactory.getLogger("PresentationWindowController");

  /**
   * .
   * @param viewFactory .
   * @param fxmlName .
   * @param mainConnection .
   */
  public PresentationWindowController(
        ViewFactory viewFactory, String fxmlName, MainConnection mainConnection, Boolean isHost) {
    super(viewFactory, fxmlName, mainConnection);
    this.connection = getMainConnection();
    this.isHost = isHost;
    mainConnection.getListener().setPresentationWindowController(this);
    log.info("Created");
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    resizePresentation(0, 0);
    if (!isHost) {
      presentationGrid.getChildren().remove(controlPane);
    }
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
    messageBox.setText("Loading...");

    String url;
    
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open Presentation File");
    fileChooser.getExtensionFilters().addAll(
            new ExtensionFilter("Presentation Files", "*.xml")
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

    try {
      connection.sendString(connection.packageClass(new PresentationRequest("uploadXml", 0)));
      connection.getListener().sendFile(selectedFile);
    } catch (IOException e) {
      log.error("Failed to send presentation", e);
    }
    
  }

  @FXML
  void nextSlide(ActionEvent event) {
    int newSlideNumber = timingManager.getSlideNumber() + 1;
    timingManager.changeSlideTo(newSlideNumber);
    try {
      connection.sendString(connection.packageClass(
          new PresentationRequest("changeSlide", newSlideNumber)));
    } catch (IOException e) {
      log.error("Failed to send presentation", e);
    }

  }

  @FXML
  void prevSlide(ActionEvent event) {
    int newSlideNumber = timingManager.getSlideNumber() - 1;
    timingManager.changeSlideTo(newSlideNumber);
    try {
      connection.sendString(connection.packageClass(
          new PresentationRequest("changeSlide", newSlideNumber)));
    } catch (IOException e) {
      log.error("Failed to send presentation", e);
    }
  }

  /**
   * .
   */
  public void displayFile(File presentation, int slideNum) {

    clearPresentation();

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
          setSlideNum(slideNum);
          
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

  /**
   * .
   */
  public void setSlideNum(int slideNum) {
    //TODO Do this properly
    while (timingManager == null) {}
    timingManager.changeSlideTo(slideNum);
  }

  /**
   * Clears the current powerpoint.
   */
  public void clearPresentation() {
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
  }

}
