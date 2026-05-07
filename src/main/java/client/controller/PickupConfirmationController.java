package client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import shared.util.ConfirmationData;
import shared.util.SceneTransition;
import shared.util.SessionManager;

public class PickupConfirmationController {

    @FXML private Label dateLabel;
    @FXML private Label reservationNumberLabel;
    @FXML private Label valetAttendantLabel;
    @FXML private Label ramIdLabel;
    @FXML private Button cancelRequestButton;
    @FXML private Button doneButton;

    @FXML
    public void initialize() {
        dateLabel.setText("Date: " + ConfirmationData.getDate());
        reservationNumberLabel.setText("Ticket: " + ConfirmationData.getTicketNumber());
        valetAttendantLabel.setText("Type: " + ConfirmationData.getType());
        ramIdLabel.setText("Status: PENDING");
    }

    @FXML
    private void handleCancelRequest() {
        goBack();
    }

    @FXML
    private void handleBackToDashboard() {
        goBack();
    }

    // Routes customers back to the customer dashboard, staff back to the staff dashboard
    private void goBack() {
        ConfirmationData.clear();
        Stage stage = (Stage) doneButton.getScene().getWindow();
        if ("CUSTOMER".equals(SessionManager.getRole())) {
            SceneTransition.fadeSwitch(stage, "/fxml/customerDashboard.fxml", "FSC Valet - Customer Dashboard");
        } else {
            SceneTransition.fadeSwitch(stage, "/fxml/dashboard.fxml", "FSC Valet - Dashboard");
        }
    }
}
