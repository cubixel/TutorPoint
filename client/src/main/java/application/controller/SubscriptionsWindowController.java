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
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
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
  private HBox userSubject1Content;

  @FXML
  private Label userSubject2Label;
  @FXML
  private HBox userSubject2Content;

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

  private StackPane createThumbail(String thumbnailText) {
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

  private FadeTransition createFade(StackPane l) {
    FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), l);
    fadeTransition.setFromValue(0.0f);
    fadeTransition.setToValue(1.0f);
    fadeTransition.setCycleCount(1);
    fadeTransition.setAutoReverse(true);
    return fadeTransition;
  }

  private AnchorPane[] createLinkHolders(HBox hBox) {
    AnchorPane[] anchorPanes = new AnchorPane[5];
    double x = 176;
    for (int i = 0; i < 5; i++) {
      anchorPanes[i] = new AnchorPane();
      anchorPanes[i].setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
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

  private int uniqueRandomNumbers(int max) {
    ArrayList<Integer> list = new ArrayList<Integer>();
    for (int i = 0; i < max; i++) {
      list.add(i);
    }
    Collections.shuffle(list);
    return list.get(0);
  }
}
