package dtu.timemanager.gui;

import dtu.timemanager.domain.TimeManager;
import dtu.timemanager.domain.User;
import dtu.timemanager.gui.helper.TimeManagerProvider;
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

public class LoginScene {
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
                    e.printStackTrace();
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
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Enter User Initials");

        ButtonType addButtonType = new ButtonType("Add", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField initialsField = new TextField();
        initialsField.setPromptText("User initials (4 letters)");

        grid.add(new Label("User Initials:"), 0, 0);
        grid.add(initialsField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        initialsField.requestFocus();

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

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("User with initials '" + initials + "' was successfully added.");
                alert.showAndWait();

                userInitials.setText(initials);

            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        });
    }
}