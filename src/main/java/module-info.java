module hellofx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jakarta.persistence;

    exports dtu.timemanager.domain;
    exports dtu.timemanager.persistence;
    exports dtu.timemanager.app;
    exports dtu.timemanager.gui;

    opens dtu.timemanager.gui to javafx.fxml;
    opens dtu.timemanager.domain to jakarta.persistence; // reflection access for JPA
}
