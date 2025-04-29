package dtu.example.ui;

import dtu.time_manager.app.*;
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
import java.util.*;

public class projectMenu {
//    @FXML
//    private ChoiceBox<Project> projectContainer;

    @FXML
    private TreeView<Object> projectTreeView;

    @FXML
    private ChoiceBox<Activity> activityContainer;

    @FXML
    private VBox projectInfoStatusContainer;

    @FXML
    private Label projectInfoStatus;

    private Project selectedProject;
    private Activity selectedActivity;

    @FXML
    private void initialize() {
        loadProjectTree();
        setSelectionListener();
    }

    private Set<String> getExpandedPaths(TreeItem<Object> root) {
        Set<String> expandedPaths = new HashSet<>();
        collectExpandedPaths(root, "", expandedPaths);
        return expandedPaths;
    }

    private void collectExpandedPaths(TreeItem<Object> node, String path, Set<String> paths) {
        if (node.isExpanded()) {
            paths.add(path);
        }
        for (TreeItem<Object> child : node.getChildren()) {
            Object value = child.getValue();
            String childName = (value != null) ? value.toString() : "null";
            collectExpandedPaths(child, path + "/" + childName, paths);
        }
    }

    private void restoreExpandedPaths(TreeItem<Object> root, Set<String> expandedPaths) {
        expandPaths(root, "", expandedPaths);
    }

    private void expandPaths(TreeItem<Object> node, String path, Set<String> expandedPaths) {
        if (expandedPaths.contains(path)) {
            node.setExpanded(true);
        }
        for (TreeItem<Object> child : node.getChildren()) {
            Object value = child.getValue();
            String childName = (value != null) ? value.toString() : "null";
            expandPaths(child, path + "/" + childName, expandedPaths);
        }
    }

    private Set<String> expandedPaths;
    private void loadProjectTree() {
        if (projectTreeView.getTreeItem(0) != null) {
            expandedPaths = getExpandedPaths(projectTreeView.getRoot());
        }

        TreeItem<Object> rootItem = new TreeItem<>("Projects");
        rootItem.setExpanded(true);

        for (Project project : TimeManager.getProjects()) {
            TreeItem<Object> projectItem = new TreeItem<>(project);

            for (Activity activity : project.getActivities()) {
                TreeItem<Object> activityItem = new TreeItem<>(activity);
                projectItem.getChildren().add(activityItem);
            }
            rootItem.getChildren().add(projectItem);
        }
        projectTreeView.setRoot(rootItem);

        if (expandedPaths != null) {
            restoreExpandedPaths(projectTreeView.getRoot(), expandedPaths);
        }
    }

