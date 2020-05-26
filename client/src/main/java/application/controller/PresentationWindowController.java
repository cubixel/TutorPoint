package application.controller;

import application.controller.presentation.PresentationObject;
import application.controller.presentation.PresentationObjectFactory;
import application.controller.presentation.TimingManager;
import application.controller.presentation.TimingManagerFactory;
import application.controller.presentation.XmlHandler;
import application.controller.presentation.XmlHandlerFactory;
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

  private final PresentationObjectFactory presentationObjectFactory;
  private final TimingManagerFactory timingManagerFactory;
  private volatile TimingManager timingManager;
  private final MainConnection connection;
  private final XmlHandlerFactory xmlHandlerFactory;
  private Thread xmlParseThread;
  private final Boolean isHost;

  private static final Logger log = LoggerFactory.getLogger("PresentationWindowController");


  /**
   * This is the default constructor. PresentationWindowController
   * extends the BaseController class.
   *
   * @param viewFactory
   *        The viewFactory used for changing Scenes
   *
   * @param fxmlName
   *        The associated FXML file describing the Login Window
   *
   * @param mainConnection
   *        The connection between client and server
   *
   * @param isHost
   *        Boolean of if the current user is the host of the stream or not
   */
  public PresentationWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, Boolean isHost) {
    super(viewFactory, fxmlName, mainConnection);
    this.connection = getMainConnection();
    this.isHost = isHost;

    timingManagerFactory = new TimingManagerFactory();
    xmlHandlerFactory = new XmlHandlerFactory();
    presentationObjectFactory = new PresentationObjectFactory();

    log.info("Created");
  }

  /**
   * This is the constructor used for testing. PresentationWindowController
   * extends the BaseController class.
   *
   * @param viewFactory
   *        The viewFactory used for changing Scenes
   *
   * @param fxmlName
   *        The associated FXML file describing the Login Window
   *
   * @param mainConnection
   *        The connection between client and server
   *
   * @param isHost
   *        Boolean of if the current user is the host of the stream or not
   *
   * @param prevSlideButton
   *        A JavaFX Button to change to the previous slide
   *
   * @param nextSlideButton
   *        A JavaFX Button to change to the next slide
   *
   * @param loadPresentationButton
   *        A JavaFX Button to open the file explorer
   *
   * @param urlBox
   *        A JavaFX TextField to display the file location
   *
   * @param messageBox
   *        A JavaFX TextField to display messages to the user
   *
   * @param pane
   *        A JavaFX Pane to display the presentation
   *
   * @param presentationGrid
   *        A JavaFX GridPane to contain the presentation objects
   *
   * @param controlPane
   *        A JavaFX Pane containing the control options
   *
   * @param timingManagerFactory
   *        Used to generate Mockito versions of the TimingManager
   *
   * @param xmlHandlerFactory
   *        Used to generate Mockito versions of the XmlHandler
   */
  public PresentationWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, Boolean isHost, Button prevSlideButton, Button nextSlideButton,
      Button loadPresentationButton, TextField urlBox, TextField messageBox, StackPane pane,
      GridPane presentationGrid, Pane controlPane, TimingManagerFactory timingManagerFactory,
      XmlHandlerFactory xmlHandlerFactory, PresentationObjectFactory presentationObjectFactory) {
    super(viewFactory, fxmlName, mainConnection);
    this.connection = getMainConnection();
    this.isHost = isHost;
    this.prevSlideButton = prevSlideButton;
    this.nextSlideButton = nextSlideButton;
    this.loadPresentationButton = loadPresentationButton;
    this.urlBox = urlBox;
    this.messageBox = messageBox;
    this.pane = pane;
    this.presentationGrid = presentationGrid;
    this.controlPane = controlPane;
    this.timingManagerFactory = timingManagerFactory;
    this.timingManager = timingManagerFactory.createTimingManager(null, null);
    this.xmlHandlerFactory = xmlHandlerFactory;
    this.presentationObjectFactory = presentationObjectFactory;

    this.presentationGrid.getChildren().add(this.controlPane);
    log.info("Created");
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    resizePresentation(0, 0);
    if (!isHost) {
      presentationGrid.getChildren().remove(controlPane);
    }
    log.info("Initialised");
    getMainConnection().getListener().addPresentationWindowController(this);
  }

  /**
   * Used to adjust the size of the presentation.
   *
   * @param width
   *        The new width of the presentation
   *
   * @param height
   *        The new height of the presentation
   */
  private void resizePresentation(double width, double height) {
    // TODO - all of these probably arent necessary
    pane.setMinSize(width, height);
    pane.setMaxSize(width, height);
    pane.setPrefSize(width, height);
    Rectangle clipRectangle = new Rectangle(width, height);
    pane.setClip(clipRectangle);
  }

  @FXML
  void loadPresentation() {
    messageBox.setText("Loading...");

    String url;

    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open Presentation File");
    fileChooser.getExtensionFilters().addAll(
            new ExtensionFilter("Presentation Files", "*.xml")
    );
    File selectedFile = fileChooser.showOpenDialog(loadPresentationButton.getScene().getWindow());
    if (selectedFile != null) {
      url = selectedFile.getAbsolutePath();
      urlBox.setText(url);
    } else {
      return;
    }

    PresentationObject presentationObject = verifyXml(selectedFile);

    // If selected presentation was invalid then don't display or send to pupils
    if (presentationObject == null) {
      log.info("Invlaid presentation selected; cancelling");
      return;
    }

    log.info("Valid presentation selected. Uploading and displaying.");

    try {
      connection.sendString(connection.packageClass(new PresentationRequest("uploadXml", 0)));
      connection.getListener().sendFile(selectedFile);
    } catch (IOException e) {
      log.error("Failed to send presentation", e);
    }

    displayFile(presentationObject, 0);
  }

  public PresentationObject verifyXml(File file) {
    XmlHandler handler = new XmlHandler();
    try {
      Document xmlDoc = handler.makeXmlFromUrl(file.getAbsolutePath());
      PresentationObject presentation = new PresentationObject(xmlDoc);
      return presentation;
    } catch (XmlLoadingException e) {
      Platform.runLater(() -> {
        messageBox.setText(e.getMessage());
      });
      log.warn("Xml Loading Error: " + e.getMessage());
      return null;
    } catch (PresentationCreationException e) {
      Platform.runLater(() -> {
        messageBox.setText(e.getMessage());
      });
      log.warn("Presentation Creation Error: " + e.getMessage());
      return null;
    }
  }

  /**
   * Changes presentation to next slide.
   */
  @FXML
  public void nextSlide() {
    int newSlideNumber = timingManager.getSlideNumber() + 1;
    timingManager.changeSlideTo(newSlideNumber);
    try {
      connection.sendString(connection.packageClass(
          new PresentationRequest("changeSlide", newSlideNumber)));
    } catch (IOException e) {
      log.error("Failed to send presentation", e);
    }
  }

  /**
   * Changes presentation to previous slide.
   */
  @FXML
  public void prevSlide() {
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
   * Used to display the XML presentation selected by the user
   * or uploaded by the host.
   *
   * @param presentation
   *        The XML presentation
   *
   * @param slideNum
   *        The slide number to display
   */
  public void displayFile(PresentationObject presentation, int slideNum) {

    clearPresentation();

    displayThread = new Thread(new Runnable() {
      @Override
      public void run() {

        //set slide size
        resizePresentation(presentation.getDfSlideWidth(), presentation.getDfSlideHeight());

        // Start timing Manager
        timingManager = new TimingManager(presentation, pane);
        timingManager.start();
        log.info("Started Timing Manager");
        setSlideNum(slideNum);
        log.info("Set slide number to " + slideNum);

        Platform.runLater(() -> {
          messageBox.setText("Finished Loading");
        });
      }
    }, "DisplayThread");
    displayThread.start();
    log.info("Started Displaying XML");
  }

  /**
   * Used to change the slide position.
   *
   * @param slideNum
   *        The slide to display
   */
  public void setSlideNum(int slideNum) {
    //TODO Do this properly
    while (timingManager == null) {
      Thread.onSpinWait();
    }
    timingManager.changeSlideTo(slideNum);
  }

  /**
   * Clears the current powerpoint.
   */
  public void clearPresentation() {
    Platform.runLater(() -> pane.getChildren().clear());

    if (displayThread != null) {
      displayThread = null;
    }

    if (timingManager != null) {
      timingManager.stopManager();
      timingManager = null;
    }
  }
}
