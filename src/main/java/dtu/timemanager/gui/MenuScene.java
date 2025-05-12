package dtu.timemanager.gui;

import dtu.timemanager.domain.TimeManager;
import dtu.timemanager.gui.helper.TimeManagerProvider;
import javafx.event.ActionEvent;

import java.io.IOException;

public class MenuScene {
    public void projectMenu(ActionEvent actionEvent) throws IOException {
        App.setRoot("projectMenu");
    }

    public void manageTime(ActionEvent actionEvent) throws IOException {
        App.setRoot("TimeOverview");
    }

    public void logout(ActionEvent actionEvent) throws Exception {
        App.setRoot("login");
        TimeManager timeManager = TimeManagerProvider.getInstance();
        timeManager.setCurrentUser(null);
    }
}
