package application.controller.presentation.exceptions;


public class DocumentInfoException extends Exception {

  private static final long serialVersionUID = 1L;

  public DocumentInfoException(String errorMessage, Throwable err) {
    super(errorMessage, err);
  }
}