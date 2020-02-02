package application.controller;

import application.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebView;

public class MainWindowController extends BaseController {


    @FXML
    private TreeView<?> mainTreeView;

    @FXML
    private WebView mainWebView;


    public MainWindowController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }

    @FXML
    void optionsAction() {
        viewFactory.showOptionsWindow();
    }

}
