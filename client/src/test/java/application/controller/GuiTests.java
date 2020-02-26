package application.controller;

import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class GuiTests {

    @BeforeAll
    public static void setUpToolkit(){
        /* This method starts the JavaFX runtime. The specified Runnable will then be
         * called on the JavaFX Application Thread. */
        Platform.startup(() -> System.out.println("Toolkit initialized ..."));
    }

    @Nested
    class Login extends LoginWindowControllerTest{

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

        @Test
        public void doTestFieldsValidation(){testFieldsValidation();}

        @Test
        public void doTestLoginAction(){testLoginAction();}

        @Test
        public void doTestSignUpButtonAction(){testSignUpButtonActionAction();}

    }

    @Nested
    class Register extends RegisterWindowControllerTest{
        
        @BeforeEach
        public void setUp(){
            /* Initializes objects annotated with Mockito annotations, e.g. @Mock. */
            initMocks(this);
            usernameField = new TextField();
            passwordField = new PasswordField();
            passwordConfirmField = new PasswordField();
            emailField = new TextField();
            emailConfirmField = new TextField();
            errorLabel = new Label();
            isTutorCheckBox = new CheckBox();

            registerWindowController = new RegisterWindowController(viewFactoryMock,null, mainConnectionMock,
                    usernameField, emailField, emailConfirmField, passwordField, passwordConfirmField, errorLabel, isTutorCheckBox, registerServiceMock);
        }

        @Test
        public void doTestFieldsValidation(){testFieldsValidation();}

        @Test
        public void doTestRegisterAction(){testRegisterAction();}

        @Test
        public void doTestBackButtonAction(){testBackButtonAction();}
    }
    
}