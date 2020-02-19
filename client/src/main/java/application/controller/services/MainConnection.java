/*
 * MainConnection.java
 * Version: 0.1.0
 * Company: CUBIXEL
 *
 * */
package application.controller.services;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
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
    private ObjectOutputStream oos = null;
    private Heartbeat heartbeat = null;

    /**
     * Constructor that creates a socket of a specific
     * IP Address and Port Number. And sets up data input
     * and output streams on that socket.
     *
     * @param connection_adr IP Address for Connection.
     * @param port Port Number.
     */
    public MainConnection(String connection_adr, int port) {
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

        heartbeat = new Heartbeat(this);
        heartbeat.start();
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

    /*Listens for incoming data. Timeout of 3s after which a network failure error is returned.*/
    public String listenForString() throws IOException {
        String incoming = null;
        long startTime = System.currentTimeMillis();

        do {
            while (dis.available() > 0) {
                incoming = dis.readUTF();
            }
            // This waits 10 seconds for a response so make sure it comes in quicker than that.
        } while ((incoming == null) && ((System.currentTimeMillis() - startTime) <= 10000));
        if (incoming == null) {
            return AccountRegisterResult.FAILED_BY_NETWORK.toString();
        } else {
            return incoming;
        }
    }

    /*Returns a JSON formatted string containing the properties of a given class as well as the name of the class/*/
    public String packageClass(Object obj){
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(obj);
        jsonElement.getAsJsonObject().addProperty("Class", obj.getClass().getSimpleName());
        return gson.toJson(jsonElement);
    }

    public void stopHeartbeat(){
        this.heartbeat.stopHeartbeat();
    }
}