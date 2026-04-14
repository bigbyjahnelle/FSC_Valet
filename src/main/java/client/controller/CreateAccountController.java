package client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import shared.util.ButtonEffects;
import shared.util.SceneTransition;
import shared.util.ServerConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CreateAccountController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button createAccountButton;
    @FXML private Button backToLoginButton;
    @FXML private Label statusLabel;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String fireBaseAPIKey = System.getenv("FIREBASE_WEB_API_KEY");

    @FXML
    public void initialize() {
        createAccountButton.setOnAction(e -> attemptCreateAccount());
        backToLoginButton.setOnAction(e -> loadLogin());

        ButtonEffects.applyAll(createAccountButton);
        ButtonEffects.applyAll(backToLoginButton);
    }

    private void attemptCreateAccount() {
        String firstName = firstNameField.getText().trim();
        String lastName  = lastNameField.getText().trim();
        String email     = emailField.getText().trim();
        String password  = passwordField.getText();
        String confirm   = confirmPasswordField.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        if (!password.equals(confirm)) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Passwords do not match.");
            return;
        }

        if (password.length() < 6) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Password must be at least 6 characters.");
            return;
        }

        statusLabel.setStyle("-fx-text-fill: orange");
        statusLabel.setText("Creating account...");

        //Added the google URL for the firebase to make it work with the new API key
        String googleUrl = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + fireBaseAPIKey;
        String json = String.format(
                "{\"firstName\":\"%s\",\"lastName\":\"%s\",\"email\":\"%s\",\"password\":\"%s\"}",
                firstName, lastName, email, password
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(googleUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        statusLabel.setStyle("-fx-text-fill: green;");
                        statusLabel.setText("Success! Redirecting...");
                        loadLogin();
                    } else {
                        statusLabel.setStyle("-fx-text-fill: red;");
                        statusLabel.setText("Server Error: " + response.body());
                    }
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> statusLabel.setText("Could not connect to server."));
                    return null;
                });
    }

    private void loadLogin() {
        Stage stage = (Stage) backToLoginButton.getScene().getWindow();
        SceneTransition.fadeSwitch(stage, "/fxml/login.fxml", "FSCValet - Login");
    }
}
