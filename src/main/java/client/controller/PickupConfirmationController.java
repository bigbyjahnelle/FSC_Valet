package client.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class PickupConfirmationController {

    @FXML
    private Label dateLabel;

    @FXML
    private Label reservationNumberLabel;

    @FXML
    private Label valetAttendantLabel;

    @FXML
    private Label ramIdLabel;

    @FXML
    private Button cancelRequestButton;

    @FXML
    private Button doneButton;

    @FXML
    public void initialize() {
        dateLabel.setText("Date: May 2, 2026");
        reservationNumberLabel.setText("Reservation Number: 2340");
        valetAttendantLabel.setText("Valet Attendant: Jimmy Dean");
        ramIdLabel.setText("RAM ID: R002");
    }

    @FXML
    private void handleCancelRequest() {
        System.out.println("Cancel request clicked");

        // For now, just send user back to dashboard
        goToDashboard();
    }

    @FXML
    private void handleBackToDashboard() {
        goToDashboard();
    }

    private void goToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) doneButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setConfirmationDetails(String date, String reservationNumber,
                                       String valetAttendant, String ramId) {
        dateLabel.setText("Date: " + date);
        reservationNumberLabel.setText("Reservation Number: " + reservationNumber);
        valetAttendantLabel.setText("Valet Attendant: " + valetAttendant);
        ramIdLabel.setText("RAM ID: " + ramId);
    }
}