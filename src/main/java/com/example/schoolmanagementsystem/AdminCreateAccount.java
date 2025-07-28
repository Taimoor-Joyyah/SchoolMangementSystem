package com.example.schoolmanagementsystem;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

public class AdminCreateAccount {
    @FXML
    private Rectangle parent;

    @FXML
    private ImageView parent_icon;

    @FXML
    private Rectangle student;

    @FXML
    private ImageView student_icon;

    @FXML
    private Rectangle teacher;

    @FXML
    private ImageView teacher_icon;

    public void initialize() {
        EventHandler<MouseEvent> parent_reg = event -> {
            if (event.getButton() == MouseButton.PRIMARY)
                AdminPortalController.getController().setAnchorScene("admin/parent_registration.fxml");
        };
        EventHandler<MouseEvent> student_reg = event -> {
            if (event.getButton() == MouseButton.PRIMARY)
                AdminPortalController.getController().setAnchorScene("admin/student_registration.fxml");
        };
        EventHandler<MouseEvent> teacher_reg = event -> {
            if (event.getButton() == MouseButton.PRIMARY)
                AdminPortalController.getController().setAnchorScene("admin/teacher_registration.fxml");
        };

        parent.setOnMouseClicked(parent_reg);
        parent_icon.setOnMouseClicked(parent_reg);
        teacher.setOnMouseClicked(teacher_reg);
        teacher_icon.setOnMouseClicked(teacher_reg);
        student.setOnMouseClicked(student_reg);
        student_icon.setOnMouseClicked(student_reg);
    }


}
