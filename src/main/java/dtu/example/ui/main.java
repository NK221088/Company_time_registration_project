package dtu.example.ui;

import dtu.time_manager.app.domain.TimeManager;
import javafx.event.ActionEvent;

import java.io.IOException;

public class main {
    private TimeManager timeManager;

    public main(TimeManager timeManager) {
        this.timeManager = timeManager;
    }

    public void projectMenu(ActionEvent actionEvent) throws IOException {
        App.setRoot("projectMenu");
    }

    public void manageTime(ActionEvent actionEvent) throws IOException {
        App.setRoot("TimeOverview");
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        App.setRoot("login");
        // MAKE TIME MANAGER A GLOBALLY ACCESSED THINGAMAJIG
        timeManager.logout();
    }
}
