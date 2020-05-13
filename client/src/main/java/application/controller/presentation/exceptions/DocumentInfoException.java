package application.controller.presentation.exceptions;

/**
 * This in an exception thrown when a problem is found with
 * a presentation documentinfo Node.
 * @author CUBIXEL
 * @see application.controller.presentation.PresentationObject
 */
public class DocumentInfoException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Constructor for DocumentInfoException.
   * @param errorMessage A message explaining why the exception was thrown.
   * @param err A previously thrown exception or a new Throwable.
   */
  public DocumentInfoException(String errorMessage, Throwable err) {
    super(errorMessage, err);
  }
}