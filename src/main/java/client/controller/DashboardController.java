package client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import shared.util.ButtonEffects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class DashboardController {

    // Nav buttons
    @FXML private Button navDashboardButton;
    @FXML private Button navCheckInButton;
    @FXML private Button navActiveVehiclesButton;

    // Action buttons
    @FXML private Button viewAllButton;
    @FXML private Button checkInFirstButton;

    // Metric cards
    @FXML private Label activeVehiclesLabel;
    @FXML private Label availableSpotsLabel;
    @FXML private Label totalCapacityLabel;
    @FXML private Label todayCheckinsLabel;

    // Parking status
    @FXML private ProgressBar occupancyBar;
    @FXML private Label occupancyPercentLabel;
    @FXML private Label parkedCountLabel;
    @FXML private Label availableCountLabel;

    // Recent check-ins section
    @FXML private VBox emptyState;
    @FXML private VBox vehicleListContainer;

    private PrintWriter out;
    private BufferedReader in;

    private static final int TOTAL_CAPACITY = 50;

    @FXML
    public void initialize() {
        ButtonEffects.applyAll(navDashboardButton);
        ButtonEffects.applyAll(navCheckInButton);
        ButtonEffects.applyAll(navActiveVehiclesButton);
        ButtonEffects.applyAll(viewAllButton);
        ButtonEffects.applyAll(checkInFirstButton);

        connectToServer();
        requestDashboardData();
    }

    private void connectToServer() {
        int attempts = 0;
        while (attempts < 5) {
            try {
                Socket socket = new Socket("localhost", 5555);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println("Dashboard connected to server.");
                return;
            } catch (IOException ex) {
                attempts++;
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            }
        }
        System.out.println("Dashboard could not connect to server.");
    }

    private void requestDashboardData() {
        // Runs on a background thread so UI doesn't freeze
        new Thread(() -> {
            try {
                out.println("GET_DASHBOARD");
                String response = in.readLine();
                // Expected format: DASHBOARD:activeVehicles:availableSpots:todayCheckins
                // Example: DASHBOARD:5:45:8
                if (response != null && response.startsWith("DASHBOARD:")) {
                    String[] parts = response.split(":");
                    if (parts.length >= 4) {
                        int active   = Integer.parseInt(parts[1]);
                        int available = Integer.parseInt(parts[2]);
                        int todayCheckins = Integer.parseInt(parts[3]);
                        // Update UI on JavaFX thread
                        Platform.runLater(() -> updateDashboard(active, available, todayCheckins));
                    }
                }
            } catch (IOException e) {
                System.out.println("Error fetching dashboard data: " + e.getMessage());
            }
        }).start();
    }

    private void updateDashboard(int active, int available, int todayCheckins) {
        int parked = TOTAL_CAPACITY - available;
        double occupancy = (double) parked / TOTAL_CAPACITY;

        activeVehiclesLabel.setText(String.valueOf(active));
        availableSpotsLabel.setText(String.valueOf(available));
        totalCapacityLabel.setText(String.valueOf(TOTAL_CAPACITY));
        todayCheckinsLabel.setText(String.valueOf(todayCheckins));

        occupancyBar.setProgress(occupancy);
        occupancyPercentLabel.setText((int)(occupancy * 100) + "%");
        parkedCountLabel.setText(parked + " Parked");
        availableCountLabel.setText(available + " Available");

        // Toggle empty state vs vehicle list
        if (active > 0) {
            emptyState.setVisible(false);
            emptyState.setManaged(false);
            vehicleListContainer.setVisible(true);
            vehicleListContainer.setManaged(true);
        }
    }

    @FXML
    private void handleCheckIn() {
        System.out.println("Navigate to Check In");
        // TODO: switch scene to check-in screen
    }

    @FXML
    private void handleActiveVehicles() {
        System.out.println("Navigate to Active Vehicles");
        // TODO: switch scene to active vehicles screen
    }

    @FXML
    private void handleViewAll() {
        System.out.println("View all tickets");
        // TODO: switch scene to tickets list
    }
}