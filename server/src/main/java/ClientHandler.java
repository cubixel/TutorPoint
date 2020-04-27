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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import services.enums.FileUploadResult;
import services.enums.RatingUpdateResult;
import services.enums.SessionRequestResult;
import services.enums.StreamingStatusUpdateResult;
import services.enums.TutorRequestResult;
import services.enums.TextChatMessageResult;
import services.enums.TextChatRequestResult;
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
  private ArrayList<TextChatHandler> allTextChatSessions;

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
      ArrayList<WhiteboardHandler> activeWhiteboardSessions, ArrayList<TextChatHandler> allTextChatSessions, MainServer mainServer) {
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
    this.allTextChatSessions = allTextChatSessions;
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
                // TODO UserID and SessionID are named like this until the whiteboard session
                //  request is refactored into the session class.
                int userID_Session = jsonObject.get("userID").getAsInt();
                int sessionID_Session = jsonObject.get("sessionID").getAsInt();
                boolean isLeaving = jsonObject.get("leavingSession").getAsBoolean();
                boolean isHost = jsonObject.get("isHost").getAsBoolean();
                log.debug("userID: " + userID_Session + " sessionID: " + sessionID_Session
                          + " isLeaving: " + isLeaving + " isHost: " + isHost);

                if (isLeaving) {
                  // use enum SessionRequestResult.END_SESSION_REQUEST_SUCCESS/FAILED
                  // TODO this should only arrive from a user not the tutor so just leave the hosts
                  //  session and send success or failed.
                  session.stopWatching(userID_Session, this);
                  JsonElement jsonElement
                      = gson.toJsonTree(SessionRequestResult.END_SESSION_REQUEST_SUCCESS);
                  dos.writeUTF(gson.toJson(jsonElement));
                } else {
                  if (isHost) {
                    /* This is for the tutor/host to setup a session initially upon
                     * upon opening the stream window on the client side. */
                    currentSessionID = sessionID_Session;
                    session = new Session(sessionID_Session, this);
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
                    // Checking that the Host of the sessionID requested to
                    // join is actually logged in
                    if (mainServer.getLoggedInClients().containsKey(sessionID_Session)) {
                      // Checking that if the Host is logged in that their session is set to Live
                      if (mainServer.getLoggedInClients().get(sessionID_Session).getSession()
                          .isLive()) {
                        currentSessionID = sessionID_Session;
                        mainServer.getLoggedInClients().get(sessionID_Session).getSession()
                            .getSessionUsers().put(currentUserID, this);
                      } else {
                        JsonElement jsonElement
                            = gson.toJsonTree(SessionRequestResult.FAILED_BY_TUTOR_NOT_LIVE);
                        dos.writeUTF(gson.toJson(jsonElement));
                      }
                    } else {
                      JsonElement jsonElement
                          = gson.toJsonTree(SessionRequestResult.FAILED_BY_TUTOR_NOT_ONLINE);
                      dos.writeUTF(gson.toJson(jsonElement));
                    }
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

              case "TextChatRequestSession":
                int sessionIDint = jsonObject.get("sessionID").getAsInt();

                // Check if session has been created or needs creating.
                sessionExists = false;
                for (TextChatHandler activeSession : allTextChatSessions) {
                  if (sessionIDint == activeSession.getSessionID()) {
                    sessionExists = true;

                    // If session exists, add user to that session.
                    String userID = jsonObject.get("userID").getAsString();
                    activeSession.addUser(this.token);
                    log.info("User " + userID + " Joined Session: " + sessionIDint);

                    // Respond with success.
                    JsonElement jsonElement
                        = gson.toJsonTree(TextChatRequestResult.SESSION_REQUEST_TRUE);
                    dos.writeUTF(gson.toJson(jsonElement));
                  }
                }
                // Else, create a new session from the session ID.
                if (!sessionExists) {
                  // Create new whiteboard handler.
                  int tutorID = jsonObject.get("userID").getAsInt();
                  TextChatHandler newSession = new TextChatHandler(sessionIDint, tutorID, token,
                      mainServer.getAllClients());
                  log.info("New text chat Session Created: " + sessionIDint);
                  log.info("User " + tutorID + " Joined Session: " + sessionIDint);
                  newSession.start();

                  // Add session to active session list.
                  allTextChatSessions.add(newSession);

                  // Respond with success.
                  JsonElement jsonElement
                      = gson.toJsonTree(TextChatRequestResult.SESSION_REQUEST_FALSE);
                  dos.writeUTF(gson.toJson(jsonElement));
                }
                break;

              case "TextChatSession":
                sessionID = jsonObject.get("sessionID").getAsString();
                for (TextChatHandler activeSession : allTextChatSessions) {
                  // Send session package to matching active session.
                  if (sessionID.equals(activeSession.getSessionID())) {
                    // Check is session user is in active session.
                    for (Integer userID : activeSession.getSessionUsers()) {
                      if (token == userID) {
                        // If a match is found, send package to that session.
                        activeSession.addToQueue(jsonObject);
                        JsonElement jsonElement
                            = gson.toJsonTree(TextChatMessageResult.TEXT_CHAT_MESSAGE_SUCCESS);
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

              case "UpdateStreamStatusRequest":
                boolean currentStatus = session.isLive();
                boolean newStatus = jsonObject.get("isLive").getAsBoolean();
                if (currentStatus == newStatus) {
                  JsonElement jsonElement
                      = gson.toJsonTree(StreamingStatusUpdateResult.STATUS_UPDATE_SUCCESS);
                  dos.writeUTF(gson.toJson(jsonElement));
                } else {
                  try {
                    if (!newStatus) {
                      sqlConnection.endLiveSession(currentSessionID, currentUserID);
                      session.setLive(false);
                      // TODO Remove people from the session
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
                }
                log.info("Current status is: " + ((session.isLive()) ? "Live" : "Not Live"));
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
                cleanUp();
                log.info("Logged off. There are now " + mainServer.getLoggedInClients().size()
                    + " logged in clients.");
                break;

              case "ProfileImage":
                log.info("Requested: ProfileImageUpdateRequest");
                try {
                  int bytesRead;
                  String path = "server" + File.separator + "src" + File.separator + "main"
                      + File.separator + "resources" + File.separator + "uploaded"
                      + File.separator + "profilePictures" + File.separator;

                  String fileName = dis.readUTF();

                  String newFileName = "user" + String.valueOf(currentUserID)
                      + "profilePicture.png";

                  File tempFile = new File(path + newFileName);
                  if (tempFile.delete()) {
                    log.info("Removed previous profile picture");
                  }

                  long size = dis.readLong();
                  log.info("Listening for file named '" + fileName + "' of size " + size);
                  OutputStream output = new FileOutputStream(path + newFileName);
                  byte[] buffer = new byte[1024];
                  while (size > 0 && (bytesRead = dis.read(buffer, 0,
                      (int) Math.min(buffer.length, size))) != -1) {
                    output.write(buffer, 0, bytesRead);
                    size -= bytesRead;
                  }

                  log.info("Profile picture updated");

                  output.close();

                  JsonElement jsonElement
                      = gson.toJsonTree(FileUploadResult.FILE_UPLOAD_SUCCESS);
                  dos.writeUTF(gson.toJson(jsonElement));
                } catch (IOException ioe) {
                  log.error("Could not create local file ", ioe);
                  JsonElement jsonElement
                      = gson.toJsonTree(FileUploadResult.FAILED_BY_SERVER_ERROR);
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

    // Perform cleanup on client disconnect
    cleanUp();
    mainServer.getAllClients().remove(token, this);
    log.info("Client " + token + " Disconnected");
  }

  /**
   * Perform any clean up necessary when a user logs out.
   */
  public void cleanUp() {
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
    if (session != null) {
      session.cleanUp();
    }
    mainServer.getLoggedInClients().get(currentSessionID).getSession()
        .stopWatching(currentUserID, this);
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
