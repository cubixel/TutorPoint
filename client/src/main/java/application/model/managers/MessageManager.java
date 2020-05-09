package application.model.managers;

import application.model.Message;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * CLASS DESCRIPTION: This class is used to manage, store and display messages.
 *
 * @author Oli Clarke
 */

public class MessageManager {

  private VBox textChatVBox;              // Text Chat GUI for client.
  private ScrollPane textChatScrollPane;  // Text Chat Scroll Pane.
  private List<Message> messages;         // Local list of all messages.

  /**
   * Main class constructor.
   */
  public MessageManager(VBox textChat, ScrollPane textChatScrollPane) {
    messages = new ArrayList<Message>();
    this.textChatVBox = textChat;
    this.textChatScrollPane = textChatScrollPane;
    textChatScrollPane.vvalueProperty().bind(textChatVBox.heightProperty());
    
    
  }

  /**
   * Method to display message on client GUI.
   */
  private void displayMessage(String userName, String chatContent) {
    TextFlow messageBox = new TextFlow();
    Text userNameText = new Text(userName + " : ");
    Text messageText = new Text(chatContent);
    userNameText.setFill(Color.web("#215590"));
    messageBox.getChildren().addAll(userNameText, messageText);
    textChatVBox.getChildren().addAll(messageBox);
    //remove messages if too many
    if (textChatVBox.getChildren().size() > 100) {
      textChatVBox.getChildren().remove(0);
    }   
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
        displayMessage(message.getUserName(), message.getMsg());
      }
    });

  }

  /**
   * Method to handle scroll events on the textChatScrollPane.
   * @param event the scroll event triggered
   */
  public void scrolled(ScrollEvent event) {
    //stick or unstick the scroll to the bottom of the window
    if (event.getDeltaY() > 0) {
      textChatScrollPane.vvalueProperty().unbind();
    } else {
      if (textChatScrollPane.getVvalue() == textChatScrollPane.getVmax()) {
        textChatScrollPane.vvalueProperty().bind(textChatVBox.heightProperty());
      }
    }
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
