package sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
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
    Subject subject = new Subject("Physics", "Maths.png","server/src/main/resources/subjects/thumbnails/Maths.png");
    db.addSubject(subject);
    subject = new Subject("Physics", "Maths.png","server/src/main/resources/subjects/thumbnails/Physics.png");
    db.addSubject(subject);
    subject = new Subject("Control", "Control.png","server/src/main/resources/subjects/thumbnails/Control.png");
    db.addSubject(subject);
    subject = new Subject("Biology", "Biology.png","server/src/main/resources/subjects/thumbnails/Biology.png");
    db.addSubject(subject);
    subject = new Subject("Electronics", "Electronics.png","server/src/main/resources/subjects/thumbnails/Electronics.png");
    db.addSubject(subject);
    subject = new Subject("Algebra", "Algebra.png","server/src/main/resources/subjects/thumbnails/Algebra.png");
    db.addSubject(subject);
  }
}
