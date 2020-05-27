package application.controller.presentation.exceptions;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test Description.
 *
 * @author Eric Walker
 */
public class ExceptionsTest {
  private void exceptionThrower(String message, String type) throws Exception {
    switch (type) {
      case "Defaults":
        throw new DefaultsException(message, new Throwable());
      case "DocumentInfo":
        throw new DocumentInfoException(message, new Throwable());
      case "DomParsing":
        throw new DomParsingException(message, new Throwable());
      case "PresentationCreation":
        throw new PresentationCreationException(message, new Throwable());
      case "XmlLoading":
        throw new XmlLoadingException(message, new Throwable());
      default:
        return;
    }
  }

  @Test
  public void testDefaultsException() {
    try {
      exceptionThrower("testing DefaultsException", "Defaults");
    } catch (Exception e) {
      assertTrue(e instanceof DefaultsException);
      assertTrue(e.getMessage().equals("testing DefaultsException"));
    }
  }

  @Test
  public void testDocumentInfoException() {
    try {
      exceptionThrower("testing DocumentInfoException", "DocumentInfo");
    } catch (Exception e) {
      assertTrue(e instanceof DocumentInfoException);
      assertTrue(e.getMessage().equals("testing DocumentInfoException"));
    }
  }

  @Test
  public void testDomParsingException() {
    try {
      exceptionThrower("testing DomParsingException", "DomParsing");
    } catch (Exception e) {
      assertTrue(e instanceof DomParsingException);
      assertTrue(e.getMessage().equals("testing DomParsingException"));
    }
  }

  @Test
  public void testPresentationCreationException() {
    try {
      exceptionThrower("testing PresentationCreationException", "PresentationCreation");
    } catch (Exception e) {
      assertTrue(e instanceof PresentationCreationException);
      assertTrue(e.getMessage().equals("testing PresentationCreationException"));
    }
  }

  @Test
  public void testXmlLoadingException() {
    try {
      exceptionThrower("testing XmlLoadingException", "XmlLoading");
    } catch (Exception e) {
      assertTrue(e instanceof XmlLoadingException);
      assertTrue(e.getMessage().equals("testing XmlLoadingException"));
    }
  }
}