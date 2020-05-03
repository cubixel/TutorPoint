package application.controller;

import application.controller.services.MainConnection;
import application.model.Account;
import application.model.Subject;
import application.model.managers.SubjectManager;
import application.model.managers.SubscriptionsManger;
import application.model.requests.SubjectRequestHome;
import application.model.requests.SubjectRequestSubscription;
import application.view.ViewFactory;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

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
  private SubscriptionsManger subscriptionsMangerOne;
  private final Account account;

  private static final Logger log = LoggerFactory.getLogger("SubscriptionsWindowController");

  public SubscriptionsWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, MainWindowController mainWindowController) {
    super(viewFactory, fxmlName, mainConnection);
    this.mainWindowController = mainWindowController;
    subjectManagerRecommendationsOne = new SubjectManager();
    subjectManagerRecommendationsTwo = new SubjectManager();
    subscriptionsMangerOne = new SubscriptionsManger();
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
          // downloadRelatedSubjects(list.get(0), subjectManagerRecommendationsOne);
          infoLabelOne.setText("Because you liked ");
          subject = account.getFollowedSubjects().get(list.get(0));
          userSubject1Label.setText(subject);
          subscriptionsMangerOne.setReferenceSubject(subject);
          break;
        default:
          infoLabelOne.setText("Because you liked ");
          subject = account.getFollowedSubjects().get(list.get(0));
          userSubject1Label.setText(subject);
          subscriptionsMangerOne.setReferenceSubject(subject);
          infoLabelTwo.setText("Because you liked ");
          userSubject2Label.setText(account.getFollowedSubjects().get(list.get(1)));
          // downloadRelatedSubjects(list.get(0), subjectManagerRecommendationsOne);
          // downloadRelatedSubjects(list.get(1), subjectManagerRecommendationsTwo);
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

  }
}
