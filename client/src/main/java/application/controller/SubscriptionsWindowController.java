package application.controller;

import application.controller.enums.SubjectRequestResult;
import application.controller.services.MainConnection;
import application.model.Account;
import application.model.Subject;
import application.model.managers.SubjectManager;
import application.model.managers.SubscriptionsManger;
import application.model.requests.SubjectRequestSubscription;
import application.view.ViewFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubscriptionsWindowController extends BaseController implements Initializable {

  @FXML
  private ScrollBar mainScrollBar;

  @FXML
  private ScrollPane mainScrollPane;

  @FXML
  private AnchorPane mainScrollContent;

  @FXML
  private Label userSubject1Label;

  @FXML
  private Label infoLabelOne;

  @FXML
  private Label infoLabelTwo;

  @FXML
  private HBox userSubject1Content;

  @FXML
  private Label userSubject2Label;

  @FXML
  private HBox userSubject2Content;

  private final MainWindowController mainWindowController;
  private final SubscriptionsManger subscriptionsMangerOne;
  private final SubscriptionsManger subscriptionsMangerTwo;
  private final Account account;

  private static final Logger log = LoggerFactory.getLogger("SubscriptionsWindowController");

  /**
   * This is the default constructor. SubscriptionsWindowController
   * extends the BaseController class.
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
   * @param mainWindowController
   *        Controller for the top level window
   */
  public SubscriptionsWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, MainWindowController mainWindowController) {
    super(viewFactory, fxmlName, mainConnection);
    this.mainWindowController = mainWindowController;
    subscriptionsMangerOne = new SubscriptionsManger();
    subscriptionsMangerTwo = new SubscriptionsManger();

    account = mainWindowController.getAccount();
  }

  /**
   * This is the constructor used for testing. SubscriptionsWindowController
   * extends the BaseController class.
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
   * @param mainWindowController
   *        Controller for the top level window
   *
   * @param mainScrollBar
   *        A JavaFX
   *
   * @param mainScrollPane
   *        A JavaFX
   *
   * @param mainScrollContent
   *        A JavaFX
   *
   * @param userSubject1Label
   *        A JavaFX
   *
   * @param infoLabelOne
   *        A JavaFX
   *
   * @param infoLabelTwo
   *        A JavaFX
   *
   * @param userSubject1Content
   *        A JavaFX
   *
   * @param userSubject2Label
   *        A JavaFX
   *
   * @param userSubject2Content
   *        A JavaFX
   */
  public SubscriptionsWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, MainWindowController mainWindowController,
      ScrollBar mainScrollBar, ScrollPane mainScrollPane, AnchorPane mainScrollContent,
      Label userSubject1Label, Label infoLabelOne, Label infoLabelTwo, HBox userSubject1Content,
      Label userSubject2Label, HBox userSubject2Content, SubscriptionsManger subscriptionsMangerOne,
      SubscriptionsManger subscriptionsMangerTwo, Account account) {
    super(viewFactory, fxmlName, mainConnection);
    this.mainWindowController = mainWindowController;
    this.mainScrollBar = mainScrollBar;
    this.mainScrollPane = mainScrollPane;
    this.mainScrollContent = mainScrollContent;
    this.userSubject1Label = userSubject1Label;
    this.infoLabelOne = infoLabelOne;
    this.infoLabelTwo = infoLabelTwo;
    this.userSubject1Content = userSubject1Content;
    this.userSubject2Label = userSubject2Label;
    this.userSubject2Content = userSubject2Content;
    this.subscriptionsMangerOne = subscriptionsMangerOne;
    this.subscriptionsMangerTwo = subscriptionsMangerTwo;
    this.account = account;
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    getMainConnection().getListener().addSubscriptionsWindowController(this);

    //Connecting Scroll Bar with Scroll Pane
    mainScrollBar.minProperty().bind(mainScrollPane.vminProperty());
    mainScrollBar.maxProperty().bind(mainScrollPane.vmaxProperty());
    mainScrollBar.visibleAmountProperty().bind(mainScrollPane.heightProperty()
        .divide(mainScrollContent.heightProperty()));
    mainScrollPane.vvalueProperty().bindBidirectional(mainScrollBar.valueProperty());

    if (account.getFollowedSubjects().size() > 0) {
      ArrayList<Integer> list = new ArrayList<>();
      for (int i = 0; i < account.getFollowedSubjects().size(); i++) {
        list.add(i);
      }
      Collections.shuffle(list);
      String subject;
      if (account.getFollowedSubjects().size() == 1) {
        infoLabelOne.setText("Because you liked ");
        subject = account.getFollowedSubjects().get(list.get(0));
        userSubject1Label.setText(subject);
        subscriptionsMangerOne.setReferenceSubject(subject);
        downloadRelatedSubjects(subscriptionsMangerOne);
      } else {
        infoLabelOne.setText("Because you liked ");
        subject = account.getFollowedSubjects().get(list.get(0));
        userSubject1Label.setText(subject);
        subscriptionsMangerOne.setReferenceSubject(subject);
        infoLabelTwo.setText("Because you liked ");
        subject = account.getFollowedSubjects().get(list.get(1));
        userSubject2Label.setText(account.getFollowedSubjects().get(list.get(1)));
        subscriptionsMangerTwo.setReferenceSubject(subject);
        downloadRelatedSubjects(subscriptionsMangerOne);
        downloadRelatedSubjects(subscriptionsMangerTwo);
      }
    }
  }

  /**
   * Sends a request to the server for another set of five
   * subjects based on a subject category.
   */
  private void downloadRelatedSubjects(SubscriptionsManger subscriptionsManger) {
    subscriptionsManger.setNumberOfSubjectsBeforeRequest();

    SubjectRequestSubscription subjectRequestSubscription = new
        SubjectRequestSubscription(subscriptionsManger.getNumberOfSubjectsBeforeRequest(),
        account.getUserID(), subscriptionsManger.getReferenceSubject());
    try {
      //noinspection StatementWithEmptyBody
      while (!getMainConnection().claim()) {
      }
      log.info("Sending Related Subjects Request");
      getMainConnection().sendString(getMainConnection().packageClass(subjectRequestSubscription));
      getMainConnection().release();
      String serverReply = getMainConnection().listenForString();
      if (serverReply == null) {
        log.error("Downloading Related Subjects: " + SubjectRequestResult.FAILED_BY_NETWORK);
      } else {
        log.info(serverReply);
      }
    } catch (IOException e) {
      log.error("Could not send request", e);
    }
  }

  /**
   * Creates a thumbnail link for a subject received from the server.
   * The ListenerThread, upon receiving a subject directed towards
   * the home window will make a call to this method.
   *
   * @param subject
   *        The subject to create a link towards
   *
   * @param likedSubject
   *        A string of the related subject
   */
  public void addSubjectLink(Subject subject, String likedSubject) {
    SubjectManager subjectManager;
    HBox subjectHBox;
    int subjectsBeforeRequest;
    if (likedSubject.equals(userSubject1Label.getText())) {
      subjectManager = subscriptionsMangerOne.getSubjectManagerRecommendations();
      subjectsBeforeRequest = subscriptionsMangerOne.getNumberOfSubjectsBeforeRequest();
      subjectHBox = userSubject1Content;
    } else {
      subjectManager = subscriptionsMangerTwo.getSubjectManagerRecommendations();
      subjectsBeforeRequest = subscriptionsMangerTwo.getNumberOfSubjectsBeforeRequest();
      subjectHBox = userSubject2Content;
    }

    subjectManager.addSubject(subject);

    if (subjectManager.getNumberOfSubjects() % 5 == 0) {
      subjectHBox.getChildren().clear();

      AnchorPane[] linkHolder = createLinkHolders(subjectHBox);

      ParallelTransition parallelTransition = new ParallelTransition();

      for (int i = subjectsBeforeRequest; i < subjectManager.getNumberOfSubjects(); i++) {
        String subjectName = subjectManager.getSubject(i).getName();
        displayLink(subjectName, parallelTransition, linkHolder[i % 5]);
        linkHolder[i % 5].setOnMouseClicked(e -> setDiscoverAnchorPaneSubject(subjectName,
            subjectManager));
      }

      parallelTransition.setCycleCount(1);
      parallelTransition.play();
    }
  }

  /**
   * Creates a thumbnail with an image from the server if one exists.
   *
   * @param thumbnailText
   *        The text to appear in the centre of the thumbnail
   *
   * @return {@code StackPane} containing the thumbnail and the text overlay
   */
  private StackPane createThumbnail(String thumbnailText) {
    Rectangle rectangle = new Rectangle();
    rectangle.setFill(Color.rgb(45, 112, 186));
    rectangle.setWidth(176);
    rectangle.setHeight(120);

    String path = "server" + File.separator + "src" + File.separator + "main"
        + File.separator + "resources" + File.separator + "subjects"
        + File.separator + "thumbnails" + File.separator;

    try {
      String thumbnailTextNoWhitespace = thumbnailText.replaceAll("\\s+","");
      FileInputStream input = new FileInputStream(path
          + thumbnailTextNoWhitespace + "thumbnail.png");
      // create a image
      Image thumbnail = new Image(input);
      ImagePattern imagePattern = new ImagePattern(thumbnail);
      rectangle.setFill(imagePattern);
    } catch (FileNotFoundException fnfe) {
      log.warn("No thumbnail on server for string: " + thumbnailText);
    }
    TextFlow textFlow = new TextFlow();
    textFlow.setMaxWidth(176);
    textFlow.setMaxHeight(120);
    textFlow.setTextAlignment(TextAlignment.CENTER);
    Text text = new Text(thumbnailText);
    text.setId("thumbnailText");
    textFlow.getChildren().add(text);
    StackPane stack = new StackPane();
    stack.getChildren().addAll(rectangle, textFlow);
    return stack;
  }

  /**
   * Creates a fade animation applied to a StackPane to be used when displaying the
   * next five thumbnail.
   *
   * @param stackPane
   *        The StackPane to apply the fade too
   *
   * @return The FadeTransition containing the StackPane
   */
  private FadeTransition createFade(StackPane stackPane) {
    FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), stackPane);
    fadeTransition.setFromValue(0.0f);
    fadeTransition.setToValue(1.0f);
    fadeTransition.setCycleCount(1);
    fadeTransition.setAutoReverse(true);
    return fadeTransition;
  }

  /**
   * Creates an array of AnchorPanes to hold five items
   * to be displayed to the user.
   *
   * @param hbox
   *        The HBox to contain the AnchorPanes
   *
   * @return An Array of AnchorPanes
   */
  private AnchorPane[] createLinkHolders(HBox hbox) {
    AnchorPane[] anchorPanes = new AnchorPane[5];
    double x = 176;
    for (int i = 0; i < 5; i++) {
      anchorPanes[i] = new AnchorPane();
      anchorPanes[i].setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
      anchorPanes[i].setPrefSize(x, 120);
      anchorPanes[i].setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
      hbox.getChildren().add(anchorPanes[i]);
    }
    return anchorPanes;
  }

  /**
   * Creates the link to be displayed.
   *
   * @param text
   *        The text to be seen in the centre of the link
   *
   * @param parallelTransition
   *        JavaFX ParallelTransition
   *
   * @param anchorPane
   *        The AnchorPane to contain the subject
   */
  private void displayLink(String text, ParallelTransition parallelTransition,
      AnchorPane anchorPane) {
    StackPane link = createThumbnail(text);

    parallelTransition.getChildren().addAll(createFade(link));

    anchorPane.getChildren().add(link);

    // TODO is this doing anything since it is 'accessing static member by instance reference'?
    anchorPane.setTopAnchor(link, 0.0);
    anchorPane.setBottomAnchor(link, 0.0);
    anchorPane.setLeftAnchor(link, 0.0);
    anchorPane.setRightAnchor(link, 0.0);
  }

  /**
   * Changes the scene on the Discover tab to the subject the user
   * clicks on and changes the view the user is seeing to that window.
   *
   * @param text
   *        The name of the subject
   *
   * @param subjectManager
   *        The subjectManager containing the subject being selected
   */
  private void setDiscoverAnchorPaneSubject(String text, SubjectManager subjectManager) {
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
}
