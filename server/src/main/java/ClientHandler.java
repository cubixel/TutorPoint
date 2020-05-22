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
import services.enums.RatingUpdateResult;
import services.enums.SessionRequestResult;
import services.enums.StreamingStatusUpdateResult;
import services.enums.SubjectRequestResult;
import services.enums.TextChatMessageResult;
import services.enums.TutorRequestResult;
import services.enums.WhiteboardRenderResult;
import sql.MySql;

/**
 * The ClientHandler is generated on a per user bases by the MainServer, it
 * handles all request made from a specific Client to the server. The
 * ClientHandler is on it's own thread running a continuous while loop waiting
 * for request from the Client and then dealing with that request before sending
 * a response back to the Client.
 *
 * <p>The Client handler interacts in some way with all other classes
 * on the server side. The while loop listens for strings on the
 * DataInputStream and based on the contents of that string makes
 * calls to other classes and functions to deal with that string.
 *
 * <p>These received strings can either be standard {@code Strings} or
 * a request class packaged as a {@code JsonObject} that contains information
 * that is useful for the request.
 *
 * @author James Gardner
 * @author Che McKirgan
 * @author Daniel Bishop
 * @author Oliver Still
 * @author Oliver Clarke
 * @author Eric Walker
 *
 * @see MainServer
 * @see ClientNotifier
 * @see MySql
 * @see Session
 */
public class ClientHandler extends Thread {

  private final int token;
  private int currentUserID;
  private int currentSessionID;
  private Session session;
  private final DataInputStream dis;
  private final DataOutputStream dos;
  private final MySql sqlConnection;
  private long lastHeartbeat;
  private boolean loggedIn;
  private final MainServer mainServer;
  private ClientNotifier notifier;
  private boolean inSession = false;
  private final SessionFactory sessionFactory;
  

  private static final Logger log = LoggerFactory.getLogger("ClientHandler");

  /**
   * This is the Constructor for the ClientHandler. Each client handler requires
   * a DataInput/OutputStream to communicate with its client. A connection to the
   * MySQL Database and a reference to the MainServer. The ClientHandler is a
   * thread within the server module. {@code setDaemon(true)} is used to prevent
   * it blocking the JVM from finishing.
   *
   * @param dis
   *        The DataInputStream to receive request from the client
   *
   * @param dos
   *        The DataOutputStream to send responses and information to the client
   *
   * @param token
   *        A unique ID for this ClientHandler
   *
   * @param sqlConnection
   *        A connection to the MySQL Database
   *
   * @param mainServer
   *        A reference to the MainServer Class
   */
  public ClientHandler(DataInputStream dis, DataOutputStream dos, int token,
      MySql sqlConnection, MainServer mainServer) {
    setDaemon(true);
    setName("ClientHandler-" + token);
    this.dis = dis;
    this.dos = dos;
    this.token = token;
    this.sqlConnection = sqlConnection;
    this.lastHeartbeat = System.currentTimeMillis();
    this.loggedIn = false;
    this.mainServer = mainServer;
    this.sessionFactory = new SessionFactory();
  }

  /**
   * This is the Constructor for the ClientHandler used for Testing.
   *
   * @param dis
   *        The DataInputStream to receive request from the client
   *
   * @param dos
   *        The DataOutputStream to send responses and information to the client
   *
   * @param token
   *        A unique ID for this ClientHandler
   *
   * @param sqlConnection
   *        A connection to the MySQL Database
   *
   * @param mainServer
   *        A reference to the MainServer Class
   *
   * @param sessionFactory
   *        Used to build sessions, needed in test constructor so a Mockito session can be used
   */
  public ClientHandler(DataInputStream dis, DataOutputStream dos, int token,
      MySql sqlConnection, MainServer mainServer, SessionFactory sessionFactory, Session session,
      ClientNotifier clientNotifier) {
    setDaemon(true);
    setName("ClientHandler-" + token);
    this.dis = dis;
    this.dos = dos;
    this.token = token;
    this.sqlConnection = sqlConnection;
    this.lastHeartbeat = System.currentTimeMillis();
    this.loggedIn = false;
    this.mainServer = mainServer;
    this.sessionFactory = sessionFactory;
    this.session = session;
    this.notifier = clientNotifier;
  }

