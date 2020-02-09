/*
 * ViewFactory.java
 * Version: 1.0.0
 * Company: CUBIXEL
 *
 * */

package application.view;

import application.controller.*;
import application.controller.services.MainConnection;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * CLASS DESCRIPTION:
 * This class is used to generate and the views that
 * the user will see. It contains the methods used to generate
 * a 'Controller' and then a 'Stage' for particular .fxml
 * windows.
 *
 * @author James Gardner
 *
 */
public class ViewFactory {

    /* This is the main connection to the server. */
    MainConnection mainConnection;

    /**
     * Constructor for the ViewFactory. Needs access
     * to the main client-server connection in order
     * to distribute this to the controllers.
     *
     * @param mainConnection A connection to a Server.
     */
    public ViewFactory(MainConnection mainConnection) {
        this.mainConnection = mainConnection;
    }

    public void showLoginWindow(){
        /* Each window needs a controller specific to it. This
         * is creating a new LoginWindowController using the
         * Abstract class BaseController. The LoginWindow.fxml
         * is passed in as an argument. */
        BaseController controller = new LoginWindowController(this, "fxml/LoginWindow.fxml", mainConnection);
        initialiseStage(controller);
    }

    public void showMainWindow(){
        /* The MainWindowController takes the MainWindow.fxml
         * as its argument. These fxml files must be placed in
         * the correct folder: resources -> view -> fxml */
        BaseController controller = new MainWindowController(this, "fxml/MainWindow.fxml", mainConnection);
        initialiseStage(controller);
    }

    public void showOptionsWindow(){
        BaseController controller = new OptionsWindowController(this, "fxml/OptionsWindow.fxml", mainConnection);
        initialiseStage(controller);
    }

    public void showRegisterWindow(){
        BaseController controller = new RegisterWindowController(this, "fxml/RegisterWindow.fxml", mainConnection);
        initialiseStage(controller);
    }

    public void showWhiteboardWindow(){
        BaseController controller = new WhiteboardWindowController(this, "fxml/WhiteboardWindow.fxml", mainConnection);
        initialiseStage(controller);
    }

    public void showMediaPlayerWindow(){
        BaseController controller = new MediaPlayerController(this, "fxml/MediaPlayerWindow.fxml", mainConnection);
        initialiseStage(controller);
    }

    /**
     * Private method used to initialise stages from
     * a supplied controller. This
     *
     * @param controller A BaseController to initialise.
     */
    private void initialiseStage(BaseController controller){
        /* Creates an FXMLoader using the .fxml file provided
         * in the controller and sets the controller associated
         * with the .fxml file. */
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(controller.getFXMLName()));
        fxmlLoader.setController(controller);

        /* A Parent is a simple Node that can hold other Nodes as children.
         * Every Scene needs exactly one Parent as the root. */
        Parent parent;
        try {
            parent = fxmlLoader.load();
        } catch (IOException e){
            e.printStackTrace();
            return;
        }

        /* Every Stage can hold exactly one Scene at a time. */
        Scene scene = new Scene(parent);

        /* A stage is a window. */
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();

        /* These two line set the stylesheet. This is applied to all scenes as
         * they are initialised but could be changed for each window. */
        //scene.getStylesheets().clear();
        //scene.getStylesheets().add(getClass().getResource("css/defaultTheme.css").toExternalForm());
    }

    /**
     * Used to close Stages. Closing the window so
     * the user no longer has access to it.
     *
     * @param stageToClose The Stage object to close.
     */
    public void closeStage(Stage stageToClose){
        stageToClose.close();
    }
}
