package application.controller.services;

import application.controller.enums.AccountLoginResult;
import application.model.Account;
import com.google.gson.Gson;
import java.io.IOException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * extends the javafx implementation of multithreading (service).
 * This should connect to the server, send over the user details and confirm
 * the result
 */
public class LoginService extends Service<AccountLoginResult> {

  Account account;
  MainConnection connection;

  public LoginService(Account account, MainConnection mainConnection) {
    this.account = account;
    this.connection = mainConnection;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  private AccountLoginResult login() {
    /* 
     * Packages the created account and sends it to the server, waits up to 3s for a reply,
     * if no reply is given network failure is assumed.
     */
    
    //TODO: Receive login token is this needed??
    try {
      connection.sendString(connection.packageClass(this.account));
      connection.listenForAccount(account);
      String serverReply = connection.listenForString();
      return new Gson().fromJson(serverReply, AccountLoginResult.class);
    } catch (IOException e) {
      e.printStackTrace();
      return AccountLoginResult.FAILED_BY_NETWORK;
    } catch (Exception e) {
      e.printStackTrace();
      return AccountLoginResult.FAILED_BY_UNEXPECTED_ERROR;
    }
  }

  @Override
  protected Task<AccountLoginResult> createTask() {
    return new Task<AccountLoginResult>() {
      @Override
      protected AccountLoginResult call() {
        return login();
      }
    };
  }

}
