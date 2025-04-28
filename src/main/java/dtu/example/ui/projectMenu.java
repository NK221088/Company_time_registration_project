package dtu.example.ui;

import dtu.time_manager.app.Activity;
import dtu.time_manager.app.Project;
import dtu.time_manager.app.TimeManager;
import dtu.time_manager.app.User;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class projectMenu {
    @FXML
    private ChoiceBox<Project> projectContainer;

    @FXML
    private ChoiceBox<Activity> activityContainer;

    @FXML
    private VBox projectInfoStatusContainer;

    @FXML
    private Label projectInfoStatus;


    @FXML
    private void initialize() {
        projectContainer.getItems().clear();
        TimeManager.getProjects().forEach(project -> projectContainer.getItems().add(project));

        // Add listener for project selection to update activity dropdown
        projectContainer.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateActivityChoices(newVal);
        });

        // Add listener for activity selection to show activity info
        activityContainer.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showActivityInformation(newVal);
            }
        });
    }

    // Method to display activity information
    private void showActivityInformation(Activity activity) {
        if (activity == null) return;

        projectInfoStatusContainer.getChildren().clear();

        Map<String, Object> activityInfo = activity.viewActivity();

        // Display activity name
        Label nameLabel = new Label("Activity name: " + activityInfo.get("Name"));

        // Display expected work hours
        Label expectedHoursLabel = new Label("Expected work hours: " + activityInfo.get("ExpectedWorkHours"));

        // Display assigned work hours
        Label assignedHoursLabel = new Label("Assigned work hours: " + activityInfo.get("AssignedWorkHours"));

        // Add the basic info to the container
        projectInfoStatusContainer.getChildren().addAll(nameLabel, expectedHoursLabel, assignedHoursLabel);

        // Display assigned users
        @SuppressWarnings("unchecked")
        ArrayList<User> assignedUsers = (ArrayList<User>) activityInfo.get("Assigned Users");

        if (assignedUsers != null && !assignedUsers.isEmpty()) {
            Label usersHeader = new Label("Assigned users:");
            projectInfoStatusContainer.getChildren().add(usersHeader);

            // Create a VBox to contain all users
            VBox usersContainer = new VBox(5);
            usersContainer.setPadding(new Insets(0, 0, 0, 15)); // Add some indentation

            // Add each user to the container
            for (User user : assignedUsers) {
                Label userLabel = new Label("• " + user.getUserInitials());
                usersContainer.getChildren().add(userLabel);
            }

            projectInfoStatusContainer.getChildren().add(usersContainer);
        } else {
            Label noUsersLabel = new Label("No users assigned to this activity.");
            projectInfoStatusContainer.getChildren().add(noUsersLabel);
        }

        // Add button to set expected work hours
        Button setExpectedHoursButton = new Button("Set Expected Hours");
        setExpectedHoursButton.setOnAction(e -> showSetExpectedHoursDialog(activity));
        projectInfoStatusContainer.getChildren().add(setExpectedHoursButton);

        // Add button to assign user to this activity
        Button assignUserButton = new Button("Assign User");
        assignUserButton.setOnAction(e -> showAssignUserDialog(activity));
        projectInfoStatusContainer.getChildren().add(assignUserButton);
    }

    // Method to show dialog for setting expected work hours
    private void showSetExpectedHoursDialog(Activity activity) {
        Dialog<Double> dialog = new Dialog<>();
        dialog.setTitle("Set Expected Work Hours");
        dialog.setHeaderText("Enter expected work hours for " + activity.getActivityName() + ":");

        ButtonType setButtonType = new ButtonType("Set", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(setButtonType, ButtonType.CANCEL);

        TextField hoursField = new TextField();
        hoursField.setPromptText("Hours");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Hours:"), 0, 0);
        grid.add(hoursField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(hoursField::requestFocus);

        final Button setButton = (Button) dialog.getDialogPane().lookupButton(setButtonType);
        setButton.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                double hours = Double.parseDouble(hoursField.getText());
                activity.setExpectedWorkHours(hours);
                showActivityInformation(activity);
            } catch (NumberFormatException e) {
                showError("Please enter a valid number for hours.");
                event.consume();
            }
        });

        dialog.setResultConverter(dialogButton -> null);
        dialog.showAndWait();
    }

    // Method to show dialog for assigning users
    private void showAssignUserDialog(Activity activity) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Assign User to Activity");
        dialog.setHeaderText("Select a user to assign to " + activity.getActivityName() + ":");

        ButtonType assignButtonType = new ButtonType("Assign", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(assignButtonType, ButtonType.CANCEL);

        ChoiceBox<User> userChoiceBox = new ChoiceBox<>();
        userChoiceBox.getItems().addAll(TimeManager.getUsers());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("User:"), 0, 0);
        grid.add(userChoiceBox, 1, 0);

        dialog.getDialogPane().setContent(grid);

        final Button assignButton = (Button) dialog.getDialogPane().lookupButton(assignButtonType);
        assignButton.addEventFilter(ActionEvent.ACTION, event -> {
            User selectedUser = userChoiceBox.getValue();

            if (selectedUser == null) {
                showError("Please select a user to assign.");
                event.consume();
                return;
            }

            try {
                activity.assignUser(selectedUser.getUserInitials());
                showActivityInformation(activity);
            } catch (RuntimeException e) {
                showError(e.getMessage());
                event.consume();
            }
        });

        dialog.setResultConverter(dialogButton -> null);
        dialog.showAndWait();
    }

    // Method to update activity choices when a project is selected
    private void updateActivityChoices(Project selectedProject) {
        activityContainer.getItems().clear();

        if (selectedProject != null) {
            List<Activity> activities = selectedProject.getActivities();
            if (activities != null && !activities.isEmpty()) {
                activityContainer.getItems().addAll(activities);
            }
        }
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
        // Clear activity selection when showing project info
        activityContainer.getSelectionModel().clearSelection();


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
                List<Activity> activities = selectedProject.getActivities();

                // Build the report text with better formatting
                StringBuilder reportText = new StringBuilder();

                // Add a title and timestamp
                reportText.append("===================================\n");
                reportText.append("          PROJECT REPORT           \n");
                reportText.append("===================================\n\n");
                reportText.append("Generated on: ").append(java.time.LocalDateTime.now().toString()).append("\n\n");

                // Project Details Section
                reportText.append("PROJECT DETAILS\n");
                reportText.append("-----------------------------------\n");
                reportText.append("Project Name: ").append(projectReport.get("Project Name")).append("\n");
                reportText.append("Project ID: ").append(projectReport.get("Project ID")).append("\n");
                reportText.append("Time Period: ").append(projectReport.get("Project interval")).append("\n\n");

                // Activity Details Section
                reportText.append("ACTIVITY DETAILS\n");
                reportText.append("-----------------------------------\n");

                if (activities != null && !activities.isEmpty()) {
                    for (int i = 0; i < activities.size(); i++) {
                        Activity activity = activities.get(i);
                        reportText.append(i+1).append(". ").append(activity.getActivityName()).append("\n");
                        reportText.append("   Expected Work Hours: ").append(activity.getExpectedWorkHours()).append(" hours\n");
                        reportText.append("   Assigned Work Hours: ").append(activity.getAssignedWorkHours()).append(" hours\n");

                        // Get all assigned users
                        ArrayList<User> assignedUsers = activity.getAssignedUsers();
                        if (assignedUsers != null && !assignedUsers.isEmpty()) {
                            reportText.append("   Assigned Users: ");
                            for (int j = 0; j < assignedUsers.size(); j++) {
                                reportText.append(assignedUsers.get(j));
                                if (j < assignedUsers.size() - 1) {
                                    reportText.append(", ");
                                }
                            }
                            reportText.append("\n");
                        } else {
                            reportText.append("   Assigned Users: None\n");
                        }

                        reportText.append("\n");
                    }
                } else {
                    reportText.append("No activities defined for this project.\n\n");
                }

                // Additional Project Info
                reportText.append("ADDITIONAL INFORMATION\n");
                reportText.append("-----------------------------------\n");

                // Add other relevant information from the project report
                for (Map.Entry<String, Object> entry : projectReport.entrySet()) {
                    String key = entry.getKey();
                    // Skip keys we've already included above
                    if (!key.equals("Project Name") && !key.equals("Project ID") && !key.equals("Project interval")
                            && !key.equals("Project Activities")) {
                        String value = (entry.getValue() != null) ? entry.getValue().toString() : "Not available";
                        reportText.append(key).append(": ").append(value).append("\n");
                    }
                }

                // Save to downloads folder
                String userHome = System.getProperty("user.home");
                String downloadsPath = userHome + "/Downloads/";
                String fileName = downloadsPath + projectName.replaceAll("\\s+", "_") + "_report.txt";

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                    writer.write(reportText.toString());

                    // Show a confirmation dialog
                    Alert confirmationAlert = new Alert(Alert.AlertType.INFORMATION);
                    confirmationAlert.setTitle("Report Generated");
                    confirmationAlert.setHeaderText(null);
                    confirmationAlert.setContentText("Project report for \"" + projectName + "\" has been generated and saved to your Downloads folder.");
                    confirmationAlert.showAndWait();

                    // Also update the status label
                    projectInfoStatus.setText("Project report generated successfully and saved to Downloads folder.");

                    // Clear the status message after 3 seconds
                    new Thread(() -> {
                        try {
                            Thread.sleep(3000);
                            Platform.runLater(() -> {
                                String text = projectInfoStatus.getText();
                                if (text.equals("Project report generated successfully and saved to Downloads folder.")) {
                                    projectInfoStatus.setText("");
                                }
                            });
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }).start();

                } catch (IOException e) {
                    showError("Error saving report file to Downloads folder: " + e.getMessage());
                    e.printStackTrace();
                }
            } catch (Exception e) {
                showError("Error generating project report: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showError("Please select a project first before generating a report.");
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

    public void assignEmployee(ActionEvent actionEvent) {
        Project selectedProject = projectContainer.getValue();

        if (selectedProject == null) {
            showError("Please select a project first before assigning an employee.");
            return;
        }

        List<Activity> activities = selectedProject.getActivities();

        if (activities == null || activities.isEmpty()) {
            showError("The selected project has no activities. Please add an activity first.");
            return;
        }

        List<User> users = TimeManager.getUsers();

        if (users == null || users.isEmpty()) {
            showError("No users available to assign. Please add users to the system first.");
            return;
        }

        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Assign Employee");
        dialog.setHeaderText("Select an employee to assign to the first activity:");

        ButtonType assignButtonType = new ButtonType("Assign", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(assignButtonType, ButtonType.CANCEL);

        ChoiceBox<User> userChoiceBox = new ChoiceBox<>();
        userChoiceBox.getItems().addAll(users);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Employee:"), 0, 0);
        grid.add(userChoiceBox, 1, 0);

        dialog.getDialogPane().setContent(grid);

        final Button assignButton = (Button) dialog.getDialogPane().lookupButton(assignButtonType);
        assignButton.addEventFilter(ActionEvent.ACTION, event -> {
            User selectedUser = userChoiceBox.getValue();

            if (selectedUser == null) {
                showError("Please select an employee to assign.");
                event.consume();
                return;
            }

            try {
                Activity firstActivity = activities.get(0);
                firstActivity.assignUser(selectedUser.getUserInitials());

                // Show confirmation
                projectInfoStatus.setText("Employee " + selectedUser.getUserInitials() + " assigned to activity " +
                        firstActivity.getActivityName() + " successfully.");

                // Refresh view
                showInformation(null);
            } catch (Exception e) {
                showError("Error assigning employee: " + e.getMessage());
                event.consume();
            }
        });

        dialog.setResultConverter(dialogButton -> null); // Handle assignment manually
        dialog.showAndWait();
    }
}
