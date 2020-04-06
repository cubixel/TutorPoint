package application.controller.presentation.exceptions;

public class DomParsingException extends Exception {
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public DomParsingException(String errorMessage, Throwable err){
    super(errorMessage, err);
  }
}