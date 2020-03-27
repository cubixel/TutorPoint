import static services.ServerTools.getSubjectService;
import static services.ServerTools.sendFileService;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import model.Account;
import services.ServerTools;
import services.enums.AccountLoginResult;
import services.enums.AccountRegisterResult;
import services.enums.AccountUpdateResult;
import services.enums.FileDownloadResult;
import sql.MySql;

public class ClientHandler extends Thread {

  private int token;
  private final DataInputStream dis;
  private final DataOutputStream dos;
  private MySql sqlConnection;
  private long lastHeartbeat;
  private boolean loggedIn;
  private ArrayList<WhiteboardHandler> activeSessions;

  /**
   * CLASS DESCRIPTION.
   * #################
   *
   * @author CUBIXEL
   *
   */
  public ClientHandler(DataInputStream dis, DataOutputStream dos, int token, MySql sqlConnection) {
    setDaemon(true);
    this.dis = dis;
    this.dos = dos;
    this.token = token;
    this.sqlConnection = sqlConnection;
    this.lastHeartbeat = System.currentTimeMillis();
    this.loggedIn = true;
    activeSessions = new ArrayList<WhiteboardHandler>();
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

      // when client disconnects then close it down.

      try {

        while (dis.available() > 0) {
          received = dis.readUTF();
        }
        if (received != null) {
          try {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(received, JsonObject.class);
            String action = jsonObject.get("Class").getAsString();
            System.out.println("Requested: " + action);



            if (action.equals("Account")) {
              if (jsonObject.get("isRegister").getAsInt() == 1) {
                createNewUser(jsonObject.get("username").getAsString(),
                    jsonObject.get("emailAddress").getAsString(),
                    jsonObject.get("hashedpw").getAsString(),
                    jsonObject.get("tutorStatus").getAsInt());
              } else {
                loginUser(jsonObject.get("username").getAsString(),
                    jsonObject.get("hashedpw").getAsString());
              }


              // This is the logic for returning a requested file.
            } else if (action.equals("FileRequest")) {
              try {
                sendFileService(dos, new File(jsonObject.get("filePath").getAsString()));
                JsonElement jsonElement = gson.toJsonTree(FileDownloadResult.SUCCESS);
                dos.writeUTF(gson.toJson(jsonElement));
              } catch (IOException e) {
                JsonElement jsonElement =
                    gson.toJsonTree(FileDownloadResult.FAILED_BY_FILE_NOT_FOUND);
                dos.writeUTF(gson.toJson(jsonElement));
              }


            } else if (action.equals("SubjectRequest")) {
              try {
                getSubjectService(dos, sqlConnection, jsonObject.get("id").getAsInt());
              } catch (SQLException e) {
                e.printStackTrace();
              }


            } else if (action.equals("AccountUpdate")) {
              try {
                System.out.println(jsonObject.get("usernameUpdate").getAsString());
                updateUserDetails(jsonObject.get("username").getAsString(),
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
              for (WhiteboardHandler activeSession : activeSessions) {
                if (sessionID.equals(activeSession.getSessionID())) {
                  // If a match is found, send package to that session.
                  //TODO - Unable to get whiteboardSession class reference here.
                  //Gson sessionPackage = new Gson().fromJson(jsonObject, WhiteboardSession.class);
                  return;
                }
              }
              // If no matches with active sessions, create a new session.
              String tutorID = jsonObject.get("tutorID").getAsString();
              WhiteboardHandler newSession = new WhiteboardHandler(sessionID, tutorID);

              // Add to active sessions.
              activeSessions.add(newSession);
              System.out.println("New sessionID: " + sessionID + " with tutorID: " + tutorID);
            }



          } catch (JsonSyntaxException e) {
            if (received.equals("Heartbeat")) {
              lastHeartbeat = System.currentTimeMillis();
              System.out.println("Received Heartbeat from client "
                  + token + " at " + lastHeartbeat);



            } else {
              System.out.println("Received string: " + received);
              writeString(received);
            }



          }
          received = null;
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    System.out.println("Client " + token + " disconnected");
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
    if (!sqlConnection.checkUserDetails(username, password)) {
      JsonElement jsonElement = gson.toJsonTree(AccountLoginResult.FAILED_BY_CREDENTIALS);
      dos.writeUTF(gson.toJson(jsonElement));
      System.out.println(gson.toJson(jsonElement));
    } else {
      String emailAddress = sqlConnection.getEmailAddress(username);
      int tutorStatus = sqlConnection.getTutorStatus(username);
      Account account = new Account(username, emailAddress, password, tutorStatus, 0);
      dos.writeUTF(ServerTools.packageClass(account));
      JsonElement jsonElement = gson.toJsonTree(AccountLoginResult.SUCCESS);
      dos.writeUTF(gson.toJson(jsonElement));
      System.out.println(gson.toJson(jsonElement));
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
        } else {
          JsonElement jsonElement = gson.toJsonTree(AccountRegisterResult.FAILED_BY_UNEXPECTED_ERROR);
          dos.writeUTF(gson.toJson(jsonElement));
        }
      } else {
        System.out.println("Email Taken");
        JsonElement jsonElement = gson.toJsonTree(AccountRegisterResult.FAILED_BY_EMAIL_TAKEN);
        dos.writeUTF(gson.toJson(jsonElement));
      }
    } else {
      System.out.println("Username Taken");
      JsonElement jsonElement = gson.toJsonTree(AccountRegisterResult.FAILED_BY_USERNAME_TAKEN);
      dos.writeUTF(gson.toJson(jsonElement));
    }
  }

  private void updateUserDetails(String username, String password, String usernameUpdate,
      String emailAddressUpdate, String hashedpwUpdate, int tutorStatusUpdate) throws IOException {
    Gson gson = new Gson();
    if (sqlConnection.checkUserDetails(username, password)) {
      if (!sqlConnection.usernameExists(usernameUpdate)) {
        if (!sqlConnection.emailExists(emailAddressUpdate)) {
          sqlConnection.updateDetails(username, usernameUpdate, emailAddressUpdate,
              hashedpwUpdate, tutorStatusUpdate);
          JsonElement jsonElement = gson.toJsonTree(AccountUpdateResult.SUCCESS);
          dos.writeUTF(gson.toJson(jsonElement));
        } else {
          System.out.println("Email Taken");
          JsonElement jsonElement = gson.toJsonTree(AccountUpdateResult.FAILED_BY_EMAIL_TAKEN);
          dos.writeUTF(gson.toJson(jsonElement));
        }
      } else {
        System.out.println("Username Taken");
        JsonElement jsonElement = gson.toJsonTree(AccountUpdateResult.FAILED_BY_USERNAME_TAKEN);
        dos.writeUTF(gson.toJson(jsonElement));
      }
    } else {
      JsonElement jsonElement = gson.toJsonTree(AccountLoginResult.FAILED_BY_CREDENTIALS);
      dos.writeUTF(gson.toJson(jsonElement));
      System.out.println(gson.toJson(jsonElement));
    }
  }


  public String toString() {
    return "This is client " + token;
  }

  public void logOff() {
    this.loggedIn = false;
  }
}
