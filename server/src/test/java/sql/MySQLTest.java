/*
 * MySQLTest.java
 * Version: 0.1.0
 * Company: CUBIXEL
 *
 *
 * */
package sql;

import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

/**
 * CLASS DESCRIPTION:
 * #################
 *
 * @author CUBIXEL
 *
 */
public class MySQLTest {
    private static MySQL db = null;

    @BeforeClass
    public static void connect2Db() throws SQLException, ClassNotFoundException {
        db = new MySQL();
    }


    @Test
    public void creatAccount() throws SQLException {
        String username = "qwerty";
        String hashpw = "lakdjsf";
        int tutorStatus = 1;
        // Checking Account doesn't exist
        assertNull(db.getUserDetails(username));
        db.creatAccount(username, hashpw, tutorStatus);
        assertNotNull(db.getUserDetails(username));
    }

}
