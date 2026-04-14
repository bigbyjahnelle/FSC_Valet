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
import shared.util.SessionManager;

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
    //New API key for the login
    private final String fireBaseAPIKey = System.getenv("FIREBASE_WEB_API_KEY");

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

        //Checks for the API key in the firebase to make sure it is valid/there on login request
        if(fireBaseAPIKey == null)
        {
            System.err.println("CRITICAL ERROR: FIREBASE_WEB_API_KEY not found in environment variables");
            return;
        }

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
                        String idToken  = parseFromJson(response.body(), "idToken");
                        String uid      = parseFromJson(response.body(), "localId");
                        String fullName = parseFromJson(response.body(), "displayName");
                        SessionManager.setUser(uid, email, fullName);
                        authenticateWithServer(idToken);
                    } else {
                        Platform.runLater(() -> statusLabel.setText("Invalid email or password."));
                    }
                });
    }

    /** Extracts a string value from a flat JSON response by key name. */
    private String parseFromJson(String json, String key) {
        try {
            String search = "\"" + key + "\": \"";
            // Firebase sometimes omits the space, so fall back
            int start = json.indexOf(search);
            if (start == -1) {
                search = "\"" + key + "\":\"";
                start  = json.indexOf(search);
            }
            if (start == -1) return "";
            start += search.length();
            int end = json.indexOf("\"", start);
            return json.substring(start, end);
        } catch (Exception e) {
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
