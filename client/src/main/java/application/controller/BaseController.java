/*
 * BaseController.java
 * Version: 0.1.0
 * Company: CUBIXEL
 *
 * */

package application.controller;

import application.controller.services.MainConnection;
import application.view.ViewFactory;

/**
 * CLASS DESCRIPTION.
 *
 *
 * @author CUBIXEL
 *
 */
public abstract class BaseController {

  protected ViewFactory viewFactory;
  private String fxmlName;
  private MainConnection mainConnection;

  /**
   * CONSTRUCTOR DESCRIPTION.
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
