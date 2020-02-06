/*
 * LoginWindowControllerTest.java
 * Version: 1.0.0
 * Company: CUBIXEL
 *
 * */

package application.controller;

import application.controller.services.LoginService;
import application.controller.services.MainConnection;
import application.view.ViewFactory;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.mockito.Mock;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * CLASS DESCRIPTION:
 * This class tests the LoginWindowController. It tests
 * the functionality of pressing the login button with and
 * without text in the username and password fields.
 *
 * @author James Gardner
 *
 */
public class LoginWindowControllerTest {

    /* Creating the Mock Objects necessary for the test. */
    @Mock
    private MainConnection mainConnectionMock;

    @Mock
    private ViewFactory viewFactoryMock;

    @Mock
    private LoginService loginServiceMock;

    /* Creating local JavaFX Objects for testing. */
    private TextField usernameField;

    private PasswordField passwordField;

    private Label errorLabel;

    private LoginWindowController loginWindowController;


    @BeforeAll
    public static void setUpToolkit(){
        /* This method starts the JavaFX runtime. The specified Runnable will then be
         * called on the JavaFX Application Thread. */
        Platform.startup(() -> System.out.println("Toolkit initialized ..."));
    }

    @BeforeEach
    public void setUp(){
        /* Initializes objects annotated with Mockito annotations, e.g. @Mock. */
        initMocks(this);
        usernameField = new TextField();
        passwordField = new PasswordField();
        errorLabel = new Label();

        loginWindowController = new LoginWindowController(viewFactoryMock,null, mainConnectionMock,
                                                          usernameField, passwordField, errorLabel, loginServiceMock);
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
}
