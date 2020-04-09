package application.model.managers;

import application.model.Account;
import java.util.ArrayList;
import java.util.List;

public class TutorManager {

  private List<Account> tutors;

  public TutorManager() {
    tutors = new ArrayList<Account>();
  }

  public void addTutor(Account account) {
    tutors.add(account);
  }

  public int getNumberOfTutors() {
    return tutors.size();
  }

  public Account getTutor(int id) {
    return tutors.get(id);
  }

  public Account getLastSubject() {
    return tutors.get(tutors.size() - 1);
  }

}
