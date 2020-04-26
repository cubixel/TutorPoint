package application.controller;

import application.controller.enums.TextChatRequestResult;
import application.controller.services.MainConnection;
import application.controller.services.TextChatRequestService;
import application.controller.services.TextChatService;
import application.model.Message;
import application.model.managers.MessageManager;
import application.view.ViewFactory;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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
  private MessageManager messageManager;

  private static final Logger log = LoggerFactory.getLogger("TextChatWindowController");

  @FXML
  private TextField textChatInput;

  @FXML
  private Button textChatSendButton;

  @FXML
  private VBox textChatVBox;

  @FXML
  void pasteText(MouseEvent event) {
    sendMsgText();
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    this.message = new Message(userID, sessionID, "init message");
    this.messageManager = new MessageManager(textChatVBox);
    startService();
    this.textChatRequestService = new TextChatRequestService(connection, userID, sessionID);
    sendRequest();
    addActionListeners();
    log.info("Text Chat Initialised.");
  }

  /**
   * Main class constructor.
   */
  public TextChatWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, String userID, String sessionID) {
    super(viewFactory, fxmlName, mainConnection);
    this.connection = mainConnection;
    this.userID = userID;
    this.sessionID = sessionID;
  }

  private void sendRequest() {
    if (!textChatRequestService.isRunning()) {
      textChatRequestService.reset();
      textChatRequestService.start();
    }

    textChatRequestService.setOnSucceeded(event -> {
      TextChatRequestResult result = textChatRequestService.getValue();
      switch (result) {
        case SESSION_REQUEST_TRUE:
          log.info("Text Chat Session Request - True.");
          break;
        case SESSION_REQUEST_FALSE:
          log.info("Text Chat Session Request - False.");
          log.info("New Text Chat Session Created - Session ID: " + sessionID);
          break;
        case FAILED_BY_NETWORK:
          log.warn("Text Chat Session Request - Network error.");
          break;
        default:
          log.warn("Text Chat Session Request - Unknown error.");
      }
    });
  }

  private void startService() {
    this.textChatService = new TextChatService(this.message, this.messageManager, this.connection,
        this.userID,
        this.sessionID);
    this.connection.getListener().setTextChatService(textChatService);
    this.textChatService.start();
  }

  /**
   * Method to initialise the main text chat UI action listeners.
   */
  private void addActionListeners() {

    // Enter key for text field.
    textChatInput.setOnKeyPressed(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent key) {
        if (key.getCode().equals(KeyCode.ENTER)) {
          sendMsgText();
        }
      }
    });

  }

  private void sendMsgText() {
    if (!textChatInput.getText().isEmpty()) {

      // Update Message Contents of controller
      this.message.setMsg(textChatInput.getText());
      this.message.setUserID(this.userID);
      this.message.setSessionID(this.sessionID);

      this.textChatService.sendSessionUpdates(this.message);

      textChatInput.clear();
    }
  }

  @FXML
  public void closeApplication() {
    Platform.exit();
    System.exit(0);
  }
}