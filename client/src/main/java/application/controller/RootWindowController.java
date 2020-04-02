package application.controller;

import application.controller.services.MainConnection;
import application.view.ViewFactory;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

public class RootWindowController extends BaseController implements Initializable {

  @FXML
  private Button tryAgainButton;

  /**
   * This is the default constructor. RootWindowController
   * extends the BaseController class.
   *
   * @param viewFactory
   *        The ViewFactory creates windows that are controlled by the controller.
   *
   * @param fxmlName
   *        The FXML file that describes a window the controller contains the logic
   *        for.
   *
   * @param mainConnection
   *        The connection between client and server
   */
  public RootWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection) {
    super(viewFactory, fxmlName, mainConnection);
  }

  @FXML
  void tryAgainButtonAction() {

  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    // Check Connection is Okay
    // Check for saved login details
    // Either log the user in or open the login screen
  }
}
