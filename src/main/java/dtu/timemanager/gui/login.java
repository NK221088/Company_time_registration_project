package dtu.timemanager.gui;

import dtu.timemanager.domain.TimeManager;
import dtu.timemanager.domain.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;

import java.io.IOException;
import java.util.Optional;

public class login {
    private TimeManager timeManager;

    @FXML
    private TextField userInitials;

    @FXML
    private Label errorMessage;

    @FXML
    private void initialize() throws Exception {
        timeManager = TimeManagerProvider.getInstance();
        errorMessage.setVisible(false);

        userInitials.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                try {
                    attemptLogin();
                } catch (IOException e) {
                    e.printStackTrace(); // or handle it more gracefully
                }
            }
        });
    }

    @FXML
    private void attemptLogin() throws IOException {
        try {
            timeManager.setCurrentUser(timeManager.getUserFromInitials(userInitials.getText()));
            App.setRoot("main");
            errorMessage.setVisible(false);
        } catch (Exception e) {
            errorMessage.setText(e.getMessage());
            errorMessage.setVisible(true);
        }
    }

    @FXML
    private void showAddUserDialog() {
        // Create the custom dialog
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Enter User Initials");

        // Set the button types
        ButtonType addButtonType = new ButtonType("Add", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create the initials label and field
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField initialsField = new TextField();
        initialsField.setPromptText("User initials (4 letters)");

        grid.add(new Label("User Initials:"), 0, 0);
        grid.add(initialsField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the initials field by default
        initialsField.requestFocus();

        // Convert the result to string when the add button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return initialsField.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(initials -> {
            try {
                timeManager.addUser(new User(initials));

                // Show success message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("User with initials '" + initials + "' was successfully added.");
                alert.showAndWait();

                // Automatically fill the login field with the new user's initials
                userInitials.setText(initials);

            } catch (Exception e) {
                // Show error message
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        });
    }
}