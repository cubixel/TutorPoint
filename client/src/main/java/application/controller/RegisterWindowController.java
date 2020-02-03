package application.controller;

import application.controller.services.AccountRegisterResult;
import application.controller.services.Security;

import application.AccountManager;
import application.controller.services.MainConnection;
import application.controller.services.RegisterService;
import application.model.account.Account;
import application.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterWindowController extends BaseController{
    public RegisterWindowController(ViewFactory viewFactory, String fxmlName, MainConnection mainConnection) {
        super(viewFactory, fxmlName, mainConnection);
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
    void registerButtonAction() {
        if (fieldsAreValid()){
            String hashpw = Security.hashPassword(passwordField.getText());
            Account account = new Account(usernameField.getText(), hashpw, isTutorCheckBox.isSelected()?1:0, 1);
            AccountManager accountManager = new AccountManager();
            RegisterService registerService = new RegisterService(account, accountManager, getMainConnection());
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
