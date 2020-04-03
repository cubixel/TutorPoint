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
 * This is the main entry for the Client side. It extends the
 * JavaFX Application class and the start() method is the
 * main entry point into the JavaFX application.
 *
 * @author James Gardner
 * @see    Logger
 * @see    MainConnection
 * @see    ViewFactory
 */
public class Launcher extends Application {

  /* Logger used by Client. Prints to both the console and to a file 'logFile.log' saved
   * under resources/logs. All classes in Client should create a Logger of the same name. */
  private static final Logger log = LoggerFactory.getLogger("Client Logger");

  @Override
  public void start(Stage stage) throws Exception {

    /* Creates the connection between the Client and the Server. The program must
     * have this connection in order to proceed. */
    try {
      MainConnection mainConnection = new MainConnection(null, 5000);
      log.info("Launcher: Successfully connected to the Server");

      /* Generates a ViewFactory and uses the showLoginWindow() method to display the
       * login window to the user. This can be changed for any window you wish to display
       * at the start of the program. For example to skip the login stage and test your own
       * window. */
      ViewFactory viewFactory = new ViewFactory(mainConnection);
      //viewFactory.showLoginWindow(stage);
      viewFactory.showStreamWindow(stage);

    } catch (IOException e) {
      log.error("Launcher: Client could not connect to the Server", e);
      Platform.exit();
      System.exit(-1);
    }
  }

  public static void main(String[] args) {
    /* This method launches the JavaFX Runtime and the JavaFX Application. */
    launch(args);
  }
}
