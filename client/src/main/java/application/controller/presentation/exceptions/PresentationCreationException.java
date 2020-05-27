package application.controller.presentation.exceptions;

/**
 * This in an exception thrown when a problem is found while
 * creating a PresentationObject.
 *
 * @author Eric Walker
 * @see application.controller.presentation.PresentationObject
 */
public class PresentationCreationException extends Exception {

  private static final long serialVersionUID = 1L;
  
  /**
   * Constructor for PresentationCreationException.
   * @param errorMessage A message explaining why the exception was thrown.
   * @param err A previously thrown exception or a new Throwable.
   */
  public PresentationCreationException(String errorMessage, Throwable err) {
    super(errorMessage, err);
  }
}