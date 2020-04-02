package application.controller;

import application.controller.services.MainConnection;
import application.view.ViewFactory;
import application.view.ViewInitialiser;

/**
 * This abstract Class is the base of all controllers. It provides
 * a common set of methods and fields that all controllers must implement.
 *
 * @author James Gardner
 * @see    ViewInitialiser
 * @see    ViewFactory
 * @see    LoginWindowController
 */
public abstract class BaseController {

  protected ViewFactory viewFactory;
  private String fxmlName;
  private MainConnection mainConnection;

  /**
   * Constructor that all controllers must use.
   *
   * @param viewFactory
   *        The ViewFactory creates windows that are controlled by the controller.
   *
   * @param fxmlName
   *        The FXML file that describes a window the controller contains the logic for.
   *        
   * @param mainConnection
   *        This is the main connection to the server, established on startup.
   */
  public BaseController(ViewFactory viewFactory, String fxmlName, MainConnection mainConnection) {
    this.viewFactory = viewFactory;
    this.fxmlName = fxmlName;
    this.mainConnection = mainConnection;
  }

  public String getFxmlName() {
    return fxmlName;
  }

  public MainConnection getMainConnection() {
    return mainConnection;
  }

  public ViewFactory getViewFactory() {
    return viewFactory;
  }
}
