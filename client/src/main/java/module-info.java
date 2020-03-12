module client {
    requires transitive java.xml;

    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.graphics;
    requires javafx.media;
    requires org.apache.commons.codec;
    requires com.google.gson;

    opens application;
    opens application.model;
    opens application.view;
    opens application.controller;
    opens application.controller.services;

    exports application.controller;
    exports application.controller.services;
    exports application.view;
    exports application.model;
    exports application.model.managers;
}