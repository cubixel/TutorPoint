package application.model.updates;

import application.model.Account;

public class AccountUpdate extends Account {
  String usernameUpdate = null;
  String emailAddressUpdate = null;
  String hashedpwUpdate = null;
  int tutorStatusUpdate = -1;

  public AccountUpdate(Account account, String usernameUpdate, String emailAddressUpdate,
      String hashedpwUpdate, int tutorStatusUpdate) {
    super(account.getUsername(), account.getEmailAddress(), account.getHashedpw(),
        account.getTutorStatus(), 0);
    this.usernameUpdate = usernameUpdate;
    this.emailAddressUpdate = emailAddressUpdate;
    this.hashedpwUpdate = hashedpwUpdate;
    this.tutorStatusUpdate = tutorStatusUpdate;
  }
}
