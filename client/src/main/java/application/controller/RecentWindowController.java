package application.controller;

import application.controller.enums.LiveTutorRequestResult;
import application.controller.enums.SubjectRequestResult;
import application.controller.enums.TutorRequestResult;
import application.controller.services.LiveTutorRequestService;
import application.controller.services.MainConnection;
import application.controller.services.SubjectRequestService;
import application.controller.services.TutorRequestService;
import application.model.Account;
import application.model.managers.SubjectManager;
import application.model.managers.TutorManager;
import application.view.ViewFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecentWindowController extends BaseController implements Initializable {

  private SubjectManager subjectManager;
  private SubjectManager subjectManagerRecommendationsOne;
  private SubjectManager subjectManagerRecommendationsTwo;
  private SubjectManager subjectManagerRecommendationsThree;
  private TutorManager tutorManager;
  private TutorManager liveTutorManager;
  private Account account;
  private static final Logger log = LoggerFactory.getLogger("RecentWindowController");
  private MainWindowController parentController;

  private SubjectRequestService subjectRequestService;
  private TutorRequestService tutorRequestService;
  private LiveTutorRequestService liveTutorRequestService;

  @FXML
  private ScrollBar mainScrollBar;

  @FXML
  private ScrollPane mainScrollPane;

  @FXML
  private AnchorPane mainScrollContent;

  @FXML
  private HBox topSubjects;

  @FXML
  private HBox topTutorCarosel;

  @FXML
  private HBox topTutors;

  @FXML
  private Label userSubject1Label;

  @FXML
  private Label userSubject2Label;

  @FXML
  private Label usernameLabel;

  @FXML
  private Label tutorStatusLabel;

  @FXML
  private Pane profilePane;

  @FXML
  private VBox sidePanelVbox;

  @FXML
  private Circle userProfilePicture;

  @FXML
  void caroselLeft(ActionEvent event) {
    final Node source = (Node) event.getSource();
    String id = source.getParent().getId();

    switch (id) {
      case "topSubjectsCarosel":
        goBackTopSubjects();
        break;
      case "topTutorCarosel":
        goBackTopTutors();
        break;
      case "userSubject1Carosel":
        break;
      case "userSubject2Carosel":
        break;
    }
  }

  @FXML
  void caroselRight(ActionEvent event) {
    final Node source = (Node) event.getSource();
    String id = source.getParent().getId();

    switch (id) {
      case "topSubjectsCarosel":
        downloadTopSubjects();
        break;
      case "topTutorCarosel":
        downloadTopTutors();
        break;
      case "userSubject1Carosel":
        break;
      case "userSubject2Carosel":
        break;
    }
  }

  /**
   * This is the default constructor. RecentWindowController
   * extends the BaseController class. This class is controlling
   * a scene that is nested within the MainWindowController.
   *
   * @param viewFactory
   *        The viewFactory used for changing Scenes
   *
   * @param fxmlName
   *        The associated FXML file describing the Login Window
   *
   * @param mainConnection
   *        The connection between client and server
   *
   * @param parentController
   *        This is the controller of the scene this class it is nested within
   */
  public RecentWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, MainWindowController parentController) {
    super(viewFactory, fxmlName, mainConnection);
    this.subjectManager = parentController.getSubjectManager();
    this.tutorManager = parentController.getTutorManager();
    this.liveTutorManager = new TutorManager();
    this.account = parentController.getAccount();
    this.parentController = parentController;
    this.subjectManagerRecommendationsOne = new SubjectManager();
    this.subjectManagerRecommendationsTwo = new SubjectManager();
    this.subjectManagerRecommendationsThree = new SubjectManager();
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    //Connecting Scroll Bar with Scroll Pane
    mainScrollBar.setOrientation(Orientation.VERTICAL);
    mainScrollBar.minProperty().bind(mainScrollPane.vminProperty());
    mainScrollBar.maxProperty().bind(mainScrollPane.vmaxProperty());
    mainScrollBar.visibleAmountProperty().bind(mainScrollPane.heightProperty()
        .divide(mainScrollContent.heightProperty()));
    mainScrollPane.vvalueProperty().bindBidirectional(mainScrollBar.valueProperty());

    downloadTopSubjects();

    //noinspection StatementWithEmptyBody
    while (!subjectRequestService.isFinished()) {
      /* This is used due to race conditions with
       * the JavaFX Service threads accessing the
       * MainConnections DataInputStream. The
       * Thread have a Boolean 'finished' that
       * is initialised as 'false'. This is set
       * true when the thread has completed. */
    }

    downloadTopTutors();

    //noinspection StatementWithEmptyBody
    while (!tutorRequestService.isFinished()) {
    }

    downloadLiveTutors();

    updateAccountViews();
  }

  private void downloadTopSubjects() {
    subjectRequestService = new SubjectRequestService(getMainConnection(), subjectManager, null);

    int subjectsBeforeRequest = subjectManager.getNumberOfSubjects();

    if (!subjectRequestService.isRunning()) {
      subjectRequestService.reset();
      subjectRequestService.start();
    } else {
      log.debug("SubjectRequestService is currently running");
    }

    subjectRequestService.setOnSucceeded(srsEvent -> {
      // TODO This seems to only fire at the end of initialise, which means all values
      // except the last are null. Very odd.
      // Added a new getter get result and this has fixed it. Not sure why getValue was not working.
      SubjectRequestResult srsResult = subjectRequestService.getResult();

      if (subjectsBeforeRequest != subjectManager.getNumberOfSubjects()) {
        topSubjects.getChildren().clear();
        if (srsResult == SubjectRequestResult.SUBJECT_REQUEST_SUCCESS
            || srsResult == SubjectRequestResult.FAILED_BY_NO_MORE_SUBJECTS) {
          AnchorPane[] linkHolder = createLinkHolders(topSubjects);

          ParallelTransition parallelTransition = new ParallelTransition();

          for (int i = subjectsBeforeRequest; i < subjectManager.getNumberOfSubjects(); i++) {
            String subjectName = subjectManager.getSubject(i).getName();
            displayLink(subjectName, parallelTransition, linkHolder[i % 5]);
            linkHolder[i % 5].setOnMouseClicked(e -> setDiscoverAnchorPaneSubject(subjectName) );
          }

          parallelTransition.setCycleCount(1);
          parallelTransition.play();
        } else {
          log.info("SubjectRequestService Result = " + srsResult);
        }
      }
    });
  }

  private void goBackTopSubjects() {
    if ((subjectManager.getNumberOfSubjects() - 10) >= 0) {
      int subjectsBack = subjectManager.getNumberOfSubjects() % 5;

      if (subjectsBack == 0) {
        subjectsBack = 5;
      }

      for (int i = 0; i < subjectsBack; i++) {
        subjectManager.popSubject();
      }

      topSubjects.getChildren().clear();
      AnchorPane[] linkHolder = createLinkHolders(topSubjects);

      ParallelTransition parallelTransition = new ParallelTransition();

      for (int i = subjectManager.getNumberOfSubjects() - 5; i < subjectManager.getNumberOfSubjects() ; i++) {
        String subjectName = subjectManager.getSubject(i).getName();
        displayLink(subjectName, parallelTransition, linkHolder[i % 5]);
        linkHolder[i % 5].setOnMouseClicked(e -> setDiscoverAnchorPaneSubject(subjectName) );
      }

      parallelTransition.setCycleCount(1);
      parallelTransition.play();
    }
  }

  private void downloadTopTutors() {
    tutorRequestService =
        new TutorRequestService(getMainConnection(), tutorManager);

    int tutorsBeforeRequest = tutorManager.getNumberOfTutors();

    if (!tutorRequestService.isRunning()) {
      tutorRequestService.reset();
      tutorRequestService.start();
    }

    tutorRequestService.setOnSucceeded(trsEvent -> {
      TutorRequestResult trsResult = tutorRequestService.getValue();

      if (tutorsBeforeRequest != tutorManager.getNumberOfTutors()) {
        topTutors.getChildren().clear();
        if (trsResult == TutorRequestResult.TUTOR_REQUEST_SUCCESS
            || trsResult == TutorRequestResult.NO_MORE_TUTORS) {
          AnchorPane[] linkHolder = createLinkHolders(topTutors);

          ParallelTransition parallelTransition = new ParallelTransition();

          for (int i = tutorsBeforeRequest; i < tutorManager.getNumberOfTutors(); i++) {
            String tutorName = tutorManager.getTutor(i).getUsername();
            displayLink(tutorName, parallelTransition, linkHolder[i % 5]);
          }

          parallelTransition.setCycleCount(1);
          parallelTransition.play();

        } else {
          log.debug("TutorRequestService Result = " + trsResult);
        }
      }
    });
  }

  private void goBackTopTutors() {
    if ((tutorManager.getNumberOfTutors() - 10) >= 0) {
      int tutorsBack = tutorManager.getNumberOfTutors() % 5;

      if (tutorsBack == 0) {
        tutorsBack = 5;
      }

      for (int i = 0; i < tutorsBack; i++) {
        tutorManager.popTutor();
      }

      topTutors.getChildren().clear();
      AnchorPane[] linkHolder = createLinkHolders(topTutors);

      ParallelTransition parallelTransition = new ParallelTransition();

      for (int i = tutorManager.getNumberOfTutors() - 5; i < tutorManager.getNumberOfTutors() ; i++) {
        String tutorName = tutorManager.getTutor(i).getUsername();
        displayLink(tutorName, parallelTransition, linkHolder[i % 5]);
        linkHolder[i % 5].setOnMouseClicked(e -> setDiscoverAnchorPaneSubject(tutorName) );
      }

      parallelTransition.setCycleCount(1);
      parallelTransition.play();
    }
  }

  private TextField createLink(String text) {
    TextField textField = new TextField(text);
    textField.setAlignment(Pos.CENTER);
    textField.setMouseTransparent(true);
    textField.setFocusTraversable(false);
    textField.setCursor(Cursor.DEFAULT);
    return textField;
  }

  private AnchorPane[] createLinkHolders(HBox hBox) {
    AnchorPane[] anchorPanes = new AnchorPane[5];
    for (int i = 0; i < 5; i++) {
      anchorPanes[i] = new AnchorPane();
      anchorPanes[i].setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
      anchorPanes[i].setPrefSize(150, 100);
      anchorPanes[i].setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
      hBox.getChildren().add(anchorPanes[i]);
    }
    return anchorPanes;
  }

  private void displayLink(String text, ParallelTransition pT, AnchorPane aP) {
    TextField link = createLink(text);

    pT.getChildren().addAll(createFade(link));

    aP.getChildren().add(link);

    aP.setTopAnchor(link, 0.0);
    aP.setBottomAnchor(link, 0.0);
    aP.setLeftAnchor(link, 0.0);
    aP.setRightAnchor(link, 0.0);
  }

  private FadeTransition createFade(TextField l) {
    FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), l);
    fadeTransition.setFromValue(0.0f);
    fadeTransition.setToValue(1.0f);
    fadeTransition.setCycleCount(1);
    fadeTransition.setAutoReverse(true);
    return fadeTransition;
  }

  private void setDiscoverAnchorPaneSubject(String text) {
    int discoverTabPosition = 2;
    try {
      parentController.getDiscoverAnchorPane().getChildren().clear();
      viewFactory
          .embedSubjectWindow(parentController.getDiscoverAnchorPane(), parentController,
              subjectManager.getElementNumber(text));
    } catch (IOException ioe) {
      log.error("Could not embed the Subject Window", ioe);
    }
    parentController.getPrimaryTabPane().getSelectionModel().select(discoverTabPosition);
  }

  // TODO Integrate this into the live tutors vbox
  private void downloadLiveTutors() {
    liveTutorRequestService =
        new LiveTutorRequestService(getMainConnection(), liveTutorManager);

    int tutorsBeforeRequest = liveTutorManager.getNumberOfTutors();

    if (!liveTutorRequestService.isRunning()) {
      liveTutorRequestService.reset();
      liveTutorRequestService.start();
    }

    liveTutorRequestService.setOnSucceeded(trsEvent -> {
      LiveTutorRequestResult trsResult = liveTutorRequestService.getValue();

      if (tutorsBeforeRequest != liveTutorManager.getNumberOfTutors()) {
        if (trsResult == LiveTutorRequestResult.LIVE_TUTOR_REQUEST_SUCCESS
            || trsResult == LiveTutorRequestResult.NO_MORE_LIVE_TUTORS) {

          for (int i = tutorsBeforeRequest; i < liveTutorManager.getNumberOfTutors(); i++) {
            createLiveTutorHolder(liveTutorManager.getTutor(i));
          }
        } else {
          log.debug("LiveTutorRequestService Result = " + trsResult);
        }
      }
    });
  }

  private void createLiveTutorHolder(Account tutor) {
    String tutorName = tutor.getUsername();
    int tutorID = tutor.getUserID();
    float rating = tutor.getRating();
    Image tutorImage = tutor.getProfilePicture();

    sidePanelVbox.getChildren().add(new Separator());

    VBox vBox = new VBox();
    Label nameLabel = new Label(tutorName);
    nameLabel.setAlignment(Pos.CENTER_RIGHT);
    Label ratingLabel = new Label("Tutor Rating: " + rating);
    ratingLabel.setAlignment(Pos.CENTER_RIGHT);

    vBox.getChildren().add(nameLabel);
    vBox.getChildren().add(ratingLabel);
    vBox.setAlignment(Pos.CENTER_RIGHT);
    vBox.setMaxWidth(122);
    vBox.setMaxHeight(60);

    Circle circle = new Circle();
    circle.setCenterX(161);
    circle.setCenterY(30);
    circle.setRadius(25);

    if (tutor.getProfilePicture() != null) {
      ImagePattern imagePattern = new ImagePattern(tutor.getProfilePicture());
      circle.setFill(imagePattern);
    }

    Pane pane = new Pane();
    pane.setMinHeight(60);
    pane.setMaxHeight(60);
    pane.getChildren().add(vBox);
    // TODO Some issue with this
    pane.getChildren().add(circle);
    //pane.setOnMouseClicked(e -> setStreamWindow(tutorID) );

    sidePanelVbox.getChildren().add(pane);

    sidePanelVbox.getChildren().add(new Separator());
  }

  private void setStreamWindow(int sessionID) {
    if (parentController.getPrimaryTabPane().getTabs().size() == 5) {
      parentController.getPrimaryTabPane().getTabs().remove(4);
    }
    AnchorPane anchorPaneStream = new AnchorPane();
    Tab tab = new Tab("Stream");
    tab.setContent(anchorPaneStream);
    parentController.getPrimaryTabPane().getTabs().add(tab);
    try {
      viewFactory.embedStreamWindow(anchorPaneStream, account, sessionID, false);
    } catch (IOException e) {
      e.printStackTrace();
    }
    // TODO This wont send through the correct sessionID for tutor account
    //  joining another tutors stream
    parentController.getPrimaryTabPane().getSelectionModel().select(4);
  }

  /*private void setDiscoverAnchorPaneTutor(String text) {
    try {
      parentController.getDiscoverAnchorPane().getChildren().clear();
      viewFactory
          .embedSubjectWindow(parentController.getDiscoverAnchorPane(), parentController,
              tutorManager.getElementNumber(text));
    } catch (IOException ioe) {
      log.error("Could not embed the Tutor Window", ioe);
    }
    parentController.getPrimaryTabPane().getSelectionModel().select(1);
  }*/

  private void setUpFollowedSubjects() {
    // TODO Move this into the subscriptions window controller
    int numberOfFollowedSubjects = account.getFollowedSubjects().size();

    switch (numberOfFollowedSubjects) {
      case 1:
        userSubject1Label.setText(account.getFollowedSubjects().get(0));
        break;
      case 2:
        userSubject1Label.setText(account.getFollowedSubjects().get(0));
        userSubject2Label.setText(account.getFollowedSubjects().get(1));
        break;
      default:
        userSubject1Label.setText(account.getFollowedSubjects().get(0));
        userSubject2Label.setText(account.getFollowedSubjects().get(1));
//        subjectLabelThree.setText(account.getFollowedSubjects().get(2));
//
//        while (!subjectRequestService.isFinished()) {
//
//        }
//
//        downloadSubjects(hboxThree, subjectManagerRecommendationsOne, account.getFollowedSubjects().get(0));
//
//        while (!subjectRequestService.isFinished()) {
//
//        }
//        downloadSubjects(hboxFour, subjectManagerRecommendationsTwo, account.getFollowedSubjects().get(1));
//        while (!subjectRequestService.isFinished()) {
//
//        }
//
//        downloadSubjects(hboxFive, subjectManagerRecommendationsThree, account.getFollowedSubjects().get(2));
//        break;
    }
  }

  private void updateAccountViews() {
    if (account != null) {
      usernameLabel.setText(account.getUsername());

      if (account.getTutorStatus() == 0) {
        tutorStatusLabel.setText("Student Account");
      } else {
        tutorStatusLabel.setText("Tutor Account");
      }

      if (account.getProfilePicture() != null) {
        ImagePattern imagePattern = new ImagePattern(account.getProfilePicture());
        userProfilePicture.setFill(imagePattern);
      }
    }
  }
}
