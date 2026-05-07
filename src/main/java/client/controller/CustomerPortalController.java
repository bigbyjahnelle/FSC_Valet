package client.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import shared.util.ConfirmationData;
import shared.util.SceneTransition;
import shared.util.ServerConfig;
import shared.util.SessionManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class CustomerPortalController {

    @FXML private BorderPane rootPane;
    @FXML private Label welcomeLabel;

    // Section containers for show/hide navigation
    @FXML private VBox checkInSection;
    @FXML private VBox myTicketsSection;
    @FXML private VBox requestPickupSection;

    // Sidebar nav buttons
    @FXML private Button sideCheckInBtn;
    @FXML private Button sideTicketsBtn;
    @FXML private Button sidePickupBtn;

    // Top nav pill buttons
    @FXML private Button navCheckInBtn;
    @FXML private Button navTicketsBtn;
    @FXML private Button navPickupBtn;

    // My Tickets tab
    @FXML private VBox ticketsEmptyState;
    @FXML private VBox ticketListContainer;

    // Request Pickup tab
    @FXML private ComboBox<CarItem> carSelectCombo;
    @FXML private Label pickupStatusLabel;

    // Check In tab
    @FXML private Label availableSpotsLabel;
    @FXML private TextField ciMakeField;
    @FXML private TextField ciModelField;
    @FXML private TextField ciYearField;
    @FXML private TextField ciColorField;
    @FXML private TextField ciPlateField;
    @FXML private TextField ciParkingSpaceField;
    @FXML private Label checkInStatusLabel;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome, " + SessionManager.getFirstName());
        fetchAvailableSpots();
    }

    // ── AVAILABLE SPOTS ────────────────────────────────────────────────────────

    private void fetchAvailableSpots() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ServerConfig.SERVER_URL + "/api/dashboard"))
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        String spots = extractInt(response.body(), "availableSpots");
                        if (spots != null) {
                            availableSpotsLabel.setText(
                                    "Please provide your vehicle details. Available spots: " + spots
                            );
                        }
                    }
                }))
                .exceptionally(ex -> {
                    System.out.println("Could not fetch available spots: " + ex.getMessage());
                    return null;
                });
    }

    // ── MY TICKETS TAB ─────────────────────────────────────────────────────────

    private void fetchMyTickets() {
        String uid = SessionManager.getUid();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ServerConfig.SERVER_URL + "/api/tickets/customer/" + uid + "/details"))
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> Platform.runLater(() -> {
                    // Debugs for if there is something wrong with getting ticket information
                    System.out.println("MY TICKETS - Status: " + response.statusCode());
                    System.out.println("MY TICKETS - Body: " + response.body());
                    if (response.statusCode() == 200) {
                        displayTickets(response.body());
                    }
                }))
                .exceptionally(ex -> {
                    System.out.println("Could not fetch tickets: " + ex.getMessage());
                    return null;
                });
    }

    private void displayTickets(String json) {
        List<String[]> tickets = parseTicketDetails(json);

        ticketListContainer.getChildren().clear();

        if (tickets.isEmpty()) {
            ticketsEmptyState.setVisible(true);
            ticketsEmptyState.setManaged(true);
            ticketListContainer.setVisible(false);
            ticketListContainer.setManaged(false);
            return;
        }

        ticketsEmptyState.setVisible(false);
        ticketsEmptyState.setManaged(false);
        ticketListContainer.setVisible(true);
        ticketListContainer.setManaged(true);

        for (String[] t : tickets) {
            ticketListContainer.getChildren().add(buildTicketCard(t));
        }
    }

    // Returns [ticketNumber, type, status, make, model, year, color, licensePlate]
    private List<String[]> parseTicketDetails(String json) {
        List<String[]> results = new ArrayList<>();
        int i = 0;
        while ((i = json.indexOf("{", i)) != -1) {
            int end = findObjectEnd(json, i);
            if (end == -1) break;
            String obj    = json.substring(i, end + 1);
            String number = extractString(obj, "ticketNumber");
            String type   = extractString(obj, "type");
            String status = extractString(obj, "status");
            String make   = extractString(obj, "make");
            String model  = extractString(obj, "model");
            String year   = extractInt(obj, "year");
            String color  = extractString(obj, "color");
            String plate  = extractString(obj, "licensePlate");
            if (number != null) {
                results.add(new String[]{number, type, status, make, model, year, color, plate});
            }
            i = end + 1;
        }
        return results;
    }

    private VBox buildTicketCard(String[] t) {
        String number = orDash(t[0]);
        String type   = orDash(t[1]);
        String status = orDash(t[2]);
        String make   = orEmpty(t[3]);
        String model  = orEmpty(t[4]);
        String year   = orEmpty(t[5]);
        String color  = orEmpty(t[6]);
        String plate  = orEmpty(t[7]);

        Label numLabel = new Label("Ticket: " + number);
        numLabel.getStyleClass().add("ticket-number");

        Label typeLabel = new Label("Type: " + type);
        typeLabel.getStyleClass().add("ticket-detail");

        Label statusBadge = new Label(status);
        statusBadge.getStyleClass().addAll("ticket-status-badge", statusStyle(status));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox topRow = new HBox(10, numLabel, spacer, statusBadge);
        topRow.setAlignment(Pos.CENTER_LEFT);

        String carText = (make + " " + model).trim();
        if (!year.isEmpty()) carText += " (" + year + ")";
        if (!color.isEmpty()) carText += " — " + color;
        if (!plate.isEmpty()) carText += " | " + plate;
        Label carLabel = new Label("Vehicle: " + carText);
        carLabel.getStyleClass().add("ticket-detail");

        VBox card = new VBox(8, topRow, typeLabel, carLabel);
        card.setPadding(new Insets(16));
        card.getStyleClass().add("ticket-card");
        return card;
    }

    private String statusStyle(String status) {
        if (status == null) return "status-pending";
        return switch (status) {
            case "IN_PROGRESS" -> "status-in-progress";
            case "COMPLETED"   -> "status-completed";
            case "CANCELLED"   -> "status-cancelled";
            default            -> "status-pending";
        };
    }

    // ── REQUEST PICKUP TAB ─────────────────────────────────────────────────────

    private void fetchCarsForPickup() {
        String uid = SessionManager.getUid();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ServerConfig.SERVER_URL + "/api/cars/" + uid))
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> Platform.runLater(() -> {
                    if (response.statusCode() == 200) {
                        List<CarItem> cars = parseCars(response.body());
                        carSelectCombo.setItems(FXCollections.observableArrayList(cars));
                        if (!cars.isEmpty()) carSelectCombo.getSelectionModel().selectFirst();
                    }
                }))
                .exceptionally(ex -> {
                    System.out.println("Could not fetch cars: " + ex.getMessage());
                    return null;
                });
    }

    private List<CarItem> parseCars(String json) {
        List<CarItem> cars = new ArrayList<>();
        int i = 0;
        while ((i = json.indexOf("{", i)) != -1) {
            int end = findObjectEnd(json, i);
            if (end == -1) break;
            String obj   = json.substring(i, end + 1);
            String carId = extractString(obj, "carId");
            String make  = extractString(obj, "make");
            String model = extractString(obj, "model");
            String color = extractString(obj, "color");
            String plate = extractString(obj, "licensePlate");
            if (carId != null) cars.add(new CarItem(carId, make, model, color, plate));
            i = end + 1;
        }
        return cars;
    }

    @FXML
    private void handleRequestPickup() {
        CarItem selected = carSelectCombo.getSelectionModel().getSelectedItem();
        if (selected == null) {
            pickupStatusLabel.setText("Please select a vehicle.");
            return;
        }
        pickupStatusLabel.setText("");
        submitRetrieveTicket(selected.carId);
    }

    private void submitRetrieveTicket(String carId) {
        String uid          = SessionManager.getUid();
        String customerName = SessionManager.getFullName();
        String ticketJson   = String.format(
                "{\"customerId\":\"%s\",\"carId\":\"%s\",\"type\":\"RETRIEVE\",\"customerName\":\"%s\"}",
                uid, carId, customerName
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ServerConfig.SERVER_URL + "/api/tickets"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(ticketJson))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> Platform.runLater(() -> {
                    if (response.statusCode() == 200 || response.statusCode() == 201) {
                        String ticketNumber = extractString(response.body(), "ticketNumber");
                        String type         = extractString(response.body(), "type");
                        ConfirmationData.set(
                                ticketNumber != null ? ticketNumber : "—",
                                type != null ? type : "RETRIEVE",
                                java.time.LocalDate.now().toString()
                        );
                        Stage stage = (Stage) rootPane.getScene().getWindow();
                        SceneTransition.fadeSwitch(stage, "/fxml/confirmation.fxml", "FSC Valet - Confirmation");
                    } else {
                        pickupStatusLabel.setText("Request failed. Please try again.");
                    }
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> pickupStatusLabel.setText("Could not connect to server."));
                    return null;
                });
    }

    // ── CHECK IN TAB ───────────────────────────────────────────────────────────

    @FXML
    private void handleCheckIn() {
        String make    = ciMakeField.getText().trim();
        String model   = ciModelField.getText().trim();
        String yearStr = ciYearField.getText().trim();
        String color   = ciColorField.getText().trim();
        String plate   = ciPlateField.getText().trim();

        if (make.isEmpty() || model.isEmpty() || yearStr.isEmpty() || color.isEmpty() || plate.isEmpty()) {
            checkInStatusLabel.setText("Please fill in all fields.");
            return;
        }

        int year;
        try {
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            checkInStatusLabel.setText("Year must be a number.");
            return;
        }

        checkInStatusLabel.setText("");
        registerCarAndCheckIn(make, model, year, color, plate, ciParkingSpaceField.getText().trim());
    }

    private void registerCarAndCheckIn(String make, String model, int year,
                                       String color, String plate, String space) {
        String uid    = SessionManager.getUid();
        String carJson = String.format(
                "{\"ownerId\":\"%s\",\"make\":\"%s\",\"model\":\"%s\",\"year\":%d,\"color\":\"%s\",\"licensePlate\":\"%s\"}",
                uid, make, model, year, color, plate
        );

        HttpRequest carRequest = HttpRequest.newBuilder()
                .uri(URI.create(ServerConfig.SERVER_URL + "/api/cars"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(carJson))
                .build();

        httpClient.sendAsync(carRequest, HttpResponse.BodyHandlers.ofString())
                .thenAccept(carResponse -> Platform.runLater(() -> {
                    if (carResponse.statusCode() == 200 || carResponse.statusCode() == 201) {
                        String carId = extractString(carResponse.body(), "carId");
                        submitParkTicket(uid, carId, space);
                    } else {
                        checkInStatusLabel.setText("Failed to register car. Try again.");
                    }
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> checkInStatusLabel.setText("Could not connect to server."));
                    return null;
                });
    }

    private void submitParkTicket(String uid, String carId, String space) {
        String customerName = SessionManager.getFullName();
        String ticketJson = String.format(
                "{\"customerId\":\"%s\",\"carId\":\"%s\",\"type\":\"PARK\",\"customerName\":\"%s\",\"parkingSpace\":\"%s\"}",
                uid, carId, customerName, space
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ServerConfig.SERVER_URL + "/api/tickets"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(ticketJson))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> Platform.runLater(() -> {
                    if (response.statusCode() == 200 || response.statusCode() == 201) {
                        String ticketNumber = extractString(response.body(), "ticketNumber");
                        String type         = extractString(response.body(), "type");
                        ConfirmationData.set(
                                ticketNumber != null ? ticketNumber : "—",
                                type != null ? type : "PARK",
                                java.time.LocalDate.now().toString()
                        );
                        Stage stage = (Stage) rootPane.getScene().getWindow();
                        SceneTransition.fadeSwitch(stage, "/fxml/confirmation.fxml", "FSC Valet - Confirmation");
                    } else {
                        checkInStatusLabel.setText("Request failed. Try again.");
                    }
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> checkInStatusLabel.setText("Could not connect to server."));
                    return null;
                });
    }

    // ── NAVIGATION ─────────────────────────────────────────────────────────────

    private void setActiveTab(Button activeSideBtn, Button activeNavBtn) {
        sideCheckInBtn.getStyleClass().remove("sidebar-button-active");
        sideTicketsBtn.getStyleClass().remove("sidebar-button-active");
        sidePickupBtn.getStyleClass().remove("sidebar-button-active");
        navCheckInBtn.getStyleClass().remove("nav-pill-active");
        navTicketsBtn.getStyleClass().remove("nav-pill-active");
        navPickupBtn.getStyleClass().remove("nav-pill-active");

        activeSideBtn.getStyleClass().add("sidebar-button-active");
        activeNavBtn.getStyleClass().add("nav-pill-active");
    }

    @FXML
    private void showCheckIn() {
        setActiveTab(sideCheckInBtn, navCheckInBtn);
        checkInSection.setVisible(true);        checkInSection.setManaged(true);
        myTicketsSection.setVisible(false);     myTicketsSection.setManaged(false);
        requestPickupSection.setVisible(false); requestPickupSection.setManaged(false);
    }

    @FXML
    private void showMyTickets() {
        setActiveTab(sideTicketsBtn, navTicketsBtn);
        checkInSection.setVisible(false);       checkInSection.setManaged(false);
        myTicketsSection.setVisible(true);      myTicketsSection.setManaged(true);
        requestPickupSection.setVisible(false); requestPickupSection.setManaged(false);
        fetchMyTickets();
    }

    @FXML
    private void showRequestPickup() {
        setActiveTab(sidePickupBtn, navPickupBtn);
        checkInSection.setVisible(false);       checkInSection.setManaged(false);
        myTicketsSection.setVisible(false);     myTicketsSection.setManaged(false);
        requestPickupSection.setVisible(true);  requestPickupSection.setManaged(true);
        fetchCarsForPickup();
    }

    @FXML
    private void handleProfile() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        SceneTransition.fadeSwitch(stage, "/fxml/profile.fxml", "FSC Valet - Profile");
    }

    @FXML
    private void handleLogout() {
        SessionManager.clear();
        Stage stage = (Stage) rootPane.getScene().getWindow();
        SceneTransition.fadeSwitch(stage, "/fxml/login.fxml", "FSC Valet - Login");
    }

    // ── JSON HELPERS ────────────────────────────────────────────────────────────

    private String extractString(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) return null;
        start += search.length();
        int end = json.indexOf("\"", start);
        return end == -1 ? null : json.substring(start, end);
    }

    // Gets an int value from the JSON string
    private String extractInt(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return null;
        start += search.length();
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) end++;
        return end > start ? json.substring(start, end) : null;
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

    private String orDash(String s)  { return (s != null && !s.isEmpty()) ? s : "—"; }
    private String orEmpty(String s) { return s != null ? s : ""; }

    // ── CAR ITEM ────────────────────────────────────────────────────────────────

    // Holds car data for the pickup ComboBox dropdown
    private static class CarItem {
        final String carId, make, model, color, plate;

        CarItem(String carId, String make, String model, String color, String plate) {
            this.carId = carId;
            this.make  = make  != null ? make  : "";
            this.model = model != null ? model : "";
            this.color = color != null ? color : "";
            this.plate = plate != null ? plate : "";
        }

        @Override
        public String toString() {
            return make + " " + model + " — " + color + " | " + plate;
        }
    }
}