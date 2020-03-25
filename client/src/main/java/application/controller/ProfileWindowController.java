package application.controller;

import application.controller.services.MainConnection;
import application.view.ViewFactory;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ProfileWindowController extends BaseController {

  @FXML
  private AnchorPane anchorPane;

  @FXML
  private Label accountNameLabelProfileTab;

  @FXML
  private AnchorPane anchorPanePassword;

  @FXML
  private Button testButton;

  /**
   * CONSTRUCTOR DESCRIPTION.
   *
   * @param viewFactory
   * @param fxmlName
   * @param mainConnection
   */
  public ProfileWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection) {
    super(viewFactory, fxmlName, mainConnection);
  }


  @FXML
  void updatePasswordAction() {
    testButton.toBack();
    try {
      viewFactory.embedPasswordPopUp(anchorPanePassword);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  void anchorPaneClickedAction() {
    anchorPanePassword.getChildren().clear();
  }
}
