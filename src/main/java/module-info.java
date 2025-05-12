module hellofx {
    requires transitive javafx.controls;
    requires javafx.fxml;

    exports dtu.timemanager.domain;

    opens dtu.timemanager.gui to javafx.fxml; // Gives access to fxml files
    exports dtu.timemanager.gui;
    exports dtu.timemanager.gui.helper;
    opens dtu.timemanager.gui.helper to javafx.fxml;
}