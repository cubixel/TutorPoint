import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
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

  private DataServer dataServer;

  private ConcurrentHashMap<Integer, ClientHandler> allClients;
  private ConcurrentHashMap<Integer, ClientHandler> loggedInClients;

  private MySqlFactory mySqlFactory;
  private MySql sqlConnection;

  /* Logger prints to both the console and to a file 'logFile.log' saved
   * under resources/logs. All classes should create a Logger of their name. */
  private static final Logger log = LoggerFactory.getLogger("MainServer");

  /**
   * Constructor that creates a serverSocket on a specific
   * Port Number.
   *
   * @param port Port Number.
   */
  public MainServer(int port) throws IOException {
    setName("MainServer");

    databaseName = "tutorpoint";

    mySqlFactory = new MySqlFactory(databaseName);
    allClients = new ConcurrentHashMap<Integer, ClientHandler>();
    loggedInClients = new ConcurrentHashMap<Integer, ClientHandler>();

    serverSocket = new ServerSocket(port);

    dataServer = new DataServer(port + 1, this);
  }

  /**
   * CONSTRUCTOR DESCRIPTION.
   *
   * @param port          DESCRIPTION
   * @param databaseName  DESCRIPTION
   */
  public MainServer(int port, String databaseName) {
    setName("MainServer");
    this.databaseName = databaseName;
    mySqlFactory = new MySqlFactory(databaseName);
    allClients = new ConcurrentHashMap<Integer, ClientHandler>();
    loggedInClients = new ConcurrentHashMap<Integer, ClientHandler>();

    try {
      serverSocket = new ServerSocket(port);
      //serverSocket.setSoTimeout(2000);
      dataServer = new DataServer(port + 1, this);
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
    setName("MainServer");
    this.databaseName = databaseName;
    this.mySqlFactory = mySqlFactory;
    allClients = new ConcurrentHashMap<Integer, ClientHandler>();
    loggedInClients = new ConcurrentHashMap<Integer, ClientHandler>();

    try {
      serverSocket = new ServerSocket(port);
      dataServer = new DataServer(port + 1, this);
      //serverSocket.setSoTimeout(2000);
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  @Override
  public void run() {
    log.info("Started");
    // Start dataServer
    dataServer.start();

    /* Main server should sit in this loop waiting for clients */
    while (true) {
      try {
        socket = serverSocket.accept();

        log.info("New Client Accepted: Token " + clientToken);

        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());

        log.info("Starting SQL Connection");
        sqlConnection = mySqlFactory.createConnection();
        log.info("Made SQL Connection");

        ClientHandler ch = new ClientHandler(dis, dos, clientToken, sqlConnection,
            this);
        allClients.put(clientToken, ch);
        dos.writeInt(clientToken);

        ch.start();

        log.info("There are now " + allClients.size() + " clients connected. "
            + loggedInClients.size() + " are logged in.");


        clientToken++;

      } catch (IOException e) {
        log.error("Failed to create DataInput/OutputStreams", e);
      } catch (SQLException e) {
        log.error("Failed to connect to MySQL database", e);
      }
    }
  }

  public ConcurrentHashMap<Integer, ClientHandler> getAllClients() {
    return allClients;
  }

  public ConcurrentHashMap<Integer, ClientHandler> getLoggedInClients() {
    return loggedInClients;
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

  /**
   * Main entry point.
   */
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
