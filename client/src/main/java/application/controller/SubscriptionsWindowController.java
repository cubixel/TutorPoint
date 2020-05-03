package application.controller;

import application.controller.enums.SubjectRequestResult;
import application.controller.services.MainConnection;
import application.model.Account;
import application.model.Subject;
import application.model.managers.SubjectManager;
import application.model.requests.SubjectRequestHome;
import application.model.requests.SubjectRequestSubscription;
import application.view.ViewFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import javafx.animation.ParallelTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
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

  private final SubjectManager subjectManagerRecommendationsOne;
  private final SubjectManager subjectManagerRecommendationsTwo;
  private int subjectsBeforeRequest;
  private final Account account;

  private static final Logger log = LoggerFactory.getLogger("SubscriptionsWindowController");

  public SubscriptionsWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, MainWindowController mainWindowController) {
    super(viewFactory, fxmlName, mainConnection);
    this.mainWindowController = mainWindowController;
    subjectManagerRecommendationsOne = new SubjectManager();
    subjectManagerRecommendationsTwo = new SubjectManager();
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
      switch (account.getFollowedSubjects().size()) {
        case 1:
          // downloadRelatedSubjects(list.get(0), subjectManagerRecommendationsOne);
          infoLabelOne.setText("Because you liked ");
          userSubject1Label.setText(account.getFollowedSubjects().get(list.get(0)));
          break;
        case 2:
          infoLabelOne.setText("Because you liked ");
          userSubject1Label.setText(account.getFollowedSubjects().get(list.get(0)));
          infoLabelTwo.setText("Because you liked ");
          userSubject2Label.setText(account.getFollowedSubjects().get(list.get(1)));
          // downloadRelatedSubjects(list.get(0), subjectManagerRecommendationsOne);
          // downloadRelatedSubjects(list.get(1), subjectManagerRecommendationsTwo);
          break;
      }
    }
  }

  private void downloadRelatedSubjects(int subject, SubjectManager subjectManager) {
    subjectsBeforeRequest = subjectManager.getNumberOfSubjects();

    SubjectRequestSubscription subjectRequestSubscription = new
        SubjectRequestSubscription(subjectManager.getNumberOfSubjects(), account.getUserID(),
        String.valueOf(account.getFollowedSubjects().get(subject)));
    try {
      getMainConnection().sendString(getMainConnection().packageClass(subjectRequestSubscription));
      String serverReply = getMainConnection().listenForString();
      if (serverReply == null) {
        log.error(String.valueOf(SubjectRequestResult.FAILED_BY_NETWORK));
      } else {
        log.info(serverReply);
      }
    } catch (IOException e) {
      log.error("Could not send request", e);
    }
  }

//  public void addSubjectLink(Subject subject) {
//    subjectManager.addSubject(subject);
//
//    if (subjectManager.getNumberOfSubjects() % 5 == 0) {
//      topSubjects.getChildren().clear();
//
//      AnchorPane[] linkHolder = createLinkHolders(topSubjects);
//
//      ParallelTransition parallelTransition = new ParallelTransition();
//
//      for (int i = subjectsBeforeRequest; i < subjectManager.getNumberOfSubjects(); i++) {
//        String subjectName = subjectManager.getSubject(i).getName();
//        displayLink(subjectName, parallelTransition, linkHolder[i % 5]);
//        linkHolder[i % 5].setOnMouseClicked(e -> setDiscoverAnchorPaneSubject(subjectName) );
//      }
//
//      parallelTransition.setCycleCount(1);
//      parallelTransition.play();
//    }
//  }

  private int UniqueRandomNumbers(int max) {
    ArrayList<Integer> list = new ArrayList<Integer>();
    for (int i = 0; i < max; i++) {
      list.add(i);
    }
    Collections.shuffle(list);
    return list.get(0);
  }
}
