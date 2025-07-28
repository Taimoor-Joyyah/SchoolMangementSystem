module com.example.schoolmangementsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.schoolmanagementsystem to javafx.fxml;
    exports com.example.schoolmanagementsystem;
}