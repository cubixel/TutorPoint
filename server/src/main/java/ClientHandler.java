import static services.ServerTools.getLiveTutors;
import static services.ServerTools.getNextFiveSubjectService;
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
import services.enums.SessionRequestResult;
import services.enums.StreamingStatusUpdateResult;
import services.enums.TutorRequestResult;
import services.enums.WhiteboardRenderResult;
import services.enums.WhiteboardRequestResult;
import sql.MySql;

public class ClientHandler extends Thread {

  private int token;
  private int currentUserID;
  private int currentSessionID;
  private Session session;
  private final DataInputStream dis;
  private final DataOutputStream dos;
  private MySql sqlConnection;
  private long lastHeartbeat;
  private boolean loggedIn;
  private MainServer mainServer;
  private ArrayList<WhiteboardHandler> activeWhiteboardSessions;
  private ClientNotifier notifier;
  

  private static final Logger log = LoggerFactory.getLogger("ClientHandler");

  /**
   * CLASS DESCRIPTION.
   * #################
   *
   * @author CUBIXEL
   *
   */
  public ClientHandler(DataInputStream dis, DataOutputStream dos, int token, MySql sqlConnection,
      ArrayList<WhiteboardHandler> activeWhiteboardSessions, MainServer mainServer) {
    setDaemon(true);
    setName("ClientHandler-" + token);
    this.dis = dis;
    this.dos = dos;
    this.token = token;
    this.sqlConnection = sqlConnection;
    this.lastHeartbeat = System.currentTimeMillis();
    this.loggedIn = false;
    this.mainServer = mainServer;
    this.activeWhiteboardSessions = activeWhiteboardSessions;
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

    String received = null;
    Gson gson = new Gson();

    while (lastHeartbeat > (System.currentTimeMillis() - 10000)) {
      // Do stuff with this client in this thread

      // When client disconnects then close it down.

      try {

        while (dis.available() > 0) {
          received = dis.readUTF();
        }
        if (received != null) {
          try {
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
                  JsonElement jsonElement =
                      gson.toJsonTree(FileDownloadResult.FILE_DOWNLOAD_SUCCESS);
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
                  if (!jsonObject.get("requestBasedOnCategory").getAsBoolean()) {
                    getNextFiveSubjectService(dos, sqlConnection,
                        jsonObject.get("numberOfSubjectsRequested").getAsInt(),
                        null);
                  } else {
                    getNextFiveSubjectService(dos, sqlConnection,
                        jsonObject.get("numberOfSubjectsRequested").getAsInt(),
                        jsonObject.get("subject").getAsString());
                  }

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

              case "LiveTutorsRequest":
                try {
                  getLiveTutors(dos, sqlConnection, jsonObject.get("id").getAsInt(), currentUserID);
                } catch (SQLException sqlException) {
                  log.warn("Error accessing MySQL Database whilst "
                      + "updating stream status", sqlException);
                  JsonElement jsonElement
                      = gson.toJsonTree(TutorRequestResult.FAILED_ACCESSING_DATABASE);
                  dos.writeUTF(gson.toJson(jsonElement));
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

              case "SessionRequest":
                int hostID = jsonObject.get("sessionID").getAsInt();
                if (jsonObject.get("isHost").getAsBoolean()) {
                  /* This is for the tutor/host to setup a session initially upon
                   * upon opening the stream window on the client side. */
                  currentSessionID = hostID;
                  session = new Session(hostID, this);
                  if (session.setUp()) {
                    JsonElement jsonElement
                        = gson.toJsonTree(SessionRequestResult.SESSION_REQUEST_TRUE);
                    dos.writeUTF(gson.toJson(jsonElement));
                  } else {
                    JsonElement jsonElement
                        = gson.toJsonTree(SessionRequestResult.SESSION_REQUEST_FALSE);
                    dos.writeUTF(gson.toJson(jsonElement));
                  }
                } else {
                  /* Here it is connecting a user to a currently active session that must
                   * be live for users to join. */
                  if (mainServer.getLoggedInClients().get(hostID).getSession().isLive()) {
                    currentSessionID = hostID;
                    mainServer.getLoggedInClients().get(hostID).getSession().getSessionUsers()
                        .put(currentUserID, this);
                  }
                }
                break;

              case "WhiteboardRequestSession":
                String sessionID = jsonObject.get("sessionID").getAsString();

                // Check if session has been created or needs creating.
                boolean sessionExists = false;
                for (WhiteboardHandler activeSession : activeWhiteboardSessions) {
                  if (sessionID.equals(activeSession.getSessionID())) {
                    sessionExists = true;
                    // If session exists, add user to that session.
                    String userID = jsonObject.get("userID").getAsString();
                    activeSession.addUser(this.token);
                    log.info("User " + userID + " Joined Session: " + sessionID);

                    // Respond with success.
                    JsonElement jsonElement
                        = gson.toJsonTree(WhiteboardRequestResult.SESSION_REQUEST_TRUE);
                    dos.writeUTF(gson.toJson(jsonElement));
                  }
                }
                // Else, create a new session from the session ID.
                if (!sessionExists) {
                  // Create new whiteboard handler.
                  String tutorID = jsonObject.get("userID").getAsString();
                  boolean tutorAccess = jsonObject.get("userID").getAsBoolean();
                  WhiteboardHandler newSession = new WhiteboardHandler(sessionID, tutorID, token,
                      mainServer.getAllClients(), tutorAccess);
                  log.info("New Whiteboard Session Created: " + sessionID);
                  log.info("User " + tutorID + " Joined Session: " + sessionID);
                  newSession.start();

                  // Add session to active session list.
                  activeWhiteboardSessions.add(newSession);

                  // Respond with success.
                  JsonElement jsonElement
                      = gson.toJsonTree(WhiteboardRequestResult.SESSION_REQUEST_FALSE);
                  dos.writeUTF(gson.toJson(jsonElement));
                }
                break;

              case "WhiteboardSession":
                sessionID = jsonObject.get("sessionID").getAsString();
                for (WhiteboardHandler activeSession : activeWhiteboardSessions) {
                  // Send session package to matching active session.
                  if (sessionID.equals(activeSession.getSessionID())) {
                    // Check is session user is in active session.
                    for (Integer userID : activeSession.getSessionUsers()) {
                      if (token == userID) {
                        // If a match is found, send package to that session.
                        activeSession.addToQueue(jsonObject);
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
                int presentationInt = jsonObject.get("slideNum").getAsInt();
                log.info("PresentationHandler Action Requested: " + presentationAction);
                session.getPresentationHandler().setSlideNum(presentationInt);
                session.getPresentationHandler().setAction(presentationAction);
                break;
                
              default:
                log.warn("Unknown Action");
            }

          } catch (JsonSyntaxException e) {
            switch (received) {
              case "Heartbeat":
                lastHeartbeat = System.currentTimeMillis();
                // log.info("Received Heartbeat from Client "
                //     + token + " at " + lastHeartbeat);

                break;

              case "Logout":
                log.info("Received logout request from Client");
                logOff();
                log.info("Logged off. There are now " + mainServer.getLoggedInClients().size()
                    + " logged in clients.");
                break;

              case "ChangeStatus":
                log.info("Received change of stream status request from Client");
                boolean status = session.isLive();
                log.info("Current status is: " + ((status) ? "Live" : "Not Live"));
                try {
                  if (status) {
                    sqlConnection.endLiveSession(currentSessionID, currentUserID);
                    session.setLive(false);
                  } else {
                    sqlConnection.startLiveSession(currentSessionID, currentUserID);
                    session.setLive(true);
                  }
                  JsonElement jsonElement
                      = gson.toJsonTree(StreamingStatusUpdateResult.STATUS_UPDATE_SUCCESS);
                  dos.writeUTF(gson.toJson(jsonElement));
                } catch (SQLException sqlException) {
                  log.warn("Error accessing MySQL Database whilst "
                      + "updating stream status", sqlException);
                  JsonElement jsonElement
                      = gson.toJsonTree(StreamingStatusUpdateResult.FAILED_ACCESSING_DATABASE);
                  dos.writeUTF(gson.toJson(jsonElement));
                }
                break;

              default:
                writeString(received);
                log.info("Received String: " + received);
                break;
            }


          }
          received = null;
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    /* Removing live sessions and live tutor status from database */
    try {
      if (sqlConnection.isSessionLive(currentSessionID)) {
        // TODO Close the session and kick all users
        log.info("Ending live session: " + currentSessionID);
        sqlConnection.endLiveSession(currentSessionID, currentUserID);
      }
    } catch (SQLException sqlException) {
      log.warn("Error accessing MySQL Database on final cleanup", sqlException);
    }

    //TODO make this work
    synchronized (activeWhiteboardSessions) {
      for (WhiteboardHandler activeSession : activeWhiteboardSessions) {
        // Check is session user is in active session.
        for (Integer userID : activeSession.getSessionUsers()) {
          if (token == userID) {
            activeSession.removeUser(token);
          }
        }
      }
    }

    if (loggedIn) {
      logOff();
    }

    // Perform cleanup on client disconnect
    mainServer.getAllClients().remove(token, this);
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

      // Reject multiple logins from one user
      if (mainServer.getLoggedInClients().putIfAbsent(userID, this) != null) {
        log.warn("User ID " + userID + " tried to log in twice; sending error");
        dos.writeUTF(ServerTools.packageClass(account));
        // TODO New Enum FAILED_BY_USER_ALREADY_LOGGED_IN
        JsonElement jsonElement = gson.toJsonTree(AccountLoginResult.FAILED_BY_UNEXPECTED_ERROR);
        dos.writeUTF(gson.toJson(jsonElement));
        return;
      }

      currentSessionID = userID;

      log.info("Added this ClientHandler to loggedInClients. Currently logged in users: "
          + mainServer.getLoggedInClients().size());

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
      loggedIn = true;
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

  /**
   * Perform all cleanup required when logging off a user.
   */
  public void logOff() {
    session.cleanUp();
    mainServer.getLoggedInClients().remove(currentUserID, this);
    this.loggedIn = false;
    this.currentUserID = -1;
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

  public boolean isLoggedIn() {
    return loggedIn;
  }

  public Session getSession() {
    return session;
  }

  public DataInputStream getDataInputStream() {
    return dis;
  }

  public DataOutputStream getDataOutputStream() {
    return dos;
  }

  public MainServer getMainServer() {
    return mainServer;
  }

}
