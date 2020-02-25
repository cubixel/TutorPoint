package application.controller;

import application.controller.services.AccountRegisterResult;
import application.controller.services.MainConnection;
import application.controller.services.RegisterService;
import application.controller.services.Security;
import application.model.account.Account;
import application.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class RegisterWindowController extends BaseController implements Initializable {
    public RegisterWindowController(ViewFactory viewFactory, String fxmlName, MainConnection mainConnection) {
        super(viewFactory, fxmlName, mainConnection);
        this.registerService = new RegisterService(null, mainConnection);
    }

    public RegisterWindowController(ViewFactory viewFactory, String fxmlName, MainConnection mainConnection,
                                 TextField usernameField, PasswordField passwordField, Label errorLabel,
                                    CheckBox isTutorCheckBox, RegisterService registerService) {
        super(viewFactory, fxmlName, mainConnection);
        this.usernameField = usernameField;
        this.passwordField = passwordField;
        this.errorLabel = errorLabel;
        this.isTutorCheckBox = isTutorCheckBox;
        this.registerService = registerService;
    }

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private CheckBox isTutorCheckBox;

    @FXML
    private Button signUpButton;

    @FXML
    private ImageView imageView;

    @FXML
    private TextField emailField;

    @FXML
    private TextField emailConfirmField;

    @FXML
    private PasswordField passwordConfirmField;

    private RegisterService registerService;


    @FXML
    void signUpButtonAction() {
    /*On register click, validates data is entered in the fields. Hashes the password and sends details to the server
    * to attempt to create the users account.*/
        if (fieldsAreValid()){
            Account account = new Account(usernameField.getText(), emailField.getText(), Security.hashPassword(passwordField.getText()),
                                          isTutorCheckBox.isSelected()?1:0, 1);
            registerService.setAccount(account);
            registerService.start();
            registerService.setOnSucceeded(event ->{
                AccountRegisterResult result = registerService.getValue();

                switch (result){
                    case SUCCESS:
                        System.out.println("Registered!");
                        viewFactory.showLoginWindow();

                        Stage stage = (Stage) errorLabel.getScene().getWindow();
                        viewFactory.closeStage(stage);
                        break;
                    case FAILED_BY_CREDENTIALS:
                        errorLabel.setText("Wong username or Password");
                        break;
                    case FAILED_BY_UNEXPECTED_ERROR:
                        errorLabel.setText("Unexpected Error");
                        break;
                    case FAILED_BY_NETWORK:
                        errorLabel.setText("Network Error");
                        break;
                }
            });
        }
    }

    private boolean fieldsAreValid() {
        if(usernameField.getText().isEmpty()){
            errorLabel.setText("Please Enter Username");
            return false;
        }

        if(usernameField.getText().length() > 20){
            errorLabel.setText("Username Too Long");
        }

        if(emailField.getText().isEmpty()){
            errorLabel.setText("Please Enter Email");
            return false;
        }

        if(!(Objects.equals(emailField.getText(), emailConfirmField.getText()))){
            errorLabel.setText("Emails Don't Match");
            return false;
        }

        if(passwordField.getText().isEmpty()){
            errorLabel.setText("Please Enter Password");
            return false;
        }

        if(!(Objects.equals(passwordField.getText(), passwordConfirmField.getText()))){
            errorLabel.setText("Passwords Don't Match");
            return false;
        }
        return true;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        signUpButton.getStyleClass().add("blue-button");
        //Creating an image
        Image image = null;
        try {
            image = new Image(new FileInputStream("client/src/main/resources/application/media/images/tutorpoint_logo_with_text.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //Setting the image view
        imageView.setImage(image);
    }
}
