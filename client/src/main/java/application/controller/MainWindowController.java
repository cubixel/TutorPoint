package application.controller;

import application.controller.services.MainConnection;
import application.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.skin.ScrollBarSkin;

import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController extends BaseController implements Initializable {

    public MainWindowController(ViewFactory viewFactory, String fxmlName, MainConnection mainConnection) {
        super(viewFactory, fxmlName, mainConnection);
    }

    @FXML
    private Label profileNameField;

    @FXML
    private ScrollBar scrollBar;

    @FXML
    void profileButtonAction() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
