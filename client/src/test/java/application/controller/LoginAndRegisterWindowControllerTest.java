/*
 * LoginAndRegisterWindowControllerTest.java
 * Version: 1.0.0
 * Company: CUBIXEL
 *
 * */

package application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import application.controller.services.LoginService;
import application.controller.services.MainConnection;
import application.controller.services.RegisterService;
import application.view.ViewFactory;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * CLASS DESCRIPTION:
 * This class tests the LoginWindowController. It tests
 * the functionality of pressing the login button with and
 * without text in the username and password fields.
 *
 * @author James Gardner
 *
 */
 public class LoginAndRegisterWindowControllerTest {
     

    /* Creating the Mock Objects necessary for the test. */
    @Mock
    private MainConnection mainConnectionMock;

    @Mock
    private ViewFactory viewFactoryMock;

    @Mock
    private LoginService loginServiceMock;

    @Mock
    private RegisterService registerServiceMock;

    /* Creating local JavaFX Objects for testing. */
    private TextField usernameField;
    private PasswordField passwordField;
    private Label errorLabel;
    private LoginWindowController loginWindowController;

    private TextField registerUsernameField;
    private PasswordField registerPasswordField;
    private Label registerErrorLabel;
    private CheckBox isTutorCheckBox;
    private RegisterWindowController registerWindowController;


    @BeforeAll
    public static void setUpToolkit(){
        /* This method starts the JavaFX runtime. The specified Runnable will then be
         * called on the JavaFX Application Thread. */
        Platform.setImplicitExit(false);
        Platform.startup(() -> { });
    }

    @AfterAll
    public static void releaseToolkit(){
        Platform.exit();
    }

    @BeforeEach
    public void setUp(){
        /* Initializes objects annotated with Mockito annotations, e.g. @Mock. */
        initMocks(this);
        usernameField = new TextField();
        passwordField = new PasswordField();
        errorLabel = new Label();

        loginWindowController = new LoginWindowController(viewFactoryMock, null, mainConnectionMock,
                                                          usernameField, passwordField, errorLabel, loginServiceMock);
        
        registerUsernameField = new TextField();
        registerPasswordField = new PasswordField();
        registerErrorLabel = new Label();
        isTutorCheckBox = new CheckBox();

        registerWindowController = new RegisterWindowController(viewFactoryMock, null, mainConnectionMock,
                registerUsernameField, registerPasswordField, registerErrorLabel, isTutorCheckBox, registerServiceMock);
    }

    /* This is testing pressing the Login Button before entering a
     * String into the username and password fields. */
    @Test
    public void testFieldsValidation(){
        loginWindowController.loginButtonAction();
        assertEquals(errorLabel.getText(), "Please Enter Username");
        usernameField.setText("someUsername");
        loginWindowController.loginButtonAction();
        assertEquals(errorLabel.getText(), "Please Enter Password");
    }

    /* This is testing pressing the Login Button before entering a
     * String into the username and password fields. */
    @Test
    public void testRegisterFieldsValidation(){
        registerWindowController.registerButtonAction();
        assertEquals(registerErrorLabel.getText(), "Please Enter Username");
        registerUsernameField.setText("someUsername");
        registerWindowController.registerButtonAction();
        assertEquals(registerErrorLabel.getText(), "Please Enter Password");
    }

    /* This is testing that the loginService is started correctly once
     * Strings are in both fields and the user presses the Login Button. */
    @Test
    public void testLoginAction(){
        Platform.runLater(() ->{
            usernameField.setText("someUsername");
            passwordField.setText("password");
            loginWindowController.loginButtonAction();
            verify(loginServiceMock).setAccount(any());
            verify(loginServiceMock).start();
        });
    }

    /* This is testing that the loginService is started correctly once
     * Strings are in both fields and the user presses the Login Button. */
    @Test
    public void testRegisterAction(){
        Platform.runLater(() -> {
            registerUsernameField.setText("someUsername");
            registerPasswordField.setText("password");
            registerWindowController.registerButtonAction();
            verify(registerServiceMock).setAccount(any());
            verify(registerServiceMock).start();
        });
    }
}
