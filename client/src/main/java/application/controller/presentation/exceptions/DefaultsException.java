package application.controller.presentation.exceptions;

/**
 * This in an exception thrown when a problem is found with
 * a presentation defaults Node.
 *
 * @author Eric Walker
 * @see application.controller.presentation.PresentationObject
 */
public class DefaultsException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Constructor for DefaultsException.
   * @param errorMessage A message explaining why the exception was thrown.
   * @param err A previously thrown exception or a new Throwable.
   */
  public DefaultsException(String errorMessage, Throwable err) {
    super(errorMessage, err);
  }
}