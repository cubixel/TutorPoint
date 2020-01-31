package application;

import application.connection.MainConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class MainController {
    //private MainConnection connection;

    @FXML
    private Label lblStatus;
    // This label is just for testing

    @FXML
    private TextField txtUserName;

    @FXML
    private TextField txtPassword;

    /*
    public MainController(){
        //connection = new MainConnection(null, 5000);
    }*/

    public void Login(ActionEvent event){
       lblStatus.setText(txtUserName.getText());
       System.out.println(event);
    }

    public void Register(ActionEvent event){

    }

    public static void main(String[] args) {
        MainController main = new MainController();
    }

    public void setLabel(String test) {
        this.lblStatus.setText(test);
    }

    public String getLabel(){
        return lblStatus.getText();
    }
}
