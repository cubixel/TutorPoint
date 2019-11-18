package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/* JavaFX used to be part of the Java SDK. But since Java 11 this is no longer the case.
* This has an issue that means that the JavaFX api no longer has the access rights to
* certain elements of the the JavaSDK. This causes an issue when 'Main extends Application'
* as it does below. This is fixed by including a module-info.java file in the java package
* that gives it access rights. This has already been done for you. */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage){
        try {
            /* This is loading the Main.fxml file that can be edited using SceneBuilder.
            * This file must be located in the resources package, which has a package
            * layout that mirrors the java package. This has already been done for you. */
            Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));

            /* A scene has a layout and contains the buttons and labels etc. */
            Scene scene = new Scene(root);

            /* The style of buttons and things can be controlled using a css style sheet. This
            * must also be located in the resources package. */
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

            primaryStage.setTitle("Tutor-Point-JavaFX-Learning-Branch");

            /* The stage contains the scene. It goes Stage->Scene->Layout->Buttons-and-Stuff.*/
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
