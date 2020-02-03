package application.controller;

import application.controller.services.MainConnection;
import application.view.ViewFactory;

public abstract class BaseController {

    protected ViewFactory viewFactory;
    private String fxmlName;
    private MainConnection mainConnection;

    public BaseController(ViewFactory viewFactory, String fxmlName, MainConnection mainConnection) {
        this.viewFactory = viewFactory;
        this.fxmlName = fxmlName;
        this.mainConnection = mainConnection;
    }

    public String getFXMLName(){
        return fxmlName;
    }

    public MainConnection getMainConnection() {
        return mainConnection;
    }
}
