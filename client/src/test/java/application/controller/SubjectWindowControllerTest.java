package application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import application.controller.enums.FollowSubjectResult;
import application.controller.services.MainConnection;
import application.model.Account;
import application.model.Subject;
import application.view.ViewFactory;
import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.mockito.Mock;

/**
 * This class tests the SubjectWindowController. It checks
 * that the Subject Window initialises correctly based on
 * if the user is a student or tutor and that following
 * a subject is working as expected.
 *
 * @author James Gardner
 * @see SubjectWindowController
 */
public class SubjectWindowControllerTest {

  /* Creating the Mock Objects necessary for the test. */
  @Mock
  protected MainConnection mainConnectionMock;

  @Mock
  protected ViewFactory viewFactoryMock;

  @Mock
  protected MainWindowController mainWindowControllerMock;

  @Mock
  protected Account accountMock;

  protected SubjectWindowController subjectWindowController;
  protected Subject subject;
  protected ImageView headerImageView;
  protected Label followingSubjectLabel;
  protected Button followSubjectButton;
  protected Button teachSubjectButton;
  protected Button backToDiscoverButton;
  protected Label subjectLabel;

  /**
   * This is testing initialiseAsStudent.
   */
  public void initialiseAsStudentTest() {
    when(accountMock.getTutorStatus()).thenReturn(0);
    assertTrue(teachSubjectButton.isVisible());
    subjectWindowController.initialize(null, null);
    assertFalse(teachSubjectButton.isVisible());
  }

  /**
   * This is testing initialiseAsTutor.
   */
  public void initialiseAsTutorTest() {
    when(accountMock.getTutorStatus()).thenReturn(1);
    assertTrue(teachSubjectButton.isVisible());
    subjectWindowController.initialize(null, null);
    assertTrue(teachSubjectButton.isVisible());
  }

  /**
   * This is testing initialiseAsTutor.
   */
  public void followingSubjectLabelAndButtonTest() {
    subject.setFollowed(true);
    subjectWindowController.initialize(null, null);
    assertEquals("You are following this subject", followingSubjectLabel.getText());
    assertEquals("Unfollow Subject", followSubjectButton.getText());
  }

  /**
   * This is testing initialiseAsTutor.
   */
  public void notFollowingSubjectLabelAndButtonTest() {
    subject.setFollowed(false);
    subjectWindowController.initialize(null, null);
    assertEquals("You are not following this subject", followingSubjectLabel.getText());
    assertEquals("Follow Subject", followSubjectButton.getText());
  }

  /**
   * This is testing followSubjectAction.
   */
  public void followSubjectActionTest() {
    /* Test Setup */
    try {
      when(mainConnectionMock.claim()).thenReturn(true);
      when(mainConnectionMock.listenForString()).thenReturn(
          String.valueOf(FollowSubjectResult.FOLLOW_SUBJECT_RESULT_SUCCESS));
    } catch (IOException e) {
      fail();
    }

    subjectWindowController.initialize(null, null);
    assertEquals("You are following this subject", followingSubjectLabel.getText());
    assertEquals("Unfollow Subject", followSubjectButton.getText());

    Platform.runLater(() -> subjectWindowController.followSubjectButton());

    long start = System.currentTimeMillis();
    long end = start + 2000;

    /*
     * Waiting for thread to catch up.
     */
    while (System.currentTimeMillis() < end) {
      Thread.onSpinWait();
    }

    assertFalse(subject.isFollowed());
    assertEquals("You are not following this subject", followingSubjectLabel.getText());
    assertEquals("Follow Subject", followSubjectButton.getText());
  }
}
