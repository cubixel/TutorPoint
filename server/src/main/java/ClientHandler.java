import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import services.AccountLoginResult;
import services.AccountRegisterResult;
import sql.MySQL;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ClientHandler implements Runnable {

    Scanner scn = new Scanner(System.in);
    private int token;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    boolean isLoggedIn;
    MySQL sqlConnection;

    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos, int token, MySQL sqlConnection){
        this.dis = dis;
        this.dos = dos;
        this.s = s;
        this.isLoggedIn = true;
        this.token = token;
        this.sqlConnection = sqlConnection;
    }

    @Override
    public void run() {
        // Does the client need to know its number?
        //writeString("Token#" + token);
        String received = null;

        while (isLoggedIn){
            // Do stuff with this client in this thread
            // when client disconnects then close it down.

            try {

                while(dis.available()>0) {
                    received = dis.readUTF();
                }

                if (received != null){
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(received, JsonObject.class);
                    String action = jsonObject.get("Class").getAsString();
                    System.out.println(action);


                    if (action.equals("Account")) {
                        if (jsonObject.get("isRegister").getAsInt() == 1) {
                            createNewUser(jsonObject.get("username").getAsString(), jsonObject.get("hashedpw").getAsString(),
                                    jsonObject.get("tutorStatus").getAsInt());
                        }else {
                            loginUser(jsonObject.get("username").getAsString(), jsonObject.get("hashedpw").getAsString());
                        }

                    }
                    received = null;
                }
            } catch (IOException | SQLException e) {
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

    public void loginUser(String username, String password) throws SQLException, IOException {
        Gson gson = new Gson();
        if (sqlConnection.checkUserDetails(username, password) == null){
            JsonElement jsonElement = gson.toJsonTree(AccountLoginResult.FAILED_BY_CREDENTIALS);
            dos.writeUTF(gson.toJson(jsonElement));
            System.out.println(gson.toJson(jsonElement));
        }else{
            JsonElement jsonElement = gson.toJsonTree(AccountLoginResult.SUCCESS);
            dos.writeUTF(gson.toJson(jsonElement));
            System.out.println(gson.toJson(jsonElement));
        }
    }

    public void createNewUser(String username, String password, int isTutor) throws IOException {
        Gson gson = new Gson();
        if (sqlConnection.getUserDetails(username) == null){
            if (sqlConnection.createAccount(username, password, isTutor)){

                JsonElement jsonElement = gson.toJsonTree(AccountRegisterResult.SUCCESS);
                dos.writeUTF(gson.toJson(jsonElement));
            } else {
                JsonElement jsonElement = gson.toJsonTree(AccountRegisterResult.FAILED_BY_UNEXPECTED_ERROR);
                dos.writeUTF(gson.toJson(jsonElement));
            }
        }else {
            JsonElement jsonElement = gson.toJsonTree(AccountRegisterResult.FAILED_BY_CREDENTIALS);
            dos.writeUTF(gson.toJson(jsonElement));
        }
    }

    public String toString()
    {
        return "This is client " + token;
    }
}
