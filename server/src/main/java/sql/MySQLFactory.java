package sql;

public class MySQLFactory {

  private String databaseName;

  public MySQLFactory(String databaseName) {
    this.databaseName = databaseName;
  }

  public MySQL createConnection() {
    return new MySQL(databaseName);
  }
}
