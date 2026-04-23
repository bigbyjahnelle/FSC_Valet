package client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.FlowPane;
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

public class DashboardController {

    @FXML private Button navCheckInButton;
    @FXML private Button navActiveVehiclesButton;
    @FXML private Button viewAllButton;
    @FXML private Button checkInFirstButton;
    @FXML private Button themeToggleButton;

    private boolean darkMode = false;

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
    @FXML private FlowPane vehicleListContainer;

    private static final int TOTAL_CAPACITY = 50;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    public void initialize() {
        userNameLabel.setText("Hello, " + SessionManager.getFirstName());

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
        // Simple JSON parsing without a library
        // Response: {"activeVehicles":0,"availableSpots":50,"totalCapacity":50,"todayCheckins":0}
        try {
            int active        = extractInt(json, "activeVehicles");
            int available     = extractInt(json, "availableSpots");
            int totalCapacity = extractInt(json, "totalCapacity");
            int todayCheckins = extractInt(json, "todayCheckins");
            updateDashboard(active, available, totalCapacity, todayCheckins);
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

    private void updateDashboard(int active, int available, int totalCapacity, int todayCheckins) {
        int parked = totalCapacity - available;
        double occupancy = (double) parked / totalCapacity;

        activeVehiclesLabel.setText(String.valueOf(active));
        availableSpotsLabel.setText(String.valueOf(available));
        totalCapacityLabel.setText(String.valueOf(totalCapacity));
        todayCheckinsLabel.setText(String.valueOf(todayCheckins));

        occupancyBar.setProgress(occupancy);
        occupancyPercentLabel.setText((int)(occupancy * 100) + "%");
        parkedCountLabel.setText(parked + " Parked");
        availableCountLabel.setText(available + " Available");

        if (active > 0) {
            emptyState.setVisible(false);
            emptyState.setManaged(false);
            vehicleListContainer.setVisible(true);
            vehicleListContainer.setManaged(true);
        }
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
        SceneTransition.fadeSwitch(stage, "/fxml/myRequests.fxml", "FSC Valet - My Requests");
    }

    @FXML
    private void handleThemeToggle() {
        darkMode = !darkMode;
        themeToggleButton.setText(darkMode ? "☀" : "☾");
    }

    @FXML
    private void handleViewAll() {
        Stage stage = (Stage) navCheckInButton.getScene().getWindow();
        SceneTransition.fadeSwitch(stage, "/fxml/myRequests.fxml", "FSC Valet - My Requests");
    }
}
