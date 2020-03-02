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
import java.util.Vector;

import sql.MySQL;
import sql.MySQLFactory;

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

    private Vector<ClientHandler> activeClients;

    private MySQLFactory mySqlFactory;
    private MySQL sqlConnection;

    /**
     * Constructor that creates a serverSocket on a specific
     * Port Number.
     *
     * @param port Port Number.
     */
    public MainServer(int port)  {
        databaseName = "tutorpointnew";
        mySqlFactory = new MySQLFactory(databaseName);
        activeClients = new Vector<>();

        try {
            serverSocket = new ServerSocket(port);
            //serverSocket.setSoTimeout(2000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MainServer(int port, String databaseName)  {
        this.databaseName = databaseName;
        mySqlFactory = new MySQLFactory(databaseName);
        activeClients = new Vector<>();

        try {
            serverSocket = new ServerSocket(port);
            //serverSocket.setSoTimeout(2000);
        } catch (IOException e) {
            e.printStackTrace();
        }
            
    }

  public MainServer(int port, MySQLFactory mySqlFactory, String databaseName)  {
    this.databaseName = databaseName;
    this.mySqlFactory = mySqlFactory;
    activeClients = new Vector<>();

    try {
      serverSocket = new ServerSocket(port);
      //serverSocket.setSoTimeout(2000);
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

    @Override
    public void run(){
        /* Main server should sit in this loop waiting for clients */
        while (true) {
            try {
                socket = serverSocket.accept();

                System.out.println("New Client Accepted: Token " + clientToken);

                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());

                sqlConnection = mySqlFactory.createConnection();

                ClientHandler ch = new ClientHandler(dis, dos, clientToken, sqlConnection);

                Thread t = new Thread(ch);

                activeClients.add(ch);

                t.start();

                clientToken++;


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public ClientHandler getClientHandler(){
        /*
         * ###################################################
         * Would you ever need to select from other clients
         * This is just client 0 atm.
         * ###################################################
         * */
        return this.activeClients.get(0);
    }

  public Vector<ClientHandler> getActiveClients() {
    return activeClients;
  }

  public boolean isSocketClosed(){
        return this.serverSocket.isClosed();
    }

    /* Getter method for binding state of serverSocket.
     * Returns true if the ServerSocket is successfully
     * bound to an address.
     * */
    public boolean isBound(){
        return serverSocket.isBound();
    }

    public static void main(String[] args) {
        MainServer main = new MainServer(5000);
        main.start();
    }
}
