package application.controller.services;

import application.controller.enums.AccountUpdateResult;
import application.model.AccountUpdate;
import com.google.gson.Gson;
import java.io.IOException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class UpdateDetailsService extends Service<AccountUpdateResult> {

  AccountUpdate accountUpdate;
  MainConnection connection;

  public UpdateDetailsService(AccountUpdate accountUpdate, MainConnection mainConnection) {
    this.accountUpdate = accountUpdate;
    this.connection = mainConnection;
  }

  public void setAccountUpdate(AccountUpdate accountUpdate) {
    this.accountUpdate = accountUpdate;
  }

  private AccountUpdateResult update() {
    /*
     * Packages the created account and sends it to the server, waits up to 3s for a reply,
     * if no reply is given network failure is assumed.
     */

    //TODO: Receive login token is this needed??
    try {
      connection.sendString(connection.packageClass(this.accountUpdate));
      String serverReply = connection.listenForString();
      return new Gson().fromJson(serverReply, AccountUpdateResult.class);
    } catch (IOException e) {
      e.printStackTrace();
      return AccountUpdateResult.FAILED_BY_NETWORK;
    } catch (Exception e) {
      e.printStackTrace();
      return AccountUpdateResult.FAILED_BY_UNEXPECTED_ERROR;
    }
  }

  @Override
  protected Task<AccountUpdateResult> createTask() {
    return new Task<AccountUpdateResult>() {
      @Override
      protected AccountUpdateResult call() {
        return update();
      }
    };
  }
}