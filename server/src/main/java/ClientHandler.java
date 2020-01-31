import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ClientHandler implements Runnable {

    Scanner scn = new Scanner(System.in);
    private int token;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    boolean isLoggedIn;

    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos, int token){
        this.dis = dis;
        this.dos = dos;
        this.s = s;
        this.isLoggedIn = true;
        this.token = token;
    }

    @Override
    public void run() {
        // Does the client need to know its number?
        //writeString("Token#" + token);
        String received;

        while (isLoggedIn){
            // Do stuff with this client in this thread
            // when client disconnects then close it down.

            try {
                received = dis.readUTF();

                StringTokenizer st = new StringTokenizer(received, "#");
                String action = st.nextToken();
                String data_one = st.nextToken();
                String data_two = st.nextToken();
                String data_three = st.nextToken();

                if (action == "register"){
                    createNewUser(data_one, data_two, data_three);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


            System.out.println("Inside Client Thread");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void logOut(){
        this.isLoggedIn = false;
    }

    public String readString(){
        try {
            return dis.readUTF();
        } catch (IOException e){
            System.out.println(e);
            return null;
        }
    }

    public void writeString(String msg){
        try {
            dos.writeUTF(msg);
        } catch (IOException IOE){
            IOE.printStackTrace();
        }
    }

    public void createNewUser(String username, String password, String isTutor){

    }

    public String toString()
    {
        return "This is client " + token;
    }
}
