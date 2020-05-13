package application.controller.presentation.exceptions;

/**
 * This in an exception thrown when a problem is found while
 * parsing a presentation XML to DOM.
 * @author CUBIXEL
 * @see application.controller.presentation.XmlHandler
 */
public class DomParsingException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Constructor for DomParsingException.
   * @param errorMessage A message explaining why the exception was thrown.
   * @param err A previously thrown exception or a new Throwable.
   */
  public DomParsingException(String errorMessage, Throwable err) {
    super(errorMessage, err);
  }
}