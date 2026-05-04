package client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.util.ButtonEffects;
import shared.util.SceneTransition;
import shared.util.ServerConfig;
import shared.util.SessionManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class DashboardController {

    @FXML private Button navCheckInButton;
    @FXML private Button navActiveVehiclesButton;
    @FXML private Button viewAllButton;
    @FXML private Button checkInFirstButton;

    @FXML private Label activeVehiclesLabel;
    @FXML private Label availableSpotsLabel;
    @FXML private Label totalCapacityLabel;
    @FXML private Label todayCheckinsLabel;

    @FXML private ProgressBar occupancyBar;
    @FXML private Label occupancyPercentLabel;
    @FXML private Label parkedCountLabel;
    @FXML private Label availableCountLabel;

    @FXML private Label userNameLabel;
    @FXML private VBox emptyState;
    @FXML private VBox vehicleListContainer;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    public void initialize() {
        userNameLabel.setText("Welcome, " + SessionManager.getFirstName());

        ButtonEffects.applyAll(navCheckInButton);
        ButtonEffects.applyAll(navActiveVehiclesButton);
        ButtonEffects.applyAll(viewAllButton);
        ButtonEffects.applyAll(checkInFirstButton);

        requestDashboardData();
    }

    private void requestDashboardData() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ServerConfig.SERVER_URL + "/api/dashboard"))
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        Platform.runLater(() -> parseDashboardResponse(response.body()));
                    }
                })
                .exceptionally(ex -> {
                    System.out.println("Could not fetch dashboard data: " + ex.getMessage());
                    return null;
                });
    }

    private void parseDashboardResponse(String json) {
        try {
            int active        = extractInt(json, "activeVehicles");
            int available     = extractInt(json, "availableSpots");
            int totalCapacity = extractInt(json, "totalCapacity");
            int todayCheckins = extractInt(json, "todayCheckins");
            List<String[]> recent = extractRecentTickets(json);
            updateDashboard(active, available, totalCapacity, todayCheckins, recent);
        } catch (Exception e) {
            System.out.println("Error parsing dashboard response: " + e.getMessage());
        }
    }

    private int extractInt(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search) + search.length();
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        return Integer.parseInt(json.substring(start, end).trim());
    }

    private String extractString(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) return null;
        start += search.length();
        int end = json.indexOf("\"", start);
        return end == -1 ? null : json.substring(start, end);
    }

    private int findObjectEnd(String json, int startIndex) {
        int depth = 0;
        for (int i = startIndex; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') { depth--; if (depth == 0) return i; }
        }
        return -1;
    }

    // Returns [ticketNumber, customerName, status, parkingSpace] for each recent ticket
    private List<String[]> extractRecentTickets(String json) {
        List<String[]> results = new ArrayList<>();
        String marker = "\"recentTickets\":[";
        int arrayStart = json.indexOf(marker);
        if (arrayStart == -1) return results;
        int i = arrayStart + marker.length();
        while ((i = json.indexOf("{", i)) != -1) {
            int end = findObjectEnd(json, i);
            if (end == -1) break;
            String entry      = json.substring(i, end + 1);
            String number     = extractString(entry, "ticketNumber");
            String customer   = extractString(entry, "customerName");
            String status     = extractString(entry, "status");
            String spot       = extractString(entry, "parkingSpace");
            if (number != null) {
                results.add(new String[]{number, customer, status, spot});
            }
            i = end + 1;
        }
        return results;
    }

    private void updateDashboard(int active, int available, int totalCapacity,
                                 int todayCheckins, List<String[]> recent) {
        int parked = totalCapacity - available;
        double occupancy = totalCapacity > 0 ? (double) parked / totalCapacity : 0;

        activeVehiclesLabel.setText(String.valueOf(active));
        availableSpotsLabel.setText(String.valueOf(available));
        totalCapacityLabel.setText(String.valueOf(totalCapacity));
        todayCheckinsLabel.setText(String.valueOf(todayCheckins));

        occupancyBar.setProgress(occupancy);
        occupancyPercentLabel.setText((int)(occupancy * 100) + "%");
        parkedCountLabel.setText(parked + " Parked");
        availableCountLabel.setText(available + " Available");

        if (!recent.isEmpty()) {
            emptyState.setVisible(false);
            emptyState.setManaged(false);
            vehicleListContainer.getChildren().clear();
            for (String[] ticket : recent) {
                vehicleListContainer.getChildren().add(buildRecentCard(ticket));
            }
            vehicleListContainer.setVisible(true);
            vehicleListContainer.setManaged(true);
        }
    }

    private HBox buildRecentCard(String[] ticket) {
        String number   = ticket[0] != null ? ticket[0] : "—";
        String customer = ticket[1] != null && !ticket[1].isEmpty() ? ticket[1] : "Unknown";
        String status   = ticket[2] != null ? ticket[2] : "—";
        String spot     = ticket[3] != null && !ticket[3].isEmpty() ? ticket[3] : "—";

        Label numLabel      = new Label(number);
        numLabel.getStyleClass().add("recent-card-ticket");

        Label customerLabel = new Label(customer);
        customerLabel.getStyleClass().add("recent-card-detail");

        Label spotLabel     = new Label("Space: " + spot);
        spotLabel.getStyleClass().add("recent-card-detail");

        VBox info = new VBox(4, numLabel, customerLabel, spotLabel);
        info.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(info, javafx.scene.layout.Priority.ALWAYS);

        Label statusLabel = new Label(status);
        statusLabel.getStyleClass().addAll("recent-card-status", statusStyleClass(status));

        HBox card = new HBox(12, info, statusLabel);
        card.setPadding(new Insets(12, 14, 12, 14));
        card.getStyleClass().add("recent-checkin-card");
        card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        return card;
    }

    private String statusStyleClass(String status) {
        if (status == null) return "status-pending";
        return switch (status) {
            case "IN_PROGRESS" -> "status-in-progress";
            case "COMPLETED"   -> "status-completed";
            case "CANCELLED"   -> "status-cancelled";
            default            -> "status-pending";
        };
    }

    @FXML
    private void handleProfile() {
        Stage stage = (Stage) navCheckInButton.getScene().getWindow();
        SceneTransition.fadeSwitch(stage, "/fxml/profile.fxml", "FSC Valet - Profile");
    }

    @FXML
    private void handleCheckIn() {
        Stage stage = (Stage) navCheckInButton.getScene().getWindow();
        SceneTransition.fadeSwitch(stage, "/fxml/makeRequest.fxml", "FSC Valet - Make a Request");
    }

    @FXML
    private void handleActiveVehicles() {
        Stage stage = (Stage) navCheckInButton.getScene().getWindow();
        SceneTransition.fadeSwitch(stage, "/fxml/myRequests.fxml", "FSC Valet - Requests");
    }

    @FXML
    private void handleViewAll() {
        Stage stage = (Stage) navCheckInButton.getScene().getWindow();
        SceneTransition.fadeSwitch(stage, "/fxml/myRequests.fxml", "FSC Valet - Requests");
    }
}
