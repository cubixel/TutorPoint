package application.controller;

import application.controller.services.MainConnection;
import application.model.managers.SubjectManager;
import application.view.ViewFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubjectWindowContoller extends BaseController implements Initializable {

  @FXML
  private AnchorPane coverAnchorPane;

  @FXML
  private Button backToDiscoverButton;

  @FXML
  private Button backToHomeButton;

  private SubjectManager subjectManager;
  private int subject;
  private AnchorPane discoverWindowAnchorPane;
  private MainWindowController mainWindowController;

  private static final Logger log = LoggerFactory.getLogger("SubjectWindowController");

  /**
   * Constructor that all controllers must use.
   *
   * @param viewFactory    The ViewFactory creates windows that are controlled by the controller.
   * @param fxmlName       The FXML file that describes a window the controller contains the logic
   *                       for.
   * @param mainConnection .
   */
  public SubjectWindowContoller(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, int subject) {
    super(viewFactory, fxmlName, mainConnection);
    this.mainWindowController = (MainWindowController)
        viewFactory.getWindowControllers().get("MainWindowController");
    this.subjectManager = mainWindowController.getSubjectManager();
    this.discoverWindowAnchorPane = mainWindowController.getDiscoverAnchorPane();
    this.subject = subject;

  }

  @FXML
  void backToDiscoverButtonAction() {
    try {
      discoverWindowAnchorPane.getChildren().clear();
      viewFactory.embedDiscoverWindow(discoverWindowAnchorPane, mainWindowController);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  void backToHomeButtonAction() {
    mainWindowController.getPrimaryTabPane().getSelectionModel().select(0);
  }


  @FXML
  void followSubjectButton() {
    log.debug("Follow Subject Button Pressed But No Action Taken");
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    TextField textField = new TextField(subjectManager.getSubject(subject).getName());
    textField.setAlignment(Pos.CENTER);
    textField.setMinHeight(300);
    textField.setMinWidth(1285);
    textField.setEditable(false);
    textField.setMouseTransparent(true);
    textField.setFocusTraversable(false);
    coverAnchorPane.getChildren().add(textField);
  }
}
