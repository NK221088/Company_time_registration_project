package dtu.example.ui;

import dtu.time_manager.app.Project;
import dtu.time_manager.app.TimeManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.text.Text;

import java.io.IOException;

public class viewProject {
    private TimeManager timeManager = new TimeManager();

    @FXML
    private ChoiceBox projectContainer;

    @FXML
    private Text projectInfo;

    @FXML
    private void initialize() {
        for (Project project : timeManager.getProjects()) {
            projectContainer.getItems().add(project);
        }
    }

    public void backToProjectMenu(ActionEvent actionEvent) throws IOException {
        App.setRoot("projectMenu");
    }

    public void showInformation(InputMethodEvent inputMethodEvent) {
        String dropDownName = projectContainer.getValue().toString();
        Project proj = timeManager.getProjectFromName(dropDownName);

        projectInfo.setText(timeManager.viewProject(proj.getProjectID()).toString());
    }
}
