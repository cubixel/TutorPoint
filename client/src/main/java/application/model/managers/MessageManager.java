package application.model.managers;

import application.model.Message;
import java.util.ArrayList;
import java.util.List;

public class MessageManager {

  private List<Message> messages;

  public MessageManager() {
    messages = new ArrayList<Message>();
  }

  public void addMessage(Message message) {
    messages.add(message);
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
