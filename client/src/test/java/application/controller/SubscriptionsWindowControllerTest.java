package application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import application.controller.services.ListenerThread;
import application.controller.services.MainConnection;
import application.model.Account;
import application.model.Subject;
import application.model.managers.SubscriptionsManger;
import application.view.ViewFactory;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.mockito.Mock;

/**
 * This class tests the SubscriptionsWindowController. It tests
 * updating the SubscriptionsWindow with new Subject.
 *
 * @author James Gardner
 * @see SubscriptionsWindowController
 */
public class SubscriptionsWindowControllerTest {
  /* Creating the Mock Objects necessary for the test. */
  @Mock
  protected MainConnection mainConnectionMock;

  @Mock
  protected ListenerThread listenerThreadMock;

  @Mock
  protected ViewFactory viewFactoryMock;

  @Mock
  protected MainWindowController mainWindowControllerMock;

  protected ScrollBar mainScrollBar;
  protected ScrollPane mainScrollPane;
  protected AnchorPane mainScrollContent;
  protected Label userSubject1Label;
  protected Label infoLabelOne;
  protected Label infoLabelTwo;
  protected HBox userSubject1Content;
  protected Label userSubject2Label;
  protected HBox userSubject2Content;
  protected SubscriptionsManger subscriptionsMangerOne;
  protected SubscriptionsManger  subscriptionsMangerTwo;
  protected Account account;

  /* Unit Under Test */
  protected SubscriptionsWindowController subscriptionsWindowController;

  volatile boolean threadDone;

  /**
   * This is testing initialiseOneFollowedSubject.
   */
  public void initialiseOneFollowedSubjectTest() {
    account.addFollowedSubjects("Science");
    subscriptionsWindowController.initialize(null, null);

    assertEquals("Because you liked ", infoLabelOne.getText());
    assertEquals("Science", userSubject1Label.getText());
    assertEquals("Science", subscriptionsMangerOne.getReferenceSubject());
  }

  /**
   * This is testing initialiseTwoFollowedSubjects.
   */
  public void initialiseTwoFollowedSubjectsTest() {
    account.addFollowedSubjects("Science");
    account.addFollowedSubjects("Maths");
    subscriptionsWindowController.initialize(null, null);

    /* Can't assert equals to subject as subjects are chosen randomly */
    assertEquals("Because you liked ", infoLabelOne.getText());
    assertNotEquals("", userSubject1Label.getText());
    assertNotEquals("", subscriptionsMangerOne.getReferenceSubject());
    assertEquals("Because you liked ", infoLabelTwo.getText());
    assertNotEquals("", userSubject2Label.getText());
    assertNotEquals("", subscriptionsMangerTwo.getReferenceSubject());
  }

  /**
   * This is testing the addSubjectLink method.
   */
  public void addSubjectLinkTest() {
    threadDone = false;

    userSubject1Label.setText("Science");
    Subject subject = new Subject(1,"Science", "TestCategory", true);
    assertEquals(0, subscriptionsMangerOne.getSubjectManagerRecommendations()
        .getNumberOfSubjects());
    assertEquals(0, userSubject1Content.getChildren().size());
    Platform.runLater(() -> {
      subscriptionsWindowController.addSubjectLink(subject, "Science");
      threadDone = true;
    });

    while (!threadDone) {
      Thread.onSpinWait();
    }

    assertEquals(1, subscriptionsMangerOne.getSubjectManagerRecommendations()
        .getNumberOfSubjects());
    assertEquals(0, userSubject1Content.getChildren().size());

    /* Setting up SubjectManager for next test */
    for (int i = 0; i < 3; i++) {
      subscriptionsMangerOne.getSubjectManagerRecommendations().addSubject(subject);
    }

    assertEquals(4, subscriptionsMangerOne.getSubjectManagerRecommendations()
        .getNumberOfSubjects());

    threadDone = false;
    Platform.runLater(() -> {
      subscriptionsWindowController.addSubjectLink(subject, "Science");
      threadDone = true;
    });

    while (!threadDone) {
      Thread.onSpinWait();
    }

    assertEquals(5, userSubject1Content.getChildren().size());

    threadDone = false;
  }
}