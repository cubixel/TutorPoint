package application.model.managers;

import application.model.Message;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * CLASS DESCRIPTION: This class is used to manage, store and display messages.
 *
 * @author Oli Clarke
 */

public class MessageManager {

  private VBox textChatVBox;              // Text Chat GUI for client.
  private ScrollPane textChatScrollPane;  // Text Chat Scroll Pane.
  private List<Message> messages;         // Local list of all messages.
  private boolean stayAtBottom;
  private DoubleProperty currentPosition;

  /**
   * Main class constructor.
   */
  public MessageManager(VBox textChat, ScrollPane textChatScrollPane) {
    messages = new ArrayList<Message>();
    this.textChatVBox = textChat;
    this.textChatScrollPane = textChatScrollPane;
    this.currentPosition = new DoubleProperty() {
    
      @Override
      public void set(double value) {
        // TODO Auto-generated method stub
        
      }
    
      @Override
      public double get() {
        // TODO Auto-generated method stub
        return 0;
      }
    
      @Override
      public void removeListener(InvalidationListener listener) {
        // TODO Auto-generated method stub
        
      }
    
      @Override
      public void addListener(InvalidationListener listener) {
        // TODO Auto-generated method stub
        
      }
    
      @Override
      public void removeListener(ChangeListener<? super Number> listener) {
        // TODO Auto-generated method stub
        
      }
    
      @Override
      public void addListener(ChangeListener<? super Number> listener) {
        // TODO Auto-generated method stub
        
      }
    
      @Override
      public String getName() {
        // TODO Auto-generated method stub
        return null;
      }
    
      @Override
      public Object getBean() {
        // TODO Auto-generated method stub
        return null;
      }
    
      @Override
      public void unbind() {
        // TODO Auto-generated method stub
        
      }
    
      @Override
      public boolean isBound() {
        // TODO Auto-generated method stub
        return false;
      }
    
      @Override
      public void bind(ObservableValue<? extends Number> observable) {
        // TODO Auto-generated method stub
        
      }
    };
    textChatScrollPane.vvalueProperty().bind(textChatVBox.heightProperty());
    textChatScrollPane.vmaxProperty().bind(textChatVBox.heightProperty());
    this.stayAtBottom = true;
    
  }

  /**
   * Method to display message on client GUI.
   */
  private void displayMessage(Integer username, String chatContent) {
    HBox newHBox = new HBox(5.0, new Label("User-" + username + ":"));
    Label c = new Label(chatContent);
    c.setWrapText(true); // enable text wrapping in chat box
    newHBox.getChildren().addAll(c);
    HBox.setHgrow(c, Priority.ALWAYS);
    double vvalueBeforeAdd = textChatScrollPane.getVvalue();
    System.out.println(vvalueBeforeAdd);
    textChatVBox.getChildren().addAll(newHBox);
    //remove messages if too many
    if (textChatVBox.getChildren().size() > 100) {
      textChatVBox.getChildren().remove(0);
    }
    //textChatScrollPane.setVvalue(vvalueBeforeAdd);
    
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

  public void scrolled(ScrollEvent event) {
    //this.currentPosition.set(this.currentPosition.get() + event.getDeltaY());

    if (event.getDeltaY() > 0) {
      //textChatScrollPane.vvalueProperty().bind(this.currentPosition);
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
