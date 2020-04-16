package application.controller;

import application.controller.enums.TextChatRequestResult;
import application.controller.services.MainConnection;
import application.controller.services.TextChatRequestService;
import application.controller.services.TextChatService;
import application.model.Message;
import application.view.ViewFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CLASS DESCRIPTION: This class is used to generate an interactive text chat which allows multiple
 * users to send and receive messages.
 *
 * @author Oli Clarke
 */

public class TextChatWindowController extends BaseController implements Initializable {

  private TextChatService textChatService;
  private TextChatRequestService textChatRequestService;
  private MainConnection connection;
  private String userID;
  private String sessionID;
  private Message message;

  private static final Logger log = LoggerFactory.getLogger("TextChatWindowController");

  @FXML
  private Label usernameLabel;

  @FXML
  private Label onlineCountLabel;

  @FXML
  private ListView userList; // List of usernames online

  @FXML
  private TextField textChatInput;

  @FXML
  private Button textChatSendButton;

  @FXML
  private VBox textChatVBox;


  @FXML
  void pasteText(MouseEvent event) {
    if (!textChatInput.getText().isEmpty()) {
      displayChat("Default", textChatInput.getText());
      /*if (( > textChatVBox.getHeight()-35)) {
        textChatVBox.getChildren().remove(0);
      }*/
      textChatInput.clear();
    }
  }


  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    // this.message = new Message(,);     \\ TODO complete init for client controller.
    // addActionListeners();              \\ TODO implement/connect action listeners for client side.
    log.info("Text Chat Initialised.");
    startService();
  }

  /**
   * Main class constructor.
   */
  public TextChatWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, String userID, String sessionID) {
    super(viewFactory, fxmlName, mainConnection);
    this.textChatRequestService = new TextChatRequestService(mainConnection, userID, sessionID);
    this.connection = mainConnection;
    this.userID = userID;
    this.sessionID = sessionID;
    sendRequest();
  }

  /**
   * .
   */
  public void displayChat(String username, String chatContent) {
    HBox newHBox = new HBox(5.0, new Label(username + ":"));
    Label c = new Label(chatContent);
    newHBox.getChildren().addAll(c);
    HBox.setHgrow(c, Priority.ALWAYS);
    textChatVBox.getChildren().addAll(newHBox);
  }


  private void sendRequest() {
    if (!textChatRequestService.isRunning()) {
      textChatRequestService.reset();
      textChatRequestService.start();
    }

    textChatRequestService.setOnSucceeded(event -> {
      TextChatRequestResult result = textChatRequestService.getValue();
      switch (result) {
        case TEXT_REQUEST_SUCCESS:
          log.info("Text Chat Session Request - Received.");
          this.textChatService = new TextChatService(message, connection, userID, sessionID);
          break;
        case FAILED_BY_SESSION_ID:
          log.warn("Text Chat Session Request - Wrong session ID.");
          break;
        case NETWORK_FAILURE:
          log.warn("Text Chat Session Request - Network error.");
          break;
        default:
          log.warn("Text Chat Session Request - Unknown error.");
      }
    });
  }

  private void startService() {
    this.textChatService = new TextChatService(this.message, this.connection, this.userID,
        this.sessionID);
    this.connection.getListener().setTextChatService(textChatService);
    this.textChatService.start();
  }


  public void keyboardSendMethod(KeyEvent event) throws IOException {
    if (event.getCode() == KeyCode.ENTER) {
      sendButtonAction();
    }
  }

  @FXML
  public void closeApplication() {
    Platform.exit();
    System.exit(0);
  }

  /**
   * Action for 'ENTER' button to send typed message.
   *
   * @throws IOException .
   */
  public void sendButtonAction() throws IOException {
    String msg = textChatInput.getText();
    if (!textChatInput.getText().isEmpty()) {
      // Listener.send(msg);  // This needs linking to Stijn GUI button
      textChatInput.clear();
    }
  }

}