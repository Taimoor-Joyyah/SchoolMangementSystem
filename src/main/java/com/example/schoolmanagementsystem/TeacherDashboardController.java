package com.example.schoolmanagementsystem;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class TeacherDashboardController {

    @FXML
    private PieChart attendance;

    @FXML
    private Label notifications;

    @FXML
    private GridPane timetable;

    public void initialize() {
        try {
            String lastNotification = SQLConnector.executeQueryFunction("select description from memo where `to` = 'S' order by id desc limit 1");
            notifications.setText(lastNotification);
        } catch (RuntimeException ignored) {
        }

        GetData.setTimetable(timetable);
    }
}
