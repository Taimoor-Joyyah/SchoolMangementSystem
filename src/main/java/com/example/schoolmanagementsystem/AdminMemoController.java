package com.example.schoolmanagementsystem;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class AdminMemoController {
    @FXML
    private TextArea description;

    @FXML
    private Button send_to_parents;

    @FXML
    private Button send_to_students;

    @FXML
    private Button send_to_teachers;

    @FXML
    private TextArea title;

    public void initialize() {
        send_to_parents.setOnAction(getAction("P"));
        send_to_teachers.setOnAction(getAction("T"));
        send_to_students.setOnAction(getAction("S"));
    }

    private EventHandler<ActionEvent> getAction(String to) {
        return event -> {
            StringBuilder builder = new StringBuilder();
            if (title.getText().isEmpty())
                builder.append("Title is Empty!\n");
            if (description.getText().isEmpty())
                builder.append("Description is Empty!\n");
            else if (description.getText().length() > 1024)
                builder.append("Description is limited to 1024 characters!\n");
            if (builder.isEmpty()) {
                SQLConnector.execute(String.format("insert into memo(title, description, `to`) value ('%s', '%s', '%s')", title.getText(), description.getText(), to));
                title.clear();
                description.clear();
                App.getInstance().popSuccess("Successfully sent MEMO to " + switch (to) {
                    case "P" -> "Parents";
                    case "T" -> "Teachers";
                    case "S" -> "Students";
                    default -> throw new IllegalStateException("Unexpected value: " + to);
                });
            } else
                App.getInstance().popWarning(builder.toString());
        };
    }
}
