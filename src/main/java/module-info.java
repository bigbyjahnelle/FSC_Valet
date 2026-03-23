module org.example.group_project_csc {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.group_project_csc to javafx.fxml;
    exports org.example.group_project_csc;
}