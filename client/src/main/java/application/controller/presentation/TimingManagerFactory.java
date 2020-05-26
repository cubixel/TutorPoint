package application.controller.presentation;

import javafx.scene.layout.StackPane;

/**
 * This class is necessary to test the PresentationWindowController class as
 * it means a Mockito version of the TimingManager class can
 * be instantiated when the TimingManagerFactory.createTimingManager()
 * method is called.
 *
 * @author James Gardner
 */
public class TimingManagerFactory {

  public TimingManagerFactory() {

  }

  public TimingManager createTimingManager(PresentationObject presentationObject,
      StackPane stackPane) {
    return new TimingManager(presentationObject, stackPane);
  }
}
