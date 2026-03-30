package client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import shared.util.ButtonEffects;
import shared.util.SceneTransition;

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

        // TODO COBIN: Firebase — register user with Firebase Authentication
        // FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
        // Then store profile (firstName, lastName) in Firebase Realtime DB or Firestore

        statusLabel.setStyle("-fx-text-fill: green;");
        statusLabel.setText("Account created! (Firebase integration pending)");
    }

    private void loadLogin() {
        Stage stage = (Stage) backToLoginButton.getScene().getWindow();
        SceneTransition.fadeSwitch(stage, "/fxml/login.fxml", "FSCValet - Login");
    }
}
