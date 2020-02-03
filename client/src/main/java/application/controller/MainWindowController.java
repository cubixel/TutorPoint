package application.controller;

import application.controller.services.MainConnection;
import application.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebView;

public class MainWindowController extends BaseController {


    @FXML
    private TreeView<?> mainTreeView;

    @FXML
    private WebView mainWebView;


    public MainWindowController(ViewFactory viewFactory, String fxmlName, MainConnection mainConnection) {
        super(viewFactory, fxmlName, mainConnection);
    }

    @FXML
    void optionsAction() {
        viewFactory.showOptionsWindow();
    }

}
