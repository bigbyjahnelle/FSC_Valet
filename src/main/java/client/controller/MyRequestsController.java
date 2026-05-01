package client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.util.SceneTransition;
import shared.util.ServerConfig;
import shared.util.SessionManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class MyRequestsController {

    @FXML private VBox emptyState;
    @FXML private ScrollPane listScroll;
    @FXML private VBox ticketList;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    public void initialize() {
        fetchTickets();
    }

    private void fetchTickets() {
        String uid = SessionManager.getUid();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ServerConfig.SERVER_URL + "/api/tickets/customer/" + uid + "/details"))
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
        listScroll.setVisible(true);
        listScroll.setManaged(true);

        for (String[] ticket : tickets) {
            ticketList.getChildren().add(buildTicketRow(ticket));
        }
    }

    // Returns [ticketNumber, type, status] for each ticket entry
    private List<String[]> parseTicketArray(String json) {
        List<String[]> results = new ArrayList<>();
        int i = 0;
        while ((i = json.indexOf("{", i)) != -1) {
            int end = json.indexOf("}", i);
            if (end == -1) break;
            String entry = json.substring(i, end + 1);
            String number = extractString(entry, "ticketNumber");
            String type   = extractString(entry, "type");
            String status = extractString(entry, "status");
            if (number != null) results.add(new String[]{number, type, status});
            i = end + 1;
        }
        return results;
    }

    private String extractString(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) return null;
        start += search.length();
        int end = json.indexOf("\"", start);
        return end == -1 ? null : json.substring(start, end);
    }

    private VBox buildTicketRow(String[] ticket) { //Jahnelle adjusted
        String number = ticket[0] != null ? ticket[0] : "—";
        String type   = ticket[1] != null ? ticket[1] : "—";
        String status = ticket[2] != null ? ticket[2] : "—";

        Label numLabel = new Label("Ticket: " + number);
        numLabel.getStyleClass().add("ticket-number");

        Label typeLabel = new Label("Type: " + type);
        typeLabel.getStyleClass().add("ticket-detail");

        Label statusLabel = new Label("Status: " + status);
        statusLabel.getStyleClass().add("ticket-status");

        VBox row = new VBox(6, numLabel, typeLabel, statusLabel);
        row.setPadding(new Insets(16));
        row.getStyleClass().add("ticket-card");

        return row;
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) ticketList.getScene().getWindow();
        SceneTransition.fadeSwitch(stage, "/fxml/dashboard.fxml", "FSC Valet - Dashboard");
    }

    @FXML
    private void handleMakeRequest() {
        Stage stage = (Stage) emptyState.getScene().getWindow();
        SceneTransition.fadeSwitch(stage, "/fxml/makeRequest.fxml", "FSC Valet - Make a Request");
    }
}
