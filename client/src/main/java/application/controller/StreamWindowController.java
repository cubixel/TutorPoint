package application.controller;

import application.controller.services.MainConnection;
import application.view.ViewFactory;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;

public class StreamWindowController extends BaseController implements Initializable {
  
  @FXML
  private TabPane primaryTabPane;

  /**
   * CONSTRUCTOR DESCRIPTION.
   *
   * @param viewFactory
   * @param fxmlName
   * @param mainConnection
   */
  public StreamWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection) {
    super(viewFactory, fxmlName, mainConnection);
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

  }
}
