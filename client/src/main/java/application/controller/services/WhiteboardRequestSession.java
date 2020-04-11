package application.controller.services;

import application.controller.enums.WhiteboardRenderResult;
import application.model.Whiteboard;
import com.google.gson.Gson;
import java.io.IOException;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WhiteboardRequestSession {

  private MainConnection connection;
  private String userID;
  private String sessionID;
  private boolean tutorOnlyAccess;
  private static final Logger log = LoggerFactory.getLogger("WhiteboardRequestService");

  public WhiteboardRequestSession(MainConnection mainConnection, String userID, String sessionID) {
    this.connection = mainConnection;
    this.userID = userID;
    this.sessionID = sessionID;
    this.tutorOnlyAccess = true;
  }

  public WhiteboardRequestSession(MainConnection mainConnection, String userID) {
    this.connection = mainConnection;
    this.userID = userID;
    this.tutorOnlyAccess = true;
  }
}
