import javax.imageio.IIOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServer extends Thread {

    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private DataInputStream dis = null;
    private DataOutputStream dos = null;


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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        MainServer main = new MainServer(5000);
    }

    public boolean getSocketState(){
        return serverSocket.isClosed();
    }

    public boolean isBound(){
        return serverSocket.isBound();
    }

    public String readString() throws IOException {
        return dis.readUTF();
    }
}
