package application.model.updates;

import application.model.Account;

/**
 * Used to update a users profile information. Contains all the information
 * that an account can update. This is then packaged and sent to the server
 * using the UpdateDetailsService.
 *
 * @author James Gardner
 * @see Account
 * @see application.controller.services.UpdateDetailsService
 */
public class AccountUpdate extends Account {
  String usernameUpdate;
  String emailAddressUpdate;
  String hashedpwUpdate;
  int tutorStatusUpdate;


  /**
   * The constructor for the Class. All information must be provided.
   *
   * @param account
   *        The account that is being updated
   *
   * @param usernameUpdate
   *        A new username, set to null if not being updated
   *
   * @param emailAddressUpdate
   *        A new email address, set to null if not being updated
   *
   * @param hashedpwUpdate
   *        A new password, set to null if not being updated
   *
   * @param tutorStatusUpdate
   *        A new tutor status, set to -1 if not being updated
   */
  public AccountUpdate(Account account, String usernameUpdate, String emailAddressUpdate,
      String hashedpwUpdate, int tutorStatusUpdate) {
    super(account.getUsername(), account.getEmailAddress(), account.getHashedpw(),
        account.getTutorStatus(), 0);
    this.setUserID(account.getUserID());
    this.usernameUpdate = usernameUpdate;
    this.emailAddressUpdate = emailAddressUpdate;
    this.hashedpwUpdate = hashedpwUpdate;
    this.tutorStatusUpdate = tutorStatusUpdate;
  }
}