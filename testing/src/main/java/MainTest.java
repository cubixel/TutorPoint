import application.connection.MainConnection;
import com.mysql.cj.protocol.a.NativePacketPayload;
import org.junit.BeforeClass;
import org.junit.Test;


import static org.junit.Assert.*;

public class MainTest {
    private static MainServer server = null;

    @BeforeClass
    public static void createServer() throws Exception {
        server = new MainServer(5000);
    }

    @Test
    public void createSocket() throws Exception {
        //MainServer Server = new MainServer(5000);
        assertEquals(false, this.server.getSocketState());
    }

    @Test
    public void createConnection() throws Exception{
        String connection_adr = null;
        MainConnection connection = new MainConnection(connection_adr, 5000);
        assertEquals(true, connection.isClosed());
    }
}
