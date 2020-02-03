package application.controller;

import application.AccountManager;
import application.controller.services.*;
import application.model.account.Account;
import application.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginWindowController extends BaseController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    public LoginWindowController(ViewFactory viewFactory, String fxmlName, MainConnection mainConnection) {
        super(viewFactory, fxmlName, mainConnection);
    }

    @FXML
    void loginButtonAction() {
        if (fieldsAreValid()){
            String hashpw = Security.hashPassword(passwordField.getText());
            Account account = new Account(usernameField.getText(), hashpw);
            AccountManager accountManager = new AccountManager();
            LoginService loginService = new LoginService(account, accountManager, getMainConnection());
            loginService.start();
            loginService.setOnSucceeded(event ->{
                AccountLoginResult result = loginService.getValue();

                switch (result){
                    case SUCCESS:
                        System.out.println("Registered!");
                        viewFactory.showMainWindow();

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

    @FXML
    void registerButtonAction() {

        viewFactory.showRegisterWindow();
        System.out.println("here");
        Stage stage = (Stage) errorLabel.getScene().getWindow();
        viewFactory.closeStage(stage);
    }


    private boolean fieldsAreValid() {
        if(usernameField.getText().isEmpty()){
            errorLabel.setText("Please Fill Username");
            return false;
        }
        if(passwordField.getText().isEmpty()){
            errorLabel.setText("Please Fill Password");
            return false;
        }
        return true;
    }

}
