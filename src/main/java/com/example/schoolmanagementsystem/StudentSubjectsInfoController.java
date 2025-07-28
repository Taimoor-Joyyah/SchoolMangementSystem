package com.example.schoolmanagementsystem;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentSubjectsInfoController {
    public static class SubjectDetail {
        private int id;
        private String name;
        private String teacherName;
        private double attendance;

        public SubjectDetail(int id, String name, String teacherName, double attendance) {
            this.id = id;
            this.name = name;
            this.teacherName = teacherName;
            this.attendance = attendance;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getTeacherName() {
            return teacherName;
        }

        public double getAttendance() {
            return attendance;
        }
    }
    @FXML
    private TableView<SubjectDetail> subjects;


    public void initialize() {
        subjects.setItems(FXCollections.observableArrayList(getSubjects()));

        subjects.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        subjects.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        subjects.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("teacherName"));
        subjects.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("attendance"));

        subjects.setOnKeyPressed(this::enterMarks);
    }

    private List<SubjectDetail> getSubjects() {
        List<SubjectDetail> list = new ArrayList<>();
        try {
            ResultSet result = SQLConnector.executeQuery("call student_subjects(" + App.getCurrentUserID() + ")");
            while (result.next()) {
                list.add(new SubjectDetail(
                        result.getInt("id"),
                        result.getString("subject"),
                        result.getString("teacher"),
                        Math.round((result.getDouble("present") / result.getDouble("total_attendance")) * 100)
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    private void enterMarks(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            SubjectDetail selected = subjects.getSelectionModel().getSelectedItem();
            if (selected != null) {
                StudentSubjectController.setSelectedSubjectId(selected.getId());
                StudentPortalController.getController().setAnchorScene("student/subject.fxml");
            }
        }
    }

}
