package application.controller;

import application.controller.services.ImageHandler;
import application.controller.services.MainConnection;
import application.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class PresentationWindowController extends BaseController implements Initializable {

    @FXML
    private Canvas canvas;

    private GraphicsContext gc ;

    public PresentationWindowController(ViewFactory viewFactory, String fxmlName, MainConnection mainConnection) {
        super(viewFactory, fxmlName, mainConnection);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLUE);

        Canvas canvas2 = new Canvas();
        ImageHandler handler = new ImageHandler(canvas2);
        handler.displayImage("https://www.w3.org/People/mimasa/test/imgformat/img/w3c_home.jpg", 0, 0, 72, 48);
    }
}
