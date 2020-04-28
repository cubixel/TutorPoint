package application.model.managers;

import application.model.Message;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MessageManager {

  private VBox textChatVBox;

  private List<Message> messages;


  /**
   * .
   */
  public void displayMessage(Integer username, String chatContent) {
    HBox newHBox = new HBox(5.0, new Label("User-" + username + ":"));
    Label c = new Label(chatContent);
    c.setWrapText(true);
    newHBox.getChildren().addAll(c);
    HBox.setHgrow(c, Priority.ALWAYS);
    textChatVBox.getChildren().addAll(newHBox);
  }

  public MessageManager(VBox textChat) {
    messages = new ArrayList<Message>();
    this.textChatVBox = textChat;
  }

  public void addMessage(Message message) {
    messages.add(message);

    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        // Update UI here.
        displayMessage(message.getUserID(), message.getMsg());
      }
    });

  }

  public int getMessagesSize() {
    return messages.size();
  }

  public Message getMessage(int id) {
    return messages.get(id);
  }

  public Message getLastMessage() {
    return messages.get(messages.size() - 1);
  }
}
