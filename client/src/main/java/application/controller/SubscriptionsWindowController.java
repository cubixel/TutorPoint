package application.controller;

import application.controller.services.MainConnection;
import application.view.ViewFactory;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

public class SubscriptionsWindowController extends BaseController implements Initializable {

  MainWindowController mainWindowController;

  @FXML
  private ScrollBar mainScrollBar;

  @FXML
  private ScrollPane mainScrollPane;

  @FXML
  private AnchorPane mainScrollContent;

  @FXML
  private Label userSubject1Label;

  @FXML
  private HBox userSubject1Carosel;

  @FXML
  private Button userSubject1Left;

  @FXML
  private HBox userSubject1Content;

  @FXML
  private Button userSubject1Right;

  @FXML
  private Label userSubject2Label;

  @FXML
  private HBox userSubject2Carosel;

  @FXML
  private Button userSubject2Left;

  @FXML
  private HBox userSubject2Content;

  @FXML
  private Button userSubject2Right;

  public SubscriptionsWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, MainWindowController mainWindowController) {
    super(viewFactory, fxmlName, mainConnection);
    this.mainWindowController = mainWindowController;
  }

  @FXML
  void caroselLeft() {

  }

  @FXML
  void caroselRight() {

  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

  }
}
