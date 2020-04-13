import static services.ServerTools.getSubjectService;
import static services.ServerTools.getTopTutorsService;
import static services.ServerTools.sendFileService;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ClientNotifier;
import services.ServerTools;
import services.enums.AccountLoginResult;
import services.enums.AccountRegisterResult;
import services.enums.AccountUpdateResult;
import services.enums.FileDownloadResult;
import services.enums.RatingUpdateResult;
import services.enums.WhiteboardRenderResult;
import services.enums.WhiteboardRequestResult;
import sql.MySql;

public class ClientHandler extends Thread {

  private int token;
  private int currentUserID;
  private final DataInputStream dis;
  private final DataOutputStream dos;
  private MySql sqlConnection;
  private long lastHeartbeat;
  private boolean loggedIn;
  private HashMap<Integer, ClientHandler> activeClients;
  private ArrayList<WhiteboardHandler> activeSessions;
  private ClientNotifier notifier;
  private PresentationHandler presentationHandler;

  private static final Logger log = LoggerFactory.getLogger("ClientHandler");

  /**
   * CLASS DESCRIPTION.
   * #################
   *
   * @author CUBIXEL
   *
   */
  public ClientHandler(DataInputStream dis, DataOutputStream dos, int token, MySql sqlConnection,
      ArrayList<WhiteboardHandler> allActiveSessions, HashMap<Integer,
      ClientHandler> activeClients) {
    setDaemon(true);
    setName("ClientHandler-" + token);
    this.dis = dis;
    this.dos = dos;
    this.token = token;
    this.sqlConnection = sqlConnection;
    this.lastHeartbeat = System.currentTimeMillis();
    this.loggedIn = true;
    this.activeClients = activeClients;
    this.activeSessions = allActiveSessions;
    this.presentationHandler = null;
  }

