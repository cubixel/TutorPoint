package application.controller.presentation.exceptions;

/**
 * This in an exception thrown when a problem is found while
 * loading a presentation XML.
 * @author CUBIXEL
 * @see application.controller.presentation.XmlHandler
 */
public class XmlLoadingException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Constructor for XmlLoadingException.
   * @param errorMessage A message explaining why the exception was thrown.
   * @param err A previously thrown exception or a new Throwable.
   */
  public XmlLoadingException(String errorMessage, Throwable err) {
    super(errorMessage, err);
  }
}