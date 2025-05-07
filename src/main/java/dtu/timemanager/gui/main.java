package dtu.timemanager.gui;

import dtu.timemanager.domain.TimeManager;
import javafx.event.ActionEvent;

import java.io.IOException;

public class main {
    private TimeManager timeManager;

    private void initialize() throws Exception {
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
        timeManager.setCurrentUser(null);
    }
}
