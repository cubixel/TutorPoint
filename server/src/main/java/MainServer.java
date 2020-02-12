/*
 * MainServer.java
 * Version: 0.1.0
 * Company: CUBIXEL
 *
 * */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.imageio.IIOException;

import sql.MySQL;

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
    private String databaseName = null;

    private DataInputStream dis = null;
    private DataOutputStream dos = null;

    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;

    private MySQL db;

    private int clientToken = 0;

    private Vector<ClientHandler> activeClients;

    /**
     * Constructor that creates a serverSocket on a specific
     * Port Number. And sets up a global timeout for that
     * serverSocket of 2 seconds.
     *
     * @param port Port Number.
     */
    public MainServer(int port)  {
        this.databaseName = "tutorpoint";
        activeClients = new Vector<>();

        try{
            serverSocket = new ServerSocket(port);
            //serverSocket.setSoTimeout(2000);
            db = new MySQL(databaseName);
        }
        catch (IIOException i){
            i.printStackTrace();
        } catch (IOException IOE){
            IOE.printStackTrace();
        }
    }

    public MainServer(int port, String databaseName)  {
        this.databaseName = databaseName;
        activeClients = new Vector<>();

        try{
            serverSocket = new ServerSocket(port);
            //serverSocket.setSoTimeout(2000);
            db = new MySQL(databaseName);
        }
        catch (IIOException i){
            i.printStackTrace();
        } catch (IOException IOE){
            IOE.printStackTrace();
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

                MySQL sqlConnection = new MySQL(databaseName);

                //ois = new ObjectInputStream(socket.getInputStream());
                //oos = new ObjectOutputStream(socket.getOutputStream());

                ClientHandler ch = new ClientHandler(socket, dis, dos, clientToken, sqlConnection);

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

    public void readObjectStream() throws IOException, ClassNotFoundException {
        Object object = (Object) ois.readObject();
        System.out.println(object);
    }
}
