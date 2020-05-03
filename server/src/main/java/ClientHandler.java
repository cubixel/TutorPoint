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
import model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ClientNotifier;
import services.enums.AccountLoginResult;
import services.enums.AccountRegisterResult;
import services.enums.AccountUpdateResult;
import services.enums.FileUploadResult;
import services.enums.FollowSubjectResult;
import services.enums.FollowTutorResult;
import services.enums.LiveTutorRequestResult;
import services.enums.RatingUpdateResult;
import services.enums.SessionRequestResult;
import services.enums.StreamingStatusUpdateResult;
import services.enums.SubjectRequestResult;
import services.enums.TutorRequestResult;
import services.enums.TextChatMessageResult;
import services.enums.WhiteboardRenderResult;
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
  private ClientNotifier notifier;
  private boolean inSession = false;
  

  private static final Logger log = LoggerFactory.getLogger("ClientHandler");

  /**
   * CLASS DESCRIPTION.
   * #################
   *
   * @author CUBIXEL
   *
   */
  public ClientHandler(DataInputStream dis, DataOutputStream dos, int token, MySql sqlConnection, MainServer mainServer) {
    setDaemon(true);
    setName("ClientHandler-" + token);
    this.dis = dis;
    this.dos = dos;
    this.token = token;
    this.sqlConnection = sqlConnection;
    this.lastHeartbeat = System.currentTimeMillis();
    this.loggedIn = false;
    this.mainServer = mainServer;
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
    JsonElement jsonElement;

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

              case "SubjectRequestHome":
                jsonElement = gson.toJsonTree(SubjectRequestResult.SUBJECT_REQUEST_SUCCESS);
                dos.writeUTF(gson.toJson(jsonElement));
                notifier.sendSubjects(sqlConnection,
                    jsonObject.get("numberOfSubjectsRequested").getAsInt(),
                    null, jsonObject.get("userID").getAsInt(), "Home");
                break;

              case "SubjectRequestSubscription":
                jsonElement = gson.toJsonTree(SubjectRequestResult.SUBJECT_REQUEST_SUCCESS);
                dos.writeUTF(gson.toJson(jsonElement));
                notifier.sendSubjects(sqlConnection,
                    jsonObject.get("numberOfSubjectsRequested").getAsInt(),
                    jsonObject.get("subject").getAsString(), jsonObject.get("userID").getAsInt(),
                    "Subscriptions");
                break;

              case "TopTutorsRequest":
                jsonElement = gson.toJsonTree(TutorRequestResult.TUTOR_REQUEST_SUCCESS);
                dos.writeUTF(gson.toJson(jsonElement));
                notifier.sendTopTutors(sqlConnection, jsonObject.get("numberOfTutorsRequested").getAsInt(),
                    jsonObject.get("userID").getAsInt());
                break;

              case "LiveTutorsRequest":
                jsonElement = gson.toJsonTree(LiveTutorRequestResult.LIVE_TUTOR_REQUEST_SUCCESS);
                dos.writeUTF(gson.toJson(jsonElement));
                notifier.sendLiveTutors(sqlConnection, currentUserID);
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
                int userID = jsonObject.get("userID").getAsInt();
                int sessionID = jsonObject.get("sessionID").getAsInt();
                boolean isLeaving = jsonObject.get("leavingSession").getAsBoolean();
                boolean isHost = jsonObject.get("isHost").getAsBoolean();
                log.debug("userID: " + userID + " sessionID: " + sessionID
                          + " isLeaving: " + isLeaving + " isHost: " + isHost);

                if (isLeaving) {
                  // use enum SessionRequestResult.END_SESSION_REQUEST_SUCCESS/FAILED
                  // TODO this should only arrive from a user not the tutor so just leave the hosts
                  //  session and send success or failed.
                  jsonElement = gson.toJsonTree(SessionRequestResult.END_SESSION_REQUEST_SUCCESS);
                  dos.writeUTF(gson.toJson(jsonElement));
                  session.stopWatching(userID, this);
                } else {
                  if (isHost) {
                    /* This is for the tutor/host to setup a session initially upon
                     * upon opening the stream window on the client side. */
                    currentSessionID = sessionID;
                    session = new Session(sessionID, this);
                    if (session.setUp()) {
                      // TODO - Send both the sessionID and the Whiteboard/TextChat history to the
                      //  client.
                      jsonElement = gson.toJsonTree(SessionRequestResult.SESSION_REQUEST_TRUE);
                      dos.writeUTF(gson.toJson(jsonElement));
                    } else {
                      jsonElement = gson.toJsonTree(SessionRequestResult.FAILED_SESSION_SETUP);
                      dos.writeUTF(gson.toJson(jsonElement));
                    }
                  } else {
                    // Checking that the Host of the sessionID requested to
                    // join is actually logged in
                    if (mainServer.getLoggedInClients().containsKey(sessionID)) {
                      // Checking that if the Host is logged in that their session is set to Live
                      if (mainServer.getLoggedInClients().get(sessionID).getSession()
                          .isLive()) {

                        jsonElement = gson.toJsonTree(SessionRequestResult.SESSION_REQUEST_TRUE);
                        dos.writeUTF(gson.toJson(jsonElement));

                        currentSessionID = sessionID;
                        mainServer.getLoggedInClients().get(sessionID).getSession()
                            .requestJoin(currentUserID);
                        inSession = true;
                        session = mainServer.getLoggedInClients().get(sessionID).getSession();

                        log.info("requested session to join: " + currentSessionID);
                        
                      } else {
                        jsonElement = gson.toJsonTree(SessionRequestResult.FAILED_BY_TUTOR_NOT_LIVE);
                        dos.writeUTF(gson.toJson(jsonElement));
                      }
                    } else {
                      jsonElement = gson.toJsonTree(SessionRequestResult.FAILED_BY_TUTOR_NOT_ONLINE);
                      dos.writeUTF(gson.toJson(jsonElement));
                    }
                  }
                }
                break;

              case "WhiteboardSession":
                if (jsonObject.get("sessionID").getAsInt() == currentSessionID) {
                  // Checking that if the Host is logged in that their session is set to Live
                  if (mainServer.getLoggedInClients().get(currentSessionID).getSession().isLive()) {
                    mainServer.getLoggedInClients().get(currentSessionID).getSession()
                        .getWhiteboardHandler()
                        .addToQueue(jsonObject);
                    jsonElement = gson.toJsonTree(WhiteboardRenderResult.WHITEBOARD_RENDER_SUCCESS);
                    dos.writeUTF(gson.toJson(jsonElement));
                  } else {
                    jsonElement = gson.toJsonTree(WhiteboardRenderResult.FAILED_BY_INCORRECT_STREAM_ID);
                    dos.writeUTF(gson.toJson(jsonElement));
                  }
                }
                break;

              case "TextChatSession":
                if (jsonObject.get("sessionID").getAsInt() == currentSessionID) {
                  // Checking that if the Host is logged in that their session is set to Live
                  if (mainServer.getLoggedInClients().get(currentSessionID).getSession().isLive()) {
                    mainServer.getLoggedInClients().get(currentSessionID).getSession().getTextChatHandler()
                        .addToQueue(jsonObject);
                    jsonElement = gson.toJsonTree(TextChatMessageResult.TEXT_CHAT_MESSAGE_SUCCESS);
                    dos.writeUTF(gson.toJson(jsonElement));
                  } else {
                    jsonElement = gson.toJsonTree(TextChatMessageResult.FAILED_BY_INCORRECT_USER_ID);
                    dos.writeUTF(gson.toJson(jsonElement));
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
                session.getPresentationHandler().setRequestSlideNum(presentationInt);
                session.getPresentationHandler().setRequestAction(presentationAction);
                break;

              case "FollowTutorRequest":
                boolean isFollowingTutor = jsonObject.get("isFollowing").getAsBoolean();
                try {
                  if (!isFollowingTutor) {
                    sqlConnection.addToFollowedTutors(currentUserID, jsonObject.get("tutorID").getAsInt());
                  } else {
                    sqlConnection.removeFromFollowedTutors(currentUserID, jsonObject.get("tutorID").getAsInt());
                  }
                  jsonElement = gson.toJsonTree(FollowTutorResult.FOLLOW_TUTOR_RESULT_SUCCESS);
                  dos.writeUTF(gson.toJson(jsonElement));
                } catch (SQLException sqlException) {
                  log.error("Error accessing database ", sqlException);
                  jsonElement = gson.toJsonTree(FollowTutorResult.FAILED_BY_DATABASE_ERROR);
                  dos.writeUTF(gson.toJson(jsonElement));
                }
                break;

              case "FollowSubjectRequest":
                boolean isFollowingSubject = jsonObject.get("isFollowing").getAsBoolean();
                try {
                  if (!isFollowingSubject) {
                    sqlConnection.addSubjectToFavourites(jsonObject.get("subjectID").getAsInt(), currentUserID);
                  } else {
                    sqlConnection.removeFromFavouriteSubjects(currentUserID, jsonObject.get("subjectID").getAsInt());
                  }
                  jsonElement = gson.toJsonTree(FollowSubjectResult.FOLLOW_SUBJECT_RESULT_SUCCESS);
                  dos.writeUTF(gson.toJson(jsonElement));
                } catch (SQLException sqlException) {
                  log.error("Error accessing database ", sqlException);
                  jsonElement = gson.toJsonTree(FollowSubjectResult.FAILED_BY_DATABASE_ERROR);
                  dos.writeUTF(gson.toJson(jsonElement));
                }
                break;

              case "UpdateStreamStatusRequest":
                boolean currentStatus = session.isLive();
                boolean newStatus = jsonObject.get("isLive").getAsBoolean();
                if (currentStatus == newStatus) {
                  jsonElement = gson.toJsonTree(StreamingStatusUpdateResult.STATUS_UPDATE_SUCCESS);
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
                    jsonElement = gson.toJsonTree(StreamingStatusUpdateResult.STATUS_UPDATE_SUCCESS);
                    dos.writeUTF(gson.toJson(jsonElement));
                  } catch (SQLException sqlException) {
                    log.warn("Error accessing MySQL Database whilst "
                        + "updating stream status", sqlException);
                    jsonElement = gson.toJsonTree(StreamingStatusUpdateResult.FAILED_ACCESSING_DATABASE);
                    dos.writeUTF(gson.toJson(jsonElement));
                  }
                }
                log.info("Current status is: " + ((session.isLive()) ? "Live" : "Not Live"));
                mainServer.updateLiveClientList();
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
                mainServer.updateLiveClientList();
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

                  jsonElement = gson.toJsonTree(FileUploadResult.FILE_UPLOAD_SUCCESS);
                  dos.writeUTF(gson.toJson(jsonElement));
                } catch (IOException ioe) {
                  log.error("Could not create local file ", ioe);
                  jsonElement = gson.toJsonTree(FileUploadResult.FAILED_BY_SERVER_ERROR);
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

    // Implicitly log off a user if timed out
    if (loggedIn) {
      logOff();
    }
    
    // Remove this handler from list of active handlers
    mainServer.getAllClients().remove(token, this);
    mainServer.updateLiveClientList();
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
      dos.writeUTF(packageClass(account));
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
        dos.writeUTF(packageClass(account));
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

      dos.writeUTF(packageClass(account));
      JsonElement jsonElement = gson.toJsonTree(AccountLoginResult.LOGIN_SUCCESS);
      dos.writeUTF(gson.toJson(jsonElement));
      log.info("Login User: " + username + " SUCCESSFUL");
      currentUserID = userID;
      loggedIn = true;
      postLoginClientUpdate();
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
    // Clean up a hosted session
    if (session != null) {
      session.cleanUp();
    }

    // Stop watching a joined session
    if (inSession) {
      if (mainServer.getLoggedInClients().containsKey(currentSessionID)) {
        mainServer.getLoggedInClients().get(currentSessionID).getSession()
            .stopWatching(currentUserID, this);
      }
      this.inSession = false;
    }

    // Remove from list of logged in users
    mainServer.getLoggedInClients().remove(currentUserID, this);
    this.loggedIn = false;
    this.currentUserID = -1;
  }

  /**
   * Returns a JSON formatted string containing the properties of a given class
   * as well as the name of the class.
   *
   * @param obj DESCRIPTION
   * @return    DESCRIPTION
   */
  public static String packageClass(Object obj) {
    Gson gson = new Gson();
    JsonElement jsonElement = gson.toJsonTree(obj);
    jsonElement.getAsJsonObject().addProperty("Class", obj.getClass().getSimpleName());
    return gson.toJson(jsonElement);
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

  public MySql getSqlConnection() {
    return sqlConnection;
  }

  public void postLoginClientUpdate() {
    notifier.sendLiveTutors(sqlConnection, currentUserID);
  }

}
