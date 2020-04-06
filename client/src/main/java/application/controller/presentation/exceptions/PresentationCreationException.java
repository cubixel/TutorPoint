package application.controller.presentation.exceptions;


public class PresentationCreationException extends Exception {

  private static final long serialVersionUID = 1L;

  public PresentationCreationException(String errorMessage, Throwable err) {
    super(errorMessage, err);
  }
}