package dtu.example.ui;

import dtu.time_manager.app.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class TimeOverview implements Initializable {

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

    // No timeManager instance needed if it's static
    private int daysToShow = 7; // Default number of days to show
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM");
    private TableView<Map<String, String>> personalTableView;
    private TableView<Map<String, String>> teamTableView;

    /**
     * Initialize the controller with data from TimeManager
     */
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize spinner
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
        personalTableView = new TableView<>();
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

        // Initialize the views
        refreshViews();

        System.out.println("TimeOverview initialized");
    }

    /**
     * Set the TimeManager reference and initialize the views
     * Note: This method is no longer needed if TimeManager is entirely static
     * but kept for compatibility in case other code calls it
     */
    public void setTimeManager(TimeManager timeManager) {
        System.out.println("setTimeManager called (not needed for static TimeManager)");
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
        User currentUser = TimeManager.getCurrentUser();
        if (currentUser != null) {
            userLabel.setText("Current User: " + currentUser.getUserInitials());
            System.out.println("Set user label to: " + userLabel.getText());
        } else {
            System.err.println("Current user is null in TimeManager!");
        }
    }

    /**
     * Refresh both views with current data
     */
    private void refreshViews() {
        refreshPersonalView();
        refreshTeamView();
    }

    /**
     * Refresh the personal time overview
     */
    private void refreshPersonalView() {
        personalTableView.getColumns().clear();
        personalTableView.getItems().clear();

        User currentUser = TimeManager.getCurrentUser();
        if (currentUser == null) {
            // No user logged in, show message
            return;
        }

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
        TableColumn<Map<String, String>, String> activityColumn = new TableColumn<>("Activity");
        activityColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("activity")));
        personalTableView.getColumns().add(activityColumn);

        // Create date columns
        for (LocalDate date : dates) {
            String dateStr = dateFormatter.format(date);
            TableColumn<Map<String, String>, String> dateColumn = new TableColumn<>(dateStr);
            dateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(dateStr)));

            // Center align the text in date columns
            dateColumn.setCellFactory(col -> new TableCell<Map<String, String>, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item);
                        setStyle("-fx-alignment: CENTER;");
                    }
                }
            });

            personalTableView.getColumns().add(dateColumn);
        }

        // Get all activities for this user
        Set<Activity> activities = new HashSet<>();
        for (TimeRegistration tr : TimeManager.getTimeRegistrations()) {
            if (tr.getRegisteredUser().equals(currentUser)) {
                activities.add(tr.getRegisteredActivity());
            }
        }

        // Create data rows
        ObservableList<Map<String, String>> items = FXCollections.observableArrayList();

        // Add data for each activity
        for (Activity activity : activities) {
            Map<String, String> rowData = new HashMap<>();
            rowData.put("activity", activity.getActivityName());

            // For each date, calculate hours spent on this activity
            for (LocalDate date : dates) {
                String dateStr = dateFormatter.format(date);
                int hours = 0;

                for (TimeRegistration tr : TimeManager.getTimeRegistrations()) {
                    if (tr.getRegisteredUser().equals(currentUser) &&
                            tr.getRegisteredActivity().equals(activity) &&
                            tr.getRegisteredDate().equals(date)) {
                        hours += tr.getRegisteredHours();
                    }
                }

                rowData.put(dateStr, hours > 0 ? String.valueOf(hours) : "-");
            }

            items.add(rowData);
        }

        // Add a "Total" row
        Map<String, String> totalRow = new HashMap<>();
        totalRow.put("activity", "Total");

        for (LocalDate date : dates) {
            String dateStr = dateFormatter.format(date);
            int totalHours = 0;

            for (TimeRegistration tr : TimeManager.getTimeRegistrations()) {
                if (tr.getRegisteredUser().equals(currentUser) && tr.getRegisteredDate().equals(date)) {
                    totalHours += tr.getRegisteredHours();
                }
            }

            totalRow.put(dateStr, totalHours > 0 ? String.valueOf(totalHours) : "-");
        }

        items.add(totalRow);
        personalTableView.setItems(items);

        // Force the columns to be equal width (except the activity column)
        personalTableView.setColumnResizePolicy(param -> {
            // Calculate width for date columns
            double width = personalTableView.getWidth();
            double activityColumnWidth = width * 0.3; // Activity column takes 30% of space
            double dateColumnWidth = (width - activityColumnWidth) / dates.size(); // Remaining space divided equally

            // Set width for activity column
            activityColumn.setPrefWidth(activityColumnWidth);

            // Set width for all date columns
            for (int i = 1; i < personalTableView.getColumns().size(); i++) {
                personalTableView.getColumns().get(i).setPrefWidth(dateColumnWidth);
            }

            return true;
        });
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
        for (User user : TimeManager.getUsers()) {
            Map<String, String> rowData = new HashMap<>();
            rowData.put("user", user.getUserInitials());

            // For each date, calculate 7.5 - hours registered
            for (LocalDate date : dates) {
                String dateStr = dateFormatter.format(date);
                int registeredHours = 0;

                for (TimeRegistration tr : TimeManager.getTimeRegistrations()) {
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
}