package com.example.schoolmanagementsystem;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ParentChildSubjectMarksController {
    public static class MarkDetail {
        private String title;
        private int total;
        private int obtained;
        private String date;

        private MarkDetail(String title, int total, int obtained, String date) {
            this.title = title;
            this.total = total;
            this.obtained = obtained;
            this.date = date;
        }

        public String getTitle() {
            return title;
        }

        public int getTotal() {
            return total;
        }

        public int getObtained() {
            return obtained;
        }

        public String getDate() {
            return date;
        }
    }

    @FXML
    private Button back;

    @FXML
    private TableView<MarkDetail> marks_table;

    public void initialize() {
        back.setOnAction(event -> ParentPortalController.getController().setAnchorScene("parent/child_subjects.fxml"));

        marks_table.setItems(FXCollections.observableArrayList(getMarks()));

        marks_table.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("title"));
        marks_table.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("total"));
        marks_table.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("obtained"));
        marks_table.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("date"));
    }

    private List<MarkDetail> getMarks() {
        List<MarkDetail> list = new ArrayList<>();
        try {
            ResultSet result = SQLConnector.executeQuery("call student_marks(" + ParentDashboardController.getSelectedStudent() + "," + ParentDashboardController.getSelectedSubject() + ")");
            while (result.next()) {
                list.add(new MarkDetail(
                        result.getString("title"),
                        result.getInt("total"),
                        result.getInt("obtained"),
                        result.getString("date_taken")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

}
