package sql;

import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Class used to quickly populate a database with test
 * data. This includes all aspects of TutorPoint with
 * many users, subjects, tutors and categories.
 *
 * @author James Gardner
 * @see MySql
 * @see MySqlTest
 */
public class MySqlQuickBuild {

  /**
   * This populates a test database with a load of test data
   * for methods to test with.
   *
   * @throws SQLException
   *         Thrown if connection or database access error
   */
  public static void populateTestDatabase(MySql db) throws SQLException {
    System.out.println("Starting populateTestDatabase");

    db.createAccount("Admin", "admin@cubixel.com",
        hashpw("admin"), 1);
    db.createAccount("MathsTutor", "MathsTutor@cubixel.com",
        hashpw("admin"), 1);
    db.createAccount("BiologyTutor", "BiologyTutor@cubixel.com",
        hashpw("admin"), 1);
    db.createAccount("PhysicsTutor", "PhysicsTutor@cubixel.com",
        hashpw("admin"), 1);
    db.createAccount("HistoryTutor", "HistoryTutor@cubixel.com",
        hashpw("admin"), 1);
    db.createAccount("ChemistryTutor", "ChemistryTutor@cubixel.com",
        hashpw("admin"), 1);
    db.createAccount("AccountingTutor", "AccountingTutor@cubixel.com",
        hashpw("admin"), 1);
    db.createAccount("BiotechnologyTutor", "BiotechnologyTutor@cubixel.com",
        hashpw("admin"), 1);
    db.createAccount("LawTutor", "LawTutor@cubixel.com",
        hashpw("admin"), 1);
    db.createAccount("GeneticsTutor", "GeneticsTutor@cubixel.com",
        hashpw("admin"), 1);
    db.createAccount("EngineeringTutor", "EngineeringTutor@cubixel.com",
        hashpw("admin"), 1);
    db.createAccount("MarketingTutor", "MarketingTutor@cubixel.com",
        hashpw("admin"), 1);
    db.createAccount("FinanceTutor", "FinanceTutor@cubixel.com",
        hashpw("admin"), 1);
    db.createAccount("CriminologyTutor", "CriminologyTutor@cubixel.com",
        hashpw("admin"), 1);
    db.createAccount("GeographyTutor", "GeographyTutor@cubixel.com",
        hashpw("admin"), 1);
    db.createAccount("MultiSubjectTutor", "MultiSubjectTutor@cubixel.com",
        hashpw("admin"), 1);

    db.createAccount("User", "user@cubixel.com",
        hashpw("user"), 0);
    db.createAccount("SomeUserOne", "SomeUserOne@cubixel.com",
        hashpw("user"), 0);
    db.createAccount("SomeUserTwo", "SomeUserTwo@cubixel.com",
        hashpw("user"), 0);
    db.createAccount("SomeUserThree", "SomeUserThree@cubixel.com",
        hashpw("user"), 0);
    db.createAccount("SomeUserFour", "SomeUserFour@cubixel.com",
        hashpw("user"), 0);
    db.createAccount("SomeUserFive", "SomeUserFive@cubixel.com",
        hashpw("user"), 0);
    db.createAccount("SomeUserSix", "SomeUserSix@cubixel.com",
        hashpw("user"), 0);
    db.createAccount("SomeUserSeven", "SomeUserSeven@cubixel.com",
        hashpw("user"), 0);

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

    db.addSubject("Maths", "Engineering");
    db.addSubject("Electronics", "Engineering");
    db.addSubject("Nanotechnology", "Engineering");
    db.addSubject("Music Technology", "Engineering");
    db.addSubject("Computer Science", "Engineering");
    db.addSubject("Engineering","Engineering");

    System.out.println("Completed Adding Engineering Subjects");

    db.addSubject("History", "History");
    db.addSubject("Archaeology", "History");
    db.addSubject("Bioarchaeology", "History");

    System.out.println("Completed Adding History Subjects");

    db.addSubject("Biology", "Biology");
    db.addSubject("Medicine", "Biology");
    db.addSubject("Biochemistry", "Biology");
    db.addSubject("Biotechnology", "Biology");
    db.addSubject("Biomedical Sciences", "Biology");
    db.addSubject("Genetics", "Biology");

    System.out.println("Completed Adding Biology Subjects");

    db.addSubject("Physics", "Science");
    db.addSubject("Chemistry", "Science");

    System.out.println("Completed Adding Science Subjects");

    db.addSubject("Social Science", "Social Science");
    db.addSubject("Criminology", "Social Science");

    System.out.println("Completed Adding Social Science Subjects");

    db.addSubject("Business and Management", "Economic");
    db.addSubject("Accounting", "Economic");
    db.addSubject("Actuarial Science", "Economic");
    db.addSubject("Economics", "Economic");
    db.addSubject("Finance", "Economic");
    db.addSubject("Marketing", "Economic");
    db.addSubject("Law", "Economic");

    System.out.println("Completed Adding Economic Subjects");

    db.addSubject("Education", "English");
    db.addSubject("English", "English");
    db.addSubject("Philosophy", "English");

    System.out.println("Completed Adding English Subjects");

    db.addSubject("Ecology", "Nature");
    db.addSubject("Geography", "Nature");
    db.addSubject("Environmental Science", "Nature");
    db.addSubject("Natural Sciences", "Nature");

    System.out.println("Completed Adding Nature Subjects");

    db.addSubject("Music", "Arts");
    db.addSubject("Film", "Arts");
    db.addSubject("Interactive Media", "Arts");

    System.out.println("Completed Adding Arts Subjects");

    db.addTutorToSubject(db.getUserID("MathsTutor"),
        db.getSubjectID("Maths"));
    db.addTutorToSubject(db.getUserID("BiologyTutor"),
        db.getSubjectID("Biology"));
    db.addTutorToSubject(db.getUserID("PhysicsTutor"),
        db.getSubjectID("Physics"));
    db.addTutorToSubject(db.getUserID("HistoryTutor"),
        db.getSubjectID("History"));
    db.addTutorToSubject(db.getUserID("ChemistryTutor"),
        db.getSubjectID("Chemistry"));
    db.addTutorToSubject(db.getUserID("AccountingTutor"),
        db.getSubjectID("Accounting"));
    db.addTutorToSubject(db.getUserID("BiotechnologyTutor"),
        db.getSubjectID("Biotechnology"));
    db.addTutorToSubject(db.getUserID("LawTutor"),
        db.getSubjectID("Law"));
    db.addTutorToSubject(db.getUserID("GeneticsTutor"),
        db.getSubjectID("Genetics"));
    db.addTutorToSubject(db.getUserID("EngineeringTutor"),
        db.getSubjectID("Engineering"));
    db.addTutorToSubject(db.getUserID("MarketingTutor"),
        db.getSubjectID("Marketing"));
    db.addTutorToSubject(db.getUserID("FinanceTutor"),
        db.getSubjectID("Finance"));
    db.addTutorToSubject(db.getUserID("CriminologyTutor"),
        db.getSubjectID("Criminology"));
    db.addTutorToSubject(db.getUserID("GeographyTutor"),
        db.getSubjectID("Geography"));

    db.addTutorToSubject(db.getUserID("MultiSubjectTutor"),
        db.getSubjectID("Maths"));
    db.addTutorToSubject(db.getUserID("MultiSubjectTutor"),
        db.getSubjectID("Biology"));
    db.addTutorToSubject(db.getUserID("MultiSubjectTutor"),
        db.getSubjectID("Physics"));

    System.out.println("Completed Connecting Tutors with Subjects");

    db.addTutorRating(db.getUserID("MathsTutor"),
        db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("MathsTutor"),
        db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("MathsTutor"),
        db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("MathsTutor"),
        db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("MathsTutor"),
        db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("MathsTutor"),
        db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("MathsTutor"),
        db.getUserID("SomeUserSeven"), getRandomInt());
    db.addTutorRating(db.getUserID("BiologyTutor"),
        db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("BiologyTutor"),
        db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("BiologyTutor"),
        db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("BiologyTutor"),
        db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("BiologyTutor"),
        db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("BiologyTutor"),
        db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("BiologyTutor"),
        db.getUserID("SomeUserSeven"), getRandomInt());
    db.addTutorRating(db.getUserID("PhysicsTutor"),
        db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("PhysicsTutor"),
        db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("PhysicsTutor"),
        db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("PhysicsTutor"),
        db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("PhysicsTutor"),
        db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("PhysicsTutor"),
        db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("PhysicsTutor"),
        db.getUserID("SomeUserSeven"), getRandomInt());
    db.addTutorRating(db.getUserID("HistoryTutor"),
        db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("HistoryTutor"),
        db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("HistoryTutor"),
        db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("HistoryTutor"),
        db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("HistoryTutor"),
        db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("HistoryTutor"),
        db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("HistoryTutor"),
        db.getUserID("SomeUserSeven"), getRandomInt());
    db.addTutorRating(db.getUserID("ChemistryTutor"),
        db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("ChemistryTutor"),
        db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("ChemistryTutor"),
        db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("ChemistryTutor"),
        db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("ChemistryTutor"),
        db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("ChemistryTutor"),
        db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("ChemistryTutor"),
        db.getUserID("SomeUserSeven"), getRandomInt());
    db.addTutorRating(db.getUserID("AccountingTutor"),
        db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("AccountingTutor"),
        db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("AccountingTutor"),
        db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("AccountingTutor"),
        db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("AccountingTutor"),
        db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("AccountingTutor"),
        db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("AccountingTutor"),
        db.getUserID("SomeUserSeven"), getRandomInt());
    db.addTutorRating(db.getUserID("BiotechnologyTutor"),
        db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("BiotechnologyTutor"),
        db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("BiotechnologyTutor"),
        db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("BiotechnologyTutor"),
        db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("BiotechnologyTutor"),
        db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("BiotechnologyTutor"),
        db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("BiotechnologyTutor"),
        db.getUserID("SomeUserSeven"), getRandomInt());
    db.addTutorRating(db.getUserID("LawTutor"),
        db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("LawTutor"),
        db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("LawTutor"),
        db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("LawTutor"),
        db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("LawTutor"),
        db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("LawTutor"),
        db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("LawTutor"),
        db.getUserID("SomeUserSeven"), getRandomInt());
    db.addTutorRating(db.getUserID("GeneticsTutor"),
        db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("GeneticsTutor"),
        db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("GeneticsTutor"),
        db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("GeneticsTutor"),
        db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("GeneticsTutor"),
        db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("GeneticsTutor"),
        db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("GeneticsTutor"),
        db.getUserID("SomeUserSeven"), getRandomInt());
    db.addTutorRating(db.getUserID("EngineeringTutor"),
        db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("EngineeringTutor"),
        db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("EngineeringTutor"),
        db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("EngineeringTutor"),
        db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("EngineeringTutor"),
        db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("EngineeringTutor"),
        db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("EngineeringTutor"),
        db.getUserID("SomeUserSeven"), getRandomInt());
    db.addTutorRating(db.getUserID("MarketingTutor"),
        db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("MarketingTutor"),
        db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("MarketingTutor"),
        db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("MarketingTutor"),
        db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("MarketingTutor"),
        db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("MarketingTutor"),
        db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("MarketingTutor"),
        db.getUserID("SomeUserSeven"), getRandomInt());
    db.addTutorRating(db.getUserID("FinanceTutor"),
        db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("FinanceTutor"),
        db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("FinanceTutor"),
        db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("FinanceTutor"),
        db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("FinanceTutor"),
        db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("FinanceTutor"),
        db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("FinanceTutor"),
        db.getUserID("SomeUserSeven"), getRandomInt());
    db.addTutorRating(db.getUserID("CriminologyTutor"),
        db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("CriminologyTutor"),
        db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("CriminologyTutor"),
        db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("CriminologyTutor"),
        db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("CriminologyTutor"),
        db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("CriminologyTutor"),
        db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("CriminologyTutor"),
        db.getUserID("SomeUserSeven"), getRandomInt());
    db.addTutorRating(db.getUserID("GeographyTutor"),
        db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("GeographyTutor"),
        db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("GeographyTutor"),
        db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("GeographyTutor"),
        db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("GeographyTutor"),
        db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("GeographyTutor"),
        db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("GeographyTutor"),
        db.getUserID("SomeUserSeven"), getRandomInt());
    db.addTutorRating(db.getUserID("MultiSubjectTutor"),
        db.getUserID("SomeUserOne"), getRandomInt());
    db.addTutorRating(db.getUserID("MultiSubjectTutor"),
        db.getUserID("SomeUserTwo"), getRandomInt());
    db.addTutorRating(db.getUserID("MultiSubjectTutor"),
        db.getUserID("SomeUserThree"), getRandomInt());
    db.addTutorRating(db.getUserID("MultiSubjectTutor"),
        db.getUserID("SomeUserFour"), getRandomInt());
    db.addTutorRating(db.getUserID("MultiSubjectTutor"),
        db.getUserID("SomeUserFive"), getRandomInt());
    db.addTutorRating(db.getUserID("MultiSubjectTutor"),
        db.getUserID("SomeUserSix"), getRandomInt());
    db.addTutorRating(db.getUserID("MultiSubjectTutor"),
        db.getUserID("SomeUserSeven"), getRandomInt());

    System.out.println("Completed Ratings of Tutors");

    db.addSubjectToFavourites(db.getSubjectID("Maths"),
        db.getUserID("User"));
    db.addSubjectToFavourites(db.getSubjectID("Electronics"),
        db.getUserID("User"));
    db.addSubjectToFavourites(db.getSubjectID("Nanotechnology"),
        db.getUserID("User"));
    db.addSubjectToFavourites(db.getSubjectID("Music Technology"),
        db.getUserID("User"));
    db.addSubjectToFavourites(db.getSubjectID("Computer Science"),
        db.getUserID("User"));

    db.addSubjectToFavourites(db.getSubjectID("Maths"),
        db.getUserID("Admin"));
    db.addSubjectToFavourites(db.getSubjectID("Electronics"),
        db.getUserID("Admin"));
    db.addSubjectToFavourites(db.getSubjectID("Nanotechnology"),
        db.getUserID("Admin"));
    db.addSubjectToFavourites(db.getSubjectID("Music Technology"),
        db.getUserID("Admin"));
    db.addSubjectToFavourites(db.getSubjectID("Computer Science"),
        db.getUserID("Admin"));

    db.addSubjectToFavourites(db.getSubjectID("Biology"),
        db.getUserID("SomeUserOne"));
    db.addSubjectToFavourites(db.getSubjectID("Accounting"),
        db.getUserID("SomeUserOne"));
    db.addSubjectToFavourites(db.getSubjectID("Ecology"),
        db.getUserID("SomeUserOne"));
    db.addSubjectToFavourites(db.getSubjectID("Music Technology"),
        db.getUserID("SomeUserOne"));
    db.addSubjectToFavourites(db.getSubjectID("Computer Science"),
        db.getUserID("SomeUserOne"));

  }

  /**
   * Hashes a String using the sha3_256Hex
   * hashing algorithm.
   *
   * @param password
   *        String to be hashed
   *
   * @return The hashed String
   */
  private static String hashpw(String password) {
    return DigestUtils.sha3_256Hex(password);
  }

  /**
   * Generates a random integer between 1 and 5.
   *
   * @return A random integer between 1 and 5.
   */
  private static int getRandomInt() {
    return ThreadLocalRandom.current().nextInt(0, 5 + 1);
  }

}
