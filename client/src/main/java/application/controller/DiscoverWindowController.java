package application.controller;

import application.controller.services.MainConnection;
import application.view.ViewFactory;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 * The DiscoverWindowController contains the control methods
 * for the FXML DiscoverWindow page. It should allow the user
 * to find new subjects and tutors.
 * NOT YET IMPLEMENTED
 *
 * @author James Gardner
 */
public class DiscoverWindowController extends BaseController implements Initializable {

  /**
   * This is the default constructor. DiscoverWindowController
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
   * @param mainWindowController
   *        Controller for the top level window
   */
  public DiscoverWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, MainWindowController mainWindowController) {
    super(viewFactory, fxmlName, mainConnection);
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    // TODO Not yet implemented
  }
}