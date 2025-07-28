package com.example.schoolmanagementsystem;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ParentDashboardController {
    public static class ChildrenDetail {
        private int studentId;
        private String studentName;
        private String grade;
        private String remarks;
        private String feeStatus;

        public ChildrenDetail(int studentId, String studentName, String grade, String remarks, String feeStatus) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.grade = grade;
            this.remarks = remarks;
            this.feeStatus = feeStatus;
        }

        public int getStudentId() {
            return studentId;
        }

        public String getStudentName() {
            return studentName;
        }

        public String getGrade() {
            return grade;
        }

        public String getRemarks() {
            return remarks;
        }

        public String getFeeStatus() {
            return feeStatus;
        }
    }

    @FXML
    private TableView<ChildrenDetail> children_table;

    @FXML
    private Label notifications;

    private static int selectedStudent;
    private static int selectedSubject;

    public static int getSelectedStudent() {
        return selectedStudent;
    }

    public static void setSelectedStudent(int selectedStudent) {
        ParentDashboardController.selectedStudent = selectedStudent;
    }

    public static int getSelectedSubject() {
        return selectedSubject;
    }

    public static void setSelectedSubject(int selectedSubject) {
        ParentDashboardController.selectedSubject = selectedSubject;
    }

    public void initialize() {
        children_table.setItems(FXCollections.observableList(getChildren()));

        children_table.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("studentId"));
        children_table.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("studentName"));
        children_table.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("grade"));
        children_table.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("remarks"));
        children_table.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("feeStatus"));

        children_table.setOnKeyPressed(this::enterChildDetail);

        try {
            String lastNotification = SQLConnector.executeQueryFunction("select description from memo where `to` = 'P' order by id desc limit 1");
            notifications.setText(lastNotification);
        } catch (RuntimeException ignored) {
        }
    }

    private List<ChildrenDetail> getChildren() {
        List<ChildrenDetail> list = new ArrayList<>();

        try {
            ResultSet set = SQLConnector.executeQuery(String.format("call parent_children('%s')", ParentPortalController.getCurrentParentCnic()));
            while (set.next()) {
                list.add(new ChildrenDetail(
                        set.getInt("student_id"),
                        set.getString("student_name"),
                        set.getString("grade"),
                        "???",
                        set.getDouble("fee_status") + " Pending"
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    private void enterChildDetail(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            ChildrenDetail selected = children_table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedStudent = selected.studentId;
                ParentPortalController.getController().setAnchorScene("parent/child_subjects.fxml");
            }
        }
    }
}
