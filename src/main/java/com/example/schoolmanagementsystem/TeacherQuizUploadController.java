package com.example.schoolmanagementsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.File;
import java.text.ParseException;

public class TeacherQuizUploadController {

    @FXML
    private ChoiceBox<GetData.GradeDetail> selectClass;
    @FXML
    public ChoiceBox<GetData.SubjectDetail> selectSubject;

    @FXML
    private DatePicker selectDate;

    @FXML
    private Button selectFile;

    @FXML
    private Text selected_file;

    @FXML
    private Button upload;
    @FXML
    private TextField title;

    @FXML
    private TextField total;
    private File selectedFile;

    public void initialize() {
        upload.setOnAction(this::upload);
        selectFile.setOnAction(event -> {
            selectedFile = GetData.selectFile();
            if (selectedFile != null) {
                selected_file.setText(selectedFile.getName());
            }
        });

        selectClass.setItems(GetData.getGrades(App.getCurrentUserID()));
        selectClass.setOnAction(event -> selectSubject.setItems(GetData.getSubjects(App.getCurrentUserID(), selectClass.getValue().getId())));
    }

    private void upload(ActionEvent event) {
        StringBuilder builder = new StringBuilder();
        if (selectDate.getEditor().getText().isBlank())
            builder.append("Date is not selected!\n");
        if (selectClass.getValue() == null)
            builder.append("Class/Grade is not selected!\n");
        if (selectSubject.getValue() == null)
            builder.append("Subject is not selected!\n");
        if (selectedFile == null)
            builder.append("File is not selected!\n");
        if (title.getText().isEmpty())
            builder.append("Title is not filled!\n");
        if (total.getText().isEmpty())
            builder.append("Total Marks is not entered!\n");
        else if (!ValidationPolicy.isAllDigit(total.getText()))
            builder.append("Total Marks must be number!\n");
        else if (Integer.parseInt(total.getText()) < 1)
            builder.append("Total Marks must be greater than 0\n");
        String dateStr = null;
        try {
            dateStr = GetData.toSqlDate(selectDate.getEditor().getText());
        } catch (ParseException e) {
            builder.append("Date is not Valid.\n");
        }
        if (!builder.isEmpty()) {
            App.getInstance().popWarning(builder.toString());
        }
        else {
            SQLConnector.execute(String.format("call upload_quiz(%d, '%s', %s, '%s', '%s')", selectSubject.getValue().getId(), title.getText(), total.getText(), dateStr, selectedFile.getAbsolutePath().replace("\\", "/")));
            App.getInstance().popSuccess("Quiz has been UPLOADED");
            TeacherPortalController.getController().setAnchorScene("teacher/upload_quiz.fxml");
        }
    }
}
