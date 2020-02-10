package application.controller;

import java.net.URL;
import java.util.ResourceBundle;

import application.controller.services.ImageHandler;
import application.controller.services.MainConnection;
import application.view.ViewFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

public class PresentationWindowController extends BaseController implements Initializable {

    @FXML
    private StackPane pane;

    @FXML
    private Canvas canvas;

    @FXML
    private Button draw1;

    @FXML
    private Button remove1;

    @FXML
    private Button draw2;

    @FXML
    private Button remove2;


    private ImageHandler imageHandler = null;

    public PresentationWindowController(ViewFactory viewFactory, String fxmlName, MainConnection mainConnection) {
        super(viewFactory, fxmlName, mainConnection);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.imageHandler = new ImageHandler(pane);
    }

    @FXML
    void drawImage1(ActionEvent event) {
        imageHandler.drawImage("https://homepages.cae.wisc.edu/~ece533/images/cat.png", 1, 200, 20, 200, 400);
    }

    @FXML
    void removeImage1(ActionEvent event) {
        imageHandler.remove(1);
    }

    @FXML
    void drawImage2(ActionEvent event) {
        imageHandler.drawImage("https://homepages.cae.wisc.edu/~ece533/images/tulips.png", 2, 20, 200, 300, 100);
    }

    @FXML
    void removeImage2(ActionEvent event) {
        imageHandler.remove(2);
    }
}
