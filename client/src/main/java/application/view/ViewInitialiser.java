package application.view;

import application.controller.BaseController;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ViewInitialiser {
  public ViewInitialiser() {
  }

  /** Public method used to initialise stages from
   * a supplied controller. This
   *
   * @param baseController A BaseController to initialise.
   */
  public void initialiseStage(BaseController baseController, Stage stage) {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(baseController.getFxmlName()));
    fxmlLoader.setController(baseController);
    Parent parent;
    try {
      parent = fxmlLoader.load();
      Scene scene = new Scene(parent);
      applyCurrentStylesToScene(scene);
      //stage.initStyle(StageStyle.UNDECORATED);
      stage.setScene(scene);
      stage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public void applyCurrentStylesToScene(Scene scene) {
    scene.getStylesheets().clear();
    scene.getStylesheets().add(String.valueOf(getClass().getResource("css/defaultTheme.css")));
  }
}
