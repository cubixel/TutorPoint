import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CLASS DESCRIPTION:
 * #################
 * DataServer is the coordinator for the second connections being created.
 * It runs on a separate thread to MainServer.
 *
 * @author CUBIXEL
 *
 */
public class SocketAcceptor extends Thread {

  ServerSocket serverSocket;
  Socket targetSocket;
  private static final Logger log = LoggerFactory.getLogger("SocketAcceptor");

  /**
   * Create a thread to accept incoming connections on a ServerSocket, accept that connection to a
   * provided Socket, and notify the required thread.
   */
  public SocketAcceptor(ServerSocket serverSock, Socket targetSocket, String name) {
    setDaemon(true);
    setName("SocketAcceptor:" + name);
    this.serverSocket = serverSock;
    this.targetSocket = targetSocket;
  }

  @Override
  public void run() {
    while (true) {
      while (targetSocket != null) {}
      try {
        targetSocket = serverSocket.accept();
      } catch (IOException e) {
        log.error("Failed to accept on server socket", e);
      }
    }
  }
}