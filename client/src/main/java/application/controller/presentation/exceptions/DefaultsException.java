package application.controller.presentation.exceptions;


public class DefaultsException extends Exception {

  private static final long serialVersionUID = 1L;

  public DefaultsException(String errorMessage, Throwable err) {
    super(errorMessage, err);
  }
}