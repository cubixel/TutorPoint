package application.controller;

import application.controller.services.ImageHandler;
import application.controller.services.MainConnection;
import application.view.ViewFactory;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class PresentationWindowController extends BaseController implements Initializable {

    @FXML
    private Canvas canvas;

    @FXML
    private AnchorPane pane;

    private GraphicsContext gc ;

    public PresentationWindowController(ViewFactory viewFactory, String fxmlName, MainConnection mainConnection) {
        super(viewFactory, fxmlName, mainConnection);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLUE);

        ImageHandler handler = new ImageHandler(pane);
        System.out.println(handler.displayImage("https://www.w3.org/People/mimasa/test/imgformat/img/w3c_home.jpg", 0, 0, 72, 48));


        // Test Canvas
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                gc.fillOval(e.getX(), e.getY(), 20, 20);
            }
        });
    }
}
