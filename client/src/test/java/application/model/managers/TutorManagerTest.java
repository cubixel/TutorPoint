package application.model.managers;

import application.model.Account;
import application.model.Tutor;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class TutorManagerTest {
  private TutorManager tutorManager;

  @Mock
  Tutor tutorMock;

  @BeforeEach
  public void setUp() {
    tutorManager = new TutorManager();
  }

  @Test
  public void addTutorTest() {

  }
}
//
//  private final List<Account> tutors;
//
//  public TutorManager() {
//    tutors = new ArrayList<Account>();
//  }
//
//  public void addTutor(Account account) {
//    tutors.add(account);
//  }
//
//  public void popTutor() {
//    tutors.remove(getLastTutor());
//  }
//
//  public int getNumberOfTutors() {
//    return tutors.size();
//  }
//
//  public Account getTutor(int id) {
//    return tutors.get(id);
//  }
//
//  public Account getLastTutor() {
//    return tutors.get(tutors.size() - 1);
//  }
//
//  public void clear() {
//    tutors.clear();
//  }
