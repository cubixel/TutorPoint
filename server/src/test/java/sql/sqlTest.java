package sql;

import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class sqlTest {
    private static MySQL db = null;

    @BeforeClass
    public static void connect2Db() throws SQLException, ClassNotFoundException {
        db = new MySQL();
    }

    @Test
    public void checkUserExists() throws SQLException {
        String username = "qwerty";
        assertNull(db.getUserDetails(username));
    }
}
