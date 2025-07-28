package com.example.schoolmanagementsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class GetData {
    public static String schoolEmail = "@school.edu.pk";

    public static String adminEmail(String username) {
        return sqeez(username) + schoolEmail;
    }

    private static String sqeez(String username) {
        StringBuilder builder = new StringBuilder();
        for (char ch : username.toCharArray()) {
            if (Character.isAlphabetic(ch))
                builder.append(Character.toLowerCase(ch));
        }
        return builder.toString();
    }

    public static String toSqlDate(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("MM/dd/yyyy").parse(date));
    }

    public static String studentUsername(String firstName, String lastName) {
        String id = SQLConnector.executeQueryFunction("select next_student_id()");
        return sqeez(firstName) + sqeez(lastName) + id;
    }

    public static String createEmail(String username) {
        return username + schoolEmail;
    }

    public static String teacherUsername(String firstName, String lastName) {
        String id = SQLConnector.executeQueryFunction("select next_teacher_id()");
        return sqeez(firstName) + sqeez(lastName) + id;
    }

    public static void setTimetable(GridPane timetable) {
        String time_table = SQLConnector.executeQueryFunction("select `table` from timetable where id = 1");
        if (!time_table.isEmpty()) {
            String[][] table = new String[10][5];
            String[] list = time_table.split("\n", 5);
            for (int i = 0; i < 5; i++) {
                String time = list[i];
                var grade = time.split(",", 10);
                for (int j = 0; j < 10; j++) {
                    table[j][i] = grade[j];
                }
            }
            ObservableList<Node> children = timetable.getChildren();
            for (Node child : children) {
                if (child instanceof Label) {
                    Label field = (Label) child;
                    String id = field.getId();
                    int grade = Integer.parseInt(String.valueOf(id.charAt(1)));
                    int time = Integer.parseInt(String.valueOf(id.charAt(2)));
                    field.setText(table[grade][time]);
                }
                else if (child instanceof TextField) {
                    TextField field = (TextField) child;
                    String id = field.getId();
                    int grade = Integer.parseInt(String.valueOf(id.charAt(1)));
                    int time = Integer.parseInt(String.valueOf(id.charAt(2)));
                    field.setText(table[grade][time]);
                }
            }
        }
    }

    public static File selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        return fileChooser.showOpenDialog(stage);
    }

    public static ObservableList<SubjectDetail> getSubjects(int teacher_id, int grade_id) {
        ObservableList<SubjectDetail> items = FXCollections.observableArrayList();
        try {
            var set = SQLConnector.executeQuery(String.format("call teacher_subjects_for_grade('%d', '%d')", teacher_id, grade_id));
            while (set.next()) {
                items.add(new SubjectDetail(
                        set.getInt("id"),
                        set.getString("name")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return items;
    }

    public static ObservableList<GradeDetail> getGrades(int teacher_id) {
        ObservableList<GradeDetail> items = FXCollections.observableArrayList();
        try {
            var set = SQLConnector.executeQuery(String.format("call teacher_classes('%d')", teacher_id));
            while (set.next()) {
                items.add(new GradeDetail(
                        set.getInt("id"),
                        set.getString("title")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return items;
    }

    public static class GradeDetail {
        private int id;
        private String title;

        public GradeDetail(int id, String title) {
            this.id = id;
            this.title = title;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public String toString() {
            return title;
        }

    }

    public static class SubjectDetail {
        private int id;
        private String name;

        public SubjectDetail(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
