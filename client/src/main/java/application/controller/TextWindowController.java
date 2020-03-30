package application.controller;

import static javafx.scene.input.KeyCode.*;

import application.controller.services.MainConnection;
import application.model.Message;
import application.view.ViewFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * CLASS DESCRIPTION:
 * This class is used to generate an interactive text chat
 * which allows multiple users to send and receive messages.
 *
 * @author Oli Clarke
 *
 */

public class TextWindowController extends BaseController implements Initializable{

  /**
   * Main class constructor.
   */
  public TextWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection) {
    super(viewFactory, fxmlName, mainConnection);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }


  @FXML private TextArea messageBox;
  @FXML private Label usernameLabel;
  @FXML private Label onlineCountLabel;
  @FXML private ListView userList;
  @FXML ListView chatPane;



  public void sendButtonAction() throws IOException {
    String msg = messageBox.getText();
    if (!messageBox.getText().isEmpty()) {
      // Listener.send(msg);  // This needs linking to Stijn GUI button
      messageBox.clear();
    }
  }

  public void setUsernameLabel(String username) {
    this.usernameLabel.setText(username);
  }

  public void setOnlineLabel(String usercount) {
    Platform.runLater(() -> onlineCountLabel.setText(usercount));
  }

  /* Method to display to update user list */
  public void setUserList(Message msg) {


  }

  /* Displays Notification when a user joins */
  public void newUserNotification(Message msg) {

  }

  public void keyboardSendMethod(KeyEvent event) throws IOException {
    if (event.getCode() == ENTER) {
      sendButtonAction();
    }
  }

  @FXML
  public void closeApplication() {
    Platform.exit();
    System.exit(0);
  }

  /* Method to display server messages */
  public synchronized void addAsServer(Message msg) {

    /*
    Task<HBox> task = new Task<HBox>() {

    };


    task.setOnSucceeded(event -> {
      chatPane.getItems().add(task.getValue());
    });

    Thread t = new Thread(task);
    t.setDaemon(true);
    t.start();
     */
  }




}
