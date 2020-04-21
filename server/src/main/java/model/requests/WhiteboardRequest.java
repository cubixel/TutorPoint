package model.requests;

import com.google.gson.JsonObject;
import java.util.ArrayList;

public class WhiteboardRequest {
  private boolean sessionExists;
  private String tutorID;
  private boolean studentAccess;
  private ArrayList<JsonObject> sessionHistory;

  public WhiteboardRequest(boolean sessionExists, String tutorID, boolean studentAccess,
      ArrayList<JsonObject> sessionHistory) {
    this.sessionExists = sessionExists;
    this.tutorID = tutorID;
    this.studentAccess = studentAccess;
    this.sessionHistory = sessionHistory;
  }

  public WhiteboardRequest(boolean sessionExists, String tutorID, boolean studentAccess) {
    this.sessionExists = sessionExists;
    this.tutorID = tutorID;
    this.studentAccess = studentAccess;
    this.sessionHistory = null;
  }

  public boolean isSessionExists() {
    return sessionExists;
  }

  public String getTutorID() {
    return tutorID;
  }

  public boolean isStudentAccess() {
    return studentAccess;
  }

  public ArrayList<JsonObject> getSessionHistory() {
    return sessionHistory;
  }
}
