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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LoginController {

    private static final String SERVER_URL = "http://localhost:8080";

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button createAccountButton;
    @FXML private Label statusLabel;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    public void initialize() {
        loginButton.setOnAction(e -> attemptLogin());
        createAccountButton.setOnAction(e -> loadCreateAccount());

        usernameField.setOnAction(e -> attemptLogin());
        passwordField.setOnAction(e -> attemptLogin());

        ButtonEffects.applyAll(loginButton);
        ButtonEffects.applyAll(createAccountButton);
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter username and password.");
            return;
        }

        loginButton.setDisable(true);
        statusLabel.setStyle("-fx-text-fill: orange;");
        statusLabel.setText("Logging in...");

        String json = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> Platform.runLater(() -> handleLoginResponse(response)))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        statusLabel.setStyle("-fx-text-fill: red;");
                        statusLabel.setText("Cannot connect to server. Is it running?");
                        loginButton.setDisable(false);
                    });
                    return null;
                });
    }

    private void handleLoginResponse(HttpResponse<String> response) {
        loginButton.setDisable(false);

        if (response.statusCode() == 200) {
            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText("Login successful!");
            loadDashboard();
        } else if (response.statusCode() == 401) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Invalid username or password.");
        } else {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Unexpected server response.");
        }
    }

    private void loadCreateAccount() {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        SceneTransition.fadeSwitch(stage, "/fxml/createAccount.fxml", "FSCValet - Create Account");
    }

    private void loadDashboard() {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        SceneTransition.fadeSwitch(stage, "/fxml/dashboard.fxml", "FSCValet - Dashboard");
    }
}
