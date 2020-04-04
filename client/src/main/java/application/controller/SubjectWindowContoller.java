package application.controller;

import application.controller.services.MainConnection;
import application.model.Account;
import application.model.managers.SubjectManager;
import application.view.ViewFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class SubjectWindowContoller extends BaseController implements Initializable {


  @FXML
  private ImageView coverImageView;

  @FXML
  private Button backButton;

  private SubjectManager subjectManager;
  private Account account;
  private int subject;
  private AnchorPane parentAnchorPane;

  /**
   * Constructor that all controllers must use.
   *
   * @param viewFactory    The ViewFactory creates windows that are controlled by the controller.
   * @param fxmlName       The FXML file that describes a window the controller contains the logic
   *                       for.
   * @param mainConnection
   */
  public SubjectWindowContoller(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, Account account, SubjectManager subjectManager, int subject,
      AnchorPane parentAnchorPane) {
    super(viewFactory, fxmlName, mainConnection);
    this.subjectManager = subjectManager;
    this.account = null;
    this.subject = subject;
    this.parentAnchorPane = parentAnchorPane;
  }

  @FXML
  void backButtonAction() {
    try {
      parentAnchorPane.getChildren().clear();
      viewFactory.embedDiscoverWindow(parentAnchorPane, account, subjectManager);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

  }
}
