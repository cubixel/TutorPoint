package application.controller.services;

import application.controller.enums.AccountRegisterResult;
import application.model.Account;
import com.google.gson.Gson;
import java.io.IOException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class extends the javafx implementation of multithreading (service).
 * It connect to the server, send over the user's register details and waits
 * for the server to respond with the outcome of the attempted registration.
 *
 * @author James Gardner
 * @author Che McKirgan
 */
public class RegisterService extends Service<AccountRegisterResult> {

  private Account account;
  private final MainConnection connection;

  private static final Logger log = LoggerFactory.getLogger("RegisterService");

  /**
   * Initialises a newly created {@code RegisterService} object. Needs
   * access to the main client-server connection in order to
   * make the register request. Needs an account object but this can
   * also be set later via the {@code setAccount()} method.
   *
   * @param account
   *        An Account object that must contain a username and password
   *
   * @param connection
   *        The connection between client and server
   */
  public RegisterService(Account account, MainConnection connection) {
    this.account = account;
    this.connection = connection;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  /**
   * This packages up the {@code Account} object into a {@code Gson} String
   * then sends this to the Server. It then tells the connection to listen for
   * String with information on if the register process was successful or why it wasn't.
   *
   * @return {@code AccountRegisterResult.ACCOUNT_REGISTER_SUCCESS} if the register was successful,
   *         otherwise various other {@code AccountRegisterResult} will explain the issue.
   */
  private AccountRegisterResult register() {

    try {
      /* Sends a packaged Account object with the isRegister variable set to 1 */
      connection.sendString(connection.packageClass(this.account));
      /* Listens for an AccountRegisterResult packaged as a string */
      String serverReply = connection.listenForString();
      return new Gson().fromJson(serverReply, AccountRegisterResult.class);
    } catch (IOException e) {
      log.error("FAILED_BY_NETWORK", e);
      return AccountRegisterResult.FAILED_BY_NETWORK;
    } catch (Exception e) {
      log.error("FAILED_BY_UNEXPECTED_ERROR", e);
      return  AccountRegisterResult.FAILED_BY_UNEXPECTED_ERROR;
    }
  }

  @Override
  protected Task<AccountRegisterResult> createTask() {
    return new Task<>() {
      @Override
      protected AccountRegisterResult call() {
        return register();
      }
    };
  }
}
