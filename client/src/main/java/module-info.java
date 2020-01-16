// If a Kotlin Error occurs build->rebuild project.
module client {
    requires javafx.controls;
    requires javafx.fxml;

    opens application to javafx.fxml;
    exports application;
}