/*
 * Launcher.java
 * Version: 1.0.0
 * Company: CUBIXEL
 *
 * */

package application;

import application.controller.services.MainConnection;
import application.view.ViewFactory;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  /* Logger used by Client. Prints to both the console and to a file 'logFile.log' saved
   * under resources/logs. */
  private static final Logger log = LoggerFactory.getLogger(Launcher.class);

  @Override
  public void start(Stage stage) throws Exception {

    /* Creates the connection between the Client and the Server. The program must
     * have this connection in order to proceed. */
    try {
      MainConnection mainConnection = new MainConnection(null, 5000, log);
      log.info("Successfully connected to the Server");

      /* Generates a ViewFactory and uses the showLoginWindow() method to display the
       * login window to the user. This can be changed for any window you wish to display
       * at the start of the program. For example to skip the login stage and test your own
       * window. */
      ViewFactory viewFactory = new ViewFactory(mainConnection, log);
      viewFactory.showLoginWindow(stage);
    } catch (IOException e) {
      log.error("Could not connect to the Server");
      Platform.exit();
      System.exit(-1);
    }

  }

  public static void main(String[] args) {
    /* This method launches the JavaFX runtime and the JavaFX application */
    launch(args);
  }
}
