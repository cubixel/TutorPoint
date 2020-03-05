package application.controller;

import application.controller.services.AccountLoginResult;
import application.controller.services.LoginService;
import application.controller.services.MainConnection;
import application.controller.tools.Security;
import application.model.Account;
import application.view.ViewFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class LoginWindowController extends BaseController implements Initializable {

  @FXML
  private TextField usernameField;

  @FXML
  private PasswordField passwordField;

  @FXML
  private Button loginButton;

  @FXML
  private Button signUpButton;

  @FXML
  private ImageView imageViewLogo;

  @FXML
  private ImageView imageViewIconOne;

  @FXML
  private ImageView imageViewIconTwo;

  @FXML
  private ImageView imageViewIconThree;

  @FXML
  private ImageView imageViewIconFour;

  @FXML
  private Label errorLabel;

  @FXML
  private CheckBox rememberMeCheckBox;

  private LoginService loginService;


  /**
   *  CONSTRUCTOR DESCRIPTION.
   */
  public LoginWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection) {
    super(viewFactory, fxmlName, mainConnection);
    this.loginService = new LoginService(null, mainConnection);
  }

  /**
   *  CONSTRUCTOR DESCRIPTION.
   */
  public LoginWindowController(ViewFactory viewFactory, String fxmlName,
        MainConnection mainConnection, TextField usernameField, PasswordField passwordField,
        Label errorLabel, LoginService loginService) {
    super(viewFactory, fxmlName, mainConnection);
    this.usernameField = usernameField;
    this.passwordField = passwordField;
    this.errorLabel = errorLabel;
    this.loginService = loginService;
  }

  @FXML
  void loginButtonAction() {
    /*
    * Triggered on user clicking login, checks users details are
    * valid before hashing the provided password and sending
    * the users account details to the server for validation.
    */
    if (fieldsAreValid()) {
      Account account = new Account(usernameField.getText(),
          Security.hashPassword(passwordField.getText()));
      loginService.setAccount(account);
      loginService.start();
      loginService.setOnSucceeded(event -> {
        AccountLoginResult result = loginService.getValue();

        switch (result) {
          case SUCCESS:
            System.out.println("Success!");
            if (rememberMeCheckBox.isSelected()) {
              try {
                // TODO Ultra basic just to get function working. Implement something like this
                // https://stackoverflow.com/questions/1354999/keep-me-logged-in-the-best-approach
                FileWriter writer =
                    new FileWriter("client/src/main/resources/application/model/userLoggedIn.txt");
                writer.write(account.getUsername());
                writer.close();
              } catch (IOException e) {
                e.printStackTrace();
              }
            }
            viewFactory.showMainWindow();

            Stage stage = (Stage) errorLabel.getScene().getWindow();
            viewFactory.closeStage(stage);
            break;
          case FAILED_BY_CREDENTIALS:
            errorLabel.setText("Wrong Username or Password");
            break;
          case FAILED_BY_UNEXPECTED_ERROR:
            errorLabel.setText("Unexpected Error");
            break;
          case FAILED_BY_NETWORK:
            errorLabel.setText("Network Error");
            break;
          default:
            errorLabel.setText("Unknown Error");
        }
      });
    }
  }

  @FXML
  void signUpButtonAction() {
    viewFactory.showRegisterWindow();
    Stage stage = (Stage) errorLabel.getScene().getWindow();
    viewFactory.closeStage(stage);
  }

  private boolean fieldsAreValid() {
    if (usernameField.getText().isEmpty()) {
      errorLabel.setText("Please Enter Username");
      return false;
    }
    if (passwordField.getText().isEmpty()) {
      errorLabel.setText("Please Enter Password");
      return false;
    }
    return true;
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    signUpButton.getStyleClass().add("blue-button");
    loginButton.getStyleClass().add("grey-button");
    //Creating an image
    Image logo = null;
    Image boardIcon = null;
    Image webcamIcon = null;
    Image chatboxIcon = null;
    Image pencilIcon = null;
    try {
      logo = new Image(new FileInputStream(
            "client/src/main/resources/application/media/icons/tutorpoint_logo_with_text.png"));
      boardIcon = new Image(new FileInputStream(
            "client/src/main/resources/application/media/icons/board.png"));
      webcamIcon = new Image(new FileInputStream(
            "client/src/main/resources/application/media/icons/webcam.png"));
      chatboxIcon = new Image(new FileInputStream(
            "client/src/main/resources/application/media/icons/chatbox.png"));
      pencilIcon = new Image(new FileInputStream(
            "client/src/main/resources/application/media/icons/pencil.png"));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    //Setting the image view
    imageViewLogo.setImage(logo);
    imageViewIconOne.setImage(boardIcon);
    imageViewIconTwo.setImage(webcamIcon);
    imageViewIconThree.setImage(chatboxIcon);
    imageViewIconFour.setImage(pencilIcon);
  }
}
