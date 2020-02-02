module client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.apache.commons.codec;
    requires com.google.gson;

    opens application;
    opens application.view;
    opens application.controller;

    exports application;
}