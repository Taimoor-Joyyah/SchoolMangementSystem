package com.example.schoolmanagementsystem;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

public class TeacherAttendanceCallController {
    public static class StudentRecord {

        private int id;
        private String name;
        private double attendance;

        private BooleanProperty present;

        public StudentRecord(int id, String name, double attendance) {
            this.id = id;
            this.name = name;
            this.attendance = attendance;
            present = new SimpleBooleanProperty();
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public double getAttendance() {
            return attendance;
        }

        public BooleanProperty getPresent() {
            return present;
        }

        public void setPresent(BooleanProperty present) {
            this.present = present;
        }

    }

    public TeacherAttendanceCallController() {
    }

    @FXML
    private TableView<StudentRecord> attendanceTable;
    @FXML
    public ChoiceBox<GetData.SubjectDetail> selectSubject;

    @FXML
    private ChoiceBox<GetData.GradeDetail> selectClass;

    @FXML
    private DatePicker selectDate;

    @FXML
    private Button submit;

    public void initialize() {
        attendanceTable.setEditable(true);
        attendanceTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        attendanceTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        attendanceTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("attendance"));

        var present = new TableColumn<StudentRecord, Boolean>("Present");

        present.setCellFactory(column -> new CheckBoxTableCell<>());
        present.setCellValueFactory(s -> s.getValue().getPresent());

        attendanceTable.getColumns().set(3, present);

        submit.setOnAction(this::submit);
        selectClass.setItems(GetData.getGrades(App.getCurrentUserID()));
        selectClass.setOnAction(event -> {
            selectSubject.setItems(GetData.getSubjects(App.getCurrentUserID(), selectClass.getValue().getId()));
            attendanceTable.getItems().clear();
        });
        selectSubject.setOnAction(event -> attendanceTable.setItems(getStudents(selectSubject.getValue().getId())));
    }

    public ObservableList<StudentRecord> getStudents(int subject_id) {
        ObservableList<StudentRecord> items = FXCollections.observableArrayList();
        try {
            var set = SQLConnector.executeQuery(String.format("call get_students_by_subject(%d)", subject_id));
            while (set.next()) {
                items.add(new StudentRecord(
                        set.getInt("id"),
                        set.getString("name"),
                        Math.round((set.getDouble("present") / set.getDouble("total")) * 100)
                ));
            }
        } catch (SQLException e) {
            App.getInstance().popWarning(e.getSQLState());
        }
        return items;
    }

    private void submit(ActionEvent event) {
        StringBuilder builder = new StringBuilder();
        if (selectDate.getEditor().getText().isBlank())
            builder.append("Date is not selected!\n");
        if (selectClass.getValue() == null)
            builder.append("Class/Grade is not selected!\n");
        if (selectSubject.getValue() == null)
            builder.append("Subject is not selected!\n");
        if (builder.isEmpty()) {
            SQLConnector.execute(String.format("call update_subject_attendance('%d')", selectSubject.getValue().getId()));

            for (StudentRecord item : attendanceTable.getItems()) {
                if (item.getPresent().get())
                    SQLConnector.execute(String.format("call mark_present('%d', '%d')", item.getId(), selectSubject.getValue().getId()));
            }

            App.getInstance().popSuccess("Attendance has been marked for SUCCESSFULLY");
            TeacherPortalController.getController().setAnchorScene("teacher/attendance_call.fxml");
        } else
            App.getInstance().popWarning(builder.toString());
    }
}
