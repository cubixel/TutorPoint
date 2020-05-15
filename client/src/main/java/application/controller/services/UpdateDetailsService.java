package application.controller.services;

import application.controller.enums.AccountUpdateResult;
import application.model.updates.AccountUpdate;
import com.google.gson.Gson;
import java.io.IOException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class extends the javafx implementation of multithreading (service).
 * It connect to the server, send over the updates to the users details and waits
 * for the server to respond with the outcome of the update.
 *
 * @author James Gardner
 */
public class UpdateDetailsService extends Service<AccountUpdateResult> {

  private AccountUpdate accountUpdate;
  private final MainConnection connection;
  private static final Logger log = LoggerFactory.getLogger("UpdateDetailsService");

  /**
   * Initialises a newly created {@code UpdateDetailsService} object. Needs
   * access to the main client-server connection in order to
   * make the update request. Needs an AccountUpdate object but this can
   * also be set later via the {@code setAccountUpdate()} method.
   *
   * @param accountUpdate
   *        An AccountUpdate object that must contain changes to an accounts information
   *
   * @param mainConnection
   *        The connection between client and server
   */
  public UpdateDetailsService(AccountUpdate accountUpdate, MainConnection mainConnection) {
    this.accountUpdate = accountUpdate;
    this.connection = mainConnection;
  }

  public void setAccountUpdate(AccountUpdate accountUpdate) {
    this.accountUpdate = accountUpdate;
  }

  /**
   * This packages up the {@code AccountUpdate} object into a {@code Gson} String
   * then sends this to the Server. It then tells the connection to listen for
   * String with information on if the update process was successful or why it wasn't.
   *
   * @return {@code AccountUpdateResult.ACCOUNT_UPDATE_SUCCESS} if the update was successful,
   *         otherwise various other {@code AccountUpdateResult} will explain the issue.
   */
  private AccountUpdateResult update() {
    try {
      connection.sendString(connection.packageClass(this.accountUpdate));
      String serverReply = connection.listenForString();
      return new Gson().fromJson(serverReply, AccountUpdateResult.class);
    } catch (IOException e) {
      log.error("FAILED_BY_NETWORK", e);
      return AccountUpdateResult.FAILED_BY_NETWORK;
    } catch (Exception e) {
      log.error("FAILED_BY_UNEXPECTED_ERROR", e);
      return AccountUpdateResult.FAILED_BY_UNEXPECTED_ERROR;
    }
  }

  @Override
  protected Task<AccountUpdateResult> createTask() {
    return new Task<>() {
      @Override
      protected AccountUpdateResult call() {
        return update();
      }
    };
  }
}