module client {
  requires transitive java.xml;

  requires transitive javafx.controls;
  requires transitive javafx.media;
  requires javafx.fxml;
  requires javafx.web;
  requires javafx.graphics;
  requires org.apache.commons.codec;
  requires com.google.gson;
  requires logback.classic;
  requires logback.core;
  requires org.slf4j;
  requires org.bytedeco.ffmpeg;
  requires org.bytedeco.javacv.platform;
  requires org.bytedeco.opencv.platform;
  requires org.bytedeco.javacpp.platform;
  requires java.desktop;

  opens application;
  opens application.model;
  opens application.view;
  opens application.controller;
  opens application.controller.services;

  exports application.controller;
  exports application.controller.enums;
  exports application.controller.services;
  exports application.view;
  exports application.model;
  exports application.model.managers;
  exports application.model.requests;
  exports application.model.updates;
}