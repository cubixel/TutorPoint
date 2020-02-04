/*
 * MainConnection.java
 * Version: 0.1.0
 * Company: CUBIXEL
 *
 * */
package application.controller.services;

import java.io.*;
import java.net.Socket;

import com.google.gson.*;
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
    private int serverToken = 0;

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

    public String listenForString() throws IOException {
        String incomming = null;
        long startTime = System.currentTimeMillis();

        while (true) {
            while (dis.available() > 0) {
                incomming = dis.readUTF();
            }
            if ((incomming !=null)||((System.currentTimeMillis()-startTime)>3000)){
                break;
            }
        }
        if (incomming == null) {
            return AccountRegisterResult.FAILED_BY_NETWORK.toString();
        } else {
            return incomming;
        }
    }

    public String packageClass(Object obj){
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(obj);
        jsonElement.getAsJsonObject().addProperty("Class", obj.getClass().getSimpleName());
        return gson.toJson(jsonElement);
    }
}


