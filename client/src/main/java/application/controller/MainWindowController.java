package application.controller;

import application.controller.services.MainConnection;
import application.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class MainWindowController extends BaseController {

    public MainWindowController(ViewFactory viewFactory, String fxmlName, MainConnection mainConnection) {
        super(viewFactory, fxmlName, mainConnection);
    }

    @FXML
    void optionsAction() {
        viewFactory.showOptionsWindow();
    }

    @FXML
    void exitAction() {
        // TODO Close the program down.
    }

    @FXML
    void mediaPlayerButtonAction() {
        viewFactory.showMediaPlayerWindow();
    }


    @FXML
    void textChatButtonAction() {

    }

    @FXML
    void webcamButtonAction() {
        viewFactory.showWebcamWindow();
    }

    @FXML
    void whiteboardButtonAction() {
        viewFactory.showWhiteboardWindow();
    }

    @FXML
    void xmlPresentationButtonAction() {
        viewFactory.showPresentationWindow();
    }


}
