package com.example.schoolmanagementsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class StudentPortalController {

    @FXML
    private AnchorPane anchor;

    @FXML
    private Button dashboard;
    @FXML
    private Button profile;

    @FXML
    private Button logout;

    @FXML
    private ImageView profile_img;

    @FXML
    private Label student_name;

    @FXML
    private Button subjects;

    private static StudentPortalController controller;

    public void initialize() {
        controller = this;

        dashboard.setOnAction(actionEvent -> setAnchorScene("student/dashboard.fxml"));
        subjects.setOnAction(actionEvent -> setAnchorScene("student/subjects_info.fxml"));
        profile.setOnAction(actionEvent -> setAnchorScene("student/profile.fxml"));

        logout.setOnAction(actionEvent -> App.getInstance().gotoLogin());

        setAnchorScene("student/dashboard.fxml");


        String name = SQLConnector.executeQueryFunction("select username from student where registration_no = " + App.getCurrentUserID());
        student_name.setText(name);
    }

    public void setAnchorScene(String fxml) {
        anchor.getChildren().clear();
        anchor.getChildren().add(App.loadFxml(fxml));
    }

    public static StudentPortalController getController() {
        return controller;
    }
}
