package application.controller;

import application.controller.services.MainConnection;
import application.view.ViewFactory;
import org.mockito.Mock;

public class MainWindowControllerTest {

    /* Creating the Mock Objects necessary for the test. */
    @Mock
    protected MainConnection mainConnectionMock;

    @Mock
    protected ViewFactory viewFactoryMock;

    protected MainWindowController mainWindowController;
}
