package application;

import application.controller.services.MainConnection;
import application.view.ViewFactory;
import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        MainConnection mainConnection = new MainConnection(null, 5000);
        ViewFactory viewFactory = new ViewFactory(mainConnection);
        viewFactory.showLoginWindow();
    }
}
