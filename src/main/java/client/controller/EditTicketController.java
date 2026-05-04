package client.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import shared.util.SceneTransition;
import shared.util.ServerConfig;
import shared.util.TicketEditData;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class EditTicketController {

    @FXML private Label ticketInfoLabel;
    @FXML private TextField customerNameField;
    @FXML private TextField parkingSpaceField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TextField notesField;
    @FXML private Label saveStatusLabel;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    public void initialize() {
        statusCombo.setItems(FXCollections.observableArrayList(
                "PENDING", "IN_PROGRESS", "COMPLETED", "CANCELLED"));

        ticketInfoLabel.setText(
                "Ticket: " + TicketEditData.getTicketNumber() +
                "   |   Type: " + TicketEditData.getType() +
                "   |   Customer ID: " + TicketEditData.getCustomerId());

        customerNameField.setText(TicketEditData.getCustomerName());
        parkingSpaceField.setText(TicketEditData.getParkingSpace());
        statusCombo.setValue(TicketEditData.getStatus().isEmpty() ? "PENDING" : TicketEditData.getStatus());
        notesField.setText(TicketEditData.getNotes());
    }

    @FXML
    private void handleSave() {
        String customerName = customerNameField.getText().trim();
        String parkingSpace = parkingSpaceField.getText().trim();
        String status       = statusCombo.getValue();
        String notes        = notesField.getText().trim();

        String json = String.format(
                "{\"customerName\":\"%s\",\"parkingSpace\":\"%s\",\"status\":\"%s\",\"notes\":\"%s\"}",
                customerName, parkingSpace, status, notes);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ServerConfig.SERVER_URL + "/api/tickets/" + TicketEditData.getTicketId()))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        TicketEditData.clear();
                        Stage stage = (Stage) saveStatusLabel.getScene().getWindow();
                        SceneTransition.fadeSwitch(stage, "/fxml/myRequests.fxml", "FSC Valet - Requests");
                    } else {
                        saveStatusLabel.setText("Save failed. Please try again.");
                        saveStatusLabel.setStyle("-fx-text-fill: red;");
                    }
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        saveStatusLabel.setText("Could not connect to server.");
                        saveStatusLabel.setStyle("-fx-text-fill: red;");
                    });
                    return null;
                });
    }

    @FXML
    private void handleCancel() {
        TicketEditData.clear();
        Stage stage = (Stage) saveStatusLabel.getScene().getWindow();
        SceneTransition.fadeSwitch(stage, "/fxml/myRequests.fxml", "FSC Valet - Requests");
    }
}
