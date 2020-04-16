package application.controller;

import application.controller.services.MainConnection;
import application.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class TextChatWindowController extends BaseController {

  /**
   * CONSTRUCTOR DESCRIPTION.
   *
   * @param viewFactory .
   * @param fxmlName .
   * @param mainConnection .
   */


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

  public TextChatWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection) {
    super(viewFactory, fxmlName, mainConnection);
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
}