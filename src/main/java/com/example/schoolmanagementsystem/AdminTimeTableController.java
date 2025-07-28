package com.example.schoolmanagementsystem;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class AdminTimeTableController {

    @FXML
    private Button save;

    @FXML
    private GridPane timetable;

    public void initialize() {
        save.setOnAction(this::save);
        GetData.setTimetable(timetable);
    }

    private void save(ActionEvent event) {
        ObservableList<Node> children = timetable.getChildren();
        String[][] table = new String[10][5];
        for (Node child : children) {
            if (child instanceof TextField) {
                TextField field = (TextField) child;
                if (field.getText().isBlank()) {
                    App.getInstance().popWarning("All cells must be filled!");
                    return;
                } else {
                    for (char ch : field.getText().toCharArray()) {
                        if (!Character.isAlphabetic(ch) && ch != ' ') {
                            App.getInstance().popWarning("All cell value must only be alphabetic or space!");
                            return;
                        }
                    }
                }
                String id = field.getId();
                int grade = Integer.parseInt(String.valueOf(id.charAt(1)));
                int time = Integer.parseInt(String.valueOf(id.charAt(2)));
                table[grade][time] = field.getText();
            }
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 5; ++i)
            for (int j = 0; j < 10; ++j)
                builder.append(table[j][i] + ((j != 9) ? "," : "\n"));
        SQLConnector.execute(String.format("update timetable set `table` = '%s' where id = 1", builder));
        App.getInstance().popSuccess("Successfully SAVED timetable");
    }

}
