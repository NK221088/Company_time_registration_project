    package dtu.example.ui;

    import dtu.time_manager.app.*;
    import javafx.beans.property.SimpleStringProperty;
    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.application.Platform;
    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.fxml.Initializable;
    import javafx.scene.control.*;
    import javafx.scene.control.cell.PropertyValueFactory;
    import javafx.scene.input.MouseButton;
    import javafx.scene.input.MouseEvent;
    import javafx.scene.layout.AnchorPane;
    import javafx.scene.layout.GridPane;
    import javafx.scene.layout.HBox;
    import javafx.scene.layout.Priority;
    import javafx.scene.layout.VBox;
    import javafx.util.Callback;

    import java.io.IOException;
    import java.net.URL;
    import java.time.LocalDate;
    import java.time.format.DateTimeFormatter;
    import java.util.*;
    import java.util.regex.Matcher;
    import java.util.regex.Pattern;
    import java.util.stream.Collectors;

    public class TimeOverview implements Initializable {
        private TimeManager timeManager;

        @FXML
        private AnchorPane rootPane;

        @FXML
        private TabPane tabPane;

        @FXML
        private Tab personalTab;

        @FXML
        private Tab teamTab;

        @FXML
        private Spinner<Integer> daysSpinner;

        @FXML
        private DatePicker datePicker;

        @FXML
        private Button updateButton;

        @FXML
        private Label userLabel;

        private int daysToShow = 7; // Default number of days to show
        private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM");
        private TreeTableView<TimeEntryRow> personalTableView;
        private TableView<Map<String, String>> teamTableView;

        // Class to represent rows in the personal time view
        private static class TimeEntryRow {
            private final String name;
            private final Map<String, String> hoursByDate;
            private final boolean isProject;
            private final boolean isActivity;
            private final boolean isTimeRegistration;
            private TimeRegistration timeRegistration; // Store reference to the actual TimeRegistration object

            public TimeEntryRow(String name, boolean isProject, boolean isActivity, boolean isTimeRegistration) {
                this.name = name;
                this.hoursByDate = new HashMap<>();
                this.isProject = isProject;
                this.isActivity = isActivity;
                this.isTimeRegistration = isTimeRegistration;
                this.timeRegistration = null;
            }

            public TimeEntryRow(String name, boolean isProject, boolean isActivity, boolean isTimeRegistration, TimeRegistration tr) {
                this(name, isProject, isActivity, isTimeRegistration);
                this.timeRegistration = tr;
            }

            public String getName() {
                return name;
            }

            public String getHoursForDate(String date) {
                return hoursByDate.getOrDefault(date, "-");
            }

            public void setHoursForDate(String date, String hours) {
                hoursByDate.put(date, hours);
            }

            public boolean isProject() {
                return isProject;
            }

            public boolean isActivity() {
                return isActivity;
            }

            public boolean isTimeRegistration() {
                return isTimeRegistration;
            }

            public TimeRegistration getTimeRegistration() {
                return timeRegistration;
            }
        }

        /**
         * Initialize the controller with data from TimeManager
         */
        public void initialize(URL location, ResourceBundle resources) {
            timeManager = TimeManagerProvider.getInstance();

            SpinnerValueFactory<Integer> valueFactory =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, daysToShow);
            daysSpinner.setValueFactory(valueFactory);

            // Initialize date picker with current date
            if (datePicker == null) {
                // If not defined in FXML, create programmatically
                datePicker = new DatePicker(LocalDate.now());
            } else {
                datePicker.setValue(LocalDate.now());
            }

            // Create table views
            personalTableView = new TreeTableView<>();
            teamTableView = new TableView<>();

            // Setup both tabs with VBox layouts to hold their content
            VBox personalVBox = new VBox(10);
            personalVBox.getChildren().add(personalTableView);
            VBox.setVgrow(personalTableView, Priority.ALWAYS);
            personalTab.setContent(personalVBox);

            VBox teamVBox = new VBox(10);
            teamVBox.getChildren().add(teamTableView);
            VBox.setVgrow(teamTableView, Priority.ALWAYS);
            teamTab.setContent(teamVBox);

            // Add update button action
            updateButton.setOnAction(event -> {
                daysToShow = daysSpinner.getValue();
                refreshViews();
            });

            // Check if userLabel exists in FXML
            if (userLabel == null) {
                System.err.println("WARNING: userLabel is null! Check your FXML file.");
            } else {
                // Setup user info directly
                setupUserInfo();
            }

            // Setup double-click listener for time registrations
            setupTimeRegistrationClickListener();

            // Initialize the views
            refreshViews();
        }

        /**
         * Setup mouse event handler for editing time registrations
         */
        private void setupTimeRegistrationClickListener() {
            personalTableView.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    TreeItem<TimeEntryRow> selectedItem = personalTableView.getSelectionModel().getSelectedItem();
                    if (selectedItem != null && selectedItem.getValue().isTimeRegistration()) {
                        TimeRegistration tr = selectedItem.getValue().getTimeRegistration();
                        if (tr != null) {
                            editTimeRegistration(tr);
                        }
                    }
                }
            });
        }

        /**
         * Open dialog to edit existing time registration
         */
        private void editTimeRegistration(TimeRegistration tr) {
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Edit Time Registration");
            dialog.setHeaderText("Update time registration details:");

            ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

            // --- Create input fields ---

            // 1. User choice box
            ChoiceBox<User> userChoiceBox = new ChoiceBox<>();
            userChoiceBox.getItems().addAll(this.timeManager.getUsers());
            userChoiceBox.setValue(tr.getRegisteredUser()); // Current user

            // 2. Activity choice box (optional - only if you want to allow changing activity)
            // Get the project of the current activity
            Project currentProject = null;
            for (Project p : this.timeManager.getProjects()) {
                if (p.getActivities().contains(tr.getRegisteredActivity())) {
                    currentProject = p;
                    break;
                }
            }

            ChoiceBox<Activity> activityChoiceBox = new ChoiceBox<>();
            if (currentProject != null) {
                activityChoiceBox.getItems().addAll(currentProject.getActivities());
            } else {
                // If project not found, add just the current activity
                activityChoiceBox.getItems().add(tr.getRegisteredActivity());
            }
            activityChoiceBox.setValue(tr.getRegisteredActivity());

            // 3. Hours and minutes dropdowns
            double currentHours = tr.getRegisteredHours();
            int wholeHours = (int) currentHours;
            int minutes = (int) ((currentHours - wholeHours) * 60);

            ChoiceBox<Integer> hoursChoiceBox = new ChoiceBox<>();
            for (int i = 0; i <= 23; i++) {
                hoursChoiceBox.getItems().add(i);
            }
            hoursChoiceBox.setValue(wholeHours);

            ChoiceBox<Integer> minutesChoiceBox = new ChoiceBox<>();
            for (int i = 0; i < 60; i += 5) {
                minutesChoiceBox.getItems().add(i);  // increments of 5 minutes
            }
            // Find closest 5-minute interval
            int closestMinutes = (minutes / 5) * 5;
            minutesChoiceBox.setValue(closestMinutes);

            // 4. Date picker
            DatePicker datePicker = new DatePicker();
            datePicker.setValue(tr.getRegisteredDate());
            datePicker.setEditable(false);

            // Optional: Restrict future dates
            datePicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    if (date.isAfter(LocalDate.now())) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc0cb;");
                    }
                }
            });

            // --- Layout ---
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.add(new Label("User:"), 0, 0);
            grid.add(userChoiceBox, 1, 0);
            grid.add(new Label("Activity:"), 0, 1);
            grid.add(activityChoiceBox, 1, 1);
            grid.add(new Label("Time:"), 0, 2);
            HBox timeBox = new HBox(5, hoursChoiceBox, new Label("h"), minutesChoiceBox, new Label("m"));
            grid.add(timeBox, 1, 2);
            grid.add(new Label("Date:"), 0, 3);
            grid.add(datePicker, 1, 3);

            dialog.getDialogPane().setContent(grid);

            // Add button functionality
            final Button updateButton = (Button) dialog.getDialogPane().lookupButton(updateButtonType);
            updateButton.addEventFilter(ActionEvent.ACTION, event -> {
                User selectedUser = userChoiceBox.getValue();
                Activity selectedActivity = activityChoiceBox.getValue();
                int hours = hoursChoiceBox.getValue();
                int whole_minutes = minutesChoiceBox.getValue();
                double totalHours;

                if (15 <= whole_minutes && whole_minutes <= 44) {
                    totalHours = hours + 0.5;
                } else if (whole_minutes >= 45) {
                    totalHours = hours + 1;
                } else {
                    totalHours = hours; // 0-14 minutes rounds down
                }

                LocalDate date = datePicker.getValue();

                // Validation
                if (selectedUser == null) {
                    showError("Please select a user.");
                    event.consume();
                    return;
                }

                if (selectedActivity == null) {
                    showError("Please select an activity.");
                    event.consume();
                    return;
                }

                if (date == null) {
                    showError("Please select a date.");
                    event.consume();
                    return;
                }

                try {
                    // Update the time registration object
                    tr.setRegisteredUser(selectedUser);
                    tr.setRegisteredActivity(selectedActivity);
                    tr.setRegisteredHours(totalHours);
                    tr.setRegisteredDate(date);

                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Time registration updated successfully!");
                    successAlert.showAndWait();

                    // Refresh view to show updated data
                    refreshViews();

                } catch (Exception e) {
                    showError("Error updating time registration: " + e.getMessage());
                    event.consume();
                }
            });

            dialog.setResultConverter(dialogButton -> null);
            dialog.showAndWait();
        }

        /**
         * Set the TimeManager reference and initialize the views
         * Note: This method is no longer needed if TimeManager is entirely static
         * but kept for compatibility in case other code calls it
         */
        public void setTimeManager(TimeManager timeManager) {
            // We don't need to store the reference anymore
            // Just refresh the views
            setupUserInfo();
            refreshViews();
        }

        /**
         * Set up user info display
         */
        private void setupUserInfo() {
            if (userLabel == null) {
                System.err.println("ERROR: userLabel is null! Check your FXML file.");
                return;
            }

            // Access static TimeManager methods directly
            User currentUser = this.timeManager.getCurrentUser();
            if (currentUser != null) {
                userLabel.setText("Current User: " + currentUser.getUserInitials());
            } else {
                System.err.println("Current user is null in TimeManager!");
            }
        }

        /**
         * Show an error dialog
         */
        private void showError(String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }

        /**
         * Refresh both views with current data
         */
        private void refreshViews() {
            refreshPersonalView();
            refreshTeamView();
        }

        /**
         * Refresh the personal time overview with hierarchical project-activity structure
         */
        private void refreshPersonalView() {
            personalTableView.getColumns().clear();

            // Initialize root if it doesn't exist
            if (personalTableView.getRoot() == null) {
                personalTableView.setRoot(new TreeItem<>(new TimeEntryRow("Root", false, false, false)));
            } else {
                personalTableView.getRoot().getChildren().clear();
            }

            User currentUser = this.timeManager.getCurrentUser();
            if (currentUser == null) {
                // No user logged in, show message
                return;
            }

            // Get dates for the column headers
            List<LocalDate> dates = new ArrayList<>();
            LocalDate selectedDate = datePicker != null ? datePicker.getValue() : LocalDate.now();

            // Calculate start date based on selected date and days to show
            int daysBeforeSelected = daysToShow / 2;
            LocalDate startDate = selectedDate.minusDays(daysBeforeSelected);

            for (int i = 0; i < daysToShow; i++) {
                dates.add(startDate.plusDays(i));
            }

            // Create name column
            TreeTableColumn<TimeEntryRow, String> nameColumn = new TreeTableColumn<>("Project/Activity");
            nameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getName()));
            personalTableView.getColumns().add(nameColumn);

            // Create date columns
            for (LocalDate date : dates) {
                String dateStr = dateFormatter.format(date);
                final String columnDate = dateStr; // For lambda capture

                TreeTableColumn<TimeEntryRow, String> dateColumn = new TreeTableColumn<>(dateStr);
                dateColumn.setCellValueFactory(param ->
                        new SimpleStringProperty(param.getValue().getValue().getHoursForDate(columnDate)));

                // Custom cell factory for styling
                dateColumn.setCellFactory(col -> new TreeTableCell<TimeEntryRow, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle(null);
                        } else {
                            setText(item);
                            setStyle("-fx-alignment: CENTER;");

                            // Get the row data to determine if it's a project, activity, or time registration
                            TimeEntryRow rowData = getTreeTableRow().getItem();
                            if (rowData != null) {
                                if (rowData.isProject()) {
                                    setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
                                } else if (rowData.isActivity()) {
                                    setStyle("-fx-alignment: CENTER; -fx-font-style: italic;");
                                } else if (rowData.isTimeRegistration()) {
                                    // Add a visual indicator that this row is editable
                                    setStyle("-fx-alignment: CENTER; -fx-text-fill: #0066cc; -fx-cursor: hand;");
                                }
                            }
                        }
                    }
                });

                personalTableView.getColumns().add(dateColumn);
            }

            // Create root for the tree table
            TreeItem<TimeEntryRow> root = new TreeItem<>(new TimeEntryRow("Root", false, false, false));
            personalTableView.setRoot(root);
            personalTableView.setShowRoot(false);

            // User's time registrations by project and activity
            Map<Project, Map<Activity, List<TimeRegistration>>> userTimeRegByProject = new HashMap<>();

            // Get all time registrations for the current user and organize them by project and activity
            for (Project project : this.timeManager.getProjects()) {
                Map<Activity, List<TimeRegistration>> activitiesMap = new HashMap<>();

                for (Activity activity : project.getActivities()) {
                    // Check if user is assigned to or working on this activity
                    boolean isUserInvolved = (activity.getAssignedUsers() != null && activity.getAssignedUsers().contains(currentUser)) ||
                            (activity.getWorkingUsers() != null && activity.getWorkingUsers().contains(currentUser));

                    List<TimeRegistration> activityRegistrations = new ArrayList<>();

                    // Get time registrations for this activity
                    for (TimeRegistration tr : this.timeManager.getTimeRegistrations()) {
                        if (tr.getRegisteredUser().equals(currentUser) &&
                                tr.getRegisteredActivity().equals(activity)) {
                            activityRegistrations.add(tr);
                            isUserInvolved = true; // User has time registrations on this activity
                        }
                    }

                    // Include this activity if user is involved (assigned, working, or has registrations)
                    if (isUserInvolved) {
                        activitiesMap.put(activity, activityRegistrations);
                    }
                }

                if (!activitiesMap.isEmpty()) {
                    userTimeRegByProject.put(project, activitiesMap);
                }
            }

            // Build the tree structure
            Map<String, Integer> projectTotals = new HashMap<>();
            Map<String, Map<String, Integer>> activityTotals = new HashMap<>();

            for (Map.Entry<Project, Map<Activity, List<TimeRegistration>>> projectEntry : userTimeRegByProject.entrySet()) {
                Project project = projectEntry.getKey();
                Map<Activity, List<TimeRegistration>> activities = projectEntry.getValue();

                // Create project row
                TimeEntryRow projectRow = new TimeEntryRow(project.getProjectName(), true, false, false);
                TreeItem<TimeEntryRow> projectItem = new TreeItem<>(projectRow);
                root.getChildren().add(projectItem);
                projectItem.setExpanded(true);

                // Initialize project totals for each date
                for (LocalDate date : dates) {
                    String dateStr = dateFormatter.format(date);
                    projectTotals.put(dateStr, 0);
                }

                List<Map.Entry<Activity, List<TimeRegistration>>> sortedActivities =
                        activities.entrySet()                    // Set …
                                .stream()                      //  ⇢ Stream
                                // If Activity exposes a number: .sorted(Comparator.comparingInt(e -> e.getKey().getActivityNumber()))
                                .sorted(Comparator.comparingInt(
                                        e -> extractLeadingNumber(e.getKey().getActivityName())))
                                .collect(Collectors.toList());

                // Add activities for this project
                for (Map.Entry<Activity, List<TimeRegistration>> activityEntry : sortedActivities) {
                    Activity activity = activityEntry.getKey();
                    List<TimeRegistration> timeRegs = activityEntry.getValue();

                    // Create activity row
                    TimeEntryRow activityRow = new TimeEntryRow(activity.getActivityName(), false, true, false);
                    TreeItem<TimeEntryRow> activityItem = new TreeItem<>(activityRow);
                    projectItem.getChildren().add(activityItem);
                    activityItem.setExpanded(true);

                    // Initialize activity totals for each date
                    Map<String, Integer> activityDateTotals = new HashMap<>();
                    for (LocalDate date : dates) {
                        String dateStr = dateFormatter.format(date);
                        activityDateTotals.put(dateStr, 0);
                    }

                    // Add time registrations for this activity
                    for (TimeRegistration tr : timeRegs) {
                        // Only include time registrations within our date range
                        String trDateStr = dateFormatter.format(tr.getRegisteredDate());
                        if (dates.contains(tr.getRegisteredDate())) {
                            // Create description for time registration
                            String description = "Time: " + tr.getRegisteredHours() + "h";
                            if (tr.getRegisteredHours() == 1) {
                                description = "Time: 1h";
                            }

                            // Create time registration row with reference to the TR object
                            TimeEntryRow trRow = new TimeEntryRow(description, false, false, true, tr);
                            trRow.setHoursForDate(trDateStr, String.valueOf(tr.getRegisteredHours()));
                            TreeItem<TimeEntryRow> trItem = new TreeItem<>(trRow);
                            activityItem.getChildren().add(trItem);

                            // Add to totals - convert double to int if needed
                            int hoursAsInt = (int)tr.getRegisteredHours();
                            activityDateTotals.put(trDateStr, activityDateTotals.get(trDateStr) + hoursAsInt);
                            projectTotals.put(trDateStr, projectTotals.get(trDateStr) + hoursAsInt);
                        }
                    }

                    // Set activity totals for each date
                    for (LocalDate date : dates) {
                        String dateStr = dateFormatter.format(date);
                        int total = activityDateTotals.get(dateStr);
                        if (total > 0) {
                            activityRow.setHoursForDate(dateStr, String.valueOf(total));
                        }
                    }

                    activityTotals.put(activity.getActivityName(), activityDateTotals);
                }

                // Set project totals for each date
                for (LocalDate date : dates) {
                    String dateStr = dateFormatter.format(date);
                    int total = projectTotals.get(dateStr);
                    if (total > 0) {
                        projectRow.setHoursForDate(dateStr, String.valueOf(total));
                    }
                }
            }

            // Add a "Total" row at the end
            TimeEntryRow totalRow = new TimeEntryRow("TOTAL", true, false, false);
            TreeItem<TimeEntryRow> totalItem = new TreeItem<>(totalRow);
            root.getChildren().add(totalItem);

            // Calculate and set user total hours for each date
            for (LocalDate date : dates) {
                String dateStr = dateFormatter.format(date);
                int totalHours = 0;

                for (TimeRegistration tr : this.timeManager.getTimeRegistrations()) {
                    if (tr.getRegisteredUser().equals(currentUser) &&
                            tr.getRegisteredDate().equals(date)) {
                        totalHours += tr.getRegisteredHours();
                    }
                }

                if (totalHours > 0) {
                    totalRow.setHoursForDate(dateStr, String.valueOf(totalHours));
                }
            }

            // Set column resize policy to make columns fill the available space
            personalTableView.setColumnResizePolicy(param -> {
                double width = personalTableView.getWidth();
                double nameColumnWidth = width * 0.4; // Name column takes 40% of space
                double dateColumnWidth = (width - nameColumnWidth) / dates.size(); // Remaining space divided equally

                // Set width for name column
                nameColumn.setPrefWidth(nameColumnWidth);

                // Set width for all date columns
                for (int i = 1; i < personalTableView.getColumns().size(); i++) {
                    personalTableView.getColumns().get(i).setPrefWidth(dateColumnWidth);
                }

                return true;
            });

            // Set tooltip for the personal table view to inform users about editing
            personalTableView.setTooltip(new Tooltip("Double-click on a time registration to edit it"));
        }

        /**
         * Refresh the team time overview
         */
        private void refreshTeamView() {
            teamTableView.getColumns().clear();
            teamTableView.getItems().clear();

            // Get dates for the column headers (selected date and previous/following days)
            List<LocalDate> dates = new ArrayList<>();
            LocalDate selectedDate = datePicker != null ? datePicker.getValue() : LocalDate.now();

            // Calculate start date based on selected date and days to show
            int daysBeforeSelected = daysToShow / 2;
            LocalDate startDate = selectedDate.minusDays(daysBeforeSelected);

            for (int i = 0; i < daysToShow; i++) {
                dates.add(startDate.plusDays(i));
            }

            // Create columns
            TableColumn<Map<String, String>, String> userColumn = new TableColumn<>("User");
            userColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("user")));
            teamTableView.getColumns().add(userColumn);

            // Create date columns
            for (LocalDate date : dates) {
                String dateStr = dateFormatter.format(date);
                TableColumn<Map<String, String>, String> dateColumn = new TableColumn<>(dateStr);
                dateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(dateStr)));

                // Custom cell factory for color coding remaining hours
                dateColumn.setCellFactory(new Callback<TableColumn<Map<String, String>, String>,
                        TableCell<Map<String, String>, String>>() {
                    @Override
                    public TableCell<Map<String, String>, String> call(TableColumn<Map<String, String>, String> param) {
                        return new TableCell<Map<String, String>, String>() {
                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);

                                if (empty || item == null) {
                                    setText(null);
                                    setStyle(null);
                                } else {
                                    setText(item);
                                    setStyle("-fx-alignment: CENTER;");


                                    try {
                                        double hours = Double.parseDouble(item);
                                        if (hours <= 0) {
                                            setStyle("-fx-background-color: #ffc8c8; -fx-alignment: CENTER;"); // Light red
                                        } else if (hours < 2) {
                                            setStyle("-fx-background-color: #ffffc8; -fx-alignment: CENTER;"); // Light yellow
                                        } else {
                                            setStyle("-fx-background-color: #c8ffc8; -fx-alignment: CENTER;"); // Light green
                                        }
                                    } catch (NumberFormatException e) {
                                        // Not a number, use default style
                                        setStyle("-fx-alignment: CENTER;");
                                    }
                                }
                            }
                        };
                    }
                });

                teamTableView.getColumns().add(dateColumn);
            }

            // Create data rows
            ObservableList<Map<String, String>> items = FXCollections.observableArrayList();

            // Add data for each user
            for (User user : this.timeManager.getUsers()) {
                Map<String, String> rowData = new HashMap<>();
                rowData.put("user", user.getUserInitials());

                // For each date, calculate 7.5 - hours registered
                for (LocalDate date : dates) {
                    String dateStr = dateFormatter.format(date);
                    int registeredHours = 0;

                    for (TimeRegistration tr : this.timeManager.getTimeRegistrations()) {
                        if (tr.getRegisteredUser().equals(user) && tr.getRegisteredDate().equals(date)) {
                            registeredHours += tr.getRegisteredHours();
                        }
                    }

                    double remainingHours = 7.5 - registeredHours;
                    if (remainingHours < 0){ //A user can't be negatively available
                        remainingHours = 0;
                    }
                    rowData.put(dateStr, String.format("%.1f", remainingHours));
                }

                items.add(rowData);
            }

            teamTableView.setItems(items);

            // Force the columns to be equal width (except the user column)
            teamTableView.setColumnResizePolicy(param -> {
                // Calculate width for date columns
                double width = teamTableView.getWidth();
                double userColumnWidth = width * 0.3; // User column takes 30% of space
                double dateColumnWidth = (width - userColumnWidth) / dates.size(); // Remaining space divided equally

                // Set width for user column
                userColumn.setPrefWidth(userColumnWidth);

                // Set width for all date columns
                for (int i = 1; i < teamTableView.getColumns().size(); i++) {
                    teamTableView.getColumns().get(i).setPrefWidth(dateColumnWidth);
                }

                return true;
            });
        }

        public void backToProjectMenu(ActionEvent actionEvent) throws IOException {
            App.setRoot("main");
        }

        public void showAddTimeRegistrationDialog() {
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Add Time Registration");
            dialog.setHeaderText("Enter time registration details:");

            ButtonType registerButtonType = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(registerButtonType, ButtonType.CANCEL);

            // Activity choices: only assigned activities
            ChoiceBox<Activity> activityChoiceBox = new ChoiceBox<>();
            User currentUser = this.timeManager.getCurrentUser();

            List<Activity> assignedActivities = this.timeManager.getProjects().stream()
                    .flatMap(p -> p.getActivities().stream())
                    .filter(a -> a.getAssignedUsers().contains(currentUser) || a.getWorkingUsers().contains(currentUser))
                    .distinct()
                    .collect(Collectors.toList());

            activityChoiceBox.getItems().addAll(assignedActivities);

            // Hours and minutes
            ChoiceBox<Integer> hoursChoiceBox = new ChoiceBox<>();
            for (int i = 0; i <= 23; i++) hoursChoiceBox.getItems().add(i);
            hoursChoiceBox.setValue(0);

            ChoiceBox<Integer> minutesChoiceBox = new ChoiceBox<>();
            for (int i = 0; i < 60; i += 5) minutesChoiceBox.getItems().add(i);
            minutesChoiceBox.setValue(0);

            // Date picker
            DatePicker datePicker = new DatePicker(LocalDate.now());
            datePicker.setEditable(false);
            datePicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    if (date.isAfter(LocalDate.now())) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc0cb;");
                    }
                }
            });

            // Layout
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.add(new Label("Activity:"), 0, 0);
            grid.add(activityChoiceBox, 1, 0);
            grid.add(new Label("Time:"), 0, 1);
            grid.add(new HBox(5, hoursChoiceBox, new Label("h"), minutesChoiceBox, new Label("m")), 1, 1);
            grid.add(new Label("Date:"), 0, 2);
            grid.add(datePicker, 1, 2);

            dialog.getDialogPane().setContent(grid);

            final Button registerButton = (Button) dialog.getDialogPane().lookupButton(registerButtonType);
            registerButton.addEventFilter(ActionEvent.ACTION, event -> {
                Activity selectedActivity = activityChoiceBox.getValue();
                int hours = hoursChoiceBox.getValue();
                int minutes = minutesChoiceBox.getValue();
                LocalDate date = datePicker.getValue();

                if (selectedActivity == null) {
                    showError("Please select an activity.");
                    event.consume();
                    return;
                }
                if (date == null) {
                    showError("Please select a date.");
                    event.consume();
                    return;
                }

                double totalHours;
                if (15 <= minutes && minutes <= 44) {
                    totalHours = hours + 0.5;
                } else if (minutes >= 45) {
                    totalHours = hours + 1;
                } else {
                    totalHours = hours;
                }

                try {
                    TimeRegistration tr = new TimeRegistration(currentUser, selectedActivity, totalHours, date);
                    this.timeManager.addTimeRegistration(tr);

                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Time registered successfully!");
                    successAlert.showAndWait();

                    refreshViews();
                } catch (Exception e) {
                    showError("Error: " + e.getMessage());
                    event.consume();
                }
            });

            dialog.setResultConverter(dialogButton -> null);
            dialog.showAndWait();
        }

        private int extractLeadingNumber(String text) {
            Matcher m = Pattern.compile("(\\d+)").matcher(text);
            return m.find() ? Integer.parseInt(m.group(1)) : Integer.MAX_VALUE; // non-numbered go last
        }

    }