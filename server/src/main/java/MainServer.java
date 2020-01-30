/*
 * MainServer.java
 * Version: 0.1.0
 * Company: CUBIXEL
 *
 * */

import javax.imageio.IIOException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

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
    private DataInputStream dis = null;
    private DataOutputStream dos = null;
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;


    /**
     * Constructor that creates a serverSocket on a specific
     * Port Number. And sets up a global timeout for that
     * serverSocket of 2 seconds.
     *
     * @param port Port Number.
     */
    public MainServer(int port) throws IOException {

        try{
            serverSocket = new ServerSocket(port);
        }
        catch (IIOException i){
            System.out.println(i);
        }

        serverSocket.setSoTimeout(2000);
    }

    @Override
    public void run(){
        while (true) {
            try {
                socket = serverSocket.accept();
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());
                oos = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isSocketClosed(){
        return serverSocket.isClosed();
    }

    /* Getter method for binding state of serverSocket.
     * Returns true if the ServerSocket is successfully
     * bound to an address.
     * */
    public boolean isBound(){
        return serverSocket.isBound();
    }

    public String readString() throws IOException {
        return dis.readUTF();
    }

    public static void main(String[] args) throws IOException {
        MainServer main = new MainServer(5000);
    }

    public void readObjectStream() throws IOException, ClassNotFoundException {
        Object object = (Object) ois.readObject();
        System.out.println(object);
    }
}
