package sql;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for testing the MySqlFactory. Tests
 * that the constructor is working as expected
 * and that the create connection class correctly
 * generates a connected MySql class and throws
 * an exception if not.
 *
 * @author James Gardner
 */
public class MySqlFactoryTest {

  private static final Logger log = LoggerFactory.getLogger("MySqlFactoryTest");

  @Test
  public void createConnectionTest() {
    String incorrectDatabaseName = "notadatabase";
    String databaseName = "tutorpoint";
    MySqlFactory mySqlFactory = new MySqlFactory(incorrectDatabaseName);
    MySqlFactory finalMySqlFactory = mySqlFactory;
    assertThrows(SQLException.class, finalMySqlFactory::createConnection);
    mySqlFactory = new MySqlFactory(databaseName);
    try {
      mySqlFactory.createConnection();
    } catch (SQLException sqlException) {
      log.error("Failed to connect to tutorpoint database", sqlException);
      fail();
    }
  }
}
