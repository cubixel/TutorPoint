package sql;

import java.sql.SQLException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlFunctionTest {
  private static MySql db = null;
  private static final Logger log = LoggerFactory.getLogger("SqlFunctionTest");

  @BeforeAll
  public static void setup() throws SQLException {
    db = new MySql("tutorpoint");
  }

  @Test
  public void functionToRun() throws SQLException {
    db.addToFollowedTutors(db.getUserID("Admin"), db.getUserID("MathsTutor"));
    //db.startLiveSession(1, db.getUserID("Admin"));
  }

}
