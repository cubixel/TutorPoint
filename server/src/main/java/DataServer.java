import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ClientNotifier;

/**
 * A DataServer provides another connection between client
 * and server on a different port to the MainConnection.
 * It connects the client to a client notifier.
 *
 * @author Daniel Bishop
 * @see MainServer
 */
public class DataServer extends Thread {

  private final ServerSocket dataServerSocket;

  private final MainServer mainServer;


  /* Logger used by Server. Prints to both the console and to a file 'logFile.log' saved
   * under resources/logs. All classes in Server should create a Logger of the same name. */
  private static final Logger log = LoggerFactory.getLogger("DataServer");

  /**
   * Constructor that creates a serverSocket on a specific
   * Port Number.
   *
   * @param port
   *        Port Number.
   */
  public DataServer(int port, MainServer mainServer) throws IOException {
    setDaemon(true);
    setName("DataServer");
    dataServerSocket = new ServerSocket(port);
    this.mainServer = mainServer;
    log.info("DataServer created successfully");
  }

  @Override
  public void run() {
    log.info("Started");
    /* Main server should sit in this loop waiting for clients */
    while (true) {
      try {
        Socket dataSocket = dataServerSocket.accept();

        DataInputStream dataIn = new DataInputStream(dataSocket.getInputStream());
        DataOutputStream dataOut = new DataOutputStream(dataSocket.getOutputStream());

        int token = dataIn.readInt();
        log.info("Incomming connection with token " + token);
        dataOut.writeInt(token);

        ClientNotifier newNotifier = new ClientNotifier(dataIn, dataOut);
        mainServer.getAllClients().get(token).setNotifier(newNotifier);
        

      } catch (IOException e) {
        log.error("Failed to create DataInput/OutputStreams", e);
      }
    }
  }
}