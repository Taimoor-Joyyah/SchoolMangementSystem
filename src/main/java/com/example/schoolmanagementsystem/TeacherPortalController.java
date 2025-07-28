package com.example.schoolmanagementsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class TeacherPortalController {

    @FXML
    private AnchorPane anchor;

    @FXML
    private Button attendance;

    @FXML
    private Button dashboard;

    @FXML
    private Button logout;

    @FXML
    private Button marks;

    @FXML
    private ImageView profile_img;

    @FXML
    private Button quizzes;

    @FXML
    private Label teacher_name;

    private static TeacherPortalController controller;

    public void initialize() {
        controller = this;
        dashboard.setOnAction(actionEvent -> setAnchorScene("teacher/dashboard.fxml"));
        attendance.setOnAction(actionEvent -> setAnchorScene("teacher/attendance_call.fxml"));
        marks.setOnAction(actionEvent -> setAnchorScene("teacher/mark_quiz.fxml"));
        quizzes.setOnAction(actionEvent -> setAnchorScene("teacher/upload_quiz.fxml"));

        logout.setOnAction(actionEvent -> App.getInstance().gotoLogin());

        setAnchorScene("teacher/dashboard.fxml");

        String name = SQLConnector.executeQueryFunction("select username from teacher where teacher_id = " + App.getCurrentUserID());
        teacher_name.setText(name);
    }

    public void setAnchorScene(String fxml) {
        anchor.getChildren().clear();
        anchor.getChildren().add(App.loadFxml(fxml));
    }

    public static TeacherPortalController getController() {
        return controller;
    }
}
