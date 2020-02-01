module client {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.commons.codec;
    requires com.google.gson;
    requires javafx.web;

    opens application;
    opens application.view;
    opens application.controller;

    exports application;
}