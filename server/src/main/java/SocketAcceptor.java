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
 * DataServer is the coordinator for the second connections being created.
 * It runs on a separate thread to MainServer.
 *
 * @author CUBIXEL
 *
 */
public class SocketAcceptor extends Thread {

  ServerSocket serverSocket;
  Socket targetSocket;
  Object notifier;

  /**
   * Create a thread to accept incoming connections on a ServerSocket, accept that connection to a
   * provided Socket, and notify the required thread.
   */
  public SocketAcceptor(ServerSocket serverSock, Socket targetSocket, Object notifier,
      String name) {
    setDaemon(true);
    setName("SocketAcceptor:" + name);
    this.serverSocket = serverSock;
    this.targetSocket = targetSocket;
    this.notifier = notifier;
  }

  @Override
  public void run() {

  }
}