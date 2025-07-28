package com.example.schoolmanagementsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.text.ParseException;
import java.time.LocalDate;

public class AdminStudentRegistrationController {

    @FXML
    private TextField address;

    @FXML
    private Button back;

    @FXML
    private TextField cnic;

    @FXML
    private PasswordField confirm_password;

    @FXML
    private DatePicker dob;

    @FXML
    private TextField first_name;

    @FXML
    private ChoiceBox<String> gender;

    @FXML
    private TextField grade;

    @FXML
    private TextField last_name;

    @FXML
    private TextField parent_cnic;

    @FXML
    private PasswordField password;

    @FXML
    private Button submit;

    public void initialize() {
        gender.getItems().add("Male");
        gender.getItems().add("Female");
        gender.setValue("Male");

        dob.setValue(LocalDate.of(2000,1,1));

        back.setOnAction(event -> AdminPortalController.getController().setAnchorScene("admin/create_account.fxml"));
        submit.setOnAction(this::submit);
    }

    private void submit(ActionEvent event) {
        StringBuilder builder = new StringBuilder();
        if (first_name.getText().isBlank())
            builder.append("First Name is missing.\n");
        else if (first_name.getText().length() < 3)
            builder.append("First Name must be at least 3 character long.");

        if (last_name.getText().isBlank())
            builder.append("Last Name is missing.\n");
        else if (last_name.getText().length() < 3)
            builder.append("Last Name must be at least 3 character long.");


        if (password.getText().isBlank())
            builder.append("Password is missing.\n");
        else {
            if (password.getText().length() < 8)
                builder.append("Password must be at least 8 character long.\n");
            if (!ValidationPolicy.isValidPassword(password.getText()))
                builder.append("Password must contain alphabet, number and special character.\n");
            if (!confirm_password.getText().equals(password.getText()))
                builder.append("Password does not match.\n");
        }

        if (dob.getEditor().getText().isBlank())
            builder.append("Date is missing.\n");

        if (address.getText().isBlank())
            builder.append("Address is missing.\n");

        if (cnic.getText().isBlank())
            builder.append("CNIC is missing.\n");
        else if (cnic.getText().length() != 13)
            builder.append("CNIC must be 13 digit.\n");
        else if (!ValidationPolicy.isAllDigit(cnic.getText()))
            builder.append("CNIC is not valid.\n");

        if (parent_cnic.getText().isBlank())
            builder.append("Parent CNIC is missing.\n");
        else if (parent_cnic.getText().length() != 13)
            builder.append("Parent CNIC must be 13 digit.\n");
        else if (!ValidationPolicy.isAllDigit(parent_cnic.getText()))
            builder.append("Parent CNIC is not valid.\n");
        else if (SQLConnector.executeQueryFunction(String.format("select exists_parent('%s')", parent_cnic.getText())).equals("0"))
            builder.append("Parent is not registered.\n");

        if (grade.getText().isBlank())
            builder.append("Grade is missing.\n");
        else {
            try {
                int gradeInt = Integer.parseInt(grade.getText());
                if (gradeInt < 1 || gradeInt > 10)
                    builder.append("Grade must be between 1 - 10.\n");
            } catch (NumberFormatException e) {
                builder.append("Grade must be integer.\n");
            }
        }

        if (!builder.isEmpty())
            App.getInstance().popWarning(builder.toString());
        else {
            String dateStr = null;
            try {
                dateStr = GetData.toSqlDate(dob.getEditor().getText());
            } catch (ParseException e) {
                builder.append("Date is not Valid.\n");
            }
            String genderStr = gender.getValue().equals("Male") ? "M" : "F";

            String username = GetData.studentUsername(first_name.getText(), last_name.getText());
            String email = GetData.createEmail(username);

            String result = SQLConnector.executeQueryFunction(String.format("select register_student('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
                    first_name.getText(),
                    last_name.getText(),
                    password.getText(),
                    dateStr,
                    genderStr,
                    address.getText(),
                    cnic.getText(),
                    parent_cnic.getText(),
                    username,
                    email,
                    grade.getText()
            ));
            if (result.equals("0"))
                App.getInstance().popWarning("Student CNIC already exists in database.");
            else{
                App.getInstance().popSuccess("Student has been successfully REGISTERED");
                AdminPortalController.getController().setAnchorScene("admin/create_account.fxml");
            }
        }
    }

}
