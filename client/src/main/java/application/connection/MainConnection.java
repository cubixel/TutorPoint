/*
 * MainConnection.java
 * Version: 0.1.0
 * Company: CUBIXEL
 *
 * */
package application.connection;

import java.io.*;
import java.net.Socket;

/**
 * CLASS DESCRIPTION:
 * #################
 *
 * @author CUBIXEL
 *
 */
public class MainConnection {
    private Socket socket = null;
    private DataInputStream dis = null;
    private DataOutputStream dos = null;
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;

    /**
     * Constructor that creates a socket of a specific
     * IP Address and Port Number. And sets up data input
     * and output streams on that socket.
     *
     * @param connection_adr IP Address for Connection.
     * @param port Port Number.
     */
    public MainConnection(String connection_adr, int port) throws IOException {
        /* If the connection address is null then it will default to localhost. */
        try{
            if (connection_adr == null){
                socket = new Socket("localhost", port);
            } else {
                socket = new Socket(connection_adr, port);
            }

            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /* Takes a String as an input and sends this to the ##### */
    public void sendString(String input) throws IOException {
        dos.writeUTF(input);
    }

    /* Getter method for the state of the socket. */
    public boolean isClosed(){
        return socket.isClosed();
    }

    public void sendObject(Object object) throws IOException {
        oos.writeObject(object);
    }
}


