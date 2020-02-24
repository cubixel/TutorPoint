package application.controller;

import application.controller.services.ImageHandler;
import application.controller.services.MainConnection;
import application.view.ViewFactory;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class PresentationWindowController extends BaseController implements Initializable {

  @FXML
  private StackPane pane;

  @FXML
  private Canvas canvas;

  @FXML
  private Button draw1;

  @FXML
  private Button remove1;

  @FXML
  private Button draw2;

  @FXML
  private Button remove2;


  private ImageHandler imageHandler = null;

  public PresentationWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection) {
    super(viewFactory, fxmlName, mainConnection);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    this.imageHandler = new ImageHandler(pane);
  }

  @FXML
  void drawImage1(ActionEvent event) {
    imageHandler.drawImage("https://homepages.cae.wisc.edu/~ece533/images/cat.png", "cat", 200, 20, 200, 200);
  }

  @FXML
  void removeImage1(ActionEvent event) {
    imageHandler.remove("cat");
  }

  @FXML
  void drawImage2(ActionEvent event) {
    imageHandler.drawImage("https://homepages.cae.wisc.edu/~ece533/images/tulips.png", "flowers", 20, 200, 300, 100);
  }

  @FXML
  void removeImage2(ActionEvent event) {
    imageHandler.remove("flowers");
  }
}
