package com.example.schoolmanagementsystem;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentSubjectController {

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

    public static class QuizDetail {
        private int no;
        private String title;
        private int total_marks;
        private String due_date;
        private String document_path;

        public QuizDetail(int no, String title, int total_marks, String due_date, String document_path) {
            this.no = no;
            this.title = title;
            this.total_marks = total_marks;
            this.due_date = due_date;
            this.document_path = document_path;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    @FXML
    private Label document_path;

    @FXML
    private Label due_date;

    @FXML
    private Label total_marks;

    @FXML
    public Text selected_file_name;

    @FXML
    private Button back;

    @FXML
    private Button selectFile;

    @FXML
    private Button upload;

    @FXML
    private TableView<MarkDetail> marks_summary;

    @FXML
    private ChoiceBox<QuizDetail> selectQuiz;


    private static int selectedSubjectId;

    private File selectedFile;

    public static void setSelectedSubjectId(int subject) {
        selectedSubjectId = subject;
    }

    public void initialize() {
        marks_summary.setItems(FXCollections.observableArrayList(getMarks()));

        marks_summary.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("title"));
        marks_summary.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("total"));
        marks_summary.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("obtained"));
        marks_summary.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("date"));

        back.setOnAction(actionEvent -> StudentPortalController.getController().setAnchorScene("student/subjects_info.fxml"));
        upload.setOnAction(this::upload);
        selectFile.setOnAction(event -> {
            selectedFile = GetData.selectFile();
            if (selectedFile != null) {
                selected_file_name.setText(selectedFile.getName());
            }
        });

        try {
            var set = SQLConnector.executeQuery(String.format("call get_quizzes_by_subject('%d')", selectedSubjectId));
            while (set.next()) {
                selectQuiz.getItems().add(new QuizDetail(
                        set.getInt("no"),
                        set.getString("title"),
                        set.getInt("total_marks"),
                        set.getString("date_taken"),
                        set.getString("document_path")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        selectQuiz.setOnAction(event -> {
            due_date.setText(selectQuiz.getValue().due_date);
            total_marks.setText(Integer.toString(selectQuiz.getValue().total_marks));
            document_path.setText(selectQuiz.getValue().document_path);

            String path = SQLConnector.executeQueryFunction(String.format("select answer_path from mark where student_registration_no = %d and subject_id = %d and quiz_no = %d", App.getCurrentUserID(), selectedSubjectId, selectQuiz.getValue().no));
            if (path != null)
                selected_file_name.setText(path);
        });
    }

    private List<MarkDetail> getMarks() {
        List<MarkDetail> list = new ArrayList<>();
        try {
            ResultSet result = SQLConnector.executeQuery("call student_marks(" + App.getCurrentUserID() + "," + selectedSubjectId + ")");
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

    private void upload(ActionEvent event) {
        StringBuilder builder = new StringBuilder();
        if (selectQuiz.getValue() == null)
            builder.append("Quiz is not selected!\n");
        if (selectedFile == null)
            builder.append("File is not selected!\n");
        if (builder.isEmpty()) {
            SQLConnector.execute(String.format("update mark set answer_path = '%s' where student_registration_no = %d and subject_id = %d and quiz_no = %d", selectedFile.getAbsolutePath().replace("\\", "/"), App.getCurrentUserID(), selectedSubjectId, selectQuiz.getValue().no));
            App.getInstance().popSuccess("Quiz Answer has been UPLOADED successfully");
            StudentPortalController.getController().setAnchorScene("student/subject.fxml");
        } else
            App.getInstance().popWarning(builder.toString());
    }
}