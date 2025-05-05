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
import javafx.util.Pair;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class projectMenu {
    private TimeManager timeManager;

    @FXML
    private TreeView<Object> projectTreeView;

    @FXML
    private ChoiceBox<Activity> activityContainer;

    @FXML
    private VBox projectInfoStatusContainer;

    @FXML
    private Label projectInfoStatus;

    @FXML
    private Button assignEmployeeButton;

    @FXML
    private Button unassignEmployeeButton;

    @FXML
    private Button addTimeRegistrationButton;

    @FXML
    private Button addActivityButton;

    private Project selectedProject;
    private Activity selectedActivity;
    private Boolean hasSelectedIndependent = false;

    @FXML
    private void initialize() {
        timeManager = TimeManagerProvider.getInstance();
        loadProjectTree();
        setSelectionListener();

        // Initial state of the finalize button
        if (finalizeActivityButton != null) {
            finalizeActivityButton.setDisable(true);
        }

        // Initial state of assign/unassign/time registration/add activity buttons
        updateButtonStates();

        // Set up custom cell factory for displaying finalized/completed status visually
        projectTreeView.setCellFactory(tv -> new TreeCell<Object>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else if (item instanceof Activity) {
                    Activity activity = (Activity) item;
                    setText(activity.getActivityName() + (activity.getFinalized() ? " ✓" : ""));
                } else if (item instanceof Project) {
                    Project project = (Project) item;
                    boolean allFinalized = project.getActivities().stream().allMatch(Activity::getFinalized);
                    setText(project.getProjectName() + (allFinalized && !project.getActivities().isEmpty() ? " (Completed)" : ""));
                } else {
                    setText(item.toString());
                }
            }
        });
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
        if (projectTreeView.getRoot() != null) {
            expandedPaths = getExpandedPaths(projectTreeView.getRoot());
        }

        TreeItem<Object> projectsItem = new TreeItem<>("Projects");
        projectsItem.setExpanded(true);

        for (Project project : timeManager.getProjects()) {
            TreeItem<Object> projectItem = new TreeItem<>(project);

            boolean allActivitiesFinalized = true;

            for (Activity activity : project.getActivities()) {
                TreeItem<Object> activityItem = new TreeItem<>(activity);

                if (!activity.getFinalized()) {
                    allActivitiesFinalized = false;
                }

                projectItem.getChildren().add(activityItem);
            }

            projectsItem.getChildren().add(projectItem);
        }

        TreeItem<Object> independentActivitiesItem = new TreeItem<>("Independent Activities");
        independentActivitiesItem.setExpanded(true);

        for (Activity activity : timeManager.getIndependentActivities()) {
            TreeItem<Object> activityItem = new TreeItem<>(activity);
            independentActivitiesItem.getChildren().add(activityItem);
        }

        TreeItem<Object> invisibleRoot = new TreeItem<>();
        invisibleRoot.setExpanded(true);
        invisibleRoot.getChildren().addAll(projectsItem, independentActivitiesItem);
        projectTreeView.setRoot(invisibleRoot);
        projectTreeView.setShowRoot(false);

        if (expandedPaths != null) {
            restoreExpandedPaths(projectTreeView.getRoot(), expandedPaths);
        }

        // Update the finalize button if an activity is selected
        updateFinalizeButtonText();
    }


    private void setSelectionListener() {
        projectTreeView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Object selectedItem = newSelection.getValue();

                if (selectedItem instanceof Project) {
                    selectedProject = (Project) selectedItem;
                    selectedActivity = null;
                    showInformation(null);
                } else if (selectedItem instanceof Activity) {
                    selectedActivity = (Activity) selectedItem;

                    // Try to get the parent project if possible
                    Object parentItem = newSelection.getParent().getValue();
                    if (parentItem instanceof Project) {
                        selectedProject = (Project) parentItem;
                    } else {
                        selectedProject = null; // Independent activity
                    }

                    showActivityInformation(selectedActivity);
                } else {
                    // This covers nodes like "Projects", "Independent Activities"
                    selectedProject = null;
                    selectedActivity = null;
                    showInformation(null);
                }

                hasSelectedIndependent = "Independent Activities".equals(selectedItem);

                updateFinalizeButtonText();
                updateButtonStates();  // ✅ Update button states based on selection
            }
        });
    }

    @FXML
    private Button finalizeActivityButton;

    private void updateFinalizeButtonText() {
        if (selectedActivity != null && selectedProject != null) {
            if (selectedActivity.getFinalized()) {
                finalizeActivityButton.setText("Unfinalize activity");
            } else {
                finalizeActivityButton.setText("Finalize activity");
            }
            finalizeActivityButton.setDisable(false);
        } else {
            finalizeActivityButton.setText("Finalize activity");
            finalizeActivityButton.setDisable(true);
        }
    }

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

        // Display finalization status - NEW
        HBox finalizedBox = new HBox(5);
        Label finalizedTitleLabel = new Label("Status:");
        Label finalizedValueLabel = new Label(activity.getFinalized() ? "Finalized" : "Not Finalized");
        // Set style based on status
        if (activity.getFinalized()) {
            finalizedValueLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        } else {
            finalizedValueLabel.setStyle("-fx-text-fill: #888888;");
        }
        finalizedBox.getChildren().addAll(finalizedTitleLabel, finalizedValueLabel);

        // Display time interval
        HBox timeIntervalBox = new HBox(5);
        Label timeIntervalTitleLabel = new Label("Time interval:");

        LocalDate startDate = (LocalDate) activityInfo.get("StartTime");
        LocalDate endDate = (LocalDate) activityInfo.get("EndTime");

        String startTime = (startDate != null)
                ? "Week " + startDate.get(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear()) + ", " + startDate.getYear()
                : "";

        String endTime = (endDate != null)
                ? "Week " + endDate.get(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear()) + ", " + endDate.getYear()
                : "";

        String intervalText;
        if (!startTime.isEmpty() && !endTime.isEmpty()) {
            intervalText = startTime + " to " + endTime;
        } else if (!startTime.isEmpty()) {
            intervalText = startTime + " onwards";
        } else if (!endTime.isEmpty()) {
            intervalText = "Until " + endTime;
        } else {
            intervalText = "Not defined";
        }

        Label timeIntervalValueLabel = new Label(intervalText);
        setupEditableTimeInterval(timeIntervalValueLabel, activity);
        timeIntervalBox.getChildren().addAll(timeIntervalTitleLabel, timeIntervalValueLabel);

        // Display expected work hours
        HBox expectedHoursBox = new HBox(5);
        Label expectedHoursTitleLabel = new Label("Expected work hours:");
        Label expectedHoursValueLabel = new Label(activityInfo.get("ExpectedWorkHours") + " hours");
        setupEditableExpectedHours(expectedHoursValueLabel, activity);
        expectedHoursBox.getChildren().addAll(expectedHoursTitleLabel, expectedHoursValueLabel);

        // Display assigned work hours
        Label assignedHoursLabel = new Label("Registered work hours: " + activityInfo.get("WorkedHours"));

        // Add the basic info to the container
        projectInfoStatusContainer.getChildren().addAll(nameBox, finalizedBox, timeIntervalBox, expectedHoursBox, assignedHoursLabel);

        // Display assigned users
        @SuppressWarnings("unchecked")
        ArrayList<User> assignedUsers = (ArrayList<User>) activityInfo.get("Assigned employees");

        if (assignedUsers != null && !assignedUsers.isEmpty()) {
            Label usersHeader = new Label("Assigned employees:");
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

        ArrayList<User> contributedUsers = (ArrayList<User>) activityInfo.get("Contributing employees");

        if (contributedUsers != null && !contributedUsers.isEmpty()) {
            Label usersHeader = new Label("Contributing employees:");
            projectInfoStatusContainer.getChildren().add(usersHeader);

            // Create a VBox to contain all users
            VBox usersContainer = new VBox(5);
            usersContainer.setPadding(new Insets(0, 0, 0, 15)); // Add some indentation

            // Add each user to the container
            for (User user : contributedUsers) {
                Label userLabel = new Label("• " + user.getUserInitials());
                usersContainer.getChildren().add(userLabel);
            }

            projectInfoStatusContainer.getChildren().add(usersContainer);
        } else {
            Label noUsersLabel = new Label("No one have worked on this activity.");
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
        userChoiceBox.getItems().addAll(timeManager.getUsers());

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
                timeManager.assignUser(activity, selectedUser);
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
        Map<String, Object> projectInfo = timeManager.viewProject(selectedProject);

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
                List<User> users = timeManager.getUsers();
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

                // Make date pickers not directly editable but clickable
                startPicker.setEditable(false);
                endPicker.setEditable(false);

                // But ensure they appear active
                startPicker.getEditor().setOpacity(1);
                endPicker.getEditor().setOpacity(1);

                startPicker.setPromptText("Start Date");
                endPicker.setPromptText("End Date");

                // Set cell factories to prevent invalid date selections
                startPicker.setDayCellFactory(picker -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        // Disable dates after end date if end date is set
                        if (endPicker.getValue() != null && date.isAfter(endPicker.getValue())) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        }
                    }
                });

                endPicker.setDayCellFactory(picker -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        // Disable dates before today
                        if (date.isBefore(LocalDate.now())) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        }
                        // Also disable dates before start date if start date is set
                        if (startPicker.getValue() != null && date.isBefore(startPicker.getValue())) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        }
                    }
                });

                HBox datePickers = new HBox(10, startPicker, endPicker);

                startPicker.setOnAction(e -> {
                    if (startPicker.getValue() != null) {
                        project.setProjectStartDate(startPicker.getValue());

                        // Reset end picker's cell factory to reflect new start date constraint
                        endPicker.setDayCellFactory(picker -> new DateCell() {
                            @Override
                            public void updateItem(LocalDate date, boolean empty) {
                                super.updateItem(date, empty);
                                // Disable dates before today
                                if (date.isBefore(LocalDate.now())) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                }
                                // Also disable dates before new start date
                                if (date.isBefore(startPicker.getValue())) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                }
                            }
                        });

                        showInformation(null);
                    }
                });

                endPicker.setOnAction(e -> {
                    if (endPicker.getValue() != null) {
                        project.setProjectEndDate(endPicker.getValue());

                        // Reset start picker's cell factory to reflect new end date constraint
                        startPicker.setDayCellFactory(picker -> new DateCell() {
                            @Override
                            public void updateItem(LocalDate date, boolean empty) {
                                super.updateItem(date, empty);
                                // Disable dates after end date
                                if (date.isAfter(endPicker.getValue())) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                }
                            }
                        });

                        showInformation(null);
                    }
                });

                int index = projectInfoStatusContainer.getChildren().indexOf(intervalLabel);
                projectInfoStatusContainer.getChildren().set(index, datePickers);
            }
        });
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
        Project selectedProject = timeManager.getProjects().get(0);
        if (selectedProject != null) {
            String projectName = selectedProject.toString();
            try {
                Map<String, Object> projectReport = timeManager.getProjectReport(selectedProject.getProjectID());
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

                        // Get all assigned users
                        ArrayList<User> assignedUsers = activity.getAssignedUsers();
                        if (assignedUsers != null && !assignedUsers.isEmpty()) {
                            reportText.append("   Assigned employees: ");
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
                        // Get all contributing users
                        ArrayList<User> contributedUsers = activity.getWorkingUsers();
                        if (contributedUsers != null && !contributedUsers.isEmpty()) {
                            reportText.append("   Contributing Users: ");
                            for (int j = 0; j < contributedUsers.size(); j++) {
                                reportText.append(contributedUsers.get(j));
                                if (j < contributedUsers.size() - 1) {
                                    reportText.append(", ");
                                }
                            }
                            reportText.append("\n");
                        } else {
                            reportText.append("   Contributing employees: None\n");
                        }

                        // Now show work hours after users
                        reportText.append("   Expected Work Hours: ").append(activity.getExpectedWorkHours()).append(" hours\n");
                        reportText.append("   Assigned Work Hours: ").append(activity.getWorkedHours()).append(" hours\n");

                        reportText.append("\n");
                    }
                } else {
                    reportText.append("No activities defined for this project.\n\n");
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

            if (timeManager.projectDuplicateExists(name)) {
                showError("A project with name '" + name + "' already exists in the system.\nPlease choose a different name.");
                event.consume();
                return;
            }

            try {
                Project project = timeManager.createProject(name);

                // Only set dates if they are chosen
                if (startDate != null) {
                    project.setProjectStartDate(startDate);
                }
                if (endDate != null) {
                    project.setProjectEndDate(endDate);
                }

                timeManager.addProject(project);
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
        if (!(selectedProject != null || hasSelectedIndependent)) {
            showError("Please select a project or independent activities first before adding an activity.");
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
                if (hasSelectedIndependent) {
                    timeManager.addIndependentActivity(activity);
                } else {
                    selectedProject.addActivity(activity); // <- This line calls your project.addActivity(name)
                }
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
//        if (!(selectedProject != null || hasSelectedIndependent)) {
//            showError("Please select a project first before assigning an employee.");
//            return;
//        }

        if (selectedActivity == null) {
            showError("Please select an activity first before assigning an employee.");
            return;
        }

        List<User> users = timeManager.getUsers();

        for (User user : users) {
            if (selectedActivity.getAssignedUsers().contains(user)) {
                users.remove(user);
            }
        }

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
                timeManager.assignUser(selectedActivity, selectedUser);

                // Show confirmation
                projectInfoStatus.setText("Employee " + selectedUser.getUserInitials() + " assigned to activity " + selectedActivity.getActivityName() + " successfully.");

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
        userChoiceBox.getItems().addAll(timeManager.getUsers());
        userChoiceBox.setValue(timeManager.getCurrentUser()); // Default to current user

        // 2. Hours and minutes dropdowns
        ChoiceBox<Integer> hoursChoiceBox = new ChoiceBox<>();
        for (int i = 0; i <= 23; i++) {
            hoursChoiceBox.getItems().add(i);
        }
        hoursChoiceBox.setValue(0); // default to 0

        ChoiceBox<Integer> minutesChoiceBox = new ChoiceBox<>();
        for (int i = 0; i < 60; i += 5) {
            minutesChoiceBox.getItems().add(i);  // increments of 5 minutes
        }
        minutesChoiceBox.setValue(0); // default to 0


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
        grid.add(new Label("Time:"), 0, 1);
        HBox timeBox = new HBox(5, hoursChoiceBox, new Label("h"), minutesChoiceBox, new Label("m"));
        grid.add(timeBox, 1, 1);


        grid.add(new Label("Date:"), 0, 2);
        grid.add(datePicker, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(userChoiceBox::requestFocus);
        final Button registerButton = (Button) dialog.getDialogPane().lookupButton(registerButtonType);
        registerButton.addEventFilter(ActionEvent.ACTION, event -> {
            User selectedUser = userChoiceBox.getValue();
            int hours = hoursChoiceBox.getValue();
            int minutes = minutesChoiceBox.getValue();
            double totalHours;

            if (15 <= minutes && minutes <= 44) {
                totalHours = hours + 0.5;
            } else if (minutes >= 45) {
                totalHours = hours + 1;
            } else {
                totalHours = hours; // add this case if you want 0–14 minutes to round down
            }

            LocalDate date = datePicker.getValue();

            if (selectedUser == null) {
                showError("Please select a user.");
                event.consume();
                return;
            }



            if (date == null) {
                showError("Please select a date.");
                event.consume();
                return;
            }

            try {
                TimeRegistration timeRegistration = new TimeRegistration(selectedUser, selectedActivity, totalHours, date);
                timeManager.addTimeRegistration(timeRegistration);

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
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        dialog.setResultConverter(dialogButton -> null);
        dialog.showAndWait();
    }


   
    public void unassignEmployee(ActionEvent actionEvent) {
        if (selectedActivity == null) {
            showError("Please select an activity first before assigning an employee.");
            return;
        }

        List<User> users = selectedActivity.getAssignedUsers();

        if (users == null || users.isEmpty()) {
            showError("No users available to assign. Please add users to the system first.");
            return;
        }

        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Unassign Employee");
        dialog.setHeaderText("Select an employee to unassign to the first activity:");

        ButtonType assignButtonType = new ButtonType("Unassign", ButtonBar.ButtonData.OK_DONE);
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
                showError("Please select an employee to unassign.");
                event.consume();
                return;
            }

            try {
                timeManager.unassignUser(selectedActivity, selectedUser);

                // Show confirmation
                projectInfoStatus.setText("Employee " + selectedUser.getUserInitials() + " unassigned to activity " +
                        selectedActivity.getActivityName() + " successfully.");

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

    private void setupEditableTimeInterval(Label timeIntervalLabel, Activity activity) {
        timeIntervalLabel.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                // Create a custom dialog for week selection
                Dialog<Pair<LocalDate, LocalDate>> dialog = new Dialog<>();
                dialog.setTitle("Set Activity Time Interval");
                dialog.setHeaderText("Select start and end weeks for activity: " + activity.getActivityName());

                // Set the button types
                ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

                // Create week selection UI
                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                // Create week spinners
                ComboBox<String> startWeekCombo = createWeekComboBox();
                ComboBox<Integer> startYearCombo = createYearComboBox();
                ComboBox<String> endWeekCombo = createWeekComboBox();
                ComboBox<Integer> endYearCombo = createYearComboBox();

                // Set current values if available
                setInitialWeekValues(activity.getActivityStartTime(), startWeekCombo, startYearCombo);
                setInitialWeekValues(activity.getActivityEndTime(), endWeekCombo, endYearCombo);

                // Add to grid
                grid.add(new Label("Start Week:"), 0, 0);
                grid.add(startWeekCombo, 1, 0);
                grid.add(new Label("Start Year:"), 0, 1);
                grid.add(startYearCombo, 1, 1);
                grid.add(new Label("End Week:"), 0, 2);
                grid.add(endWeekCombo, 1, 2);
                grid.add(new Label("End Year:"), 0, 3);
                grid.add(endYearCombo, 1, 3);

                dialog.getDialogPane().setContent(grid);

                // Convert the result to dates when confirmed
                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == confirmButtonType) {
                        LocalDate startDate = convertWeekToDate(startWeekCombo.getValue(), startYearCombo.getValue());
                        LocalDate endDate = convertWeekToDate(endWeekCombo.getValue(), endYearCombo.getValue());
                        return new Pair<>(startDate, endDate);
                    }
                    return null;
                });

                // Process the result
                Optional<Pair<LocalDate, LocalDate>> result = dialog.showAndWait();
                result.ifPresent(dates -> {
                    if (dates.getKey() != null) {
                        activity.setActivityStartTime(dates.getKey());
                    }
                    if (dates.getValue() != null) {
                        activity.setActivityEndTime(dates.getValue());
                    }
                    showActivityInformation(activity);
                });
            }
        });
    }

    // Helper methods for week selection
    private ComboBox<String> createWeekComboBox() {
        ComboBox<String> weekCombo = new ComboBox<>();
        // Populate with weeks 1-53
        for (int i = 1; i <= 53; i++) {
            weekCombo.getItems().add("Week " + i);
        }
        // Add an empty option
        weekCombo.getItems().add(0, "");
        weekCombo.setPromptText("Select Week");
        return weekCombo;
    }

    private ComboBox<Integer> createYearComboBox() {
        ComboBox<Integer> yearCombo = new ComboBox<>();
        // Populate with reasonable year range
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 5; i <= currentYear + 10; i++) {
            yearCombo.getItems().add(i);
        }
        // Add an empty option
        yearCombo.getItems().add(0, null);
        yearCombo.setPromptText("Select Year");
        return yearCombo;
    }

    private void setInitialWeekValues(LocalDate date, ComboBox<String> weekCombo, ComboBox<Integer> yearCombo) {
        if (date != null) {
            int week = date.get(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear());
            int year = date.getYear();
            weekCombo.setValue("Week " + week);
            yearCombo.setValue(year);
        } else {
            weekCombo.setValue("");
            yearCombo.setValue(null);
        }
    }

    private LocalDate convertWeekToDate(String weekString, Integer year) {
        if (weekString == null || weekString.isEmpty() || year == null) {
            return null;
        }

        // Extract week number from "Week X" format
        int week = Integer.parseInt(weekString.replace("Week ", ""));

        // Convert week/year to a date (first day of the week)
        return LocalDate.ofYearDay(year, 1)
                .with(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear(), week)
                .with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
    }

    public void finalizeActivity(ActionEvent actionEvent) {
        if (selectedActivity == null) {
            showError("Please select an activity first before finalizing.");
            return;
        }

        // Check if we have a selected project (independent activities cannot be finalized)
        if (selectedProject == null) {
            showError("Only activities that belong to a project can be finalized.");
            return;
        }

        // Get current finalization status from the activity
        boolean isCurrentlyFinalized = selectedActivity.getFinalized();

        // Create a confirmation dialog with proper wording based on current status
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle(isCurrentlyFinalized ? "Unfinalize Activity" : "Finalize Activity");
        confirmDialog.setHeaderText(null);

        if (isCurrentlyFinalized) {
            confirmDialog.setContentText("Are you sure you want to mark activity \"" +
                    selectedActivity.getActivityName() + "\" as NOT finalized?");
        } else {
            confirmDialog.setContentText("Are you sure you want to finalize activity \"" +
                    selectedActivity.getActivityName() + "\"?\n\nThis will mark the activity as complete.");
        }

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (isCurrentlyFinalized) {
                    // Unfinalize the activity
                    selectedProject.setActivityAsUnFinalized(selectedActivity);
                    projectInfoStatus.setText("Activity \"" + selectedActivity.getActivityName() +
                            "\" has been marked as not finalized.");
                } else {
                    // Finalize the activity
                    selectedProject.setActivityAsFinalized(selectedActivity);
                    projectInfoStatus.setText("Activity \"" + selectedActivity.getActivityName() +
                            "\" has been finalized successfully.");

                    // Check if all activities are now finalized, which means the project is finalized
                    boolean allActivitiesFinalized = true;
                    for (Activity activity : selectedProject.getActivities()) {
                        if (!activity.getFinalized()) {
                            allActivitiesFinalized = false;
                            break;
                        }
                    }

                    if (allActivitiesFinalized) {
                        Alert projectFinalizedAlert = new Alert(Alert.AlertType.INFORMATION);
                        projectFinalizedAlert.setTitle("Project Finalized");
                        projectFinalizedAlert.setHeaderText(null);
                        projectFinalizedAlert.setContentText("All activities in project \"" +
                                selectedProject.getProjectName() + "\" are now finalized.\n\n" +
                                "The project has been automatically marked as finalized.");
                        projectFinalizedAlert.showAndWait();
                    }
                }

                // Refresh the activity information display
                showActivityInformation(selectedActivity);
                updateButtonStates();

                // Update the project tree to reflect changes
                loadProjectTree();

                // Clear the status message after 3 seconds
                new Thread(() -> {
                    try {
                        Thread.sleep(3000);
                        Platform.runLater(() -> {
                            String text = projectInfoStatus.getText();
                            if (text.contains("finalized")) {
                                projectInfoStatus.setText("");
                            }
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();

            } catch (Exception e) {
                showError("Error " + (isCurrentlyFinalized ? "unfinalizing" : "finalizing") +
                        " activity: " + e.getMessage());
            }
        }
    }

    private void updateButtonStates() {
        // Disable assign, unassign, and add time registration if the selected activity is finalized
        boolean activityFinalized = selectedActivity != null && selectedActivity.getFinalized();

        if (assignEmployeeButton != null) {
            assignEmployeeButton.setDisable(selectedActivity == null || activityFinalized);
        }
        if (unassignEmployeeButton != null) {
            unassignEmployeeButton.setDisable(selectedActivity == null || activityFinalized);
        }
        if (addTimeRegistrationButton != null) {
            addTimeRegistrationButton.setDisable(selectedActivity == null || activityFinalized);
        }

        // Disable add activity if the selected project is fully finalized (all activities finalized)
        boolean projectFinalized = false;
        if (selectedProject != null && !selectedProject.getActivities().isEmpty()) {
            projectFinalized = selectedProject.getActivities().stream().allMatch(Activity::getFinalized);
        }

        if (addActivityButton != null) {
            addActivityButton.setDisable(selectedProject == null || projectFinalized);
        }
    }


}
