package sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import model.Subject;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.concurrent.ThreadLocalRandom;

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

  //@Test
  public void controlFuntion() throws SQLException {
    ResultSet resultSet = db.getTutorsDescendingByAvgRating();
    resultSet.next();
    resultSet.next();
    System.out.println("TutorID = " + resultSet.getInt("tutorID"));
    System.out.println("rating = " + resultSet.getFloat("rating"));
    System.out.println("Username " + db.getUsername(resultSet.getInt("tutorID")));
  }

  @Test
  public void populateTestDatabase() throws SQLException {
    System.out.println("Starting populateTestDatabase");

    db.createAccount("Admin", "admin@cubixel.com", hashpw("admin"), 1);
    db.createAccount("MathsTutor", "MathsTutor@cubixel.com", hashpw("admin"), 1);
    db.createAccount("BiologyTutor", "BiologyTutor@cubixel.com", hashpw("admin"), 1);
    db.createAccount("PhysicsTutor", "PhysicsTutor@cubixel.com", hashpw("admin"), 1);
    db.createAccount("HistoryTutor", "HistoryTutor@cubixel.com", hashpw("admin"), 1);
    db.createAccount("ChemistryTutor", "ChemistryTutor@cubixel.com", hashpw("admin"), 1);
    db.createAccount("AccountingTutor", "AccountingTutor@cubixel.com", hashpw("admin"), 1);
    db.createAccount("BiotechnologyTutor", "BiotechnologyTutor@cubixel.com", hashpw("admin"), 1);
    db.createAccount("LawTutor", "LawTutor@cubixel.com", hashpw("admin"), 1);
    db.createAccount("GeneticsTutor", "GeneticsTutor@cubixel.com", hashpw("admin"), 1);
    db.createAccount("EngineeringTutor", "EngineeringTutor@cubixel.com", hashpw("admin"), 1);
    db.createAccount("MarketingTutor", "MarketingTutor@cubixel.com", hashpw("admin"), 1);
    db.createAccount("FinanceTutor", "FinanceTutor@cubixel.com", hashpw("admin"), 1);
    db.createAccount("CriminologyTutor", "CriminologyTutor@cubixel.com", hashpw("admin"), 1);
    db.createAccount("GeographyTutor", "GeographyTutor@cubixel.com", hashpw("admin"), 1);
    db.createAccount("MultiSubjectTutor", "MultiSubjectTutor@cubixel.com", hashpw("admin"), 1);

    db.createAccount("User", "user@cubixel.com", hashpw("user"), 0);
    db.createAccount("SomeUserOne", "SomeUserOne@cubixel.com", hashpw("user"), 0);
    db.createAccount("SomeUserTwo", "SomeUserTwo@cubixel.com", hashpw("user"), 0);
    db.createAccount("SomeUserThree", "SomeUserThree@cubixel.com", hashpw("user"), 0);
    db.createAccount("SomeUserFour", "SomeUserFour@cubixel.com", hashpw("user"), 0);
    db.createAccount("SomeUserFive", "SomeUserFive@cubixel.com", hashpw("user"), 0);
    db.createAccount("SomeUserSix", "SomeUserSix@cubixel.com", hashpw("user"), 0);
    db.createAccount("SomeUserSeven", "SomeUserSeven@cubixel.com", hashpw("user"), 0);

    System.out.println("Completed Adding Users and Tutors");

    db.addCategory("Engineering");
    db.addCategory("History");
    db.addCategory("Biology");
    db.addCategory("Science");
    db.addCategory("Social Science");
    db.addCategory("Economic");
    db.addCategory("English");
    db.addCategory("Nature");
    db.addCategory("Arts");

    System.out.println("Completed Adding Categories");

    db.addSubject("Maths");
    db.addSubject("Electronics");
    db.addSubject("Nanotechnology");
    db.addSubject("Music Technology");
    db.addSubject("Computer Science");
    db.addSubject("Engineering");

    db.addSubjectCategory(db.getSubjectID("Maths"), db.getCategoryID("Engineering"));
    db.addSubjectCategory(db.getSubjectID("Electronics"), db.getCategoryID("Engineering"));
    db.addSubjectCategory(db.getSubjectID("Nanotechnology"), db.getCategoryID("Engineering"));
    db.addSubjectCategory(db.getSubjectID("Music Technology"), db.getCategoryID("Engineering"));
    db.addSubjectCategory(db.getSubjectID("Computer Science"), db.getCategoryID("Engineering"));
    db.addSubjectCategory(db.getSubjectID("Engineering"), db.getCategoryID("Engineering"));

    System.out.println("Completed Adding Engineering Subjects");

    db.addSubject("History");
    db.addSubject("Archaeology");
    db.addSubject("Bioarchaeology");

    db.addSubjectCategory(db.getSubjectID("History"), db.getCategoryID("History"));
    db.addSubjectCategory(db.getSubjectID("Archaeology"), db.getCategoryID("History"));
    db.addSubjectCategory(db.getSubjectID("Bioarchaeology"), db.getCategoryID("History"));

    System.out.println("Completed Adding History Subjects");

    db.addSubject("Biology");
    db.addSubject("Medicine");
    db.addSubject("Biochemistry");
    db.addSubject("Biotechnology");
    db.addSubject("Biomedical Sciences");
    db.addSubject("Genetics");

    db.addSubjectCategory(db.getSubjectID("Biology"), db.getCategoryID("Biology"));
    db.addSubjectCategory(db.getSubjectID("Medicine"), db.getCategoryID("Biology"));
    db.addSubjectCategory(db.getSubjectID("Biochemistry"), db.getCategoryID("Biology"));
    db.addSubjectCategory(db.getSubjectID("Biotechnology"), db.getCategoryID("Biology"));
    db.addSubjectCategory(db.getSubjectID("Biomedical Sciences"), db.getCategoryID("Biology"));
    db.addSubjectCategory(db.getSubjectID("Genetics"), db.getCategoryID("Biology"));

    System.out.println("Completed Adding Biology Subjects");

    db.addSubject("Physics");
    db.addSubject("Chemistry");

    db.addSubjectCategory(db.getSubjectID("Physics"), db.getCategoryID("Science"));
    db.addSubjectCategory(db.getSubjectID("Chemistry"), db.getCategoryID("Science"));

    System.out.println("Completed Adding Science Subjects");

    db.addSubject("Social Science");
    db.addSubject("Criminology");

    db.addSubjectCategory(db.getSubjectID("Social Science"), db.getCategoryID("Social Science"));
    db.addSubjectCategory(db.getSubjectID("Criminology"), db.getCategoryID("Social Science"));

    System.out.println("Completed Adding Social Science Subjects");

    db.addSubject("Business and Management");
    db.addSubject("Accounting");
    db.addSubject("Actuarial Science");
    db.addSubject("Economics");
    db.addSubject("Finance");
    db.addSubject("Marketing");
    db.addSubject("Law");

    db.addSubjectCategory(db.getSubjectID("Business and Management"), db.getCategoryID("Economic"));
    db.addSubjectCategory(db.getSubjectID("Accounting"), db.getCategoryID("Economic"));
    db.addSubjectCategory(db.getSubjectID("Actuarial Science"), db.getCategoryID("Economic"));
    db.addSubjectCategory(db.getSubjectID("Economics"), db.getCategoryID("Economic"));
    db.addSubjectCategory(db.getSubjectID("Finance"), db.getCategoryID("Economic"));
    db.addSubjectCategory(db.getSubjectID("Marketing"), db.getCategoryID("Economic"));
    db.addSubjectCategory(db.getSubjectID("Law"), db.getCategoryID("Economic"));

    System.out.println("Completed Adding Economic Subjects");

    db.addSubject("Education");
    db.addSubject("English");
    db.addSubject("Philosophy");

    db.addSubjectCategory(db.getSubjectID("Education"), db.getCategoryID("English"));
    db.addSubjectCategory(db.getSubjectID("English"), db.getCategoryID("English"));
    db.addSubjectCategory(db.getSubjectID("Philosophy"), db.getCategoryID("English"));

    System.out.println("Completed Adding English Subjects");

    db.addSubject("Ecology");
    db.addSubject("Geography");
    db.addSubject("Environmental Science");
    db.addSubject("Natural Sciences");

    db.addSubjectCategory(db.getSubjectID("Ecology"), db.getCategoryID("Nature"));
    db.addSubjectCategory(db.getSubjectID("Geography"), db.getCategoryID("Nature"));
    db.addSubjectCategory(db.getSubjectID("Environmental Science"), db.getCategoryID("Nature"));
    db.addSubjectCategory(db.getSubjectID("Natural Sciences"), db.getCategoryID("Nature"));

    System.out.println("Completed Adding Nature Subjects");

    db.addSubject("Music");
    db.addSubject("Film");
    db.addSubject("Interactive Media");

    db.addSubjectCategory(db.getSubjectID("Music"), db.getCategoryID("Arts"));
    db.addSubjectCategory(db.getSubjectID("Film"), db.getCategoryID("Arts"));
    db.addSubjectCategory(db.getSubjectID("Interactive Media"), db.getCategoryID("Arts"));

    System.out.println("Completed Adding Arts Subjects");

    db.addTutorToSubject(db.getUserID("MathsTutor"), db.getSubjectID("Maths"));
    db.addTutorToSubject(db.getUserID("BiologyTutor"), db.getSubjectID("Biology"));
    db.addTutorToSubject(db.getUserID("PhysicsTutor"), db.getSubjectID("Physics"));
    db.addTutorToSubject(db.getUserID("HistoryTutor"), db.getSubjectID("History"));
    db.addTutorToSubject(db.getUserID("ChemistryTutor"), db.getSubjectID("Chemistry"));
    db.addTutorToSubject(db.getUserID("AccountingTutor"), db.getSubjectID("Accounting"));
    db.addTutorToSubject(db.getUserID("BiotechnologyTutor"), db.getSubjectID("Biotechnology"));
    db.addTutorToSubject(db.getUserID("LawTutor"), db.getSubjectID("Law"));
    db.addTutorToSubject(db.getUserID("GeneticsTutor"), db.getSubjectID("Genetics"));
    db.addTutorToSubject(db.getUserID("EngineeringTutor"), db.getSubjectID("Engineering"));
    db.addTutorToSubject(db.getUserID("MarketingTutor"), db.getSubjectID("Marketing"));
    db.addTutorToSubject(db.getUserID("FinanceTutor"), db.getSubjectID("Finance"));
    db.addTutorToSubject(db.getUserID("CriminologyTutor"), db.getSubjectID("Criminology"));
    db.addTutorToSubject(db.getUserID("GeographyTutor"), db.getSubjectID("Geography"));

    db.addTutorToSubject(db.getUserID("MultiSubjectTutor"), db.getSubjectID("Maths"));
    db.addTutorToSubject(db.getUserID("MultiSubjectTutor"), db.getSubjectID("Biology"));
    db.addTutorToSubject(db.getUserID("MultiSubjectTutor"), db.getSubjectID("Physics"));

    System.out.println("Completed Connecting Tutors with Subjects");

    db.addTutorRating(db.getUserID("MathsTutor"), db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("MathsTutor"), db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("MathsTutor"), db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("MathsTutor"), db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("MathsTutor"), db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("MathsTutor"), db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("MathsTutor"), db.getUserID("SomeUserSeven"), getRandomInt());
    db.addTutorRating(db.getUserID("BiologyTutor"), db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("BiologyTutor"), db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("BiologyTutor"), db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("BiologyTutor"), db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("BiologyTutor"), db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("BiologyTutor"), db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("BiologyTutor"), db.getUserID("SomeUserSeven"), getRandomInt());
    db.addTutorRating(db.getUserID("PhysicsTutor"), db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("PhysicsTutor"), db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("PhysicsTutor"), db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("PhysicsTutor"), db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("PhysicsTutor"), db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("PhysicsTutor"), db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("PhysicsTutor"), db.getUserID("SomeUserSeven"), getRandomInt());
    db.addTutorRating(db.getUserID("HistoryTutor"), db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("HistoryTutor"), db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("HistoryTutor"), db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("HistoryTutor"), db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("HistoryTutor"), db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("HistoryTutor"), db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("HistoryTutor"), db.getUserID("SomeUserSeven"), getRandomInt());
    db.addTutorRating(db.getUserID("ChemistryTutor"), db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("ChemistryTutor"), db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("ChemistryTutor"), db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("ChemistryTutor"), db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("ChemistryTutor"), db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("ChemistryTutor"), db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("ChemistryTutor"), db.getUserID("SomeUserSeven"), getRandomInt());
    db.addTutorRating(db.getUserID("AccountingTutor"), db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("AccountingTutor"), db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("AccountingTutor"), db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("AccountingTutor"), db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("AccountingTutor"), db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("AccountingTutor"), db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("AccountingTutor"), db.getUserID("SomeUserSeven"), getRandomInt());
    db.addTutorRating(db.getUserID("BiotechnologyTutor"), db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("BiotechnologyTutor"), db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("BiotechnologyTutor"), db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("BiotechnologyTutor"), db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("BiotechnologyTutor"), db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("BiotechnologyTutor"), db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("BiotechnologyTutor"), db.getUserID("SomeUserSeven"), getRandomInt());
    db.addTutorRating(db.getUserID("LawTutor"), db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("LawTutor"), db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("LawTutor"), db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("LawTutor"), db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("LawTutor"), db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("LawTutor"), db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("LawTutor"), db.getUserID("SomeUserSeven"), getRandomInt());
    db.addTutorRating(db.getUserID("GeneticsTutor"), db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("GeneticsTutor"), db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("GeneticsTutor"), db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("GeneticsTutor"), db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("GeneticsTutor"), db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("GeneticsTutor"), db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("GeneticsTutor"), db.getUserID("SomeUserSeven"), getRandomInt());
    db.addTutorRating(db.getUserID("EngineeringTutor"), db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("EngineeringTutor"), db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("EngineeringTutor"), db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("EngineeringTutor"), db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("EngineeringTutor"), db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("EngineeringTutor"), db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("EngineeringTutor"), db.getUserID("SomeUserSeven"), getRandomInt());
    db.addTutorRating(db.getUserID("MarketingTutor"), db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("MarketingTutor"), db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("MarketingTutor"), db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("MarketingTutor"), db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("MarketingTutor"), db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("MarketingTutor"), db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("MarketingTutor"), db.getUserID("SomeUserSeven"), getRandomInt());
    db.addTutorRating(db.getUserID("FinanceTutor"), db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("FinanceTutor"), db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("FinanceTutor"), db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("FinanceTutor"), db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("FinanceTutor"), db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("FinanceTutor"), db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("FinanceTutor"), db.getUserID("SomeUserSeven"), getRandomInt());
    db.addTutorRating(db.getUserID("CriminologyTutor"), db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("CriminologyTutor"), db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("CriminologyTutor"), db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("CriminologyTutor"), db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("CriminologyTutor"), db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("CriminologyTutor"), db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("CriminologyTutor"), db.getUserID("SomeUserSeven"), getRandomInt());
    db.addTutorRating(db.getUserID("GeographyTutor"), db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("GeographyTutor"), db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("GeographyTutor"), db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("GeographyTutor"), db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("GeographyTutor"), db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("GeographyTutor"), db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("GeographyTutor"), db.getUserID("SomeUserSeven"), getRandomInt());
    db.addTutorRating(db.getUserID("MultiSubjectTutor"), db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("MultiSubjectTutor"), db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("MultiSubjectTutor"), db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("MultiSubjectTutor"), db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("MultiSubjectTutor"), db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("MultiSubjectTutor"), db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("MultiSubjectTutor"), db.getUserID("SomeUserSeven"), getRandomInt());

    System.out.println("Completed Ratings of Tutors");

    db.addSubjectToFavourites(db.getSubjectID("Maths"), db.getUserID("User"));
    db.addSubjectToFavourites(db.getSubjectID("Electronics"), db.getUserID("User"));
    db.addSubjectToFavourites(db.getSubjectID("Nanotechnology"), db.getUserID("User"));
    db.addSubjectToFavourites(db.getSubjectID("Music Technology"), db.getUserID("User"));
    db.addSubjectToFavourites(db.getSubjectID("Computer Science"), db.getUserID("User"));

  }

  private String hashpw(String password) {
    return DigestUtils.sha3_256Hex(password);
  }

  private int getRandomInt() {
    return ThreadLocalRandom.current().nextInt(0, 5 + 1);
  }

}
