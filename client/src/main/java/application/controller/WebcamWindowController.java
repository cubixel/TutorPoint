package application.controller;

import java.net.URL;
import java.util.ResourceBundle;

import application.controller.services.MainConnection;
import application.view.ViewFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

//import com.github.sarxos.webcam.Webcam;

/**
 *
 * https://github.com/sarxos/webcam-capture/tree/master/webcam-capture-examples/webcam-capture-javafx-fxml
 *
 * */
public class WebcamWindowController extends BaseController implements Initializable {

    @FXML
    private FlowPane fpBottomPane;

    @FXML
    private Button btnStartCamera;

    @FXML
    private Button btnStopCamera;

    @FXML
    private Button btnDisposeCamera;

    @FXML
    private BorderPane bpWebCamPaneHolder;

    @FXML
    private ImageView imgWebCamCapturedImage;

    @FXML
    private ComboBox<?> cbCameraOptions;


    public WebcamWindowController(ViewFactory viewFactory, String fxmlName, MainConnection mainConnection) {
        super(viewFactory, fxmlName, mainConnection);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


    @FXML
    void disposeCamera(ActionEvent event) {

    }

    @FXML
    void startCamera(ActionEvent event) {

    }

    @FXML
    void stopCamera(ActionEvent event) {

    }
}