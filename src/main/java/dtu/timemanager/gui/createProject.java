package dtu.timemanager.gui;

import java.io.IOException;

import dtu.timemanager.domain.TimeManager;
import dtu.timemanager.domain.Project;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class createProject {
    private TimeManager timeManager;

    @FXML
    private TextField projectName;

    @FXML
    private Label errorMessage;

    @FXML
    private void initialize() throws Exception {
        timeManager = TimeManagerProvider.getInstance();
        errorMessage.setVisible(false);
    }

    @FXML
    private void createProject() throws IOException {
        try {
            timeManager.addProject(projectName.getText());
            errorMessage.setVisible(false);
        } catch (Exception e) {
            errorMessage.setText(e.getMessage());
            errorMessage.setVisible(true);
        }
    }
}
