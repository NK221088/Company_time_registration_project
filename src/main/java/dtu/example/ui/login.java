package dtu.example.ui;

import dtu.time_manager.app.domain.TimeManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

public class login {
    private TimeManager timeManager;

    @FXML
    private TextField userInitials;

    @FXML
    private Label errorMessage;

    @FXML
    private void initialize() {
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
            timeManager.setCurrentUser(userInitials.getText());
            App.setRoot("main");
            errorMessage.setVisible(false);
        } catch (Exception e) {
            errorMessage.setText(e.getMessage());
            errorMessage.setVisible(true);
        }
    }
}
