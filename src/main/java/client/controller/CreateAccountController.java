package client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import shared.util.ButtonEffects;
import shared.util.SceneTransition;

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

        // Basic validation
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

        //This helps set up the data that is going to be sent to the register api
        String json = String.format("{\"username\":\"%s\",\"password\":\"%s\"}",
                email, password);

        //This helps set up the data that is going to be sent to the register api
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:8080/api/auth/register"))
                .header("Content-Type", "application/json")
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(json))
                .build();

        /*
            Once the data is sent in and done successfully it will redirect the user to the login screen.
            Other-wise it will tell the user the certain error that is occurring.
        */
        java.net.http.HttpClient.newHttpClient()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> javafx.application.Platform.runLater(() -> {
                    if(response.statusCode() == 200)
                    {
                        statusLabel.setStyle("-fx-text-fill: green;");
                        statusLabel.setText("Success! Redirecting...");
                        loadLogin();
                    }
                    else
                    {
                        statusLabel.setStyle("-fx-text-fill: red;");
                        statusLabel.setText("Sever Error: " + response.body());
                    }
                }))
                .exceptionally(ex ->{
                        javafx.application.Platform.runLater(() -> {
                            statusLabel.setText("Could not connect to server.");
                        });

                        return null;
                        });

        /*
            Just for us to talk about. I asked AI why it didn't want me to do this,
                and it said it would give users access to the firebase and delete data from it.
        */
        // TODO COBIN: Firebase — register user with Firebase Authentication
        // FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
        // Then store profile (firstName, lastName) in Firebase Realtime DB or Firestore
    }

    private void loadLogin() {
        Stage stage = (Stage) backToLoginButton.getScene().getWindow();
        SceneTransition.fadeSwitch(stage, "/fxml/login.fxml", "FSCValet - Login");
    }
}
