module hellofx {
    requires transitive javafx.controls;
    requires javafx.fxml;

    exports dtu.time_manager.app;

    opens dtu.example.ui to javafx.fxml; // Gives access to fxml files
    exports dtu.example.ui; // Exports the class inheriting from javafx.application.Application
}