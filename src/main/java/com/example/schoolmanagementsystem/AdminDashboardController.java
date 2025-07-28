package com.example.schoolmanagementsystem;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;

public class AdminDashboardController {

    @FXML
    private PieChart fees_paid;

    @FXML
    private Label parent_count;

    @FXML
    private Label passed_count;

    @FXML
    private BarChart<String, Double> progress_rate;

    @FXML
    private Label student_count;

    @FXML
    private LineChart<String, Integer> student_enrolled;

    @FXML
    private Label teacher_count;

    public void initialize() {
        parent_count.setText(SQLConnector.executeQueryFunction("select count(*) from parent") + " Parents Registered");
        student_count.setText(SQLConnector.executeQueryFunction("select count(*) from student") + " Students Registered");
        teacher_count.setText(SQLConnector.executeQueryFunction("select count(*) from teacher") + " Teachers Registered");

        double totalFees = 0;
        double paidFees = 0;
        String total = SQLConnector.executeQueryFunction("select sum(amount) from fee");
        if (total != null) {
            totalFees = Double.parseDouble(total);
            String paid = SQLConnector.executeQueryFunction("select sum(amount) from fee where status = 'paid'");
            if (paid != null) {
                paidFees = Double.parseDouble(paid);
            }
        }

        fees_paid.getData().add(new PieChart.Data("Paid", paidFees));
        fees_paid.getData().add(new PieChart.Data("Pending", totalFees - paidFees));
    }

}
