package application.connection;

import java.io.IOException;
import java.net.Socket;

public class MainConnection {
    private Socket socket = null;

    public MainConnection(String connection_adr, int port) throws IOException {
        if (connection_adr == null){
            socket = new Socket("localhost", port);
        } else {
            socket = new Socket(connection_adr, port);
        }

    }

    public boolean isClosed(){
        return socket.isClosed();
    }
}


