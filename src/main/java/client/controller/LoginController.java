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

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button createAccountButton;
    @FXML private Label statusLabel;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String fireBaseAPIKey = "AIzaSyCAscy9pfbEQIMNY3QlP3i643FposKj3yc";

    @FXML
    public void initialize() {
        loginButton.setOnAction(e -> attemptLogin());
        createAccountButton.setOnAction(e -> loadCreateAccount());

        emailField.setOnAction(e -> attemptLogin());
        passwordField.setOnAction(e -> attemptLogin());

        ButtonEffects.applyAll(loginButton);
        ButtonEffects.applyAll(createAccountButton);
    }

    private void attemptLogin() {
        String email    = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter email and password.");
            return;
        }

        loginButton.setDisable(true);
        statusLabel.setStyle("-fx-text-fill: orange;");
        statusLabel.setText("Logging in...");

        String url  = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + fireBaseAPIKey;
        String json = String.format("{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}", email, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    System.out.println("GOOGLE RESPONSE CODE: " + response.statusCode());
                    System.out.println("GOOGLE RESPONSE BODY: " + response.body());

                    if (response.statusCode() == 200) {
                        String idToken = parseTokenFromJson(response.body());
                        authenticateWithServer(idToken);
                    } else {
                        Platform.runLater(() -> statusLabel.setText("Invalid email or password."));
                    }
                });
    }

    private String parseTokenFromJson(String responseBody) {
        try {
            String key   = "\"idToken\": \"";
            int start    = responseBody.indexOf(key) + key.length();
            int end      = responseBody.indexOf("\"", start);
            System.out.println("LOG: Successfully extracted token!");
            return responseBody.substring(start, end);
        } catch (Exception e) {
            System.out.println("LOG: Extraction failed. Response was: " + responseBody);
            return "";
        }
    }

    private void authenticateWithServer(String idToken) {
        String email = emailField.getText().trim();
        String json  = String.format("{\"email\":\"%s\",\"password\":\"\",\"token\":\"%s\"}", email, idToken);

        System.out.println("DEBUG: Sending to Server -> " + json);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ServerConfig.SERVER_URL + "/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    System.out.println("DEBUG: Server Response Code -> " + response.statusCode());
                    System.out.println("DEBUG: Server Response Body -> " + response.body());
                    Platform.runLater(() -> handleLoginResponse(response));
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> statusLabel.setText("Server connection failed."));
                    return null;
                });
    }

    private void handleLoginResponse(HttpResponse<String> response) {
        loginButton.setDisable(false);

        if (response.statusCode() == 200) {
            statusLabel.setStyle("-fx-text-fill: green;");
            System.out.println("DEBUG: Received Token");
            statusLabel.setText("Login successful!");
            loadDashboard();
        } else if (response.statusCode() == 400 || response.statusCode() == 401) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Invalid email or password.");
        } else {
            statusLabel.setStyle("-fx-text-fill: red;");
            System.out.println("Debug: Google Error" + response.body());
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
