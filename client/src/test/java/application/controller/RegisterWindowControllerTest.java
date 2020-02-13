package application.controller;

import application.controller.services.MainConnection;
import application.controller.services.RegisterService;
import application.view.ViewFactory;
import javafx.application.Platform;
import javafx.scene.control.CheckBox;
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

public class RegisterWindowControllerTest {

    /* Creating the Mock Objects necessary for the test. */
    @Mock
    private MainConnection mainConnectionMock;

    @Mock
    private ViewFactory viewFactoryMock;

    @Mock
    private RegisterService registerServiceMock;

    /* Creating local JavaFX Objects for testing. */
    private TextField usernameField;

    private PasswordField passwordField;

    private Label errorLabel;

    private CheckBox isTutorCheckBox;

    private RegisterWindowController registerWindowController;


    @BeforeAll
    public static void setUpToolkit(){
        /* This method starts the JavaFX runtime. The specified Runnable will then be
         * called on the JavaFX Application Thread. */
        Platform.startup(() -> { });

    }

    @BeforeEach
    public void setUp(){
        /* Initializes objects annotated with Mockito annotations, e.g. @Mock. */
        initMocks(this);
        usernameField = new TextField();
        passwordField = new PasswordField();
        errorLabel = new Label();
        isTutorCheckBox = new CheckBox();

        registerWindowController = new RegisterWindowController(viewFactoryMock,null, mainConnectionMock,
                usernameField, passwordField, errorLabel, isTutorCheckBox, registerServiceMock);
    }

    /* This is testing pressing the Login Button before entering a
     * String into the username and password fields. */
    @Test
    public void testFieldsValidation(){
        registerWindowController.registerButtonAction();
        assertEquals(errorLabel.getText(), "Please Enter Username");
        usernameField.setText("someUsername");
        registerWindowController.registerButtonAction();
        assertEquals(errorLabel.getText(), "Please Enter Password");
    }

    /* This is testing that the loginService is started correctly once
     * Strings are in both fields and the user presses the Login Button. */
    @Test
    public void testRegisterAction(){
        Platform.runLater(() -> {
            usernameField.setText("someUsername");
            passwordField.setText("password");
            registerWindowController.registerButtonAction();
            verify(registerServiceMock).setAccount(any());
            verify(registerServiceMock).start();
        });
    }
}