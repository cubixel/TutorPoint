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

  public void clear() {
    tutors.clear();
  }

}
