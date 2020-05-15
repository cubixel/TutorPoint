package sql;

import java.sql.SQLException;

/**
 * This class is necessary to test the MainServer as
 * it means a Mockito version of the MySql class can
 * be instatiated when the MySqlFactory.createConnection()
 * method is called.
 *
 * @author James Gardner
 */
public class MySqlFactory {

  private final String databaseName;

  public MySqlFactory(String databaseName) {
    this.databaseName = databaseName;
  }

  public MySql createConnection() throws SQLException {
    return new MySql(databaseName);
  }
}
