package application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.mockito.Mock;

import application.controller.services.MainConnection;
import application.controller.services.RegisterService;
import application.view.ViewFactory;
import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterWindowControllerTest {

    /* Creating the Mock Objects necessary for the test. */
    @Mock
    protected MainConnection mainConnectionMock;

    @Mock
    protected ViewFactory viewFactoryMock;

    @Mock
    protected RegisterService registerServiceMock;

    /* Creating local JavaFX Objects for testing. */
    protected TextField usernameField;

    protected PasswordField passwordField;

    protected Label errorLabel;

    protected CheckBox isTutorCheckBox;

    protected RegisterWindowController registerWindowController;

    /* This is testing pressing the Login Button before entering a
     * String into the username and password fields. */
    public void testFieldsValidation(){
        registerWindowController.registerButtonAction();
        assertEquals(errorLabel.getText(), "Please Enter Username");
        usernameField.setText("someUsername");
        registerWindowController.registerButtonAction();
        assertEquals(errorLabel.getText(), "Please Enter Password");
        System.out.println("Tested Register Fields Action");
    }

    /* This is testing that the loginService is started correctly once
     * Strings are in both fields and the user presses the Login Button. */
    public void testRegisterAction(){
        Platform.runLater(() -> {
            usernameField.setText("someUsername");
            passwordField.setText("password");
            registerWindowController.registerButtonAction();
            verify(registerServiceMock).setAccount(any());
            verify(registerServiceMock).start();
            System.out.println("Tested Register Action");
        });
    }
}