import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sql.MySql;
import sql.MySqlFactory;

/**
 * MainServer is the top level server class. It contains the main
 * entry point for the Server module.
 *
 * <p>MainServer sits on its own thread listening for
 * Clients requesting to connect. Once it receives a request
 * for connection it constructs the neccessary classes such as
 * a MySQL connection and the ClientHandler class and passes
 * management of that client over to the ClientHandler.
 *
 * <p>MainServer contains some methods and Objects needed for
 * communicating across the ClientHandlers such as updating
 * live tutors and the list of currently active clients.
 *
 * @author Che McKirgan
 * @author James Gardner
 * @author Daniel Bishop
 *
 * @see ClientHandler
 * @see MySql
 * @see DataServer
 */
public class MainServer extends Thread {

  private ServerSocket serverSocket = null;
  private final String databaseName;

  private int clientToken = 0;

  private DataServer dataServer;

  private final ConcurrentHashMap<Integer, ClientHandler> allClients;
  private final ConcurrentHashMap<Integer, ClientHandler> loggedInClients;

  private final MySqlFactory mySqlFactory;

  /* Logger prints to both the console and to a file 'logFile.log' saved
   * under resources/logs. All classes should create a Logger of their name. */
  private static final Logger log = LoggerFactory.getLogger("MainServer");

  /**
   * Constructor that creates a serverSocket on a specific
   * Port Number.
   *
   * @param port
   *        Port number
   */
  public MainServer(int port) {
    setName("MainServer");

    databaseName = "tutorpoint";

    mySqlFactory = new MySqlFactory(databaseName);
    allClients = new ConcurrentHashMap<>();
    loggedInClients = new ConcurrentHashMap<>();

    try {
      serverSocket = new ServerSocket(port);
      dataServer = new DataServer(port + 1, this);
    } catch (IOException e) {
      log.error("Failed to create ServerSocket and DataServer", e);
    }
  }

  /**
   * Constructor that creates a serverSocket on a specific
   * Port Number and connects to a specific database name.
   *
   * @param port
   *        Port number
   *
   * @param databaseName
   *        Name of the MySQL database to use
   */
  public MainServer(int port, String databaseName) {
    setName("MainServer");
    this.databaseName = databaseName;
    mySqlFactory = new MySqlFactory(databaseName);
    allClients = new ConcurrentHashMap<>();
    loggedInClients = new ConcurrentHashMap<>();

    try {
      serverSocket = new ServerSocket(port);
      dataServer = new DataServer(port + 1, this);
    } catch (IOException e) {
      log.error("Failed to create ServerSocket and DataServer", e);
    }
  }

  /**
   * Constructor that creates a serverSocket on a specific
   * Port Number and connects to a specific database name
   * and also allows access to the MySqlFactory. This
   * constructor is used for testing the MainServer Class.
   *
   * @param port
   *        Port number
   *
   * @param mySqlFactory
   *        Used to allow mockito to supply a mocked MySql class
   *
   * @param databaseName
   *        Name of the MySQL database to use
   *
   * @param allClients
   *        A list of all the ClientHandlers created
   *
   * @param loggedInClients
   *        A list of all the ClientHandlers that have a user logged in
   */
  public MainServer(int port, MySqlFactory mySqlFactory, String databaseName,
      ConcurrentHashMap<Integer, ClientHandler> allClients, ConcurrentHashMap<Integer,
      ClientHandler> loggedInClients)  {
    setName("MainServer");
    this.databaseName = databaseName;
    this.mySqlFactory = mySqlFactory;
    this.allClients = new ConcurrentHashMap<>();
    this.loggedInClients = new ConcurrentHashMap<>();

    try {
      serverSocket = new ServerSocket(port);
      dataServer = new DataServer(port + 1, this);
    } catch (IOException e) {
      log.error("Failed to create ServerSocket and DataServer", e);
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
        Socket socket = serverSocket.accept();

        log.info("New Client Accepted: Token " + clientToken);

        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        log.info("Starting SQL Connection");
        MySql sqlConnection = mySqlFactory.createConnection();
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

  /**
   * Updates all the currently connected clients with changes to
   * the list of LiveTutors.
   */
  public void updateLiveClientList() {
    getLoggedInClients().forEach((id, handler) ->
        handler.getNotifier().sendLiveTutors(handler.getSqlConnection(), handler.getUserID()));
  }

  /**
   * Main entry point to the Server Module.
   */
  public static void main(String[] args) {
    MainServer main = new MainServer(5000);
    main.start();
    log.info("Server Started");
  }
}