  /**
   * The ClientHandler Thread sits in a while loop listening
   * for requests from the client. It then handles those requests
   * and sends a response detailing the outcome of that request.
   */
  @Override
  public void run() {
    String received = null;
    Gson gson = new Gson();
    JsonElement jsonElement;

    while (lastHeartbeat > (System.currentTimeMillis() - 10000)) {
      /* Do stuff with this client in this thread
       * When client disconnects then close it down */
      try {

        if (dis.available() > 0) {
          received = dis.readUTF();
        }

        if (received != null) {

          try {
            JsonObject jsonObject = gson.fromJson(received, JsonObject.class);
            String action = jsonObject.get("Class").getAsString();
            log.info("Requested: " + action);

            // TODO: Does switch have a performance improvement in java?

            /* This switch case occurs if the object received was a JsonObject. It
             * now checks the Class of that JsonObject to see what the request involves. */
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
                notifier.sendTopTutors(sqlConnection,
                    jsonObject.get("numberOfTutorsRequested").getAsInt(),
                    jsonObject.get("userID").getAsInt());
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
                  log.error("Error writing to DataOutputStream", e);
                }
                break;

              case "SessionRequest":
                int userID = jsonObject.get("userID").getAsInt();
                int sessionID = jsonObject.get("sessionID").getAsInt();
                boolean isLeaving = jsonObject.get("leavingSession").getAsBoolean();
                boolean isHost = jsonObject.get("isHost").getAsBoolean();
                log.debug("userID: " + userID + " sessionID: " + sessionID
                          + " isLeaving: " + isLeaving + " isHost: " + isHost);

                if (isLeaving) {
                  // TODO use enum SessionRequestResult.END_SESSION_FAILED
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
                    log.info("Creating Session ID " + sessionID);
                    session = sessionFactory.createSession(sessionID, this);
                    if (session.setUp()) {
                      // TODO - Send Whiteboard/TextChat history to the client.
                      jsonElement = gson.toJsonTree(SessionRequestResult.SESSION_REQUEST_TRUE);
                    } else {
                      jsonElement = gson.toJsonTree(SessionRequestResult.FAILED_SESSION_SETUP);
                    }
                    dos.writeUTF(gson.toJson(jsonElement));
                  } else {
                    /* Checking that the Host of the sessionID requested to
                     * join is actually logged in */
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
                        jsonElement = gson.toJsonTree(
                            SessionRequestResult.FAILED_BY_TUTOR_NOT_LIVE);
                        dos.writeUTF(gson.toJson(jsonElement));
                      }
                    } else {
                      jsonElement = gson.toJsonTree(
                          SessionRequestResult.FAILED_BY_TUTOR_NOT_ONLINE);
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
                  } else {
                    jsonElement = gson.toJsonTree(WhiteboardRenderResult.FAILED_BY_CREDENTIALS);
                  }
                  dos.writeUTF(gson.toJson(jsonElement));
                }
                break;

              case "TextChatSession":
                if (jsonObject.get("sessionID").getAsInt() == currentSessionID) {
                  // Checking that if the Host is logged in that their session is set to Live
                  if (mainServer.getLoggedInClients().get(currentSessionID).getSession().isLive()) {
                    mainServer.getLoggedInClients().get(currentSessionID)
                        .getSession().getTextChatHandler().addToQueue(jsonObject);
                    jsonElement = gson.toJsonTree(TextChatMessageResult.TEXT_CHAT_MESSAGE_SUCCESS);
                  } else {
                    jsonElement = gson.toJsonTree(
                        TextChatMessageResult.FAILED_BY_INCORRECT_USER_ID);
                  }
                  dos.writeUTF(gson.toJson(jsonElement));
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
                    sqlConnection.addToFollowedTutors(currentUserID,
                        jsonObject.get("tutorID").getAsInt());
                  } else {
                    sqlConnection.removeFromFollowedTutors(currentUserID,
                        jsonObject.get("tutorID").getAsInt());
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
                    sqlConnection.addSubjectToFavourites(jsonObject.get("subjectID").getAsInt(),
                        currentUserID);
                  } else {
                    sqlConnection.removeFromFavouriteSubjects(currentUserID,
                        jsonObject.get("subjectID").getAsInt());
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
                      session.kickAll();
                      session.setLive(false);
                    } else {
                      sqlConnection.startLiveSession(currentSessionID, currentUserID);
                      session.setLive(true);
                    }
                    jsonElement = gson.toJsonTree(
                        StreamingStatusUpdateResult.STATUS_UPDATE_SUCCESS);
                    dos.writeUTF(gson.toJson(jsonElement));
                  } catch (SQLException sqlException) {
                    log.warn("Error accessing MySQL Database whilst "
                        + "updating stream status", sqlException);
                    jsonElement = gson.toJsonTree(
                        StreamingStatusUpdateResult.FAILED_ACCESSING_DATABASE);
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
            /* If the received string wasn't a JsonObject then check other values of String. */
            switch (received) {
              case "Heartbeat":
                lastHeartbeat = System.currentTimeMillis();
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
                  String path = "src" + File.separator + "main"
                      + File.separator + "resources" + File.separator + "uploaded"
                      + File.separator + "profilePictures" + File.separator;

                  String fileName = dis.readUTF();

                  String newFileName = "user" + currentUserID
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
                /* Used for pinging the ClientHandler from the client */
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
   * Used to write a String to the DataOutputStream.
   *
   * @param msg
   *        The String to be sent
   */
  public void writeString(String msg) {
    try {
      dos.writeUTF(msg);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Performs all the checks needed to successfully log in a user via
   * the MySQL Database. If the Login is successful then it sends
   * the user information as a packaged Account class back to the client.
   * If it is not successful a packaged enum AccountLoginResult will
   * explain the error to the Client.
   *
   * @param username
   *        The username String provided by the User upon login on the client
   *
   * @param password
   *        The hashed password provided by the user to be checked on the database
   *
   * @throws IOException
   *         Thrown if there is an error writing to the DataOutputStream
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
      int userID;
      try {
        userID = sqlConnection.getUserID(username);
        String emailAddress = sqlConnection.getEmailAddress(userID);
        int tutorStatus = sqlConnection.getTutorStatus(userID);
        account.setUserID(userID);
        account.setEmailAddress(emailAddress);
        account.setTutorStatus(tutorStatus);
      } catch (SQLException sqlException) {
        log.warn("Failed to access database.");
        dos.writeUTF(packageClass(account));
        JsonElement jsonElement = gson.toJsonTree(AccountLoginResult.FAILED_BY_CREDENTIALS);
        dos.writeUTF(gson.toJson(jsonElement));
        return;
      }
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
   * Used to create a new user on the MySQL Database. Performs all the
   * checks that the details do not clash with another currently registered
   * user and then returns the success state of the operation to the client
   * via a packaged AccountRegisterResult enum.
   *
   * @param username
   *        The unique username used to identify the account
   *
   * @param email
   *        The unique email used to identify the account
   *
   * @param password
   *        A sha3_256Hex encrypted password
   *
   * @param isTutor
   *        Integer of tutorStatus 1 = true, 0 = false
   *
   * @throws IOException
   *         Thrown if error writing to DataOutputStream
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

  /**
   * Used to update a users details on the MySQL Database. Performs all the
   * checks that the details do not clash with another currently registered
   * user and then returns the success state of the update to the client
   * via a packaged AccountUpdateResult enum.
   *
   * @param userID
   *        A userID that is assigned to a user upon account creation
   *
   * @param username
   *        The current username associated with the account
   *
   * @param password
   *        The current password associated with the account
   *
   * @param usernameUpdate
   *        A new username, provide the String "null" if no update
   *
   * @param emailAddressUpdate
   *        A new email, provide the String "null" if no update
   *
   * @param hashedpwUpdate
   *        A new password, provide the String "null" if no update
   *
   * @param tutorStatusUpdate
   *        A new tutor status, provide -1 if no update
   *
   * @throws IOException
   *         Thrown if error writing to DataOutputStream
   */
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

  /**
   * Updates a users rating of a tutor account.
   *
   * @param rating
   *        The rating the user has provided
   *
   * @param userID
   *        A userID that is assigned to the user making the rating
   *
   * @param tutorID
   *        A userID that is assigned to the tutor upon account creation
   */
  private void updateRating(int rating, int userID, int tutorID) {
    Gson gson = new Gson();
    try {
      try {
        sqlConnection.addTutorRating(tutorID, userID, rating);
        JsonElement jsonElement = gson.toJsonTree(RatingUpdateResult.RATING_UPDATE_SUCCESS);
        dos.writeUTF(gson.toJson(jsonElement));
        log.info("UpdateRating: Updated new rating for Tutor " + tutorID
            + " by User " + userID);
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
   * Performs the necessary steps to correctly logoff a user.
   */
  private void logOff() {
    // Clean up a hosted session
    if (session != null) {
      session.cleanUp();
      session.setLive(false);
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
   * Returns a JSON formatted string containing the properties of a given class as
   * well as the name of the class.
   *
   * @param obj
   *        The object to be packaged as a Json
   *
   * @return {@code JsonElement} version of the object sent in
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

  public Session getSession() {
    return session;
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

  public void setInSession(boolean inSession) {
    this.inSession = inSession;
  }

  public boolean isInSession() {
    return inSession;
  }
}
