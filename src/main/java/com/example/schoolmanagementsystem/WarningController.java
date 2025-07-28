package com.example.schoolmanagementsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;

public class WarningController {
    @FXML
    private Label warning;

    @FXML
    private Button ok;

    private static String message;

    @FXML
    public void initialize() {
        warning.setText(message);
        warning.setTextAlignment(TextAlignment.JUSTIFY);
        ok.setOnAction(this::clickOK);
    }

    public static void setMessage(String message) {
        WarningController.message = message;
    }

    private void clickOK(ActionEvent event) {
        App.getInstance().closeMessageStage();
    }
}
