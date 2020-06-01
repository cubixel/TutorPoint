package application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import application.controller.services.ListenerThread;
import application.controller.services.MainConnection;
import application.model.Account;
import application.model.Subject;
import application.model.Tutor;
import application.model.managers.SubjectManager;
import application.model.managers.TutorManager;
import application.view.ViewFactory;
import java.util.HashMap;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.mockito.Mock;

/**
 * This class tests the HomeWindowController. It tests
 * updating the HomeWindow with new Subject, Tutors and
 * live Tutors.
 *
 * @author James Gardner
 * @see HomeWindowController
 */
public class HomeWindowControllerTest {

  /* Creating the Mock Objects necessary for the test. */
  @Mock
  protected MainConnection mainConnectionMock;

  @Mock
  protected ListenerThread listenerThreadMock;

  @Mock
  protected ViewFactory viewFactoryMock;

  @Mock
  protected MainWindowController mainWindowControllerMock;

  @Mock
  protected Account accountMock;

  protected SubjectManager subjectManager;
  protected TutorManager tutorManager;

  protected VBox homeContent;
  protected HBox topSubjects;
  protected HBox topTutors;
  protected Label usernameLabel;
  protected Label tutorStatusLabel;
  protected Pane profilePane;
  protected VBox liveTutorsVbox;
  protected Circle userProfilePicture;
  protected HashMap<Integer, Tutor> liveTutorManger;

  protected HomeWindowController homeWindowController;

  volatile boolean threadDone;

  /**
   * This is testing the addSubjectLink method.
   */
  public void addSubjectLinkTest() {
    threadDone = false;

    Subject subject = new Subject(1,"TestSubject", "TestCategory", true);
    assertEquals(0, subjectManager.getNumberOfSubjects());
    assertEquals(0, topSubjects.getChildren().size());
    Platform.runLater(() -> {
      homeWindowController.addSubjectLink(subject);
      threadDone = true;
    });

    while (!threadDone) {
      Thread.onSpinWait();
    }

    assertEquals(1, subjectManager.getNumberOfSubjects());
    assertEquals(0, topSubjects.getChildren().size());

    /* Setting up SubjectManager for next test */
    for (int i = 0; i < 3; i++) {
      subjectManager.addSubject(subject);
    }

    assertEquals(4, subjectManager.getNumberOfSubjects());

    threadDone = false;
    Platform.runLater(() -> {
      homeWindowController.addSubjectLink(subject);
      threadDone = true;
    });

    while (!threadDone) {
      Thread.onSpinWait();
    }

    assertEquals(5, topSubjects.getChildren().size());

    threadDone = false;
  }

  /**
   * This is testing the addTutorLink method.
   */
  public void addTutorLinkTest() {
    threadDone = false;

    Tutor tutor = new Tutor("TestTutor", 1, 3, true);
    assertEquals(0, tutorManager.getNumberOfTutors());
    assertEquals(0, topTutors.getChildren().size());
    Platform.runLater(() -> {
      homeWindowController.addTutorLink(tutor);
      threadDone = true;
    });

    while (!threadDone) {
      Thread.onSpinWait();
    }

    assertEquals(1, tutorManager.getNumberOfTutors());
    assertEquals(0, topTutors.getChildren().size());

    /* Setting up SubjectManager for next test */
    for (int i = 0; i < 3; i++) {
      tutorManager.addTutor(tutor);
    }

    assertEquals(4, tutorManager.getNumberOfTutors());

    threadDone = false;
    Platform.runLater(() -> {
      homeWindowController.addTutorLink(tutor);
      threadDone = true;
    });

    while (!threadDone) {
      Thread.onSpinWait();
    }

    assertEquals(5, topTutors.getChildren().size());

    threadDone = false;
  }

  /**
   * This is testing the addLiveTutorLink method.
   */
  public void addLiveTutorLinkTest() {
    threadDone = false;

    Tutor tutor = new Tutor("TestTutor", 1, 3, true);
    tutor.setLive(true);

    assertEquals(0, liveTutorManger.size());
    assertEquals(0, liveTutorsVbox.getChildren().size());
    Platform.runLater(() -> {
      homeWindowController.addLiveTutorLink(tutor);
      threadDone = true;
    });

    while (!threadDone) {
      Thread.onSpinWait();
    }

    assertEquals(3, liveTutorsVbox.getChildren().size());
  }
}