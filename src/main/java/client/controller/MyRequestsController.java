package client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.util.SceneTransition;
import shared.util.ServerConfig;
import shared.util.TicketEditData;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class MyRequestsController {

    @FXML private VBox emptyState;
    @FXML private VBox ticketList;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    public void initialize() {
        fetchTickets();
    }

    private void fetchTickets() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ServerConfig.SERVER_URL + "/api/tickets"))
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        parseAndDisplay(response.body());
                    }
                }))
                .exceptionally(ex -> {
                    System.out.println("Could not fetch tickets: " + ex.getMessage());
                    return null;
                });
    }

    private void parseAndDisplay(String json) {
        List<String[]> tickets = parseTicketArray(json);

        if (tickets.isEmpty()) return;

        emptyState.setVisible(false);
        emptyState.setManaged(false);
        ticketList.setVisible(true);
        ticketList.setManaged(true);

        for (String[] ticket : tickets) {
            ticketList.getChildren().add(buildTicketRow(ticket));
        }
    }

    // Returns [ticketId, ticketNumber, type, status, customerId, customerName, parkingSpace, notes]
    private List<String[]> parseTicketArray(String json) {
        List<String[]> results = new ArrayList<>();
        int i = 0;
        while ((i = json.indexOf("{", i)) != -1) {
            int end = findObjectEnd(json, i);
            if (end == -1) break;
            String entry      = json.substring(i, end + 1);
            String ticketId   = extractString(entry, "ticketId");
            String number     = extractString(entry, "ticketNumber");
            String type       = extractString(entry, "type");
            String status     = extractString(entry, "status");
            String customerId = extractString(entry, "customerId");
            String customerName = extractString(entry, "customerName");
            String parkingSpace = extractString(entry, "parkingSpace");
            String notes      = extractString(entry, "notes");
            if (number != null) {
                results.add(new String[]{ticketId, number, type, status, customerId, customerName, parkingSpace, notes});
            }
            i = end + 1;
        }
        return results;
    }

    private int findObjectEnd(String json, int startIndex) {
        int depth = 0;
        for (int i = startIndex; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    private String extractString(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) return null;
        start += search.length();
        int end = json.indexOf("\"", start);
        return end == -1 ? null : json.substring(start, end);
    }

    private VBox buildTicketRow(String[] ticket) {
        String ticketId     = ticket[0] != null ? ticket[0] : "";
        String number       = ticket[1] != null ? ticket[1] : "—";
        String type         = ticket[2] != null ? ticket[2] : "—";
        String status       = ticket[3] != null ? ticket[3] : "—";
        String customerId   = ticket[4] != null ? ticket[4] : "—";
        String customerName = ticket[5] != null && !ticket[5].isEmpty() ? ticket[5] : "—";
        String parkingSpace = ticket[6] != null && !ticket[6].isEmpty() ? ticket[6] : "—";
        String notes        = ticket[7] != null && !ticket[7].isEmpty() ? ticket[7] : "";

        Label numLabel    = new Label("Ticket: " + number);
        numLabel.getStyleClass().add("ticket-number");

        Label customerLabel = new Label("Customer: " + customerName + "  (" + customerId + ")");
        customerLabel.getStyleClass().add("ticket-detail");

        Label typeLabel   = new Label("Type: " + type);
        typeLabel.getStyleClass().add("ticket-detail");

        Label statusLabel = new Label("Status: " + status);
        statusLabel.getStyleClass().add("ticket-status");

        Label spotLabel = new Label("Parking Space: " + parkingSpace);
        spotLabel.getStyleClass().add("ticket-detail");

        VBox details = new VBox(6, numLabel, customerLabel, typeLabel, statusLabel, spotLabel);
        if (!notes.isEmpty()) {
            Label notesLabel = new Label("Notes: " + notes);
            notesLabel.getStyleClass().add("ticket-detail");
            details.getChildren().add(notesLabel);
        }

        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().add("edit-btn");

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("delete-btn");

        HBox actions = new HBox(10, editBtn, deleteBtn);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(10, 0, 0, 0));

        VBox row = new VBox(0, details, actions);
        row.setPadding(new Insets(16));
        row.getStyleClass().add("ticket-card");

        editBtn.setOnAction(e -> {
            TicketEditData.set(ticketId, number, type, status, customerId,
                    ticket[5] != null ? ticket[5] : "",
                    ticket[6] != null ? ticket[6] : "",
                    ticket[7] != null ? ticket[7] : "");
            Stage stage = (Stage) editBtn.getScene().getWindow();
            SceneTransition.fadeSwitch(stage, "/fxml/editTicket.fxml", "FSC Valet - Edit Request");
        });

        deleteBtn.setOnAction(e -> deleteTicket(ticketId, row));

        return row;
    }

    private void deleteTicket(String ticketId, VBox row) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ServerConfig.SERVER_URL + "/api/tickets/" + ticketId))
                .DELETE()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        ticketList.getChildren().remove(row);
                        if (ticketList.getChildren().isEmpty()) {
                            ticketList.setVisible(false);
                            ticketList.setManaged(false);
                            emptyState.setVisible(true);
                            emptyState.setManaged(true);
                        }
                    }
                }))
                .exceptionally(ex -> null);
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) emptyState.getScene().getWindow();
        SceneTransition.fadeSwitch(stage, "/fxml/dashboard.fxml", "FSC Valet - Dashboard");
    }

    @FXML
    private void handleMakeRequest() {
        Stage stage = (Stage) emptyState.getScene().getWindow();
        SceneTransition.fadeSwitch(stage, "/fxml/makeRequest.fxml", "FSC Valet - Make a Request");
    }
}
