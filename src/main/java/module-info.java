module GROUP_PROJECT_CSC {
    requires javafx.controls;
    requires javafx.fxml;

    opens client to javafx.fxml;
    opens client.controller to javafx.fxml;

    exports client;
    exports client.controller;
    exports server;
    exports protocol;
}