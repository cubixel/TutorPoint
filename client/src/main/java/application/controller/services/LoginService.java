package application.controller.services;

import application.controller.enums.AccountLoginResult;
import application.model.Account;
import com.google.gson.Gson;
import java.io.IOException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class extends the javafx implementation of multithreading (service).
 * It connect to the server, send over the user's login details and waits
 * for the Account of the user to be returned followed by the AccountLoginResult.
 *
 * @author Che McKirgan
 * @author James Gardner
 */
public class LoginService extends Service<AccountLoginResult> {

  private Account account;
  private MainConnection connection;
  private static final Logger log = LoggerFactory.getLogger("LoginService");

  /**
   * Initialises a newly created {@code LoginService} object. Needs
   * access to the main client-server connection in order to
   * make the login request. Needs an account object but this can
   * also be set later via the {@code setAccount()} method.
   *
   * @param account
   *        An Account object that must contain a username and password
   *
   * @param mainConnection
   *        The connection between client and server
   */
  public LoginService(Account account, MainConnection mainConnection) {
    this.account = account;
    this.connection = mainConnection;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  /**
   * This packages up the {@code Account} object into a {@code Gson} String
   * then sends this to the Server. It then tells the connection to listen for
   * a packaged {@code Account} from the Server followed by a String with information on
   * if the login process was successful or why it wasn't.
   *
   * @return {@code AccountLoginResult.LOGIN_SUCCESS} if the login was successful, otherwise various
   *         other {@code AccountLoginResult} will explain the issue.
   */
  private AccountLoginResult login() {
    // TODO: Receive login token, though is this needed?
    try {
      /* Sends a packaged Account object with the isRegister variable set to 0 */
      connection.sendString(connection.packageClass(this.account));
      /* Listens for an Account from the server containing rest of user details */
      Account accountReceived = connection.listenForAccount();
      /* Updating local account with new account information */
      connection.setUserID(accountReceived.getUserID());
      log.info("This user has user ID: " + accountReceived.getUserID());
      account.setUserID(accountReceived.getUserID());
      account.setEmailAddress(accountReceived.getEmailAddress());
      account.setTutorStatus(accountReceived.getTutorStatus());
      account.setFollowedSubjects(accountReceived.getFollowedSubjects());
      account.setProfilePicture(accountReceived.getProfilePicture());
      /* Listens for an AccountLoginResult packaged as a string */
      String serverReply = connection.listenForString();
      return new Gson().fromJson(serverReply, AccountLoginResult.class);
    } catch (IOException e) {
      log.error("FAILED_BY_NETWORK", e);
      return AccountLoginResult.FAILED_BY_NETWORK;
    } catch (Exception e) {
      log.error("FAILED_BY_UNEXPECTED_ERROR", e);
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
