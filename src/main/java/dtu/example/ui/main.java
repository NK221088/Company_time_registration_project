package dtu.example.ui;

import dtu.time_manager.app.TimeManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

public class main {
    private TimeManager timeManager;

    private void initialize() {
        timeManager = TimeManagerProvider.getInstance();
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
