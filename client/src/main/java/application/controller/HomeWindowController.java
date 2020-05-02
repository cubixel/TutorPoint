package application.controller;

import application.controller.enums.SubjectRequestResult;
import application.controller.enums.TutorRequestResult;
import application.controller.services.MainConnection;
import application.controller.services.SubjectRequestService;
import application.controller.services.TutorRequestService;
import application.model.Account;
import application.model.Subject;
import application.model.Tutor;
import application.model.managers.SubjectManager;
import application.model.managers.TutorManager;
import application.view.ViewFactory;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
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

public class HomeWindowController extends BaseController implements Initializable {

  private final SubjectManager subjectManager;
  private int subjectsBeforeRequest;
  private int tutorsBeforeRequest;
  private SubjectManager subjectManagerRecommendationsOne;
  private SubjectManager subjectManagerRecommendationsTwo;
  private SubjectManager subjectManagerRecommendationsThree;
  private final TutorManager tutorManager;
  private final HashMap<Integer, Tutor> liveTutorManger;
  private final Account account;
  private final MainWindowController mainWindowController;
  private SubjectRequestService subjectRequestService;
  private TutorRequestService tutorRequestService;

  private static final Logger log = LoggerFactory.getLogger("HomeWindowController");

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
  private VBox liveTutorsVbox;

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
   * This is the default constructor. HomeWindowController
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
   */
  public HomeWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, MainWindowController mainWindowController) {
    super(viewFactory, fxmlName, mainConnection);
    this.mainWindowController = mainWindowController;
    this.subjectManager = mainWindowController.getSubjectManager();
    this.tutorManager = mainWindowController.getTutorManager();
    this.account = mainWindowController.getAccount();
    this.subjectManagerRecommendationsOne = new SubjectManager();
    this.subjectManagerRecommendationsTwo = new SubjectManager();
    this.subjectManagerRecommendationsThree = new SubjectManager();

    liveTutorManger = new HashMap<Integer, Tutor>();
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

    /* Adding reference to controller to the ListenerThread so Subjects, Tutors and Live Tutors
     * can download in the background. Off the Application Thread. */
    getMainConnection().getListener().addHomeWindowController(this);

    downloadTopSubjects();

    downloadTopTutors();

    updateAccountViews();
  }

  private void checkSafeToDownload() {
    try {
      //noinspection StatementWithEmptyBody
      while (!subjectRequestService.isFinished()) {
      }
    } catch (NullPointerException e) {
      log.info("Downloading first subjects");
    }

    try {
      //noinspection StatementWithEmptyBody
      while (!tutorRequestService.isFinished()) {
      }
    } catch (NullPointerException e) {
      log.info("Downloading first top tutors");
    }
  }

  private void downloadTopSubjects() {
    checkSafeToDownload();

    subjectRequestService = new SubjectRequestService(getMainConnection(), subjectManager,
        null, account.getUserID());

    subjectsBeforeRequest = subjectManager.getNumberOfSubjects();

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
      log.info("SubjectRequestService Result = " + srsResult);
    });
  }

  public void addSubjectLink(Subject subject) {
    subjectManager.addSubject(subject);

    if (subjectManager.getNumberOfSubjects() % 5 == 0) {
      topSubjects.getChildren().clear();

      AnchorPane[] linkHolder = createLinkHolders(topSubjects);

      ParallelTransition parallelTransition = new ParallelTransition();

      for (int i = subjectsBeforeRequest; i < subjectManager.getNumberOfSubjects(); i++) {
        String subjectName = subjectManager.getSubject(i).getName();
        displayLink(subjectName, parallelTransition, linkHolder[i % 5]);
        linkHolder[i % 5].setOnMouseClicked(e -> setDiscoverAnchorPaneSubject(subjectName) );
      }

      parallelTransition.setCycleCount(1);
      parallelTransition.play();
    }
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
    checkSafeToDownload();

    tutorRequestService =
        new TutorRequestService(getMainConnection(), tutorManager, account.getUserID());

    tutorsBeforeRequest = tutorManager.getNumberOfTutors();

    if (!tutorRequestService.isRunning()) {
      tutorRequestService.reset();
      tutorRequestService.start();
    }

    tutorRequestService.setOnSucceeded(trsEvent -> {
      TutorRequestResult trsResult = tutorRequestService.getValue();
      log.info("TutorRequestService Result = " + trsResult);
    });
  }

  public void addTutorLink(Tutor tutor) {
    tutorManager.addTutor(tutor);

    if (tutorManager.getNumberOfTutors() % 5 == 0) {
      topTutors.getChildren().clear();

      AnchorPane[] linkHolder = createLinkHolders(topTutors);

      ParallelTransition parallelTransition = new ParallelTransition();

      for (int i = tutorsBeforeRequest; i < tutorManager.getNumberOfTutors(); i++) {
        Account tutorTemp = tutorManager.getTutor(i);
        String tutorName = tutorTemp.getUsername();
        displayLink(tutorName, parallelTransition, linkHolder[i % 5]);
        linkHolder[i % 5].setOnMouseClicked(e -> setDiscoverAnchorPaneTutor(tutorTemp) );
      }

      parallelTransition.setCycleCount(1);
      parallelTransition.play();
    }
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
        Account tutor = tutorManager.getTutor(i);
        String tutorName = tutor.getUsername();
        displayLink(tutorName, parallelTransition, linkHolder[i % 5]);
        linkHolder[i % 5].setOnMouseClicked(e -> setDiscoverAnchorPaneTutor(tutor) );
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
      mainWindowController.getDiscoverAnchorPane().getChildren().clear();
      viewFactory
          .embedSubjectWindow(mainWindowController.getDiscoverAnchorPane(), subjectManager.getElementNumber(text));
    } catch (IOException ioe) {
      log.error("Could not embed the Subject Window", ioe);
    }
    mainWindowController.getPrimaryTabPane().getSelectionModel().select(discoverTabPosition);
  }

  private void setDiscoverAnchorPaneTutor(Account tutor) {
    int discoverTabPosition = 2;
    try {
      mainWindowController.getDiscoverAnchorPane().getChildren().clear();
      viewFactory
          .embedTutorWindow(mainWindowController.getDiscoverAnchorPane(), mainWindowController, tutor);
    } catch (IOException ioe) {
      log.error("Could not embed the Subject Window", ioe);
    }
    mainWindowController.getPrimaryTabPane().getSelectionModel().select(discoverTabPosition);
  }

  /**
   * Creat a place holder on the right of the window
   * that shows the tutors name, rating and profile picture.
   *
   * @param tutor
   *        An Account class containing basic Tutor information
   */
  public void addLiveTutorLink(Tutor tutor) {
    liveTutorManger.put(tutor.getUserID(), tutor);
    liveTutorsVbox.getChildren().clear();

    liveTutorManger.forEach((key, object) -> {
      if (object.isLive()) {
        String tutorName = object.getUsername();
        int tutorID = object.getUserID();
        float rating = object.getRating();

        liveTutorsVbox.getChildren().add(new Separator());

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
        pane.getChildren().add(circle);
        pane.setOnMouseClicked(e -> setStreamWindow(tutorID));

        liveTutorsVbox.getChildren().add(pane);

        liveTutorsVbox.getChildren().add(new Separator());
      }
    });
  }

  /**
   * When the user clicks on a live tutors place holder on the side
   * it opens the Stream Window and joins that live tutors session.
   *
   * @param sessionID
   *        An integer uniquely identifying the session, use the tutor's ID
   */
  private void setStreamWindow(int sessionID) {
    // TODO currently parent controller is passed in as an argument to
    //  the constructor. ViewFactory has been modified to allow access
    //  to any window controller by calling getWindowControllers()
    if (mainWindowController.getPrimaryTabPane().getTabs().size() == 5) {
      mainWindowController.getPrimaryTabPane().getTabs().remove(4);
    }
    AnchorPane anchorPaneStream = new AnchorPane();
    Tab tab = new Tab("Stream");
    tab.setContent(anchorPaneStream);
    mainWindowController.getPrimaryTabPane().getTabs().add(tab);
    try {
      viewFactory.embedStreamWindow(anchorPaneStream, account, sessionID, false);
    } catch (IOException e) {
      e.printStackTrace();
    }
    // TODO This wont send through the correct sessionID for tutor account
    //  joining another tutors stream
    mainWindowController.getPrimaryTabPane().getSelectionModel().select(4);
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
