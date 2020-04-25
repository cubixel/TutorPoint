package model.requests;

import com.google.gson.JsonObject;
import java.util.ArrayList;

public class WhiteboardRequest {
  private boolean sessionExists;
  private int sessionID;
  private boolean studentAccess;
  private ArrayList<JsonObject> sessionHistory;

  public WhiteboardRequest(boolean sessionExists, int sessionID, boolean studentAccess,
      ArrayList<JsonObject> sessionHistory) {
    this.sessionExists = sessionExists;
    this.sessionID = sessionID;
    this.studentAccess = studentAccess;
    this.sessionHistory = sessionHistory;
  }

  public WhiteboardRequest(boolean sessionExists, int sessionID, boolean studentAccess) {
    this.sessionExists = sessionExists;
    this.sessionID = sessionID;
    this.studentAccess = studentAccess;
    this.sessionHistory = null;
  }
}