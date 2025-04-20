package dtu.example.ui;

import dtu.time_manager.app.Project;
import dtu.time_manager.app.TimeManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.text.Text;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class projectMenu {
    @FXML
    private ChoiceBox<Project> projectContainer;

    @FXML
    private Text projectInfo;

    @FXML
    private void initialize() {
        projectContainer.getItems().clear(); // <-- Clear existing items
        TimeManager.getProjects().forEach(project -> projectContainer.getItems().add(project));
    }

    public void backToProjectMenu(ActionEvent actionEvent) throws IOException {
        App.setRoot("main");
    }

    public void showInformation(ActionEvent actionEvent) {
        // Retrieve the selected project from the ChoiceBox
        Project selectedProject = projectContainer.getValue();

        // If a project is selected, display the project info
        if (selectedProject != null) {
            String projectName = selectedProject.toString();
            Project proj = TimeManager.getProjectFromName(projectName);

            // Update the Text node with the project's information
            projectInfo.setText(TimeManager.viewProject(proj.getProjectID()).toString());
        } else {
            // If no project is selected, clear the text or show a message
            projectInfo.setText("No project selected.");
        }
    }


    public void projectReport(ActionEvent actionEvent) {
        Project selectedProject = TimeManager.getProjectFromName(projectContainer.getValue().toString());
        if (selectedProject != null) {
            String projectName = selectedProject.toString();
            try {
                Map<String, Object> projectReport = TimeManager.getProjectReport(selectedProject.getProjectID());

                // Build the string to show on screen
                StringBuilder reportText = new StringBuilder();
                for (Map.Entry<String, Object> entry : projectReport.entrySet()) {
                    String key = entry.getKey();
                    String value = (entry.getValue() != null) ? entry.getValue().toString() : "null";
                    reportText.append(key).append(": ").append(value).append("\n");
                }

                // Display in GUI
                projectInfo.setText(reportText.toString());

                // Save to file
                String fileName = projectName.replaceAll("\\s+", "_") + "_report.txt";
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                    writer.write(reportText.toString());
                }

            } catch (IOException e) {
                projectInfo.setText("Error writing report file.");
                e.printStackTrace();
            }
        } else {
            projectInfo.setText("Please select a project first.");
        }
    }



}
