package sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import model.Account;
import model.Subject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * CLASS DESCRIPTION.
 * #################
 *
 * @author CUBIXEL
 *
 */
public class MySqlQuickBuild {

  private static MySql db = null;

  /**
   * CLASS DESCRIPTION.
   * #################
   *
   * @author CUBIXEL
   *
   */
  @BeforeAll
  public static void createDatabaseTestServer() throws Exception {
    db = new MySql("tutorpoint");
  }

  @Test
  public void controlFuntion() {

    try {
      ResultSet rs = db.getSubjectsDescendingByRating();
      rs.next();
      System.out.println(rs.getInt("subjectID"));
      System.out.println(rs.getFloat("rating"));
    } catch (SQLException e) {
      e.printStackTrace();
    }

  }
}
