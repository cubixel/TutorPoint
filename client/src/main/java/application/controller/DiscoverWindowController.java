package application.controller;

import application.controller.services.MainConnection;
import application.model.Account;
import application.model.managers.SubjectManager;
import application.view.ViewFactory;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

public class DiscoverWindowController extends BaseController implements Initializable {

  private SubjectManager subjectManager;
  private Account account;

  /**
   * Constructor that all controllers must use.
   *
   * @param viewFactory    The ViewFactory creates windows that are controlled by the controller.
   * @param fxmlName       The FXML file that describes a window the controller contains the logic
   *                       for.
   * @param mainConnection
   */
  public DiscoverWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, Account account, SubjectManager subjectManager) {
    super(viewFactory, fxmlName, mainConnection);
    this.subjectManager = subjectManager;
    this.account = null;
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

  }
}
