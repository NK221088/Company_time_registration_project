package dtu.example.ui;

import dtu.time_manager.app.Activity;
import dtu.time_manager.app.Project;
import dtu.time_manager.app.TimeManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
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

        if (selectedProject != null) {
            String projectName = selectedProject.toString();
            Project proj = TimeManager.getProjectFromName(projectName);

            // Get project information from TimeManager
            Map<String, Object> projectInfoMap = TimeManager.viewProject(proj.getProjectID());

            // Start building formatted information
            StringBuilder formattedInfo = new StringBuilder();
            formattedInfo.append("Project Details\n");
            formattedInfo.append("─────────────────────\n\n");

            // Define the preferred order of fields
            String[] preferredOrder = {"Project name", "Project ID", "Project interval", "Project activities"};

            // First: Add the fields in the preferred order
            for (String key : preferredOrder) {
                if (projectInfoMap.containsKey(key)) {
                    Object value = projectInfoMap.get(key);
                    if (value != null) {
                        formattedInfo.append(capitalizeFirstLetter(key)).append(": ")
                                .append(value.toString().replace(", ", "\n").replace("=", ": "))
                                .append("\n\n");
                    }
                }
            }

            // Second: Add any remaining fields not in the preferred list
            for (Map.Entry<String, Object> entry : projectInfoMap.entrySet()) {
                String key = entry.getKey();
                if (!containsIgnoreCase(preferredOrder, key)) {
                    Object value = entry.getValue();
                    if (value != null) {
                        formattedInfo.append(capitalizeFirstLetter(key)).append(": ")
                                .append(value.toString().replace(", ", "\n").replace("=", ": "))
                                .append("\n\n");
                    }
                }
            }

            // Update the Text node with the formatted project information
            projectInfo.setText(formattedInfo.toString());

        } else {
            // If no project is selected
            projectInfo.setText("No project selected.");
        }
    }
    // Helper method to check if array contains a string (case-insensitive)
    private boolean containsIgnoreCase(String[] array, String value) {
        for (String item : array) {
            if (item.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
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


    public void addProject(ActionEvent actionEvent) {
        Dialog<Project> dialog = new Dialog<>();
        dialog.setTitle("Add New Project");
        dialog.setHeaderText("Please enter project details:");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        TextField nameField = new TextField();
        nameField.setPromptText("Project Name");

        TextField startDateField = new TextField();
        startDateField.setPromptText("Start Date (e.g., 2025-05-01)");

        TextField endDateField = new TextField();
        endDateField.setPromptText("End Date (e.g., 2025-06-01)");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Start Date:"), 0, 1);
        grid.add(startDateField, 1, 1);
        grid.add(new Label("End Date:"), 0, 2);
        grid.add(endDateField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(nameField::requestFocus);

        // 🔵 Handle validation manually
        final Button addButton = (Button) dialog.getDialogPane().lookupButton(addButtonType);
        addButton.addEventFilter(ActionEvent.ACTION, event -> {
            String name = nameField.getText();
            String startDate = startDateField.getText();
            String endDate = endDateField.getText();

            if (name == null || name.trim().isEmpty()) {
                showError("Project name cannot be empty. Please enter a name.");
                event.consume(); // ← 🔵 Prevent dialog from closing
                return;
            }

            if (TimeManager.projectDuplicateExists(name)) {
                showError("A project with name '" + name + "' already exists in the system.\nPlease choose a different name.");
                event.consume(); // ← 🔵 Prevent dialog from closing
                return;
            }

            try {
                LocalDate startDateLocalFormat = LocalDate.parse(startDate);
                LocalDate endDateLocalFormat = LocalDate.parse(endDate);

                Project project = new Project(name);
                project.setProjectStartDate(startDateLocalFormat);
                project.setProjectEndDate(endDateLocalFormat);

                TimeManager.addProject(project);
                projectContainer.getItems().add(project);
            } catch (Exception e) {
                showError("Error adding project: " + e.getMessage());
                event.consume(); // ← 🔵 Prevent dialog from closing
            }
        });

        dialog.setResultConverter(dialogButton -> null); // We already handle adding manually
        dialog.showAndWait();
    }

    // Helper method to show an error alert
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void addActivity(ActionEvent actionEvent) {
        Project selectedProject = projectContainer.getValue();

        if (selectedProject == null) {
            showError("Please select a project first before adding an activity.");
            return;
        }

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add New Activity");
        dialog.setHeaderText("Please enter the name of the activity:");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        TextField activityNameField = new TextField();
        activityNameField.setPromptText("Activity Name");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Activity Name:"), 0, 0);
        grid.add(activityNameField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(activityNameField::requestFocus);

        final Button addButton = (Button) dialog.getDialogPane().lookupButton(addButtonType);
        addButton.addEventFilter(ActionEvent.ACTION, event -> {
            String activityName = activityNameField.getText();

            if (activityName == null || activityName.trim().isEmpty()) {
                showError("Activity name cannot be empty. Please enter a name.");
                event.consume();
                return;
            }

            try {
                Activity activity = new Activity(activityName);
                selectedProject.addActivity(activity); // <- This line calls your project.addActivity(name)
                showInformation(null); // <- Refresh the project information view
            } catch (Exception e) {
                showError("Error adding activity: " + e.getMessage());
                event.consume();
            }
        });

        dialog.setResultConverter(dialogButton -> null); // Handle addition manually
        dialog.showAndWait();
    }

}