  /**
   * CLASS DESCRIPTION.
   * #################
   *
   * @author CUBIXEL
   *
   */
  @Override
  public void run() {
    // Does the client need to know its number?
    //writeString("Token#" + token);
    presentationHandler = new PresentationHandler(dis, dos, token);
    presentationHandler.run();

    String received = null;

    while (lastHeartbeat > (System.currentTimeMillis() - 10000) & loggedIn) {
      // Do stuff with this client in this thread

      // When client disconnects then close it down.

      try {

        while (dis.available() > 0) {
          received = dis.readUTF();
        }
        if (received != null) {
          try {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(received, JsonObject.class);
            String action = jsonObject.get("Class").getAsString();
            log.info("Requested: " + action);

            //TODO: Does switch have a performance improvement in java?
            switch (action) {
              case "Account":
                if (jsonObject.get("isRegister").getAsInt() == 1) {
                  log.info("Attempting to Register New Account");
                  createNewUser(jsonObject.get("username").getAsString(),
                      jsonObject.get("emailAddress").getAsString(),
                      jsonObject.get("hashedpw").getAsString(),
                      jsonObject.get("tutorStatus").getAsInt());
                } else {
                  log.info("Login Username: " + jsonObject.get("username").getAsString());
                  loginUser(jsonObject.get("username").getAsString(),
                      jsonObject.get("hashedpw").getAsString());
                }

                // This is the logic for returning a requested file.
                break;

              case "FileRequest":
                try {
                  sendFileService(dos, new File(jsonObject.get("filePath").getAsString()));
                  JsonElement jsonElement = gson
                      .toJsonTree(FileDownloadResult.FILE_DOWNLOAD_SUCCESS);
                  dos.writeUTF(gson.toJson(jsonElement));
                  log.info("File Sent Successfully");
                } catch (IOException e) {
                  JsonElement jsonElement =
                      gson.toJsonTree(FileDownloadResult.FAILED_BY_FILE_NOT_FOUND);
                  dos.writeUTF(gson.toJson(jsonElement));
                  log.error("File: " + jsonObject.get("filePath").getAsString() + " Not Found");
                }

                break;

              case "SubjectRequest":
                try {
                  getSubjectService(dos, sqlConnection, jsonObject.get("id").getAsInt());
                } catch (SQLException e) {
                  e.printStackTrace();
                }

                break;

              case "TopTutorsRequest":
                try {
                  getTopTutorsService(dos, sqlConnection, jsonObject.get("id").getAsInt());
                } catch (SQLException e) {
                  e.printStackTrace();
                }

                break;

              case "AccountUpdate":
                try {
                  updateUserDetails(jsonObject.get("userID").getAsInt(),
                      jsonObject.get("username").getAsString(),
                      jsonObject.get("hashedpw").getAsString(),
                      jsonObject.get("usernameUpdate").getAsString(),
                      jsonObject.get("emailAddressUpdate").getAsString(),
                      jsonObject.get("hashedpwUpdate").getAsString(),
                      jsonObject.get("tutorStatusUpdate").getAsInt());
                } catch (IOException e) {
                  e.printStackTrace();
                }

                break;

              case "WhiteboardRequestSession":
                String sessionID = jsonObject.get("sessionID").getAsString();
                if (sessionID.equals("session-000") && (activeSessions.isEmpty())) {
                  //New Session
                  sessionID = "session-000"; //UUID.randomUUID().toString();
                  String tutorID = jsonObject.get("userID").getAsString();
                  WhiteboardHandler newSession = new WhiteboardHandler(sessionID, tutorID, token,
                      activeClients);
                  log.info("User " + tutorID + " Joined Session: " + sessionID);
                  activeSessions.add(newSession);
                  JsonElement jsonElement
                      = gson.toJsonTree(WhiteboardRequestResult.WHITEBOARD_REQUEST_SUCCESS);
                  newSession.start();
                  dos.writeUTF(gson.toJson(jsonElement));
                } else {
                  //Join existing
                  for (WhiteboardHandler activeSession : activeSessions) {
                    if (sessionID.equals(activeSession.getSessionID())) {
                      String userID = jsonObject.get("userID").getAsString();
                      activeSession.addUser(this.token);
                      log.info("User " + userID + " Joined Session: " + sessionID);
                      for (Integer user : activeSession.getSessionUsers()) {
                        log.info("Users " + user);
                      }
                      JsonElement jsonElement
                          = gson.toJsonTree(WhiteboardRequestResult.WHITEBOARD_REQUEST_SUCCESS);
                      dos.writeUTF(gson.toJson(jsonElement));
                    }
                  }
                }
                break;

              case "WhiteboardSession":
                sessionID = jsonObject.get("sessionID").getAsString();
                for (WhiteboardHandler activeSession : activeSessions) {
                  // Send session package to matching active session.
                  if (sessionID.equals(activeSession.getSessionID())) {
                    // Check is session user is in active session.
                    for (Integer userID : activeSession.getSessionUsers()) {
                      if (token == userID) {
                        // If a match is found, send package to that session.
                        if (activeSession.getState() == State.RUNNABLE) {
                          activeSession.addToQueue(jsonObject);
                        } else {
                          activeSession.run(jsonObject);
                        }
                        JsonElement jsonElement
                            = gson.toJsonTree(WhiteboardRenderResult.WHITEBOARD_RENDER_SUCCESS);
                        dos.writeUTF(gson.toJson(jsonElement));
                      }
                    }
                  }
                }
                break;
              
              case "RatingUpdate":
                log.info("ClientHandler: Received RatingUpdate from Client");
                updateRating(jsonObject.get("rating").getAsInt(),
                    jsonObject.get("userID").getAsInt(),
                    jsonObject.get("tutorID").getAsInt());
                break;

              case "PresentationRequest":
                String presentationAction = jsonObject.get("action").getAsString();
                log.info("PresentationHandler Action Requested: " + presentationAction);
                presentationHandler.run(presentationAction);

                break;
                
              default:
                log.warn("Unknown Action");
            }

          } catch (JsonSyntaxException e) {
            if (received.equals("Heartbeat")) {
              lastHeartbeat = System.currentTimeMillis();
              // log.info("Received Heartbeat from Client "
              //     + token + " at " + lastHeartbeat);

            } else if (received.equals("Logout")) {
              log.info("Received logout request from Client");
              logOff();

            } else {
              writeString(received);
              log.info("Received String: " + received);
            }


          }
          received = null;
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    //TODO End any sessions on sql, remove tutors from live table on sql
    //if (sqlConnection.isSessionLive(#SessionID)) {
    //  sqlConnection.endLiveSession(#SessionID);
    //}

    log.info("Client " + token + " Disconnected");
  }

  /**
   * CLASS DESCRIPTION.
   * #################
   *
   * @author CUBIXEL
   *
   */
  public void writeString(String msg) {
    try {
      dos.writeUTF(msg);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * CLASS DESCRIPTION.
   * #################
   *
   * @author CUBIXEL
   *
   */
  private void loginUser(String username, String password) throws IOException {
    Gson gson = new Gson();
    Account account = new Account(username, password);
    if (!sqlConnection.checkUserDetails(username, password)) {
      dos.writeUTF(ServerTools.packageClass(account));
      JsonElement jsonElement = gson.toJsonTree(AccountLoginResult.FAILED_BY_CREDENTIALS);
      dos.writeUTF(gson.toJson(jsonElement));
      log.info("LoginUser: FAILED_BY_CREDENTIALS");
    } else {
      int userID = sqlConnection.getUserID(username);
      String emailAddress = sqlConnection.getEmailAddress(userID);
      int tutorStatus = sqlConnection.getTutorStatus(userID);
      account.setUserID(userID);
      account.setEmailAddress(emailAddress);
      account.setTutorStatus(tutorStatus);

      ResultSet resultSet = sqlConnection.getFavouriteSubjects(userID);
      try {
        while (resultSet.next()) {
          String subjectName = sqlConnection.getSubjectName(resultSet.getInt("subjectID"));
          account.addFollowedSubjects(subjectName);
        }
      } catch (SQLException e) {
        log.warn("LoginUser: No Followed Subjects");
      }

      dos.writeUTF(ServerTools.packageClass(account));
      JsonElement jsonElement = gson.toJsonTree(AccountLoginResult.LOGIN_SUCCESS);
      dos.writeUTF(gson.toJson(jsonElement));
      log.info("Login User: " + username + " SUCCESSFUL");
      currentUserID = userID;
    }
  }

  /**
   * CLASS DESCRIPTION.
   * #################
   *
   * @author CUBIXEL
   *
   */
  private void createNewUser(String username, String email,
      String password, int isTutor) throws IOException {
    Gson gson = new Gson();
    if (!sqlConnection.usernameExists(username)) {
      if (!sqlConnection.emailExists(email)) {
        if (sqlConnection.createAccount(username, email, password, isTutor)) {
          JsonElement jsonElement = gson.toJsonTree(AccountRegisterResult.ACCOUNT_REGISTER_SUCCESS);
          dos.writeUTF(gson.toJson(jsonElement));
          log.info("Register New User: SUCCESSFUL");
        } else {
          log.error("Register New User: FAILED_BY_UNEXPECTED_ERROR");
          JsonElement jsonElement
              = gson.toJsonTree(AccountRegisterResult.FAILED_BY_UNEXPECTED_ERROR);
          dos.writeUTF(gson.toJson(jsonElement));
        }
      } else {
        JsonElement jsonElement = gson.toJsonTree(AccountRegisterResult.FAILED_BY_EMAIL_TAKEN);
        dos.writeUTF(gson.toJson(jsonElement));
        log.info("Register New User: FAILED_BY_EMAIL_TAKEN");
      }
    } else {
      JsonElement jsonElement = gson.toJsonTree(AccountRegisterResult.FAILED_BY_USERNAME_TAKEN);
      dos.writeUTF(gson.toJson(jsonElement));
      log.info("Register New User: FAILED_BY_USERNAME_TAKEN");
    }
  }

  private void updateUserDetails(int userID, String username, String password,
      String usernameUpdate, String emailAddressUpdate, String hashedpwUpdate,
      int tutorStatusUpdate) throws IOException {
    Gson gson = new Gson();
    if (sqlConnection.checkUserDetails(username, password)) {
      if (!sqlConnection.usernameExists(usernameUpdate)) {
        if (!sqlConnection.emailExists(emailAddressUpdate)) {
          sqlConnection.updateDetails(userID, usernameUpdate, emailAddressUpdate,
              hashedpwUpdate, tutorStatusUpdate);
          JsonElement jsonElement = gson.toJsonTree(AccountUpdateResult.ACCOUNT_UPDATE_SUCCESS);
          dos.writeUTF(gson.toJson(jsonElement));
          log.info("Update User Details: SUCCESSFUL");
        } else {
          JsonElement jsonElement = gson.toJsonTree(AccountUpdateResult.FAILED_BY_EMAIL_TAKEN);
          dos.writeUTF(gson.toJson(jsonElement));
          log.info("Update User Details: FAILED_BY_EMAIL_TAKEN");
        }
      } else {
        JsonElement jsonElement = gson.toJsonTree(AccountUpdateResult.FAILED_BY_USERNAME_TAKEN);
        dos.writeUTF(gson.toJson(jsonElement));
        log.info("Update User Details: FAILED_BY_USERNAME_TAKEN");
      }
    } else {
      JsonElement jsonElement = gson.toJsonTree(AccountLoginResult.FAILED_BY_CREDENTIALS);
      dos.writeUTF(gson.toJson(jsonElement));
      log.info("Update User Details: FAILED_BY_CREDENTIALS");
    }
  }

  private void updateRating(int rating, int userID, int tutorID) {
    Gson gson = new Gson();
    try {
      try {
        if (sqlConnection.getTutorsRating(tutorID, userID) == -1) {
          sqlConnection.addTutorRating(tutorID, userID, rating);
          JsonElement jsonElement = gson.toJsonTree(RatingUpdateResult.RATING_UPDATE_SUCCESS);
          dos.writeUTF(gson.toJson(jsonElement));
          log.info("UpdateRating: Created new rating for Tutor " + tutorID
              + "by User " + userID);
        } else {
          sqlConnection.updateTutorRating(tutorID, userID, rating);
          JsonElement jsonElement = gson.toJsonTree(RatingUpdateResult.RATING_UPDATE_SUCCESS);
          dos.writeUTF(gson.toJson(jsonElement));
          log.info("UpdateRating: Update rating for Tutor " + tutorID
              + "by User " + userID);
        }
      } catch (SQLException e) {
        log.error("UpdateRating: Failed to access MySQL Database ", e);
        JsonElement jsonElement = gson.toJsonTree(RatingUpdateResult.FAILED_BY_DATABASE_ACCESS);
        dos.writeUTF(gson.toJson(jsonElement));
      }
    } catch (IOException ioe) {
      log.error("UpdateRating: Could not write to DataOutputStream ", ioe);
    }
  }


  public String toString() {
    return "This is client " + token;
  }

  public void logOff() {
    this.loggedIn = false;
  }

  public void setNotifier(ClientNotifier notifier) {
    this.notifier = notifier;
  }

  public ClientNotifier getNotifier() {
    return notifier;
  }

  public int getUserID() {
    return currentUserID;
  }
}
