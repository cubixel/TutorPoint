package sql;

public class MySqlFactory {

  private String databaseName;

  public MySqlFactory(String databaseName) {
    this.databaseName = databaseName;
  }

  public MySql createConnection() {
    return new MySql(databaseName);
  }
}
