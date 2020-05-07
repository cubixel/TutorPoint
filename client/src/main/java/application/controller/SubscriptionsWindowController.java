package application.controller;

import application.controller.enums.SubjectRequestResult;
import application.controller.services.MainConnection;
import application.model.Account;
import application.model.Subject;
import application.model.managers.SubjectManager;
import application.model.managers.SubscriptionsManger;
import application.model.requests.SubjectRequestSubscription;
import application.view.ViewFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubscriptionsWindowController extends BaseController implements Initializable {

  MainWindowController mainWindowController;

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
  private HBox userSubject1Carosel;

  @FXML
  private Button userSubject1Left;

  @FXML
  private HBox userSubject1Content;

  @FXML
  private Button userSubject1Right;

  @FXML
  private Label userSubject2Label;

  @FXML
  private HBox userSubject2Carosel;

  @FXML
  private Button userSubject2Left;

  @FXML
  private HBox userSubject2Content;

  @FXML
  private Button userSubject2Right;

  private SubscriptionsManger subscriptionsMangerOne;
  private SubscriptionsManger subscriptionsMangerTwo;
  private final Account account;

  private static final Logger log = LoggerFactory.getLogger("SubscriptionsWindowController");

  public SubscriptionsWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, MainWindowController mainWindowController) {
    super(viewFactory, fxmlName, mainConnection);
    this.mainWindowController = mainWindowController;
    subscriptionsMangerOne = new SubscriptionsManger();
    subscriptionsMangerTwo = new SubscriptionsManger();

    account = mainWindowController.getAccount();
  }

  @FXML
  void caroselLeft() {

  }

  @FXML
  void caroselRight() {

  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    getMainConnection().getListener().addSubscriptionsWindowController(this);

    if (account.getFollowedSubjects().size() > 0) {
      ArrayList<Integer> list = new ArrayList<Integer>();
      for (int i = 0; i < account.getFollowedSubjects().size(); i++) {
        list.add(i);
      }
      Collections.shuffle(list);
      String subject;
      switch (account.getFollowedSubjects().size()) {
        case 1:
          infoLabelOne.setText("Because you liked ");
          subject = account.getFollowedSubjects().get(list.get(0));
          userSubject1Label.setText(subject);
          subscriptionsMangerOne.setReferenceSubject(subject);
          downloadRelatedSubjects(list.get(0), subscriptionsMangerOne);
          break;
        default:
          infoLabelOne.setText("Because you liked ");
          subject = account.getFollowedSubjects().get(list.get(0));
          userSubject1Label.setText(subject);
          subscriptionsMangerOne.setReferenceSubject(subject);
          infoLabelTwo.setText("Because you liked ");
          subject = account.getFollowedSubjects().get(list.get(1));
          userSubject2Label.setText(account.getFollowedSubjects().get(list.get(1)));
          subscriptionsMangerTwo.setReferenceSubject(subject);
          downloadRelatedSubjects(list.get(0), subscriptionsMangerOne);
          downloadRelatedSubjects(list.get(1), subscriptionsMangerTwo);
          break;
      }
    }
  }

  private void downloadRelatedSubjects(int subject, SubscriptionsManger subscriptionsManger) {
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
        log.error("Downloading Related Subjects: " + String.valueOf(SubjectRequestResult.FAILED_BY_NETWORK));
      } else {
        log.info(serverReply);
      }
    } catch (IOException e) {
      log.error("Could not send request", e);
    }
  }

  public void addSubjectLink(Subject subject, String likedSubject) {
    SubjectManager subjectManager;
    HBox subjectHBox;
    int subjectsBeforeRequest;
    if (likedSubject.equals(userSubject1Label.getText())) {
      subjectManager = subscriptionsMangerOne.getSubjectManagerRecommendations();
      subjectsBeforeRequest = subscriptionsMangerOne.getNumberOfSubjectsBeforeRequest();
      subjectHBox = userSubject1Carosel;
    } else {
      subjectManager = subscriptionsMangerTwo.getSubjectManagerRecommendations();
      subjectsBeforeRequest = subscriptionsMangerTwo.getNumberOfSubjectsBeforeRequest();
      subjectHBox = userSubject2Carosel;
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

  private TextField createLink(String text) {
    TextField textField = new TextField(text);
    textField.setAlignment(Pos.CENTER);
    textField.setMouseTransparent(true);
    textField.setFocusTraversable(false);
    textField.setCursor(Cursor.DEFAULT);
    return textField;
  }

  private FadeTransition createFade(TextField l) {
    FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), l);
    fadeTransition.setFromValue(0.0f);
    fadeTransition.setToValue(1.0f);
    fadeTransition.setCycleCount(1);
    fadeTransition.setAutoReverse(true);
    return fadeTransition;
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

  private void setDiscoverAnchorPaneSubject(String text, SubjectManager subjectManager) {
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

  private int UniqueRandomNumbers(int max) {
    ArrayList<Integer> list = new ArrayList<Integer>();
    for (int i = 0; i < max; i++) {
      list.add(i);
    }
    Collections.shuffle(list);
    return list.get(0);
  }
}
