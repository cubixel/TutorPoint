package sql;

import java.sql.SQLException;
import org.slf4j.Logger;

public class MySqlFactory {

  private String databaseName;

  public MySqlFactory(String databaseName) {
    this.databaseName = databaseName;
  }

  public MySql createConnection() throws SQLException {
    return new MySql(databaseName);
  }
}
