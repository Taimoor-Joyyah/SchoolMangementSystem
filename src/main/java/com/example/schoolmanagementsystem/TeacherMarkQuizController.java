package com.example.schoolmanagementsystem;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DefaultStringConverter;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TeacherMarkQuizController {
    public static class QuizDetail {
        private int no;
        private String title;
        private int total;

        public QuizDetail(int no, String title, int total) {
            this.no = no;
            this.title = title;
            this.total = total;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    public static class StudentRecord {
        private int id;
        private String name;
        private StringProperty obtained;
        private StringProperty percentage;

        public StudentRecord(int id, String name) {
            this.id = id;
            this.name = name;
            this.obtained = new SimpleStringProperty();
            this.percentage = new SimpleStringProperty();
        }

        public void setPercentage(int total) {
            percentage.set(Math.round((Double.parseDouble(obtained.get()) / (double) total) * 100) + " %");
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public StringProperty getObtained() {
            return obtained;
        }

        public StringProperty getPercentage() {
            return percentage;
        }

        public void setObtained(String obtained) {
            this.obtained.set(obtained);
        }

        public void setPercentage(String percentage) {
            this.percentage.set(percentage);
        }
    }

    @FXML
    private ChoiceBox<GetData.GradeDetail> selectClass;
    @FXML
    public ChoiceBox<GetData.SubjectDetail> selectSubject;
    @FXML
    private ChoiceBox<QuizDetail> selectQuiz;

    @FXML
    private TableView<StudentRecord> studentMarking;

    @FXML
    private Button submit;

    public void initialize() {
        submit.setOnAction(this::submit);
        selectClass.setItems(GetData.getGrades(App.getCurrentUserID()));
        selectClass.setOnAction(event -> {
            selectSubject.setItems(GetData.getSubjects(App.getCurrentUserID(), selectClass.getValue().getId()));
            studentMarking.setEditable(false);
            studentMarking.getItems().clear();
            selectQuiz.getItems().clear();
            studentMarking.getColumns().get(2).setText("Obtained Marks");
        });
        selectSubject.setOnAction(event -> {
            selectQuiz.setItems(getQuizzes(selectSubject.getValue().getId()));
            studentMarking.setItems(getStudents(selectSubject.getValue().getId()));
            studentMarking.setEditable(false);
            studentMarking.getColumns().get(2).setText("Obtained Marks");
        });
        selectQuiz.setOnAction(event -> {
            studentMarking.getColumns().get(2).setText("Obtained Marks (" + selectQuiz.getValue().total + ")");
            getMarks();
            studentMarking.setEditable(true);
        });

        studentMarking.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        studentMarking.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));

        var percentage = new TableColumn<StudentRecord, String>("Percentage");
        percentage.setCellValueFactory(s -> s.getValue().getPercentage());
        percentage.setPrefWidth(125);
        studentMarking.getColumns().set(3, percentage);

        var obtained = new TableColumn<StudentRecord, String>("Obtained Marks");
        obtained.setCellFactory(column -> new TextFieldTableCell<>(new DefaultStringConverter()));
        obtained.setCellValueFactory(s -> s.getValue().getObtained());
        obtained.setPrefWidth(150);
        obtained.setOnEditCommit(event -> {
            StudentRecord record = event.getRowValue();
            String value = event.getNewValue();
            record.setObtained(value);
            if (!value.isEmpty() && ValidationPolicy.isAllDigit(value)) {
                int mark = Integer.parseInt(value);
                if (mark >= 0 && mark <= selectQuiz.getValue().total)
                    record.setPercentage(selectQuiz.getValue().total);
                else
                    record.setPercentage("");
            } else {
                record.setPercentage("");
            }
        });
        studentMarking.getColumns().set(2, obtained);
    }

    private void getMarks() {
        Map<Integer, Integer> obtains = new HashMap<>();
        try {
            var set = SQLConnector.executeQuery(String.format("select student_registration_no as id, obtained_marks as marks from mark where subject_id = %d and quiz_no = %d", selectSubject.getValue().getId(), selectQuiz.getValue().no));
            while (set.next()) {
                obtains.put(set.getInt("id"), set.getInt("marks"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (StudentRecord item : studentMarking.getItems()) {
            int marks = obtains.getOrDefault(item.id, -1);
            if (marks != -1) {
                item.setObtained(Integer.toString(marks));
                item.setPercentage(selectQuiz.getValue().total);
            } else {
                item.setObtained("");
                item.setPercentage("");
            }
        }
    }

    private ObservableList<QuizDetail> getQuizzes(int subject_id) {
        ObservableList<QuizDetail> items = FXCollections.observableArrayList();
        try {
            var set = SQLConnector.executeQuery(String.format("call get_quizzes_by_subject('%d')", subject_id));
            while (set.next()) {
                items.add(new QuizDetail(
                        set.getInt("no"),
                        set.getString("title"),
                        set.getInt("total_marks")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return items;
    }

    private ObservableList<StudentRecord> getStudents(int subject_id) {
        ObservableList<StudentRecord> items = FXCollections.observableArrayList();
        try {
            var set = SQLConnector.executeQuery(String.format("call get_students_by_subject(%d)", subject_id));
            while (set.next()) {
                items.add(new StudentRecord(
                        set.getInt("id"),
                        set.getString("name")
                ));
            }
        } catch (SQLException e) {
            App.getInstance().popWarning(e.getSQLState());
        }
        return items;
    }

    private void submit(ActionEvent event) {
        StringBuilder builder = new StringBuilder();
        if (selectClass.getValue() == null)
            builder.append("Class/Grade is not selected!\n");
        if (selectSubject.getValue() == null)
            builder.append("Subject is not selected!\n");
        if (selectQuiz.getValue() == null)
            builder.append("Quiz is not selected!\n");
        for (StudentRecord item : studentMarking.getItems()) {
            if (item.getObtained().get().isEmpty()) {
                builder.append("Mark every student!\n");
                break;
            }
            try {
                int mark = Integer.parseInt(item.getObtained().get());
                if (mark < 0 || mark > selectQuiz.getValue().total) {
                    builder.append("Marks must be between 0 and Total!\n");
                    break;
                }
            } catch (NumberFormatException e) {
                builder.append("Enter only marks in number!\n");
                break;
            }
        }
        if (!builder.isEmpty()) {
            App.getInstance().popWarning(builder.toString());
        } else {
            for (StudentRecord item : studentMarking.getItems()) {
                SQLConnector.execute(String.format("call upload_mark(%d,%d,%d,%d)", selectSubject.getValue().getId(), item.id, selectQuiz.getValue().no, Integer.parseInt(item.getObtained().get())));
            }
            App.getInstance().popSuccess("Marks has been Submitted");
            TeacherPortalController.getController().setAnchorScene("teacher/mark_quiz.fxml");
        }
    }

}
