package application.controller;

import application.controller.services.MainConnection;
import application.model.Account;
import application.model.managers.SubjectManager;
import application.view.ViewFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
  private Account account;
  private int subject;
  private AnchorPane parentAnchorPane;
  private MainWindowController parentController;

  private static final Logger log = LoggerFactory.getLogger("SubjectWindowController");

  /**
   * Constructor that all controllers must use.
   *
   * @param viewFactory    The ViewFactory creates windows that are controlled by the controller.
   * @param fxmlName       The FXML file that describes a window the controller contains the logic
   *                       for.
   * @param mainConnection
   */
  public SubjectWindowContoller(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, MainWindowController parentController, int subject,
      AnchorPane parentAnchorPane) {
    super(viewFactory, fxmlName, mainConnection);
    this.subjectManager = parentController.getSubjectManager();
    this.account = null;
    this.subject = subject;
    this.parentAnchorPane = parentAnchorPane;
    this.parentController = parentController;
  }

  @FXML
  void backToDiscoverButtonAction() {
    try {
      parentAnchorPane.getChildren().clear();
      viewFactory.embedDiscoverWindow(parentAnchorPane, parentController);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  void backToHomeButtonAction() {
    parentController.getPrimaryTabPane().getSelectionModel().select(0);
  }


  @FXML
  void followSubjectButton() {

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
