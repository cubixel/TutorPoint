package model.managers;

import java.util.ArrayList;
import java.util.List;
import model.Subject;

public class SubjectManager {

  private List<Subject> subjects;

  public SubjectManager() {
    subjects = new ArrayList<Subject>();
  }

  public void addSubject(Subject subject) {
    subjects.add(subject);
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
}