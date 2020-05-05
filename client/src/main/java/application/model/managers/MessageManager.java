package application.model.managers;

import application.model.Message;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * CLASS DESCRIPTION: This class is used to manage, store and display messages.
 *
 * @author Oli Clarke
 */

public class MessageManager {

  private VBox textChatVBox;         // Text Chat GUI for client.
  private List<Message> messages;    // Local list of all messages.

  /**
   * Main class constructor.
   */
  public MessageManager(VBox textChat) {
    messages = new ArrayList<Message>();
    this.textChatVBox = textChat;
  }

  /**
   * Method to display message on client GUI.
   */
  public void displayMessage(Integer username, String chatContent) {
    HBox newHBox = new HBox(5.0, new Label("User-" + username + ":"));
    Label c = new Label(chatContent);
    c.setWrapText(true); // enable text wrapping in chat box
    newHBox.getChildren().addAll(c);
    HBox.setHgrow(c, Priority.ALWAYS);
    textChatVBox.getChildren().addAll(newHBox);
  }

  /**
   * Method to add message to locally stored history.
   */
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

  /**
   * GETTERS & SETTERS.
   **/

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
