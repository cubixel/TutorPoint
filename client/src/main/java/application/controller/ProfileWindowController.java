package application.controller;

import application.controller.services.MainConnection;
import application.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class ProfileWindowController extends BaseController {

  @FXML
  private AnchorPane anchorPane;

  @FXML
  private Label accountNameLabelProfileTab;

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

  }
}
