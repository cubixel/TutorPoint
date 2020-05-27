package application.model.managers;

import application.model.Subject;
import java.util.ArrayList;
import java.util.List;

/**
 * The SubjectManger class is used to contain and manage
 * a list of Subjects received from the Server.
 *
 * @author James Gardner
 * @see application.model.Subject
 */
public class SubjectManager {

  private final List<Subject> subjects;

  public SubjectManager() {
    subjects = new ArrayList<Subject>();
  }

  public void addSubject(Subject subject) {
    subjects.add(subject);
  }

  public void popSubject() {
    subjects.remove(getLastSubject());
  }

  public int getNumberOfSubjects() {
    return subjects.size();
  }

  public Subject getSubject(int id) {
    return subjects.get(id);
  }

  public Subject getLastSubject() {
    return subjects.get(subjects.size() - 1);
  }

  /**
   * Returns the position of a Subject with
   * the name subjectName if it is within the list
   * otherwise returns 0.
   *
   * @param subjectName
   *        The name of the subject to find within the list
   *
   * @return Subject position or -1 if subject not found
   */
  public int getElementNumber(String subjectName) {
    int i = 0;
    for (Subject subject : subjects) {
      if (subject.getName().equals(subjectName)) {
        return i;
      }
      i++;
    }
    return -1;
  }

}
