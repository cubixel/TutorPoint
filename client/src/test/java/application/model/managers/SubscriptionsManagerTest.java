package application.model.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import application.model.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This is the test class for the SubscriptionsManager.
 *
 * @author James Gardner
 * @see application.controller.SubscriptionsWindowController
 * @see SubscriptionsManger
 * @see SubjectManager
 * @see Subject
 */
public class SubscriptionsManagerTest {

  SubscriptionsManger subscriptionsManger;

  @BeforeEach
  public void setUp() {
    subscriptionsManger = new SubscriptionsManger();
  }

  @Test
  public void getAndSetReferenceSubject() {
    String referenceSubject = "testSubject";
    subscriptionsManger.setReferenceSubject(referenceSubject);
    assertEquals(referenceSubject, subscriptionsManger.getReferenceSubject());
  }

  @Test
  public void getAndSetNumberOfSubjectsBeforeRequest() {
    SubjectManager subjectManager = subscriptionsManger.getSubjectManagerRecommendations();
    assertEquals(0, subscriptionsManger.getNumberOfSubjectsBeforeRequest());
    subjectManager.addSubject(new Subject(1, "test", "test", true));
    subjectManager.addSubject(new Subject(1, "test", "test", true));
    subscriptionsManger.setNumberOfSubjectsBeforeRequest();
    assertEquals(2, subscriptionsManger.getNumberOfSubjectsBeforeRequest());
  }
}