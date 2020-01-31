package application;

import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.FlowView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.testfx.api.FxAssert.verifyThat;

import org.testfx.framework.junit.ApplicationTest;
import org.testfx.api.FxAssert;
import org.testfx.matcher.base.NodeMatchers;

public class MainClientTest extends ApplicationTest {
    public MainController controller;

    @Override
    public void start(Stage stage) throws FlowException {
        Flow flow = new Flow(MainController.class);

        FlowHandler handler = flow.createHandler();
        StackPane sceneRoot = handler.start();

        FlowView view = handler.getCurrentView();
        controller = (MainController) view.getViewContext().getController();

        stage.setScene(new Scene(sceneRoot));
        stage.show();
    }

    @Test
    public void set_Label(){
        interact(() -> {
            controller.setLabel("Hello");
        });

        assertEquals(controller.getLabel(), "Hello");
        //verifyThat("#lblStatus", NodeMatchers.hasText("Hello"));

    }
}
