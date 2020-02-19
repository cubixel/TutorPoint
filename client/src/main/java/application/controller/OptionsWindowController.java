package application.controller;

import application.controller.services.MainConnection;
import application.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;

public class OptionsWindowController extends BaseController {

    public OptionsWindowController(ViewFactory viewFactory, String fxmlName, MainConnection mainConnection) {
        super(viewFactory, fxmlName, mainConnection);
    }

    @FXML
    private Slider sliderPicker;

    @FXML
    private ChoiceBox<?> itemPicker;

    @FXML
    void applyButtonAction() {

    }
    @FXML
    void cancelButtonAction() {

    }


}
