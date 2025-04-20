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
    private TimeManager timeManager = new TimeManager();

    @FXML
    private ChoiceBox projectContainer;

    @FXML
    private Text projectInfo;

    @FXML
    private void initialize() {
        timeManager.getProjects().forEach(project -> projectContainer.getItems().add(project));
    }

    public void backToProjectMenu(ActionEvent actionEvent) throws IOException {
        App.setRoot("projectMenu");
    }

    public void showInformation(InputMethodEvent inputMethodEvent) {
        String dropDownName = projectContainer.getValue().toString();
        Project proj = timeManager.getProjectFromName(dropDownName);

        projectInfo.setText(timeManager.viewProject(proj.getProjectID()).toString());
    }

    public void projectReport(ActionEvent actionEvent) {
        Object selectedItem = projectContainer.getValue();
        if (selectedItem != null) {
            String projectName = selectedItem.toString();
            try {
                Map<String, Object> projectReport = TimeManager.getProjectReport(projectName);

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
