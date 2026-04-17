package client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import shared.util.ConfirmationData;
import shared.util.SceneTransition;

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
        goToDashboard();
    }

    @FXML
    private void handleBackToDashboard() {
        goToDashboard();
    }

    private void goToDashboard() {
        ConfirmationData.clear();
        Stage stage = (Stage) doneButton.getScene().getWindow();
        SceneTransition.fadeSwitch(stage, "/fxml/dashboard.fxml", "FSC Valet - Dashboard");
    }
}
