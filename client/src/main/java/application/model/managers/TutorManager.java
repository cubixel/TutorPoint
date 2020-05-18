package application.model.managers;

import application.model.Account;
import java.util.ArrayList;
import java.util.List;

/**
 * The TutorManager class is used to contain and manage
 * a list of Tutors received from the Server.
 *
 * @author James Gardner
 * @see application.model.Tutor
 */
public class TutorManager {

  private final List<Account> tutors;

  public TutorManager() {
    tutors = new ArrayList<>();
  }

  public void addTutor(Account account) {
    tutors.add(account);
  }

  public void popTutor() {
    tutors.remove(getLastTutor());
  }

  public int getNumberOfTutors() {
    return tutors.size();
  }

  public Account getTutor(int id) {
    return tutors.get(id);
  }

  public Account getLastTutor() {
    return tutors.get(tutors.size() - 1);
  }
}