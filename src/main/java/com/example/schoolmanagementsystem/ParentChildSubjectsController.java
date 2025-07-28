package com.example.schoolmanagementsystem;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ParentChildSubjectsController {
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

    private class VoucherDetail {
        private int serial_no;
        private double amount;
        private String due_date;
        private double late_charges;

        public VoucherDetail(int serial_no, double amount, String due_date, double late_charges) {
            this.serial_no = serial_no;
            this.amount = amount;
            this.due_date = due_date;
            this.late_charges = late_charges;
        }

        @Override
        public String toString() {
            return Integer.toString(serial_no);
        }
    }

    @FXML
    private Button back;

    @FXML
    private Button selectFile;

    @FXML
    private Text selectFileName;

    @FXML
    private TableView<SubjectDetail> subject_table;

    @FXML
    private Button upload;

    @FXML
    private Label due_date;

    @FXML
    private Label late_charges;

    @FXML
    private Label amount;

    @FXML
    private ChoiceBox<VoucherDetail> selectVoucher;
    private File selectedFile;

    public void initialize() {
        back.setOnAction(event -> ParentPortalController.getController().setAnchorScene("parent/dashboard.fxml"));
        upload.setOnAction(this::upload);

        subject_table.setItems(FXCollections.observableArrayList(getSubjects()));

        subject_table.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        subject_table.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        subject_table.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("teacherName"));
        subject_table.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("attendance"));

        subject_table.setOnKeyPressed(this::enterMarks);

        selectFile.setOnAction(event -> {
            selectedFile = GetData.selectFile();
            if (selectedFile != null) {
                selectFileName.setText(selectedFile.getName());
            }
        });

        try {
            var set = SQLConnector.executeQuery(String.format("select serial_no, amount, due_date, late_charges from fee where student_registration_no = %d and status = 'pending'", ParentDashboardController.getSelectedStudent()));
            while (set.next()) {
                selectVoucher.getItems().add(new VoucherDetail(
                        set.getInt("serial_no"),
                        set.getDouble("amount"),
                        set.getString("due_date"),
                        set.getDouble("late_charges")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        selectVoucher.setOnAction(event -> {
            due_date.setText(selectVoucher.getValue().due_date);
            amount.setText(Double.toString(selectVoucher.getValue().amount));
            late_charges.setText(Double.toString(selectVoucher.getValue().late_charges));
        });
    }

    private void upload(ActionEvent event) {
        StringBuilder builder = new StringBuilder();
        if (selectVoucher.getValue() == null)
            builder.append("Voucher is not selected!\n");
        if (selectedFile == null)
            builder.append("File is not selected!\n");
        if (builder.isEmpty()) {
            SQLConnector.execute(String.format("update fee set status = 'processing' where student_registration_no = %d and serial_no = %d", ParentDashboardController.getSelectedStudent(), selectVoucher.getValue().serial_no));
            App.getInstance().popSuccess("Fee Voucher has been successfully UPLOADED");
            ParentPortalController.getController().setAnchorScene("parent/child_subjects.fxml");
        } else
            App.getInstance().popWarning(builder.toString());
    }

    private List<SubjectDetail> getSubjects() {
        List<SubjectDetail> list = new ArrayList<>();
        try {
            ResultSet result = SQLConnector.executeQuery("call student_subjects(" + ParentDashboardController.getSelectedStudent() + ")");
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
            SubjectDetail selected = subject_table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                ParentDashboardController.setSelectedSubject(selected.getId());
                ParentPortalController.getController().setAnchorScene("parent/subject_marks.fxml");
            }
        }
    }
}
