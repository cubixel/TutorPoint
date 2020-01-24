package application.connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MainConnection {
    private Socket socket = null;
    private DataInputStream dis = null;
    private DataOutputStream dos = null;

    public MainConnection(String connection_adr, int port) throws IOException {
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

    public void sendString(String input) throws IOException {
        dos.writeUTF(input);
    }

    public boolean isClosed(){
        return socket.isClosed();
    }
}


