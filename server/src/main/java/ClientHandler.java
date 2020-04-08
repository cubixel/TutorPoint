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
import java.util.List;
import model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ServerTools;
import services.enums.AccountLoginResult;
import services.enums.AccountRegisterResult;
import services.enums.AccountUpdateResult;
import services.enums.FileDownloadResult;
import services.enums.RatingUpdateResult;
import services.enums.WhiteboardRenderResult;
import sql.MySql;

public class ClientHandler extends Thread {

  private int token;
  private int currentUserID;
  private final DataInputStream dis;
  private final DataOutputStream dos;
  private  DataInputStream dataIn;
  private  DataOutputStream dataOut;
  private MySql sqlConnection;
  private long lastHeartbeat;
  private boolean loggedIn;
  private ArrayList<WhiteboardHandler> activeSessions;
  private static final Logger log = LoggerFactory.getLogger("Server Logger");

  /**
   * CLASS DESCRIPTION.
   * #################
   *
   * @author CUBIXEL
   *
   */
  public ClientHandler(DataInputStream dis, DataOutputStream dos, int token, MySql sqlConnection,
      ArrayList<WhiteboardHandler> allActiveSessions) {

    setDaemon(true);
    this.dis = dis;
    this.dos = dos;
    this.token = token;
    this.sqlConnection = sqlConnection;
    this.lastHeartbeat = System.currentTimeMillis();
    this.loggedIn = true;
    this.activeSessions = allActiveSessions;
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



            if (action.equals("Account")) {
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
            } else if (action.equals("FileRequest")) {
              try {
                sendFileService(dos, new File(jsonObject.get("filePath").getAsString()));
                JsonElement jsonElement = gson.toJsonTree(FileDownloadResult.SUCCESS);
                dos.writeUTF(gson.toJson(jsonElement));
                log.info("File Sent Successfully");
              } catch (IOException e) {
                JsonElement jsonElement =
                    gson.toJsonTree(FileDownloadResult.FAILED_BY_FILE_NOT_FOUND);
                dos.writeUTF(gson.toJson(jsonElement));
                log.error("File: " + jsonObject.get("filePath").getAsString() + " Not Found");
              }


            } else if (action.equals("SubjectRequest")) {
              try {
                getSubjectService(dos, sqlConnection, jsonObject.get("id").getAsInt());
              } catch (SQLException e) {
                e.printStackTrace();
              }

            } else if (action.equals("TopTutorsRequest")) {
              try {
                getTopTutorsService(dos, sqlConnection, jsonObject.get("id").getAsInt());
              } catch (SQLException e) {
                e.printStackTrace();
              }


            } else if (action.equals("AccountUpdate")) {
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


            } else if (action.equals("WhiteboardSession")) {
              String sessionID = jsonObject.get("sessionID").getAsString();

              // Check if session package is for an existing active session by comparing sessionID.
              if (!activeSessions.isEmpty()) {
                for (WhiteboardHandler activeSession : activeSessions) {
                  System.out.println(activeSession.getSessionID());
                  if (sessionID.equals(activeSession.getSessionID())) {
                    for (String userID : activeSession.getSessionUsers()) {
                      if (userID.equals(jsonObject.get("userID").getAsString())) {
                        //TODO - Update whiteboard
                        activeSession.updateWhiteboard(jsonObject);
                      }
                      // If a match is found, send package to that session.
                      //TODO - Unable to get whiteboardSession class reference here.
                      //Gson sessionPackage = new Gson().fromJson(jsonObject,
                      //    WhiteboardSession.class);
                    }
                    //User is not in the active session and must be added
                    activeSession.addUser(jsonObject.get("userID").getAsString());
                    for (String user : activeSession.getSessionUsers()) {
                      System.out.println(user);
                    }
                  }
                }
                // If no matches with active sessions, create a new session.
                String tutorID = jsonObject.get("userID").getAsString();
                WhiteboardHandler newSession = new WhiteboardHandler(sessionID, tutorID);
                //Sends confirmation to client
                JsonElement jsonElement = gson.toJsonTree(WhiteboardRenderResult.SUCCESS);
                dos.writeUTF(gson.toJson(jsonElement));
                // Add to active sessions.
                activeSessions.add(newSession);
                System.out.println("New sessionID: " + sessionID + " with tutorID: " + tutorID);
              } else {
                // If no matches with active sessions, create a new session.
                String tutorID = jsonObject.get("userID").getAsString();
                WhiteboardHandler newSession = new WhiteboardHandler(sessionID, tutorID);

                // Add to active sessions.
                activeSessions.add(newSession);
                System.out.println("New sessionID: " + sessionID + " with tutorID: " + tutorID);
              }

            } else if (action.equals("RatingUpdate")) {
              log.info("ClientHandler: Received RatingUpdate from Client");
              updateRating(jsonObject.get("rating").getAsInt(),
                  jsonObject.get("userID").getAsInt(),
                  jsonObject.get("tutorID").getAsInt());
            }



          } catch (JsonSyntaxException e) {
            if (received.equals("Heartbeat")) {
              lastHeartbeat = System.currentTimeMillis();
              log.info("Received Heartbeat from Client "
                  + token + " at " + lastHeartbeat);

            } else if (received.equals("Logout")) {
              log.info("ClientHandler: Received logout request from Client");
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
      log.info("Login: FAILED_BY_CREDENTIALS");
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
        log.warn("ClientHandler: loginUser() No Followed Subjects");
      }

      dos.writeUTF(ServerTools.packageClass(account));
      JsonElement jsonElement = gson.toJsonTree(AccountLoginResult.LOGIN_SUCCESS);
      dos.writeUTF(gson.toJson(jsonElement));
      log.info("Login: SUCCESSFUL");
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
          JsonElement jsonElement = gson.toJsonTree(AccountRegisterResult.SUCCESS);
          dos.writeUTF(gson.toJson(jsonElement));
          log.info("Register New User: SUCCESSFUL");
        } else {
          log.error("Register New User: FAILED_BY_UNEXPECTED_ERROR");
          JsonElement jsonElement = gson.toJsonTree(AccountRegisterResult.FAILED_BY_UNEXPECTED_ERROR);
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

  private void updateUserDetails(int userID, String username, String password, String usernameUpdate,
      String emailAddressUpdate, String hashedpwUpdate, int tutorStatusUpdate) throws IOException {
    Gson gson = new Gson();
    if (sqlConnection.checkUserDetails(username, password)) {
      if (!sqlConnection.usernameExists(usernameUpdate)) {
        if (!sqlConnection.emailExists(emailAddressUpdate)) {
          sqlConnection.updateDetails(userID, usernameUpdate, emailAddressUpdate,
              hashedpwUpdate, tutorStatusUpdate);
          JsonElement jsonElement = gson.toJsonTree(AccountUpdateResult.SUCCESS);
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
          JsonElement jsonElement = gson.toJsonTree(RatingUpdateResult.SUCCESS);
          dos.writeUTF(gson.toJson(jsonElement));
          log.info("ClientHandler: updateRating() created new rating for Tutor " + tutorID
              + "by User " + userID);
        } else {
          sqlConnection.updateTutorRating(tutorID, userID, rating);
          JsonElement jsonElement = gson.toJsonTree(RatingUpdateResult.SUCCESS);
          dos.writeUTF(gson.toJson(jsonElement));
          log.info("ClientHandler: updateRating() update rating for Tutor " + tutorID
              + "by User " + userID);
        }
      } catch (SQLException e) {
        log.error("ClientHandler: updateRating() failed to access MySQL Database ", e);
        JsonElement jsonElement = gson.toJsonTree(RatingUpdateResult.FAILED_BY_DATABASE_ACCESS);
        dos.writeUTF(gson.toJson(jsonElement));
      }
    } catch (IOException ioe) {
      log.error("ClientHandler: updateRating() could not write to DataOutputStream ", ioe);
    }
  }


  public String toString() {
    return "This is client " + token;
  }

  public void logOff() {
    this.loggedIn = false;
  }

  public void addDataConnection(DataInputStream dataIn, DataOutputStream dataOut) {
    this.dataIn = dataIn;
    this.dataOut = dataOut;
  }
}
