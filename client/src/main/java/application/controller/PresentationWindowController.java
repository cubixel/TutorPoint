package application.controller;

import application.controller.presentation.ImageHandler;
import application.controller.presentation.PresentationObject;
import application.controller.presentation.PresentationSlide;
import application.controller.presentation.TextHandler;
import application.controller.presentation.TimingManager;
import application.controller.presentation.VideoHandler;
import application.controller.services.MainConnection;
import application.controller.services.XmlHandler;
import application.view.ViewFactory;
import java.net.URL;
import java.util.ResourceBundle;

import org.w3c.dom.Document;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

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

  public PresentationWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection) {
    super(viewFactory, fxmlName, mainConnection);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    Rectangle clipRectangle = new Rectangle(pane.getMaxWidth(), pane.getMaxHeight());
    pane.setClip(clipRectangle);



  }

  @FXML
  void loadPresentation(ActionEvent event) {
    XmlHandler handler = new XmlHandler();
    Document xmlDoc = handler.makeXmlFromUrl(urlBox.getText());
    if (xmlDoc != null) {
      PresentationObject presentation = new PresentationObject(xmlDoc);
      TextHandler textHandler = new TextHandler(pane, presentation.getDfFont(), 
          presentation.getDfFontSize(), presentation.getDfFontColor());
      ImageHandler imageHandler = new ImageHandler(pane);
      VideoHandler videoHandler = new VideoHandler(pane);
      if (presentation.getValid()) {
        timingManager = new TimingManager(presentation, pane, textHandler, imageHandler, 
            videoHandler);
        timingManager.start();
      } else {
        messageBox.setText("Invalid presentation.");
      }
    } else {
      messageBox.setText("Invalid presentation xml.");
    }
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
