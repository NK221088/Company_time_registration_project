package dtu.example.ui;

import java.io.IOException;

import dtu.time_manager.app.TimeManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class createProject {
    private TimeManager timeManager = new TimeManager();

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
            TimeManager.createProject(projectName.getText());
            errorMessage.setVisible(false);
        } catch (Exception e) {
            errorMessage.setText(e.getMessage());
            errorMessage.setVisible(true);
        }
        System.out.println(TimeManager.getProjects().toString());
    }
}
