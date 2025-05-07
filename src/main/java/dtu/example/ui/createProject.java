package dtu.example.ui;

import java.io.IOException;

import dtu.time_manager.app.domain.TimeManager;
import dtu.time_manager.app.domain.Project;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class createProject {
    private TimeManager timeManager;

    public createProject(TimeManager timeManager) {
        this.timeManager = timeManager;
    }

    @FXML
    private TextField projectName;

    @FXML
    private Label errorMessage;

    @FXML
    private void initialize() {
        errorMessage.setVisible(false);
    }

    @FXML
    private void createProject() throws IOException {
        try {
            Project project = timeManager.createProject(projectName.getText());
            timeManager.addProject(project);
            errorMessage.setVisible(false);
        } catch (Exception e) {
            errorMessage.setText(e.getMessage());
            errorMessage.setVisible(true);
        }
    }
}
