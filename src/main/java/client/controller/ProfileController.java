package client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import shared.util.SceneTransition;
import shared.util.SessionManager;

public class ProfileController {

    // Read-only labels
    @FXML private Label fullNameLabel;
    @FXML private Label emailLabel;
    @FXML private Label roleLabel;

    // Edit profile
    @FXML private TextField fullNameField;
    @FXML private TextField phoneField;
    @FXML private Label profileStatusLabel;

    // Change password
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label passwordStatusLabel;

    @FXML
    public void initialize() {
        fullNameLabel.setText(SessionManager.getFullName());
        emailLabel.setText(SessionManager.getEmail());
        roleLabel.setText("CUSTOMER"); // TODO: store role in SessionManager

        fullNameField.setText(SessionManager.getFullName());
    }

    @FXML
    private void handleSaveProfile() {
        String name  = fullNameField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty()) {
            profileStatusLabel.setText("Name cannot be empty.");
            profileStatusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // TODO: POST updated name/phone to server endpoint (not yet implemented)
        profileStatusLabel.setText("Changes saved."); // placeholder until endpoint exists
        profileStatusLabel.setStyle("-fx-text-fill: green;");
    }

    @FXML
    private void handleChangePassword() {
        String newPass     = newPasswordField.getText();
        String confirmPass = confirmPasswordField.getText();

        if (newPass.isEmpty()) {
            passwordStatusLabel.setText("Please enter a new password.");
            passwordStatusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (newPass.length() < 6) {
            passwordStatusLabel.setText("Password must be at least 6 characters.");
            passwordStatusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            passwordStatusLabel.setText("Passwords do not match.");
            passwordStatusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // TODO: POST new password to server endpoint (not yet implemented)
        passwordStatusLabel.setText("Password updated."); // placeholder until endpoint exists
        passwordStatusLabel.setStyle("-fx-text-fill: green;");
    }

    @FXML
    private void handleLogOut() {
        SessionManager.clear();
        Stage stage = (Stage) fullNameLabel.getScene().getWindow();
        SceneTransition.fadeSwitch(stage, "/fxml/login.fxml", "FSC Valet - Login");
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) fullNameLabel.getScene().getWindow();
        SceneTransition.fadeSwitch(stage, "/fxml/dashboard.fxml", "FSC Valet - Dashboard");
    }
}
