/*
 * Launcher.java
 * Version: 1.0.0
 * Company: CUBIXEL
 *
 * */

package application;

import application.controller.services.MainConnection;
import application.view.ViewFactory;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * CLASS DESCRIPTION:
 * This is the main class for the program. It extends the
 * JavaFX Application class and the start() method is the
 * main entry point into the JavaFX application.
 *
 * @author CUBIXEL
 *
 */
public class Launcher extends Application {

  @Override
  public void start(Stage stage) throws Exception {

    /* Creates the connection between the Client and the Server. The program must
     * have this connection in order to proceed. */
    // ########### This could do with some error handling ############################
    MainConnection mainConnection = new MainConnection(null, 5000);

    /* Generates a ViewFactory and uses the showLoginWindow() method to display the
     * login window to the user. This can be changed for any window you wish to display
     * at the start of the program. For example to skip the login stage and test your own
     * window. */
    ViewFactory viewFactory = new ViewFactory(mainConnection);
    viewFactory.showMainWindow();
  }

  public static void main(String[] args) {
    /* This method launches the JavaFX runtime and the JavaFX application */
    launch(args);
  }
}