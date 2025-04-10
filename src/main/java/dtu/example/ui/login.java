package dtu.example.ui;

import dtu.time_manager.app.TimeManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

public class login {
    private TimeManager timeManager = new TimeManager();

    @FXML
    private TextField userInitials;

    @FXML
    private Label errorMessage;

    @FXML
    private void initialize() {
        errorMessage.setVisible(false);
    }

    @FXML
    private void attemptLogin() throws IOException {
        try {
            timeManager.login(userInitials.getText());
            App.setRoot("main");
            errorMessage.setVisible(false);
        } catch (Exception e) {
            errorMessage.setText(e.getMessage());
            errorMessage.setVisible(true);
        }
        System.out.println(timeManager.logged_in);
    }
}
