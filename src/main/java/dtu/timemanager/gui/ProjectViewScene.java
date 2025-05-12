package dtu.timemanager.gui;

import dtu.timemanager.domain.*;
import dtu.timemanager.gui.helper.TimeHelper;
import dtu.timemanager.gui.helper.TimeManagerProvider;
import dtu.timemanager.gui.helper.TreeViewHelper;
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

public class ProjectViewScene {
    private TimeManager timeManager;
    private Project selectedProject;
    private Activity selectedActivity;
    private Set<String> expandedPaths; // For saving which folders were opened in the tree view

    @FXML
    private TreeView<Object> projectTreeView;

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

    @FXML
    private Button finalizeActivityButton;

    @FXML
    private void initialize() throws Exception {
        timeManager = TimeManagerProvider.getInstance();
        loadProjectTree();
        setSelectionListener();

        if (finalizeActivityButton != null) {
            finalizeActivityButton.setDisable(true);
        }

        updateButtonStates();

        // Correctly updating names and status when finalizing etc.
        projectTreeView.setCellFactory(tv -> new TreeCell<Object>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else if (item instanceof Activity activity) {
                    setText(activity.getActivityName() + (activity.getFinalized() ? " ✓" : ""));
                } else if (item instanceof Project project) {
                    boolean allFinalized = project.getActivities().stream().allMatch(Activity::getFinalized);
                    setText(project.getProjectName() + (allFinalized && !project.getActivities().isEmpty() ? " (Completed)" : ""));
                } else {
                    setText(item.toString());
                }
            }
        });
    }

    private void loadProjectTree() {
        if (projectTreeView.getRoot() != null) {
            expandedPaths = TreeViewHelper.getExpandedPaths(projectTreeView.getRoot());
        }

        TreeItem<Object> rootItem = new TreeItem<>("Projects");
        rootItem.setExpanded(true);

        for (Project project : timeManager.getProjects()) {
            TreeItem<Object> projectItem = new TreeItem<>(project);
            for (Activity activity : project.getActivities()) {
                TreeItem<Object> activityItem = new TreeItem<>(activity);
                projectItem.getChildren().add(activityItem);
            }
            rootItem.getChildren().add(projectItem);
        }

        projectTreeView.setRoot(rootItem);
        projectTreeView.setShowRoot(false);

        if (expandedPaths != null) {
            TreeViewHelper.restoreExpandedPaths(projectTreeView.getRoot(), expandedPaths);
        }
        updateFinalizeButtonText();
    }

    private void setSelectionListener() {
        projectTreeView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Object selectedItem = newSelection.getValue();

                if (selectedItem instanceof Project project) {
                    selectedProject = project;
                    selectedActivity = null;
                    showInformation(null);
                } else if (selectedItem instanceof Activity activity) {
                    selectedActivity = activity;
                    Object parentItem = newSelection.getParent().getValue();
                    if (parentItem instanceof Project project) {
                        selectedProject = project;
                    }
                    showActivityInformation(selectedActivity);
                } else {
                    selectedProject = null;
                    selectedActivity = null;
                    showInformation(null);
                }
                updateFinalizeButtonText();
                updateButtonStates();
            }
        });
    }

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

        HBox nameBox = new HBox(5);
        Label nameTitleLabel = new Label("Activity name:");
        Label nameValueLabel = new Label((String) activityInfo.get("Name"));
        setupEditableActivityName(nameValueLabel, activity);
        nameBox.getChildren().addAll(nameTitleLabel, nameValueLabel);

        HBox finalizedBox = new HBox(5);
        Label finalizedTitleLabel = new Label("Status:");
        Label finalizedValueLabel = new Label(activity.getFinalized() ? "Finalized" : "Not Finalized");
        if (activity.getFinalized()) {
            finalizedValueLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        } else {
            finalizedValueLabel.setStyle("-fx-text-fill: #888888;");
        }
        finalizedBox.getChildren().addAll(finalizedTitleLabel, finalizedValueLabel);

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

        HBox expectedHoursBox = new HBox(5);
        Label expectedHoursTitleLabel = new Label("Expected work hours:");
        Label expectedHoursValueLabel = new Label(activityInfo.get("ExpectedWorkHours") + " hours");
        setupEditableExpectedHours(expectedHoursValueLabel, activity);
        expectedHoursBox.getChildren().addAll(expectedHoursTitleLabel, expectedHoursValueLabel);

        Label assignedHoursLabel = new Label("Registered work hours: " + activityInfo.get("WorkedHours"));

        projectInfoStatusContainer.getChildren().addAll(nameBox, finalizedBox, timeIntervalBox, expectedHoursBox, assignedHoursLabel);

        @SuppressWarnings("unchecked")
        ArrayList<User> assignedUsers = (ArrayList<User>) activityInfo.get("Assigned employees");

        if (assignedUsers != null && !assignedUsers.isEmpty()) {
            Label usersHeader = new Label("Assigned employees:");
            projectInfoStatusContainer.getChildren().add(usersHeader);

            VBox usersContainer = new VBox(5);
            usersContainer.setPadding(new Insets(0, 0, 0, 15)); // Add some indentation

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

            VBox usersContainer = new VBox(5);
            usersContainer.setPadding(new Insets(0, 0, 0, 15)); // Add some indentation

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

    public void backToProjectMenu(ActionEvent actionEvent) throws IOException {
        App.setRoot("main");
    }

    public void showInformation(ActionEvent actionEvent) {
        projectInfoStatusContainer.getChildren().clear();

        if (selectedProject == null) {
            projectInfoStatusContainer.getChildren().add(new Label("No project selected."));
            return;
        }

        ProjectReport projectReport = timeManager.getProjectReport(selectedProject);

        Label nameLabel = new Label("Project name: " + projectReport.getProjectName());
        setupEditableName(nameLabel, selectedProject);

        Label idLabel = new Label("Project ID: " + projectReport.getProjectID());

        Object projectLead = projectReport.getProjectLead();
        if (projectLead == null) {
            projectLead = "";
        }
        Label projectLeadLabel = new Label("Project lead: " + projectLead);
        setupEditableProjectLead(projectLeadLabel, selectedProject);

        Label intervalLabel = new Label("Project interval: " + projectReport.getProjectInterval());
        setupEditableInterval(intervalLabel, selectedProject);

        projectInfoStatusContainer.getChildren().addAll(nameLabel, idLabel, projectLeadLabel, intervalLabel);
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
                List<User> users = timeManager.getUsers();
                if (users == null || users.isEmpty()) {
                    showError("No users available to assign. Please add users to the system first.");
                    return;
                }

                Dialog<User> dialog = new Dialog<>();
                dialog.setTitle("Assign Project Lead");
                dialog.setHeaderText("Select a user to assign as project lead:");

                ButtonType assignButtonType = new ButtonType("Assign", ButtonBar.ButtonData.OK_DONE);
                ButtonType clearButtonType = new ButtonType("Clear Lead", ButtonBar.ButtonData.LEFT);
                dialog.getDialogPane().getButtonTypes().addAll(assignButtonType, clearButtonType, ButtonType.CANCEL);

                ChoiceBox<User> userChoiceBox = new ChoiceBox<>();
                userChoiceBox.getItems().addAll(users);

                if (project.getProjectLead() != null) {
                    for (User user : users) {
                        if (user.getUserInitials().equals(project.getProjectLead().getUserInitials())) {
                            userChoiceBox.setValue(user);
                            break;
                        }
                    }
                }

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.add(new Label("Project Lead:"), 0, 0);
                grid.add(userChoiceBox, 1, 0);
                dialog.getDialogPane().setContent(grid);

                final Button assignButton = (Button) dialog.getDialogPane().lookupButton(assignButtonType);
                assignButton.addEventFilter(ActionEvent.ACTION, e -> {
                    User selectedUser = userChoiceBox.getValue();
                    if (selectedUser == null) {
                        showError("Please select a user to assign as project lead.");
                        e.consume();
                        return;
                    }

                    try {
                        project.setProjectLead(selectedUser);
                        showInformation(null); // Refresh view
                    } catch (Exception ex) {
                        showError("Error assigning project lead: " + ex.getMessage());
                        e.consume();
                    }
                });

                final Button clearButton = (Button) dialog.getDialogPane().lookupButton(clearButtonType);
                clearButton.addEventFilter(ActionEvent.ACTION, e -> {
                    project.setProjectLead(null);
                    showInformation(null); // Refresh view
                    dialog.close();
                });

                dialog.setResultConverter(dialogButton -> null);

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

                startPicker.getEditor().setOpacity(1);
                endPicker.getEditor().setOpacity(1);

                startPicker.setPromptText("Start Date");
                endPicker.setPromptText("End Date");

                startPicker.setDayCellFactory(picker -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
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
                        if (date.isBefore(LocalDate.now())) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        }
                        if (startPicker.getValue() != null && date.isBefore(startPicker.getValue())) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        }
                    }
                });

                HBox datePickers = new HBox(10, startPicker, endPicker);

                startPicker.setOnAction(e -> {
                    if (startPicker.getValue() != null) {
                        try {
                            project.setProjectStartDate(startPicker.getValue());
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }

                        endPicker.setDayCellFactory(picker -> new DateCell() {
                            @Override
                            public void updateItem(LocalDate date, boolean empty) {
                                super.updateItem(date, empty);
                                if (date.isBefore(LocalDate.now())) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                }
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
                        try {
                            project.setProjectEndDate(endPicker.getValue());
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }

                        startPicker.setDayCellFactory(picker -> new DateCell() {
                            @Override
                            public void updateItem(LocalDate date, boolean empty) {
                                super.updateItem(date, empty);
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

    public void projectReport(ActionEvent actionEvent) {
        if (selectedProject != null) {
            String projectName = selectedProject.toString();
            try {
                ProjectReport projectReport = timeManager.getProjectReport(selectedProject);
                List<Activity> activities = projectReport.getActivities();

                StringBuilder reportText = new StringBuilder();

                reportText.append("===================================\n");
                reportText.append("          PROJECT REPORT           \n");
                reportText.append("===================================\n\n");
                reportText.append("Generated on: ").append(TimeHelper.getFormattedCurrentDateTime()).append("\n\n");

                reportText.append("PROJECT DETAILS\n");
                reportText.append("-----------------------------------\n");
                reportText.append("Project Name: ").append(projectReport.getProjectName()).append("\n");
                reportText.append("Project ID: ").append(projectReport.getProjectID()).append("\n");
                String projectLead;
                if (projectReport.getProjectLead() == null) {
                    projectLead = "";
                } else {
                    projectLead = projectReport.getProjectLead().toString();
                }

                if (projectLead == ""){
                    reportText.append("Project Lead: ").append("No one assigned yet").append("\n");
                } else {
                    reportText.append("Project Lead: ").append(projectLead).append("\n");
                }
                reportText.append("Time Period: ").append(projectReport.getProjectInterval()).append("\n\n");

                reportText.append("ACTIVITY DETAILS\n");
                reportText.append("-----------------------------------\n");

                if (activities != null && !activities.isEmpty()) {
                    for (int i = 0; i < activities.size(); i++) {
                        Activity activity = activities.get(i);
                        reportText.append(i+1).append(". ").append(activity.getActivityName()).append("\n");

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

                        ArrayList<User> contributedUsers = activity.getContributingUsers();
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

                        reportText.append("   Expected Work Hours: ").append(activity.getExpectedWorkHours()).append(" hours\n");
                        reportText.append("   Assigned Work Hours: ").append(activity.getWorkedHours()).append(" hours\n");

                        reportText.append("\n");
                    }
                } else {
                    reportText.append("No activities defined for this project.\n\n");
                }

                String userHome = System.getProperty("user.home");
                String downloadsPath = userHome + "/Downloads/";
                String fileName = downloadsPath + projectName.replaceAll("\\s+", "_") + "_report.txt";

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                    writer.write(reportText.toString());

                    Alert confirmationAlert = new Alert(Alert.AlertType.INFORMATION);
                    confirmationAlert.setTitle("Report Generated");
                    confirmationAlert.setHeaderText(null);
                    confirmationAlert.setContentText("Project report for \"" + projectName + "\" has been generated and saved to your Downloads folder.");
                    confirmationAlert.showAndWait();

                    projectInfoStatus.setText("Project report generated successfully and saved to Downloads folder.");

                    new Thread(() -> {
                        try {
                            Thread.sleep(2000);
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

            if (timeManager.projectExists(selectedProject)) {
                showError("A project with name '" + name + "' already exists in the system.\nPlease choose a different name.");
                event.consume();
                return;
            }

            try {
                Project project = timeManager.addProject(name);

                if (startDate != null) {
                    project.setProjectStartDate(startDate);
                }
                if (endDate != null) {
                    project.setProjectEndDate(endDate);
                }
            } catch (Exception e) {
                showError("Error adding project: " + e.getMessage());
                event.consume();
            }
            loadProjectTree();
        });


        dialog.setResultConverter(dialogButton -> null); // We already handle adding manually
        dialog.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void addActivity(ActionEvent actionEvent) {
        if (!(selectedProject != null)) {
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

                projectInfoStatus.setText("Employee " + selectedUser.getUserInitials() + " assigned to activity " + selectedActivity.getActivityName() + " successfully.");

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

        ChoiceBox<User> userChoiceBox = new ChoiceBox<>();
        userChoiceBox.getItems().addAll(timeManager.getUsers());
        userChoiceBox.setValue(timeManager.getCurrentUser()); // Default to current user

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

        DatePicker datePicker = new DatePicker();
        datePicker.setEditable(false);
        datePicker.setPromptText("Select Date");

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
                totalHours = hours; // 0–14 minutes rounds down
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

                projectInfoStatus.setText("Employee " + selectedUser.getUserInitials() + " unassigned to activity " +
                        selectedActivity.getActivityName() + " successfully.");

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
                Dialog<Pair<LocalDate, LocalDate>> dialog = new Dialog<>();
                dialog.setTitle("Set Activity Time Interval");
                dialog.setHeaderText("Select start and end weeks for activity: " + activity.getActivityName());

                ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                ComboBox<String> startWeekCombo = TimeHelper.createWeekComboBox();
                ComboBox<Integer> startYearCombo = TimeHelper.createYearComboBox();
                ComboBox<String> endWeekCombo = TimeHelper.createWeekComboBox();
                ComboBox<Integer> endYearCombo = TimeHelper.createYearComboBox();

                setInitialWeekValues(activity.getActivityStartTime(), startWeekCombo, startYearCombo);
                setInitialWeekValues(activity.getActivityEndTime(), endWeekCombo, endYearCombo);

                grid.add(new Label("Start Week:"), 0, 0);
                grid.add(startWeekCombo, 1, 0);
                grid.add(new Label("Start Year:"), 0, 1);
                grid.add(startYearCombo, 1, 1);
                grid.add(new Label("End Week:"), 0, 2);
                grid.add(endWeekCombo, 1, 2);
                grid.add(new Label("End Year:"), 0, 3);
                grid.add(endYearCombo, 1, 3);

                dialog.getDialogPane().setContent(grid);

                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == confirmButtonType) {
                        LocalDate startDate = TimeHelper.convertWeekToDate(startWeekCombo.getValue(), startYearCombo.getValue());
                        LocalDate endDate = TimeHelper.convertWeekToDate(endWeekCombo.getValue(), endYearCombo.getValue());
                        return new Pair<>(startDate, endDate);
                    }
                    return null;
                });

                Optional<Pair<LocalDate, LocalDate>> result = dialog.showAndWait();

                result.ifPresent(dates -> {
                    if (dates.getKey() != null) {
                        try {
                            activity.setActivityStartTime(dates.getKey());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (dates.getValue() != null) {
                        try {
                            activity.setActivityEndTime(dates.getValue());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    showActivityInformation(activity);
                });
            }
        });
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

    public void finalizeActivity(ActionEvent actionEvent) {
        if (selectedActivity == null) {
            showError("Please select an activity first before finalizing.");
            return;
        }

        if (selectedProject == null) {
            showError("Only activities that belong to a project can be finalized.");
            return;
        }

        boolean isCurrentlyFinalized = selectedActivity.getFinalized();

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
                    selectedProject.setActivityAsUnFinalized(selectedActivity);
                    projectInfoStatus.setText("Activity \"" + selectedActivity.getActivityName() +
                            "\" has been marked as not finalized.");
                } else {
                    selectedProject.setActivityAsFinalized(selectedActivity);
                    projectInfoStatus.setText("Activity \"" + selectedActivity.getActivityName() +
                            "\" has been finalized successfully.");

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

                showActivityInformation(selectedActivity);
                updateButtonStates();

                loadProjectTree();

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

        boolean projectFinalized = false;
        if (selectedProject != null && !selectedProject.getActivities().isEmpty()) {
            projectFinalized = selectedProject.getActivities().stream().allMatch(Activity::getFinalized);
        }

        if (addActivityButton != null) {
            addActivityButton.setDisable(selectedProject == null || projectFinalized);
        }
    }
}
