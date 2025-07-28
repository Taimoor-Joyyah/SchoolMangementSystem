package com.example.schoolmanagementsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentProfileController {
    @FXML
    private Label address;

    @FXML
    private Label dob;

    @FXML
    private Label email;

    @FXML
    private Label father_name;

    @FXML
    private Label full_name;

    @FXML
    private Label gender;

    @FXML
    private Label grade;

    @FXML
    private Label registration_no;

    public void initialize() {
        try {
            ResultSet set = SQLConnector.executeQuery("call student_profile(" + App.getCurrentUserID() + ")");
            set.next();
            registration_no.setText(set.getString("student_id"));
            full_name.setText(set.getString("full_name"));
            father_name.setText(set.getString("father_name"));
            dob.setText(set.getString("birth_date"));
            email.setText(set.getString("email"));
            grade.setText(set.getString("grade"));
            gender.setText(set.getString("gender").equals("M") ? "Male" : "Female");
            address.setText(set.getString("address"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
