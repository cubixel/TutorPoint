package application.controller.presentation.exceptions;


public class XmlLoadingException extends Exception {
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public XmlLoadingException(String errorMessage, Throwable err){
    super(errorMessage, err);
  }
}