    private void setSelectionListener() {
        projectTreeView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                if (newSelection.getValue() instanceof Project)  {
                    selectedProject = (Project) newSelection.getValue();
                    showInformation(null);
                } else if (newSelection.getValue() instanceof Activity) {
                    selectedProject = (Project) newSelection.getParent().getValue();
                    selectedActivity = (Activity) newSelection.getValue();
                    showActivityInformation(selectedActivity);
                }
            }
        });
    }

    // Method to display activity information
    private void showActivityInformation(Activity activity) {
        if (activity == null) return;

        projectInfoStatusContainer.getChildren().clear();

        Map<String, Object> activityInfo = activity.viewActivity();

        // Display activity name
        HBox nameBox = new HBox(5);
        Label nameTitleLabel = new Label("Activity name:");
        Label nameValueLabel = new Label((String) activityInfo.get("Name"));
        setupEditableActivityName(nameValueLabel, activity);
        nameBox.getChildren().addAll(nameTitleLabel, nameValueLabel);

        // Display expected work hours
        HBox expectedHoursBox = new HBox(5);
        Label expectedHoursTitleLabel = new Label("Expected work hours:");
        Label expectedHoursValueLabel = new Label(activityInfo.get("ExpectedWorkHours") + " hours");
        setupEditableExpectedHours(expectedHoursValueLabel, activity);
        expectedHoursBox.getChildren().addAll(expectedHoursTitleLabel, expectedHoursValueLabel);

        // Display assigned work hours
        Label assignedHoursLabel = new Label("Registered work hours: " + activityInfo.get("WorkedHours"));

        // Add the basic info to the container
        projectInfoStatusContainer.getChildren().addAll(nameBox, expectedHoursBox, assignedHoursLabel);

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
                loadProjectTree();
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

        // Display project ID (non-editable)
        Label projectLeadLabel = new Label("Project lead: " + projectInfo.get("Project Lead"));
        setupEditableProjectLead(projectLeadLabel, selectedProject);

        // Display interval (editable)
        Label intervalLabel = new Label("Project interval: " + projectInfo.get("Project interval"));
        setupEditableInterval(intervalLabel, selectedProject);

        // Add the basic info to the container
        projectInfoStatusContainer.getChildren().addAll(nameLabel, idLabel, projectLeadLabel, intervalLabel);

        // Display activities (non-editable list)
//        @SuppressWarnings("unchecked")
//        List<Activity> activities = (List<Activity>) projectInfo.get("Project activities");
//
//        if (activities != null && !activities.isEmpty()) {
//            Label activitiesHeader = new Label("Project activities:");
//            projectInfoStatusContainer.getChildren().add(activitiesHeader);
//
//            // Create a VBox to contain all activities
//            VBox activitiesContainer = new VBox(5);
//            activitiesContainer.setPadding(new Insets(0, 0, 0, 15)); // Add some indentation
//
//            // Add each activity to the container
//            for (Activity activity : activities) {
//                Label activityLabel = new Label("• " + activity.getActivityName());
//                activitiesContainer.getChildren().add(activityLabel);
//            }

//            projectInfoStatusContainer.getChildren().add(activitiesContainer);
//        } else {
//            Label noActivitiesLabel = new Label("No activities for this project.");
//            projectInfoStatusContainer.getChildren().add(noActivitiesLabel);
        // Clear activity selection when showing project info
//        activityContainer.getSelectionModel().clearSelection();

//        loadProjectTree();
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
    private void setupEditableProjectLead(Label projectLeadLabel, Project project) {
        projectLeadLabel.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                // Get all available users
                List<User> users = TimeManager.getUsers();
                if (users == null || users.isEmpty()) {
                    showError("No users available to assign. Please add users to the system first.");
                    return;
                }

                // Create a popup dialog with a choice box
                Dialog<User> dialog = new Dialog<>();
                dialog.setTitle("Assign Project Lead");
                dialog.setHeaderText("Select a user to assign as project lead:");

                // Add buttons
                ButtonType assignButtonType = new ButtonType("Assign", ButtonBar.ButtonData.OK_DONE);
                ButtonType clearButtonType = new ButtonType("Clear Lead", ButtonBar.ButtonData.LEFT);
                dialog.getDialogPane().getButtonTypes().addAll(assignButtonType, clearButtonType, ButtonType.CANCEL);

                // Create and configure the choice box
                ChoiceBox<User> userChoiceBox = new ChoiceBox<>();
                userChoiceBox.getItems().addAll(users);

                // Set the current project lead as selected if it exists
                if (project.getProjectLead() != null) {
                    for (User user : users) {
                        if (user.getUserInitials().equals(project.getProjectLead().getUserInitials())) {
                            userChoiceBox.setValue(user);
                            break;
                        }
                    }
                }

                // Create the dialog content
                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.add(new Label("Project Lead:"), 0, 0);
                grid.add(userChoiceBox, 1, 0);
                dialog.getDialogPane().setContent(grid);

                // Handle the assign button
                final Button assignButton = (Button) dialog.getDialogPane().lookupButton(assignButtonType);
                assignButton.addEventFilter(ActionEvent.ACTION, e -> {
                    User selectedUser = userChoiceBox.getValue();
                    if (selectedUser == null) {
                        showError("Please select a user to assign as project lead.");
                        e.consume();
                        return;
                    }

                    try {
                        project.assignProjectLead(selectedUser);
                        showInformation(null); // Refresh view
                    } catch (Exception ex) {
                        showError("Error assigning project lead: " + ex.getMessage());
                        e.consume();
                    }
                });

                // Handle the clear button
                final Button clearButton = (Button) dialog.getDialogPane().lookupButton(clearButtonType);
                clearButton.addEventFilter(ActionEvent.ACTION, e -> {
                    project.assignProjectLead(null);
                    showInformation(null); // Refresh view
                    dialog.close();
                });

                // Set result converter (not really using the return value)
                dialog.setResultConverter(dialogButton -> null);

                // Show the dialog
                dialog.showAndWait();
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
        Project selectedProject = TimeManager.getProjects().get(0);
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
                reportText.append("Generated on: ").append(getFormattedCurrentDateTime()).append("\n\n");


                // Project Details Section
                reportText.append("PROJECT DETAILS\n");
                reportText.append("-----------------------------------\n");
                reportText.append("Project Name: ").append(projectReport.get("Project Name")).append("\n");
                reportText.append("Project ID: ").append(projectReport.get("Project ID")).append("\n");
                String projectLead = projectReport.get("Project Lead").toString();
                if (projectLead == ""){
                    reportText.append("Project Lead: ").append("No one assigned yet").append("\n");
                } else {
                    reportText.append("Project Lead: ").append(projectLead).append("\n");
                }
                reportText.append("Time Period: ").append(projectReport.get("Project interval")).append("\n\n");

                // Activity Details Section
                reportText.append("ACTIVITY DETAILS\n");
                reportText.append("-----------------------------------\n");

                if (activities != null && !activities.isEmpty()) {
                    for (int i = 0; i < activities.size(); i++) {
                        Activity activity = activities.get(i);
                        reportText.append(i+1).append(". ").append(activity.getActivityName()).append("\n");
                        reportText.append("   Expected Work Hours: ").append(activity.getExpectedWorkHours()).append(" hours\n");
                        reportText.append("   Assigned Work Hours: ").append(activity.getWorkedHours()).append(" hours\n");

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

                        // Now show work hours after users
                        reportText.append("   Expected Work Hours: ").append(activity.getExpectedWorkHours()).append(" hours\n");
                        reportText.append("   Assigned Work Hours: ").append(activity.getWorkedHours()).append(" hours\n");

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
                loadProjectTree();
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
                loadProjectTree();
            } catch (Exception e) {
                showError("Error adding activity: " + e.getMessage());
                event.consume();
            }
        });

        dialog.setResultConverter(dialogButton -> null); // Handle addition manually
        dialog.showAndWait();
    }

    public void assignEmployee(ActionEvent actionEvent) {
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
                showActivityInformation(selectedActivity);
            } catch (Exception e) {
                showError("Error assigning employee: " + e.getMessage());
                event.consume();
            }
        });

        dialog.setResultConverter(dialogButton -> null); // Handle assignment manually
        dialog.showAndWait();
    }

    private void setupEditableActivityName(Label nameValueLabel, Activity activity) {
        nameValueLabel.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                TextField textField = new TextField(activity.getActivityName());
                textField.setOnAction(e -> {
                    activity.setActivityName(textField.getText());
                    showActivityInformation(activity); // Refresh view
                    loadProjectTree();
                });
                textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                    if (!isNowFocused) {
                        activity.setActivityName(textField.getText());
                        showActivityInformation(activity); // Refresh view
                        loadProjectTree();
                    }
                });

                HBox parent = (HBox) nameValueLabel.getParent();
                int index = parent.getChildren().indexOf(nameValueLabel);
                parent.getChildren().set(index, textField);
                textField.requestFocus();
            }
        });
    }

    private void setupEditableExpectedHours(Label expectedHoursValueLabel, Activity activity) {
        expectedHoursValueLabel.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String currentText = expectedHoursValueLabel.getText().replace(" hours", "").trim();
                TextField textField = new TextField(currentText);
                textField.setOnAction(e -> {
                    try {
                        double newHours = Double.parseDouble(textField.getText());
                        activity.setExpectedWorkHours(newHours);
                        showActivityInformation(activity); // Refresh view
                        loadProjectTree();
                    } catch (NumberFormatException ex) {
                        showError("Please enter a valid number for expected hours.");
                    }
                });
                textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                    if (!isNowFocused) {
                        try {
                            double newHours = Double.parseDouble(textField.getText());
                            activity.setExpectedWorkHours(newHours);
                            showActivityInformation(activity); // Refresh view
                            loadProjectTree();
                        } catch (NumberFormatException ex) {
                            showError("Please enter a valid number for expected hours.");
                        }
                    }
                });

                HBox parent = (HBox) expectedHoursValueLabel.getParent();
                int index = parent.getChildren().indexOf(expectedHoursValueLabel);
                parent.getChildren().set(index, textField);
                textField.requestFocus();
            }
        });
    }

    private String getFormattedCurrentDateTime() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        int day = now.getDayOfMonth();
        int month = now.getMonthValue();
        int year = now.getYear();
        int hour = now.getHour();
        int minute = now.getMinute();

        String[] monthNames = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };

        String monthName = monthNames[month - 1];

        // Pad single-digit minutes with a leading zero
        String minuteFormatted = (minute < 10) ? "0" + minute : String.valueOf(minute);

        return day + " " + monthName + " " + year + " at " + hour + ":" + minuteFormatted;
    }

    public void addTimeRegistration(ActionEvent actionEvent) {
        if (selectedActivity == null) {
            showError("Please select an activity first before registering time.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add Time Registration");
        dialog.setHeaderText("Enter time registration details:");

        ButtonType registerButtonType = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(registerButtonType, ButtonType.CANCEL);

        // --- Create clean inputs ---

        // 1. User choice box
        ChoiceBox<User> userChoiceBox = new ChoiceBox<>();
        userChoiceBox.getItems().addAll(TimeManager.getUsers());
        userChoiceBox.setValue(TimeManager.getCurrentUser()); // Default to current user

        // 2. Hours choice box
        ChoiceBox<Integer> hoursChoiceBox = new ChoiceBox<>();
        for (int i = 1; i <= 24; i++) {  // or you can allow more if needed
            hoursChoiceBox.getItems().add(i);
        }
        hoursChoiceBox.setValue(1);

        // 3. Date picker (non-editable)
        DatePicker datePicker = new DatePicker();
        datePicker.setEditable(false);
        datePicker.setPromptText("Select Date");

        // Optionally restrict future dates
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isAfter(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;"); // Optional: pink color for disabled
                }
            }
        });

        // --- Layout ---
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("User:"), 0, 0);
        grid.add(userChoiceBox, 1, 0);
        grid.add(new Label("Hours:"), 0, 1);
        grid.add(hoursChoiceBox, 1, 1);
        grid.add(new Label("Date:"), 0, 2);
        grid.add(datePicker, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(userChoiceBox::requestFocus);

        final Button registerButton = (Button) dialog.getDialogPane().lookupButton(registerButtonType);
        registerButton.addEventFilter(ActionEvent.ACTION, event -> {
            User selectedUser = userChoiceBox.getValue();
            Integer hours = hoursChoiceBox.getValue();
            LocalDate date = datePicker.getValue();

            if (selectedUser == null) {
                showError("Please select a user.");
                event.consume();
                return;
            }

            if (hours == null || hours <= 0) {
                showError("Please select valid hours.");
                event.consume();
                return;
            }

            if (date == null) {
                showError("Please select a date.");
                event.consume();
                return;
            }

            try {
                TimeRegistration timeRegistration = new TimeRegistration(selectedUser, selectedActivity, hours, date);
                TimeManager.addTimeRegistration(timeRegistration);

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Time registered successfully!");
                successAlert.showAndWait();

                showActivityInformation(selectedActivity);
                loadProjectTree();

            } catch (IllegalArgumentException e) {
                showError(e.getMessage());
                event.consume();
            }
        });

        dialog.setResultConverter(dialogButton -> null);
        dialog.showAndWait();
    }


}
