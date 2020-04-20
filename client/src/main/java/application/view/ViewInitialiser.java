package application.view;

import application.controller.BaseController;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ViewInitialiser is used by the ViewFactory to
 * setup JavaFX Scenes. It takes FXML files containing
 * the Scenes' layout information and connects it to the
 * associated Controller.
 *
 * <p>This can be done to replace the whole Scene within
 * the window or to embed a smaller Scene into an
 * Anchor Pane within the current window. This is
 * useful for separating out control logic.
 *
 * @author James Gardner
 * @see    ViewFactory
 * @see    BaseController
 */
public class ViewInitialiser {

  /* Logger prints to both the console and to a file 'logFile.log' saved
   * under resources/logs. All classes should create a Logger of their name. */
  private static final Logger log = LoggerFactory.getLogger("ViewInitialiser");

  public ViewInitialiser() {
  }

  /**
   * Public method used to initialise a supplied Stage with a new FXML Scene
   * obtained from the supplied controller. This replaces the current Scene
   * within the entire Window.
   *
   * @param baseController
   *        The controller to connect to the FXML Scene and Stage.
   *
   * @param stage
   *        The JavaFX Stage that the Scene will be applied to.
   */
  public void initialiseStage(BaseController baseController, Stage stage) {
    /* Obtaining the FXML Scene from the Controller */
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(baseController.getFxmlName()));
    /* Associating the Controller with the FXML */
    fxmlLoader.setController(baseController);
    Parent parent;
    try {
      parent = fxmlLoader.load();
      Scene scene = new Scene(parent);
      applyCurrentStylesToScene(scene);
      stage.setScene(scene);
      //stage.setFullScreen(true);
      stage.show();
    } catch (IOException e) {
      log.error("Could not Initialise Stage", e);
    }
  }

  /**
   * This is used to embed a smaller Scene or Controller into an supplied Anchor Pane.
   * This is useful for separating out control logic.
   *
   * @param  baseController
   *         The controller to connect to the FXML Scene and Anchor Pane.
   *
   * @param  anchorPane
   *         The Anchor Pane the controller and Scene will be applied to.
   *
   * @throws IOException
   *         Thrown if the FXML file supplied with the Controller can't be found.
   */
  public void initialiseEmbeddedStage(BaseController baseController,
      AnchorPane anchorPane) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(baseController.getFxmlName()));
    fxmlLoader.setController(baseController);
    AnchorPane tempPane = (AnchorPane) fxmlLoader.load();
    anchorPane.getChildren().setAll(tempPane);

    anchorPane.setTopAnchor(tempPane,0.0);
    anchorPane.setBottomAnchor(tempPane,0.0);
    anchorPane.setLeftAnchor(tempPane,0.0);
    anchorPane.setRightAnchor(tempPane,0.0);
  }

  /**
   * Applies a css StyleSheet to a provided Scene. This currently
   * only applies the DefaultStyleSheet.css.
   *
   * @param scene
   *        The JavaFX Scene to apply a css StyleSheet too.
   */
  public void applyCurrentStylesToScene(Scene scene) {
    scene.getStylesheets().clear();
    scene.getStylesheets().add(String.valueOf(getClass().getResource("css/DefaultStyleSheet.css")));
  }
}
