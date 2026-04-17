package client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import shared.util.SceneTransition;
import shared.util.ServerConfig;
import shared.util.SessionManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MakeRequestController {

    @FXML private TextField makeField;
    @FXML private TextField modelField;
    @FXML private TextField yearField;
    @FXML private TextField colorField;
    @FXML private TextField licensePlateField;
    @FXML private Label statusLabel;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    private void handleBack() {
        Stage stage = (Stage) makeField.getScene().getWindow();
        SceneTransition.fadeSwitch(stage, "/fxml/dashboard.fxml", "FSC Valet - Dashboard");
    }

    @FXML
    private void handleSubmit() {
        String make    = makeField.getText().trim();
        String model   = modelField.getText().trim();
        String yearStr = yearField.getText().trim();
        String color   = colorField.getText().trim();
        String plate   = licensePlateField.getText().trim();

        if (make.isEmpty() || model.isEmpty() || yearStr.isEmpty() || color.isEmpty() || plate.isEmpty()) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        int year;
        try {
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            statusLabel.setText("Year must be a number.");
            return;
        }

        statusLabel.setText("");
        submitRequest(make, model, year, color, plate);
    }

    private void submitRequest(String make, String model, int year, String color, String plate) {
        String uid = SessionManager.getUid();

        String json = String.format(
            "{\"ownerId\":\"%s\",\"make\":\"%s\",\"model\":\"%s\",\"year\":%d,\"color\":\"%s\",\"licensePlate\":\"%s\"}",
            uid, make, model, year, color, plate
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ServerConfig.SERVER_URL + "/api/tickets"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> Platform.runLater(() -> {
                    if (response.statusCode() == 200 || response.statusCode() == 201) {
                        Stage stage = (Stage) makeField.getScene().getWindow();
                        SceneTransition.fadeSwitch(stage, "/fxml/confirmation.fxml", "FSC Valet - Confirmation");
                    } else {
                        statusLabel.setText("Request failed. Please try again.");
                    }
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> statusLabel.setText("Could not connect to server."));
                    return null;
                });
    }
}
