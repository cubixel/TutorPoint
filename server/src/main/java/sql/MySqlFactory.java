package sql;

import java.sql.SQLException;
import org.slf4j.Logger;

public class MySqlFactory {

  private String databaseName;
  private Logger log;

  public MySqlFactory(String databaseName, Logger log) {
    this.databaseName = databaseName;
    this.log = log;
  }

  public MySql createConnection() throws SQLException {
    return new MySql(databaseName, log);
  }
}
