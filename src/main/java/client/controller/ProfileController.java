package client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import shared.util.SceneTransition;
import shared.util.SessionManager;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;

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

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    public void initialize() {
        fullNameLabel.setText(SessionManager.getFullName());
        emailLabel.setText(SessionManager.getEmail());
        roleLabel.setText("CUSTOMER"); // TODO: store role in SessionManager

        fullNameField.setText(SessionManager.getFullName());
        phoneField.setText(SessionManager.getPhone());
    }

    @FXML
    private void handleSaveProfile() {
        String name  = fullNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String uid = shared.util.SessionManager.getUid();

        if (name.isEmpty()) {
            profileStatusLabel.setText("Name cannot be empty.");
            profileStatusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        String json = String.format(
                "{\"uid\":\"%s\", \"fullName\":\"%s\", \"phone\":\"%s\"}",
                uid, name, phone
        );

        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(shared.util.ServerConfig.SERVER_URL + "/api/auth/update-profile"))
                .header("Content-Type", "application/json")
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(json))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        // Update the session variables individually
                        SessionManager.setFullName(name);
                        SessionManager.setPhone(phone);

                        fullNameLabel.setText(name);
                        profileStatusLabel.setText("Profile updated.");
                        profileStatusLabel.setStyle("-fx-text-fill: green;");
                    } else {
                        profileStatusLabel.setText("Update failed.");
                        profileStatusLabel.setStyle("-fx-text-fill: red;");
                    }
                }));
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

        String json = String.format("{\"uid\":\"%s\", \"password\":\"%s\"}",
                shared.util.SessionManager.getUid(), newPass);

        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(shared.util.ServerConfig.SERVER_URL + "/api/auth/change-password"))
                .header("Content-Type", "application/json")
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(json))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        passwordStatusLabel.setText("Password updated.");
                        passwordStatusLabel.setStyle("-fx-text-fill: green;");

                        // Optional: Clear fields after success
                        newPasswordField.clear();
                        confirmPasswordField.clear();
                    } else {
                        passwordStatusLabel.setText("Failed to update password.");
                        passwordStatusLabel.setStyle("-fx-text-fill: red;");
                    }
                }));
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
