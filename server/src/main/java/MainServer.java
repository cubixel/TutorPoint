import javax.imageio.IIOException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServer {

    private ServerSocket serverSocket = null;

    public MainServer(int port) throws IOException {

        try{
            serverSocket = new ServerSocket(port);
        }
        catch (IIOException i){
            System.out.println(i);
        }

    }
    public static void main(String[] args) throws IOException {
        MainServer main = new MainServer(5000);
    }

    public boolean getSocketState(){
        return serverSocket.isClosed();
    }
}
