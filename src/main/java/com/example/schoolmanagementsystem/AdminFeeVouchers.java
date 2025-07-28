package com.example.schoolmanagementsystem;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminFeeVouchers {
    public static class FeeDetail {
        private String grade;
        private int studentId;
        private String studentName;
        private double feeAmount;
        private String dueDate;
        private double lateCharges;
        private String status;

        public FeeDetail(String grade, int studentId, String studentName, double feeAmount, String dueDate, double lateCharges, String status) {
            this.grade = grade;
            this.studentId = studentId;
            this.studentName = studentName;
            this.feeAmount = feeAmount;
            this.dueDate = dueDate;
            this.lateCharges = lateCharges;
            this.status = status;
        }

        public String getGrade() {
            return grade;
        }

        public int getStudentId() {
            return studentId;
        }

        public String getStudentName() {
            return studentName;
        }

        public double getFeeAmount() {
            return feeAmount;
        }

        public String getDueDate() {
            return dueDate;
        }

        public double getLateCharges() {
            return lateCharges;
        }

        public String getStatus() {
            return status;
        }
    }

    @FXML
    private TableView<FeeDetail> fee_table;

    public void initialize() {
        fee_table.setItems(FXCollections.observableList(getFees()));
        fee_table.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("grade"));
        fee_table.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("studentId"));
        fee_table.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("studentName"));
        fee_table.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("feeAmount"));
        fee_table.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        fee_table.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("lateCharges"));
        fee_table.getColumns().get(6).setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private List<FeeDetail> getFees() {
        List<FeeDetail> feeDetails = new ArrayList<>();

        try {
            ResultSet set = SQLConnector.executeQuery("call admin_fees_detail()");
            while (set.next()) {
                feeDetails.add(new FeeDetail(
                        set.getString("grade"),
                        set.getInt("student_id"),
                        set.getString("student_name"),
                        set.getDouble("fee"),
                        set.getString("due_date"),
                        set.getDouble("late_charges"),
                        set.getString("status")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return feeDetails;
    }

}
