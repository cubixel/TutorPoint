package application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import application.controller.enums.FollowTutorResult;
import application.controller.services.ListenerThread;
import application.controller.services.MainConnection;
import application.model.Tutor;
import application.view.ViewFactory;
import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import org.mockito.Mock;

/**
 * This class tests the TutorWindowController. It checks
 * that the Tutor Window initialises correctly based on
 * if the user is a student or tutor and that following
 * a tutor is working as expected.
 *
 * @author James Gardner
 * @see TutorWindowController
 */
public class TutorWindowControllerTest {
  /* Creating the Mock Objects necessary for the test. */
  @Mock
  protected MainConnection mainConnectionMock;

  @Mock
  protected ViewFactory viewFactoryMock;

  @Mock
  protected ListenerThread listenerThreadMock;

  @Mock
  protected MainWindowController mainWindowControllerMock;

  protected Tutor tutor;
  protected AnchorPane parentAnchorPane;
  protected Button backToDiscoverButton;
  protected Label tutorNameLabel;
  protected Button backToHomeButton;
  protected Button followTutorButton;
  protected Circle profilePictureHolder;
  protected Label tutorRatingLabel;
  protected Label followingTutorLabel;
  protected Slider ratingSlider;
  protected Button submitRatingButton;
  protected HBox subjectsHBox;

  TutorWindowController tutorWindowController;

  /**
   * This is testing initialiseAsTutor.
   */
  public void followingTutorLabelAndButtonTest() {
    tutor.setFollowed(true);
    tutorWindowController.initialize(null, null);
    assertEquals("You are following this tutor", followingTutorLabel.getText());
    assertEquals("Unfollow Tutor", followTutorButton.getText());
    assertEquals(tutor.getUsername(), tutorNameLabel.getText());
    assertEquals(String.valueOf(Math.round(tutor.getRating())), tutorRatingLabel.getText());
  }

  /**
   * This is testing initialiseAsTutor.
   */
  public void notFollowingTutorLabelAndButtonTest() {
    tutor.setFollowed(false);
    tutorWindowController.initialize(null, null);
    assertEquals("You are not following this tutor", followingTutorLabel.getText());
    assertEquals("Follow Tutor", followTutorButton.getText());
    assertEquals(tutor.getUsername(), tutorNameLabel.getText());
    assertEquals(String.valueOf(Math.round(tutor.getRating())), tutorRatingLabel.getText());
  }

  /**
   * This is testing followSubjectAction.
   */
  public void followTutorActionTest() {
    /* Test Setup */
    try {
      when(mainConnectionMock.claim()).thenReturn(true);
      when(mainConnectionMock.listenForString()).thenReturn(
          String.valueOf(FollowTutorResult.FOLLOW_TUTOR_RESULT_SUCCESS));
    } catch (IOException e) {
      fail();
    }

    tutorWindowController.initialize(null, null);
    assertEquals("You are following this tutor", followingTutorLabel.getText());
    assertEquals("Unfollow Tutor", followTutorButton.getText());

    Platform.runLater(() -> tutorWindowController.followTutorButton());

    long start = System.currentTimeMillis();
    long end = start + 3000;

    /*
     * Waiting for thread to catch up.
     */
    while (System.currentTimeMillis() < end) {
      Thread.onSpinWait();
    }

    assertFalse(tutor.isFollowed());
    assertEquals("You are not following this tutor", followingTutorLabel.getText());
    assertEquals("Follow Tutor", followTutorButton.getText());
  }
}