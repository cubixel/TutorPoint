/*
 * MainServer.java
 * Version: 0.1.0
 * Company: CUBIXEL
 *
 * */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sql.MySql;
import sql.MySqlFactory;

/**
 * CLASS DESCRIPTION:
 * #################
 * MainServer is the top level server class. It runs on a separate
 * thread to Main........
 *
 * @author CUBIXEL
 *
 */
public class MainServer extends Thread {

  private ServerSocket serverSocket = null;
  private Socket socket = null;
  private String databaseName;

  private DataInputStream dis = null;
  private DataOutputStream dos = null;

  private int clientToken = 0;

  private ArrayList<WhiteboardHandler> activeSessions;
  private Vector<ClientHandler> activeClients;

  private MySqlFactory mySqlFactory;
  private MySql sqlConnection;

  private static final Logger log = LoggerFactory.getLogger(MainServer.class);

  /**
   * Constructor that creates a serverSocket on a specific
   * Port Number.
   *
   * @param port Port Number.
   */
  public MainServer(int port) throws IOException {
    databaseName = "tutorpointnew";
    mySqlFactory = new MySqlFactory(databaseName, log);
    activeClients = new Vector<>();

    //This should probably be synchronized
    activeSessions = new ArrayList<>();

    serverSocket = new ServerSocket(port);
  }

  /**
   * CONSTRUCTOR DESCRIPTION.
   *
   * @param port          DESCRIPTION
   * @param databaseName  DESCRIPTION
   */
  public MainServer(int port, String databaseName) {
    this.databaseName = databaseName;
    mySqlFactory = new MySqlFactory(databaseName, log);
    activeClients = new Vector<>();
    //This should probably be synchronized
    activeSessions = new ArrayList<>();

    try {
      serverSocket = new ServerSocket(port);
      //serverSocket.setSoTimeout(2000);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * CONSTRUCTOR DESCRIPTION.
   *
   * @param port          DESCRIPTION
   * @param mySqlFactory  DESCRIPTION
   * @param databaseName  DESCRIPTION
   */
  public MainServer(int port, MySqlFactory mySqlFactory, String databaseName)  {
    this.databaseName = databaseName;
    this.mySqlFactory = mySqlFactory;
    activeClients = new Vector<>();
    //This should probably be synchronized
    activeSessions = new ArrayList<>();

    try {
      serverSocket = new ServerSocket(port);
      //serverSocket.setSoTimeout(2000);
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  @Override
  public void run() {
    /* Main server should sit in this loop waiting for clients */
    while (true) {
      try {
        socket = serverSocket.accept();

        log.info("New Client Accepted: Token " + clientToken);

        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());

        sqlConnection = mySqlFactory.createConnection();

        ClientHandler ch = new ClientHandler(dis, dos, clientToken, sqlConnection, log, activeSessions);

        Thread t = new Thread(ch);

        activeClients.add(ch);

        t.start();

        clientToken++;

      } catch (IOException e) {
        log.error("Failed to create DataInput/OutputStreams", e);
      } catch (SQLException e) {
        log.error("Failed to connect to MySQL database", e);
      }
    }
  }

  /**
   * METHOD DESCRIPTION.
   */
  public ClientHandler getClientHandler() {
    /*
     * ###################################################
     * Would you ever need to select from other clients
     * This is just client 0 atm.
     * ###################################################
     */
    return this.activeClients.get(0);
  }

  public Vector<ClientHandler> getActiveClients() {
    return activeClients;
  }


  public boolean isSocketClosed() {
    return this.serverSocket.isClosed();
  }

  /* Getter method for binding state of serverSocket.
    * Returns true if the ServerSocket is successfully
    * bound to an address.
    * */
  public boolean isBound() {
    return serverSocket.isBound();
  }

  public static void main(String[] args) {
    MainServer main = null;
    try {
      main = new MainServer(5000);
      main.start();
      log.info("Server started successfully");
    } catch (IOException e) {
      log.error("Could not start the server", e);
    }

  }
}
