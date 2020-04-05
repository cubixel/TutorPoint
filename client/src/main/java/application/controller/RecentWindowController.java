package application.controller;

import application.controller.enums.SubjectRequestResult;
import application.controller.services.MainConnection;
import application.controller.services.SubjectRequestService;
import application.model.Account;
import application.model.managers.SubjectManager;
import application.view.ViewFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecentWindowController extends BaseController implements Initializable {

  private SubjectManager subjectManager;
  private Account account;
  private static final Logger log = LoggerFactory.getLogger("Client Logger");
  private MainWindowController parentController;

  @FXML
  private ImageView tutorAvatarOne;

  @FXML
  private Label tutorLabelOne;

  @FXML
  private ImageView tutorAvatarTwo;

  @FXML
  private Label tutorLabelTwo;

  @FXML
  private ImageView tutorAvatarThree;

  @FXML
  private Label tutorLabelThree;

  @FXML
  private ImageView tutorAvatarFour;

  @FXML
  private Label tutorLabelFour;

  @FXML
  private ImageView tutorAvatarFive;

  @FXML
  private Label tutorLabelFive;

  @FXML
  private ScrollBar mainRecentScrollBar;

  @FXML
  private ScrollPane mainRecentScrollPane;

  @FXML
  private AnchorPane mainRecentScrollContent;

  @FXML
  private ScrollPane topSubjectsScrollPane;

  @FXML
  private HBox hboxOne;

  @FXML
  private HBox hboxTwo;

  @FXML
  private Label subjectLabelOne;

  @FXML
  private HBox hboxThree;

  @FXML
  private Label subjectLabelTwo;

  @FXML
  private HBox hboxFour;

  @FXML
  private Label subjectLabelThree;

  @FXML
  private HBox hboxFive;

  /**
   * Constructor that all controllers must use.
   *
   * @param viewFactory    The ViewFactory creates windows that are controlled by the controller.
   * @param fxmlName       The FXML file that describes a window the controller contains the logic
   *                       for.
   * @param mainConnection
   */
  public RecentWindowController(ViewFactory viewFactory, String fxmlName,
      MainConnection mainConnection, Account account, SubjectManager subjectManager,
      MainWindowController parentController) {
    super(viewFactory, fxmlName, mainConnection);
    this.subjectManager  = subjectManager;
    this.account = account;
    this.parentController = parentController;
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

    //Connecting Scroll Bar with Scroll Pane
    mainRecentScrollBar.setOrientation(Orientation.VERTICAL);
    mainRecentScrollBar.minProperty().bind(mainRecentScrollPane.vminProperty());
    mainRecentScrollBar.maxProperty().bind(mainRecentScrollPane.vmaxProperty());
    mainRecentScrollBar.visibleAmountProperty().bind(mainRecentScrollPane.heightProperty().divide(mainRecentScrollContent.heightProperty()));
    mainRecentScrollPane.vvalueProperty().bindBidirectional(mainRecentScrollBar.valueProperty());

    /* TODO Set Up Screen
     * Request from server the top set of subjects.
     * with each one get the server to send the thumbnail too.
     * Fill out the display with the subjects and the thumbnails
     *
     * */
    downloadSubjects();

  }

  @FXML
  void hBoxMouserClickedAction(MouseEvent event) {
    int widthOfImages = 225;

    if (event.getTarget() instanceof ImageView) {
      // TODO Not sure if this way leads to a solution but looks hopeful
      ImageView imageView = (ImageView) event.getTarget();
      imageView.getImage().getUrl(); // This just returns null
    }
    // TODO fix for widths of variable size
    int element = (int) event.getX()/widthOfImages;
    try {
      parentController.getDiscoverAnchorPane().getChildren().clear();
      viewFactory.embedSubjectWindow(parentController.getDiscoverAnchorPane(), account, subjectManager, element);
    } catch (IOException e) {
      e.printStackTrace();
    }
    parentController.getPrimaryTabPane().getSelectionModel().select(1);
  }

  private void downloadSubjects() {
    //TODO Lots of error handling.
    SubjectRequestService subjectRequestService =
        new SubjectRequestService(getMainConnection(), subjectManager);

    int subjectsBeforeRequest = subjectManager.getNumberOfSubjects();

    if (!subjectRequestService.isRunning()) {
      subjectRequestService.reset();
      subjectRequestService.start();
    }
    subjectRequestService.setOnSucceeded(srsEvent -> {
      SubjectRequestResult srsResult = subjectRequestService.getValue();

      if (srsResult == SubjectRequestResult.SUCCESS || srsResult == SubjectRequestResult.FAILED_BY_NO_MORE_SUBJECTS) {
        FileInputStream input = null;
        for (int i = subjectsBeforeRequest; i < subjectManager.getNumberOfSubjects(); i++) {
          try {
            input = new FileInputStream(subjectManager.getSubject(i).getThumbnailPath());
            Image image = new Image(input);
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(130);
            imageView.setFitWidth(225);
            hboxOne.getChildren().add(imageView);
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          }
        }
      } else {
        System.out.println("Here in mainController " + srsResult);
      }
    });
  }
}
