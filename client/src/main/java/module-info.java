// If a Kotlin Error occurs build->rebuild project.
module client {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.commons.codec;


    opens application to javafx.fxml;
    exports application;
}