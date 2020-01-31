// If a Kotlin Error occurs build->rebuild project.
module client {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.testfx;
    requires org.apache.commons.codec;
    requires com.google.gson;

    opens application to javafx.fxml, org.testfx;
    exports application;
}