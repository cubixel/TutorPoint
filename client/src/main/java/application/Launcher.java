package application;

import application.controller.services.MainConnection;
import application.view.ViewFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the main entry for the Client side. It extends the
 * JavaFX Application class and the start() method is the
 * main entry point into the JavaFX Application.
 *
 * @author James Gardner
 * @see    Logger
 * @see    MainConnection
 * @see    ViewFactory
 */
public class Launcher extends Application {

  /* Logger prints to both the console and to a file 'logFile.log' saved
   * under resources/logs. All classes should create a Logger of their name. */
  private static final Logger log = LoggerFactory.getLogger("Launcher");

  @Override
  public void start(Stage stage) {

    /* connectionAddress = null defaults to LocalHost. Change this to the IP Address
     * of your remote machine if running the Server Module remotely.
     *
     * You will need to open Ports 5000 to 5001 on your remote network.
     *
     * For Example:
     *
     * String connectionAddress = "172.16.254.1";
     * String connectionAddress = "some.url.that.resolves.as.your.remote.ip";
     */
    String connectionAddress = null;

    /* Creates the connection between the Client and the Server. The program must
     * have this connection in order to proceed. */
    
    try {
      stage.setTitle("TutorPoint");
      stage.getIcons().add(new Image(
          new FileInputStream("client" + File.separator + "src" + File.separator + "main" 
          + File.separator + "resources" + File.separator + "application" + File.separator 
          + "model" + File.separator + "TutorPoint_Pin.png")));

      MainConnection mainConnection = new MainConnection(connectionAddress, 5000);
      mainConnection.start();
      log.info("Successfully connected to the Server");

      /* Generates a ViewFactory and uses the showLoginWindow() method to display the
       * login window to the user. This can be changed for any window you wish to display
       * at the start of the program. For example to skip the login stage and test the stream
       * window use showStreamWindow(stage). However issues may occur due to lack of any Account. */
      // TODO Start with the root window and sign the user in if they have chosen to be remembered.
      ViewFactory viewFactory = new ViewFactory(mainConnection);
      viewFactory.showLoginWindow(stage);

      stage.setOnCloseRequest(e -> {
        if (mainConnection.getMainWindow() != null) {
          mainConnection.getMainWindow().logout();
        }
        Platform.exit();
        System.exit(0);
      });

    } catch (IOException e) {
      // TODO Don't just close, use the root screen to show the option to try again to connect.
      log.error("Client could not connect to the Server", e);
      Platform.exit();
      System.exit(-1);
    }
  }

  public static void main(String[] args) {
    /* This method launches the JavaFX Runtime and the JavaFX Application. */
    launch(args);
  }
}