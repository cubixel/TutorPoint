package application.controller;

import application.controller.services.MainConnection;
import application.view.ViewFactory;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

/**
 * The RootWindow will be the initial window viewable to the user.
 * It should check to see if the user has saved their login details
 * and attempt to log the user in if they have. If not then it should
 * take the user to the login screen. It should also allow the user
 * the option to attempt to reconnect to the server should any issue
 * occur on startup.
 *
 * @author James Gardner
 */
public class RootWindowController extends BaseController implements Initializable {

  @FXML
  private final Button tryAgainButton;

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
    this.tryAgainButton = new Button();
  }

  @FXML
  void tryAgainButtonAction() {

  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    // TODO Check Connection is Okay
    // TODO Check for saved login details
    // TODO Either log the user in or open the login screen
  }
}