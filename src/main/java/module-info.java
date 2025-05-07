module hellofx {
    requires transitive javafx.controls;
    requires javafx.fxml;

    exports dtu.timemanager.domain;

    opens dtu.timemanager.ui to javafx.fxml; // Gives access to fxml files
    exports dtu.timemanager.ui;
}