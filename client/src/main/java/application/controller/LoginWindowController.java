package application.controller;

import application.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginWindowController extends BaseController {

    @FXML
    private TextField emailAddressField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    public LoginWindowController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }

    @FXML
    void loginButtonAction() {
        System.out.println("loginButtonAction");
        viewFactory.showMainWindow();

        // Obtaining the Stage that the Login Window Controller is controlling
        // this is done using a the label as a source of information
        Stage stage = (Stage) errorLabel.getScene().getWindow();
        viewFactory.closeStage(stage);
    }

}