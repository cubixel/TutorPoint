package application.view;

import application.controller.BaseController;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ViewInitialiser {

  public ViewInitialiser(){}

  /**
   * Public method used to initialise stages from
   * a supplied controller. This
   *
   * @param baseController A BaseController to initialise.
   */
  public Stage initialiseStage(BaseController baseController) {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(baseController.getFxmlName()));
    fxmlLoader.setController(baseController);
    Parent parent;
    try {
      parent = fxmlLoader.load();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    Scene scene = new Scene(parent);
    Stage stage = new Stage();
    stage.setScene(scene);
    stage.show();
    return stage;
  }
}
