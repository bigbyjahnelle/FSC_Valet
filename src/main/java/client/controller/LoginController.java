package client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label statusLabel;

    private PrintWriter out;
    private BufferedReader in;

    @FXML
    public void initialize() {
        loginButton.setOnAction(e -> attemptLogin());
        connectToServer();
    }

    //Wait for server connection instead of crashing on startup
    private void connectToServer() {
        int attempts = 0;
        while (attempts < 5) {
            try {
                Socket socket = new Socket("localhost", 5555);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                statusLabel.setText("");
                System.out.println("Connected to server.");
                return;
            } catch (IOException ex) {
                attempts++;
                statusLabel.setText("Connecting to server... (" + attempts + "/5)");
                System.out.println("Connection attempt " + attempts + " failed, retrying...");
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            }
        }
        statusLabel.setText("Cannot connect to server. Is it running?");
        loginButton.setDisable(true);
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter username and password.");
            return;
        }

        try {
            out.println("LOGIN_REQUEST:" + username + ":" + password);
            String response = in.readLine();

            if ("LOGIN_SUCCESS".equals(response)) {
                statusLabel.setStyle("-fx-text-fill: green;");
                statusLabel.setText("Login successful!");
            } else if ("LOGIN_FAIL:INVALID_CREDENTIALS".equals(response)) {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Invalid username or password.");
            } else if ("LOGIN_FAIL:MALFORMED_REQUEST".equals(response)) {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Bad request sent to server.");
            } else {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Unexpected server response.");
            }

        } catch (IOException e) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Lost connection to server.");
        }
    }
}