package application.connection;
import com.google.gson.*;
import application.security.Register;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

public class MainConnectionTest {



    @Test
    public void packageData(){
        Register user = new Register("qwerty", "encryptthispls", 1);
        MainConnection connection = new MainConnection(null, 5000);
        String json = connection.packageClass(user);
        System.out.println(json);
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject jsonTree = parser.parse(json).getAsJsonObject();
        assertEquals("Register", jsonTree.get("Class").getAsString());

    }
}
