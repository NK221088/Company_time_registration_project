package dtu.example.ui;

import dtu.time_manager.app.Activity;
import dtu.time_manager.app.Project;
import dtu.time_manager.app.TimeManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class projectMenu {
    @FXML
    private ChoiceBox<Project> projectContainer;

    @FXML
    private VBox projectInfoStatusContainer;

    @FXML
    private Label projectInfoStatus;


    @FXML
    private void initialize() {
        projectContainer.getItems().clear(); // <-- Clear existing items
        TimeManager.getProjects().forEach(project -> projectContainer.getItems().add(project));
    }

    public void backToProjectMenu(ActionEvent actionEvent) throws IOException {
        App.setRoot("main");
    }

    public void showInformation(ActionEvent actionEvent) {
        projectInfoStatusContainer.getChildren().clear();

        Project selectedProject = projectContainer.getValue();
        if (selectedProject == null) {
            projectInfoStatusContainer.getChildren().add(new Label("No project selected."));
            return;
        }

        // Get all project information using the TimeManager.viewProject method
        Map<String, Object> projectInfo = TimeManager.viewProject(selectedProject.getProjectID());

        // Display project name (editable)
        Label nameLabel = new Label("Project name: " + projectInfo.get("Project name"));
        setupEditableName(nameLabel, selectedProject);

        // Display project ID (non-editable)
        Label idLabel = new Label("Project ID: " + projectInfo.get("Project ID"));

        // Display interval (editable)
        Label intervalLabel = new Label("Project interval: " + projectInfo.get("Project interval"));
        setupEditableInterval(intervalLabel, selectedProject);

        // Add the basic info to the container
        projectInfoStatusContainer.getChildren().addAll(nameLabel, idLabel, intervalLabel);

        // Display activities (non-editable list)
        @SuppressWarnings("unchecked")
        List<Activity> activities = (List<Activity>) projectInfo.get("Project activities");

        if (activities != null && !activities.isEmpty()) {
            Label activitiesHeader = new Label("Project activities:");
            projectInfoStatusContainer.getChildren().add(activitiesHeader);

            // Create a VBox to contain all activities
            VBox activitiesContainer = new VBox(5);
            activitiesContainer.setPadding(new Insets(0, 0, 0, 15)); // Add some indentation

            // Add each activity to the container
            for (Activity activity : activities) {
                Label activityLabel = new Label("• " + activity.getActivityName());
                activitiesContainer.getChildren().add(activityLabel);
            }

            projectInfoStatusContainer.getChildren().add(activitiesContainer);
        } else {
            Label noActivitiesLabel = new Label("No activities for this project.");
            projectInfoStatusContainer.getChildren().add(noActivitiesLabel);
        }
    }
    private void setupEditableName(Label nameLabel, Project project) {
        nameLabel.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                TextField textField = new TextField(project.getProjectName());
                textField.setOnAction(e -> {
                    project.setProjectName(textField.getText());
                    showInformation(null); // Refresh view
                });
                textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                    if (!isNowFocused) {
                        project.setProjectName(textField.getText());
                        showInformation(null); // Refresh view
                    }
                });

                int index = projectInfoStatusContainer.getChildren().indexOf(nameLabel);
                projectInfoStatusContainer.getChildren().set(index, textField);
                textField.requestFocus();
            }
        });
    }
    private void setupEditableInterval(Label intervalLabel, Project project) {
        intervalLabel.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                DatePicker startPicker = new DatePicker(project.getStartDate());
                DatePicker endPicker = new DatePicker(project.getEndDate());

                startPicker.setEditable(false);
                endPicker.setEditable(false);

                startPicker.setPromptText("Start Date");
                endPicker.setPromptText("End Date");

                HBox datePickers = new HBox(10, startPicker, endPicker);

                startPicker.setOnAction(e -> {
                    if (startPicker.getValue() != null) {
                        project.setProjectStartDate(startPicker.getValue());
                        showInformation(null);
                    }
                });

                endPicker.setOnAction(e -> {
                    if (endPicker.getValue() != null) {
                        project.setProjectEndDate(endPicker.getValue());
                        showInformation(null);
                    }
                });

                int index = projectInfoStatusContainer.getChildren().indexOf(intervalLabel);
                projectInfoStatusContainer.getChildren().set(index, datePickers);
            }
        });
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
                    String currentInfo = projectInfoStatus.getText();

                    // Show a brief confirmation message while preserving existing info
                    projectInfoStatus.setText(currentInfo + "\n\n(Report saved to Downloads folder.)");

                    // Remove only the confirmation part after 3 seconds
                    new Thread(() -> {
                        try {
                            Thread.sleep(3000);
                            javafx.application.Platform.runLater(() -> {
                                // Check if our confirmation message is still there
                                String text = projectInfoStatus.getText();
                                if (text.endsWith("(Report saved to Downloads folder.)")) {
                                    // Restore just the original project info without the confirmation
                                    projectInfoStatus.setText(currentInfo);
                                }
                            });
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }).start();

                } catch (IOException e) {
                    // Store the current project info text
                    String currentInfo = projectInfoStatus.getText();

                    // Show error while preserving existing info
                    projectInfoStatus.setText(currentInfo + "\n\n(Error saving report file to Downloads folder.)");
                    e.printStackTrace();
                }
            } catch (Exception e) {
                // Store the current project info text
                String currentInfo = projectInfoStatus.getText();

                // Show error while preserving existing info
                projectInfoStatus.setText(currentInfo + "\n\n(Error generating project report.)");
                e.printStackTrace();
            }
        } else {
            // Preserve any existing text in projectInfoStatus
            String currentInfo = projectInfoStatus.getText();
            if (currentInfo == null || currentInfo.isEmpty() || currentInfo.equals("No project selected.")) {
                projectInfoStatus.setText("Please select a project first.");
            } else {
                projectInfoStatus.setText(currentInfo + "\n\n(Please select a project first.)");
            }

            // Clear only the message after 3 seconds if needed
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    javafx.application.Platform.runLater(() -> {
                        String text = projectInfoStatus.getText();
                        if (text.endsWith("(Please select a project first.)")) {
                            projectInfoStatus.setText(text.replace("\n\n(Please select a project first.)", ""));
                        } else if (text.equals("Please select a project first.")) {
                            projectInfoStatus.setText("");
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

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Select Start Date");
        startDatePicker.getEditor().setDisable(true);
        startDatePicker.getEditor().setOpacity(1);

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("Select End Date");
        endDatePicker.getEditor().setDisable(true);
        endDatePicker.getEditor().setOpacity(1);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Start Date:"), 0, 1);
        grid.add(new Label("Start Date:"), 0, 1);
        grid.add(startDatePicker, 1, 1);
        grid.add(new Label("End Date:"), 0, 2);
        grid.add(endDatePicker, 1, 2);


        dialog.getDialogPane().setContent(grid);

        Platform.runLater(nameField::requestFocus);

        final Button addButton = (Button) dialog.getDialogPane().lookupButton(addButtonType);
        addButton.addEventFilter(ActionEvent.ACTION, event -> {
            String name = nameField.getText();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            if (name == null || name.trim().isEmpty()) {
                showError("Project name cannot be empty. Please enter a name.");
                event.consume();
                return;
            }

            if (TimeManager.projectDuplicateExists(name)) {
                showError("A project with name '" + name + "' already exists in the system.\nPlease choose a different name.");
                event.consume();
                return;
            }

            try {
                Project project = new Project(name);

                // Only set dates if they are chosen
                if (startDate != null) {
                    project.setProjectStartDate(startDate);
                }
                if (endDate != null) {
                    project.setProjectEndDate(endDate);
                }

                TimeManager.addProject(project);
                projectContainer.getItems().add(project);
            } catch (Exception e) {
                showError("Error adding project: " + e.getMessage());
                event.consume();
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
