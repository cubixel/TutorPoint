package application.controller;

import static javafx.scene.input.KeyCode.*;

import application.controller.services.MainConnection;
import application.model.Message;
import application.view.ViewFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;

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
