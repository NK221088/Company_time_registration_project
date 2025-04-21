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

            // Get project information from TimeManager
            String rawProjectInfo = TimeManager.viewProject(proj.getProjectID()).toString();

            // Remove curly braces from the beginning and end of the raw info
            if (rawProjectInfo.startsWith("{")) {
                rawProjectInfo = rawProjectInfo.substring(1);
            }
            if (rawProjectInfo.endsWith("}")) {
                rawProjectInfo = rawProjectInfo.substring(0, rawProjectInfo.length() - 1);
            }

            // Format the project information to look nicer
            StringBuilder formattedInfo = new StringBuilder();
            formattedInfo.append("Project Details\n");
            formattedInfo.append("─────────────────────\n\n");

            // Split the raw info by lines and format each line
            String[] lines = rawProjectInfo.split("\\n|\\r\\n");
            for (String line : lines) {
                // Handle different types of data formatting
                if (line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    String key = parts[0].trim()  ;
                    String value = parts.length > 1 ? parts[1].trim() : "";

                    // Replace commas with line breaks in the value
                    value = value.replace(", ", "\n");

                    // Format key and value on the same line
                    formattedInfo.append(capitalizeFirstLetter(key)).append(": ")
                            .append(value).append("\n\n");
                } else if (!line.trim().isEmpty()) {
                    // For lines without equal signs
                    String formattedLine = line.trim().replace(", ", "\n");
                    formattedInfo.append(formattedLine).append("\n\n");
                }
            }

            // Update the Text node with the formatted project information
            projectInfo.setText(formattedInfo.toString());
        } else {
            // If no project is selected, show a message
            projectInfo.setText("No project selected.");
        }
    }

    // Helper method to capitalize the first letter of each word
    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder result = new StringBuilder();
        String[] words = text.split("\\s+");

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase());
            }

            if (i < words.length - 1) {
                result.append(" ");
            }
        }

        return result.toString();
    }


    public void projectReport(ActionEvent actionEvent) {
        Project selectedProject = projectContainer.getValue();
        if (selectedProject != null) {
            String projectName = selectedProject.toString();
            try {
                Map<String, Object> projectReport = TimeManager.getProjectReport(selectedProject.getProjectID());

                // Build the report text for the file only (not for display)
                StringBuilder reportText = new StringBuilder();
                for (Map.Entry<String, Object> entry : projectReport.entrySet()) {
                    String key = entry.getKey();
                    String value = (entry.getValue() != null) ? entry.getValue().toString() : "null";
                    reportText.append(key).append(": ").append(value).append("\n");
                }

                // Save to downloads folder instead of project directory
                String userHome = System.getProperty("user.home");
                String downloadsPath = userHome + "/Downloads/";
                String fileName = downloadsPath + projectName.replaceAll("\\s+", "_") + "_report.txt";

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                    writer.write(reportText.toString());

                    // Store the current project info text
                    String currentInfo = projectInfo.getText();

                    // Show a brief confirmation message while preserving existing info
                    projectInfo.setText(currentInfo + "\n\n(Report saved to Downloads folder.)");

                    // Remove only the confirmation part after 3 seconds
                    new Thread(() -> {
                        try {
                            Thread.sleep(3000);
                            javafx.application.Platform.runLater(() -> {
                                // Check if our confirmation message is still there
                                String text = projectInfo.getText();
                                if (text.endsWith("(Report saved to Downloads folder.)")) {
                                    // Restore just the original project info without the confirmation
                                    projectInfo.setText(currentInfo);
                                }
                            });
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }).start();

                } catch (IOException e) {
                    // Store the current project info text
                    String currentInfo = projectInfo.getText();

                    // Show error while preserving existing info
                    projectInfo.setText(currentInfo + "\n\n(Error saving report file to Downloads folder.)");
                    e.printStackTrace();
                }
            } catch (Exception e) {
                // Store the current project info text
                String currentInfo = projectInfo.getText();

                // Show error while preserving existing info
                projectInfo.setText(currentInfo + "\n\n(Error generating project report.)");
                e.printStackTrace();
            }
        } else {
            // Preserve any existing text in projectInfo
            String currentInfo = projectInfo.getText();
            if (currentInfo == null || currentInfo.isEmpty() || currentInfo.equals("No project selected.")) {
                projectInfo.setText("Please select a project first.");
            } else {
                projectInfo.setText(currentInfo + "\n\n(Please select a project first.)");
            }

            // Clear only the message after 3 seconds if needed
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    javafx.application.Platform.runLater(() -> {
                        String text = projectInfo.getText();
                        if (text.endsWith("(Please select a project first.)")) {
                            projectInfo.setText(text.replace("\n\n(Please select a project first.)", ""));
                        } else if (text.equals("Please select a project first.")) {
                            projectInfo.setText("");
                        }
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }



}
