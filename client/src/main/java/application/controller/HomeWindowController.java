package application.controller;

import application.controller.enums.SubjectRequestResult;
import application.controller.enums.TutorRequestResult;
import application.controller.services.MainConnection;
import application.model.Account;
import application.model.Subject;
import application.model.Tutor;
import application.model.managers.SubjectManager;
import application.model.managers.TutorManager;
import application.model.requests.SubjectRequestHome;
import application.model.requests.TopTutorsRequest;
import application.view.ViewFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomeWindowController extends BaseController implements Initializable {

  private final SubjectManager subjectManager;
  private int subjectsBeforeRequest;
  private int tutorsBeforeRequest;
  private final TutorManager tutorManager;
  private final HashMap<Integer, Tutor> liveTutorManger;
  private final Account account;
  private final MainWindowController mainWindowController;

  private static final Logger log = LoggerFactory.getLogger("HomeWindowController");

  @FXML
  private VBox homeContent;

  @FXML
  private HBox topSubjects;

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

    liveTutorManger = new HashMap<Integer, Tutor>();
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

    /* Adding reference to controller to the ListenerThread so Subjects, Tutors and Live Tutors
     * can download in the background. Off the Application Thread. */
    getMainConnection().getListener().addHomeWindowController(this);

    downloadTopSubjects();

    downloadTopTutors();

    updateAccountViews();
  }

  private void downloadTopSubjects() {
    subjectsBeforeRequest = subjectManager.getNumberOfSubjects();

    SubjectRequestHome subjectRequestHome = new
        SubjectRequestHome(subjectManager.getNumberOfSubjects(), account.getUserID());
    try {
      //noinspection StatementWithEmptyBody
      while (!getMainConnection().claim()) {
      }
      log.info("Sending Top Subjects Request");
      getMainConnection().sendString(getMainConnection().packageClass(subjectRequestHome));
      getMainConnection().release();
      String serverReply = getMainConnection().listenForString();
      if (serverReply == null) {
        log.error("Downloading Top Subjects: " + String.valueOf(SubjectRequestResult.FAILED_BY_NETWORK));
      } else {
        log.info(serverReply);
      }
    } catch (IOException e) {
      log.error("Could not send request", e);
    }
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
    tutorsBeforeRequest = tutorManager.getNumberOfTutors();
    TopTutorsRequest topTutorsRequest = new TopTutorsRequest(tutorManager.getNumberOfTutors(), account.getUserID());
    try {
      //noinspection StatementWithEmptyBody
      while (!getMainConnection().claim()) {
      }
      log.info("Sending Top Tutor Request");
      getMainConnection().sendString(getMainConnection().packageClass(topTutorsRequest));
      getMainConnection().release();
      String serverReply = getMainConnection().listenForString();
      if (serverReply == null) {
        log.error("Downloading Top Tutors: " + String.valueOf(TutorRequestResult.FAILED_BY_NETWORK));
      } else {
        log.info(serverReply);
      }
    } catch (IOException e) {
      log.error("Could not send request", e);
    }
  }

  public void addTutorLink(Tutor tutor) {
    tutorManager.addTutor(tutor);

    if (tutorManager.getNumberOfTutors() % 5 == 0) {
      topTutors.getChildren().clear();

      AnchorPane[] linkHolder = createLinkHolders(topTutors);

      ParallelTransition parallelTransition = new ParallelTransition();

      for (int i = tutorsBeforeRequest; i < tutorManager.getNumberOfTutors(); i++) {
        Tutor tutorTemp = (Tutor) tutorManager.getTutor(i);
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
        Tutor tutor = (Tutor) tutorManager.getTutor(i);
        String tutorName = tutor.getUsername();
        displayLink(tutorName, parallelTransition, linkHolder[i % 5]);
        linkHolder[i % 5].setOnMouseClicked(e -> setDiscoverAnchorPaneTutor(tutor));
      }

      parallelTransition.setCycleCount(1);
      parallelTransition.play();
    }
  }

  private StackPane createThumbail(String subject) {
    Rectangle rectangle = new Rectangle();
    rectangle.setWidth((homeContent.getWidth() / 5) - 40);
    rectangle.setHeight(120);

    String path = "server" + File.separator + "src" + File.separator + "main"
        + File.separator + "resources" + File.separator + "subjects"
        + File.separator + "thumbnails" + File.separator;

    try {
      FileInputStream input = new FileInputStream(path + subject + "thumbnail.png");
      // create a image
      Image thumbnail = new Image(input);
      ImagePattern imagePattern = new ImagePattern(thumbnail);
      rectangle.setFill(imagePattern);
    } catch (FileNotFoundException fnfe) {
      log.warn("No subject thumbnail on server");
    }
    StackPane stack = new StackPane();
    Text text = new Text(subject);
    text.setId("subjectNames");
    stack.getChildren().addAll(rectangle, text);
    return stack;
  }

  //  private TextField createLink(String text) {
  //    TextField textField = new TextField(text);
  //    textField.setAlignment(Pos.CENTER);
  //    textField.setMouseTransparent(true);
  //    textField.setFocusTraversable(false);
  //    textField.setCursor(Cursor.DEFAULT);
  //    return textField;
  //  }

  private AnchorPane[] createLinkHolders(HBox hBox) {
    AnchorPane[] anchorPanes = new AnchorPane[5];
    double x = (homeContent.getWidth() / 5) - 40;
    for (int i = 0; i < 5; i++) {
      anchorPanes[i] = new AnchorPane();
      anchorPanes[i].setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
      anchorPanes[i].setPrefSize(x, 120);
      anchorPanes[i].setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
      hBox.getChildren().add(anchorPanes[i]);
    }
    return anchorPanes;
  }

  private void displayLink(String text, ParallelTransition pT, AnchorPane aP) {
    StackPane link = createThumbail(text);

    pT.getChildren().addAll(createFade(link));

    aP.getChildren().add(link);

    aP.setTopAnchor(link, 0.0);
    aP.setBottomAnchor(link, 0.0);
    aP.setLeftAnchor(link, 0.0);
    aP.setRightAnchor(link, 0.0);
  }

  private FadeTransition createFade(StackPane l) {
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
          .embedSubjectWindow(mainWindowController.getDiscoverAnchorPane(), mainWindowController,
              subjectManager.getElementNumber(text));
    } catch (IOException ioe) {
      log.error("Could not embed the Subject Window", ioe);
    }
    mainWindowController.getPrimaryTabPane().getSelectionModel().select(discoverTabPosition);
  }

  private void setDiscoverAnchorPaneTutor(Tutor tutor) {
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
