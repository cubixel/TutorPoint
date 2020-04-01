package application.controller.services;

import application.controller.enums.AccountLoginResult;
import application.model.Account;
import com.google.gson.Gson;
import java.io.IOException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * This class extends the javafx implementation of multithreading (service).
 * It connect to the server, send over the user's login details and waits
 * for the Account of the user to be returned followed by the AccountLoginResult.
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
      // Sends a packaged Account object with the isRegister variable set to 0
      connection.sendString(connection.packageClass(this.account));
      // Listens for an Account from the server with user details
      connection.listenForAccount(account);
      // Listens for an AccountLoginResult packaged as a string
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
