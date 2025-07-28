package com.example.schoolmanagementsystem;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDashboardController {

    @FXML
    private PieChart attendance;

    @FXML
    private Label notifications;

    @FXML
    private GridPane timetable;

    public void initialize() {
        int presents = 0;
        int total = 0;
        try {
            ResultSet result = SQLConnector.executeQuery("call student_subjects(" + App.getCurrentUserID() + ")");
            while (result.next()) {
                presents += result.getInt("present");
                total += result.getInt("total_attendance");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        attendance.getData().add(new PieChart.Data("Presents", presents));
        attendance.getData().add(new PieChart.Data("Absents", total - presents));

        try {
            String lastNotification = SQLConnector.executeQueryFunction("select description from memo where `to` = 'S' order by id desc limit 1");
            notifications.setText(lastNotification);
        } catch (RuntimeException ignored) {
        }

        GetData.setTimetable(timetable);
    }
}
