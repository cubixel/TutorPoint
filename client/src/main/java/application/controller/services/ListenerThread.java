package application.controller.services;

import application.controller.HomeWindowController;
import application.controller.PresentationWindowController;
import application.controller.SubscriptionsWindowController;
import application.controller.TutorWindowController;
import application.model.Subject;
import application.model.Tutor;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ListenerThread extends Thread {

  private WhiteboardService whiteboardService;
  private TextChatService textChatService;
  private ArrayList<PresentationWindowController> presentationWindowControllers;
  private HomeWindowController homeWindowController;
  private TutorWindowController tutorWindowController;
  private SubscriptionsWindowController subscriptionsWindowController;
  private String targetAddress;
  private int targetPort;
  private Socket newSock;
  private DataInputStream listenIn;
  private DataOutputStream listenOut;
  private int expectedPresentationControllers = 1;
  

  private static final Logger log = LoggerFactory.getLogger("Listener");

  /**
   * Thread to listen for updates from server.
   */
  public ListenerThread(String address, int port, int token) throws IOException {
    setDaemon(true);
    setName("ListenerThread");
    this.targetAddress = address;
    this.targetPort = port;
    this.presentationWindowControllers = new ArrayList<PresentationWindowController>();

    /* try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      log.error("Interrupted by something? (Not meant to be...)");
    } */

    newSock = new Socket(targetAddress, targetPort);
    listenIn = new DataInputStream(newSock.getInputStream());
    listenOut = new DataOutputStream(newSock.getOutputStream());
    listenOut.writeInt(token);
    log.info("Successfully registered data connection with token " + listenIn.readInt());
  }

  public void setWhiteboardService(WhiteboardService service) {
    this.whiteboardService = service;
  }

  public void setTextChatService(TextChatService service) {
    this.textChatService = service;
  }

  /**
   * sets PresentationWindowController.
   * @param presentationWindowController the presentationWindowController to set
   */
  public void addPresentationWindowController(
      PresentationWindowController presentationWindowController) {
    this.presentationWindowControllers.add(presentationWindowController);
    log.info("There are now " + presentationWindowControllers.size()
        + " presentation controllers registered");
  }

  public void addHomeWindowController(HomeWindowController homeWindowController) {
    this.homeWindowController = homeWindowController;
  }

  public void addTutorWindowController(TutorWindowController tutorWindowController) {
    this.tutorWindowController = tutorWindowController;
  }

  public void addSubscriptionsWindowController(SubscriptionsWindowController subscriptionsWindowController) {
    this.subscriptionsWindowController = subscriptionsWindowController;
  }

  public void clearPresentationWindowControllers() {
    this.presentationWindowControllers.removeAll(presentationWindowControllers);
  }

  public ArrayList<PresentationWindowController> getPresentationWindowControllers() {
    return presentationWindowControllers;
  }

  /**
   * Check if a controller is registered.
   */
  public boolean hasCorrectPresentationWindowControllers() {
    if (presentationWindowControllers.size() == expectedPresentationControllers) {
      return true;
    }
    return false;
  }

  @Override
  public void run() {
    String received = null;
    while (true) {
      try {

        //noinspection StatementWithEmptyBody
        while (listenIn.available() == 0) {
        }
        received = listenIn.readUTF();
        // log.info(received);

        if (received != null) {
          try {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(received, JsonObject.class);
            String action = jsonObject.get("Class").getAsString();
            // log.info("Requested: " + action);

            // Code for different actions goes here
            // (use the 'if (action.equals("ActionName"))' setup from ClientHandler)

            if ((action.equals("WhiteboardSession")) && (whiteboardService != null)) {
              whiteboardService.updateWhiteboardSession(jsonObject);
            } else if ((action.equals("ArrayList")) && (whiteboardService != null)) {
              int index = jsonObject.get("Index").getAsInt();

              // If existing session, write all changes to canvas.
              for (int i = 0; i < index; i++) {
                JsonObject sessionUpdate = jsonObject.get("WhiteboardSession" + i)
                    .getAsJsonObject();
                whiteboardService.updateWhiteboardSession(sessionUpdate);
              }
            } else if (action.equals("SubjectHomeWindowResponse")) {
              Subject subject = new Subject(jsonObject.get("id").getAsInt(),
                  jsonObject.get("name").getAsString(), jsonObject.get("category").getAsString(),
                  jsonObject.get("isFollowed").getAsBoolean());
              Platform.runLater(() -> homeWindowController.addSubjectLink(subject));

            } else if (action.equals("SubjectSubscriptionsWindowResponse")) {
              Subject subject = new Subject(jsonObject.get("id").getAsInt(),
                  jsonObject.get("name").getAsString(), jsonObject.get("category").getAsString(),
                  jsonObject.get("isFollowed").getAsBoolean());

              String likedSubject = jsonObject.get("originalSubject").getAsString();
              Platform.runLater(() -> subscriptionsWindowController.addSubjectLink(subject,
                  likedSubject));

            } else if (action.equals("TopTutorHomeWindowResponse")) {
              Tutor tutor = new Tutor(jsonObject.get("tutorName").getAsString(),
                  jsonObject.get("tutorID").getAsInt(), jsonObject.get("rating").getAsFloat(),
                  jsonObject.get("isFollowed").getAsBoolean());
              Platform.runLater(() -> homeWindowController.addTutorLink(tutor));

            } else if (action.equals("LiveTutorHomeWindowUpdate")) {
              String path = "server" + File.separator + "src" + File.separator + "main"
                  + File.separator + "resources" + File.separator + "uploaded"
                  + File.separator + "profilePictures" + File.separator;

              Tutor tutor = new Tutor(jsonObject.get("tutorName").getAsString(),
                  jsonObject.get("tutorID").getAsInt(), jsonObject.get("rating").getAsFloat(),
                  true);

              tutor.setLive(jsonObject.get("isLive").getAsBoolean());

              try {
                FileInputStream input = new FileInputStream(path + "user"
                    + jsonObject.get("tutorID").getAsInt() + "profilePicture.png");
                // create a image
                Image profileImage = new Image(input);
                tutor.setProfilePicture(profileImage);
              } catch (FileNotFoundException fnfe) {
                log.warn("Tutor " + jsonObject.get("tutorName").getAsString()
                    + " has no profile picture");
              }

              Platform.runLater(() -> homeWindowController.addLiveTutorLink(tutor));
            } else if (action.equals("PresentationChangeSlideRequest")) {
              presentationWindowControllers.forEach((controller) -> {
                controller.setSlideNum(jsonObject.get("slideNum").getAsInt());
              });
            }

            // If text chat session recieved, get text chat object and call update client service.
            if ((action.equals("TextChatSession")) && (textChatService != null)) {
              textChatService.updateTextChatSession(jsonObject);

            }


            // End action code

          } catch (JsonSyntaxException e) {
            if (received.equals("SendingPresentation")) {
              // log.info("Listening for file");
              File presentation = listenForFile(
                  "client/src/main/resources/application/media/downloads/");
              int slideNum = Integer.parseInt(listenIn.readUTF());
              //TODO Do this properly


              // while (!hasCorrectPresentationWindowControllers()) {}
              while (presentationWindowControllers.size() == 0) {}


              log.info("Starting presentation at slide " + slideNum);
              
              presentationWindowControllers.forEach((controller) -> {
                controller.displayFile(presentation, slideNum);
              });

              log.info("Finished displaying file");

            } else {
              log.error("Received String: " + received);
            }
          }
          received = null;
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Send a file using the client->server side of the second connection.
   * 
   * @param file The file to send
   * @throws IOException Communication Error occured
   */
  public void sendFile(File file) throws IOException {
    final Logger log = LoggerFactory.getLogger("SendFileLogger");

    byte[] byteArray = new byte[(int) file.length()];

    FileInputStream fis = new FileInputStream(file);
    BufferedInputStream bis = new BufferedInputStream(fis);
    DataInputStream dis = new DataInputStream(bis);

    dis.readFully(byteArray, 0, byteArray.length);
    log.info("Sending filename '" + file.getName() + "' of size " + byteArray.length);
    listenOut.writeUTF(file.getName());
    listenOut.writeLong(byteArray.length);
    listenOut.write(byteArray, 0, byteArray.length);
    listenOut.flush();
    dis.close();
  }

  /**
   * METHOD DESCRIPTION.
   */
  public File listenForFile(String filePath) throws IOException {

    // TODO handle the exceptions better as it just throws a generic IOException.
    int bytesRead;

    String fileName = listenIn.readUTF();
    long size = listenIn.readLong();
    log.info("Listening for file named '" + fileName + "' of size " + size);
    File tempFile = new File(filePath);
    tempFile.mkdirs();
    OutputStream output =
        new FileOutputStream(filePath + "currentPresentation.xml");
    byte[] buffer = new byte[1024];
    while (size > 0
        && (bytesRead = listenIn.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
      output.write(buffer, 0, bytesRead);
      size -= bytesRead;
    }

    log.info("Finished recieving file");

    output.close();

    return new File(filePath + "currentPresentation.xml");
  }


}