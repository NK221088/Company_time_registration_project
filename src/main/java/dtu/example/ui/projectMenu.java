package dtu.example.ui;

import dtu.time_manager.app.TimeManager;
import javafx.event.ActionEvent;

import java.io.IOException;

public class projectMenu {
    private TimeManager timeManager = new TimeManager();

    public void manageProjects(ActionEvent actionEvent) throws IOException {
        App.setRoot("projectMenu");
    }

    public void manageTime(ActionEvent actionEvent) throws IOException {
        App.setRoot("timeMenu");
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        App.setRoot("login");
        // MAKE TIME MANAGER A GLOBALLY ACCESSED THINGAMAJIG
        timeManager.logout();
    }
}
