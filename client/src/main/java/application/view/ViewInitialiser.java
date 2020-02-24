package application.view;

import application.controller.BaseController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ViewInitialiser {

    public ViewInitialiser(){}
    /**
     * Public method used to initialise stages from
     * a supplied controller. This
     *
     * @param baseController A BaseController to initialise.
     */
    public Stage initialiseStage(BaseController baseController, String themeCSS){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(baseController.getFXMLName()));
        fxmlLoader.setController(baseController);
        Parent parent;
        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Scene scene = new Scene(parent);
        applyCurrentStylesToScene(scene, themeCSS);
        Stage stage = new Stage();
        //stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
        return stage;
    }

    public void applyCurrentStylesToScene(Scene scene, String themeCSS){
        scene.getStylesheets().clear();
        scene.getStylesheets().add(String.valueOf(getClass().getResource(themeCSS)));
    }
}
