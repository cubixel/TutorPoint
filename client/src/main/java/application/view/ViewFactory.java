package application.view;

import application.controller.BaseController;
import application.controller.LoginWindowController;
import application.controller.MainWindowController;
import application.controller.OptionsWindowController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewFactory {

    public ViewFactory() {
    }

    public void showLoginWindow(){

        BaseController controller = new LoginWindowController(this, "LoginWindow.fxml");
        initialiseStage(controller);
    }

    public void showMainWindow(){
        BaseController controller = new MainWindowController(this, "MainWindow.fxml");
        initialiseStage(controller);
    }

    public void showOptionsWindow(){
        BaseController controller = new OptionsWindowController(this, "OptionsWindow.fxml");
        initialiseStage(controller);
    }

    private void initialiseStage(BaseController baseController){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(baseController.getFXMLName()));
        fxmlLoader.setController(baseController);
        Parent parent;
        try {
            parent = fxmlLoader.load();
        } catch (IOException e){
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

    public void closeStage(Stage stageToClose){
        stageToClose.close();
    }
}
