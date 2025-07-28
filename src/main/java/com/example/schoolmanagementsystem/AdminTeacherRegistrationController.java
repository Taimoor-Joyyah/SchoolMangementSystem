package com.example.schoolmanagementsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.text.ParseException;
import java.time.LocalDate;

public class AdminTeacherRegistrationController {

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
    private TextField last_name;

    @FXML
    private PasswordField password;

    @FXML
    private TextField phone;

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

        if (phone.getText().isBlank())
            builder.append("Phone is missing.\n");
        else if (phone.getText().length() != 11)
            builder.append("Phone must be 11 digit.\n");
        else if (!ValidationPolicy.isAllDigit(phone.getText()))
            builder.append("Phone is not valid.\n");

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

            String username = GetData.teacherUsername(first_name.getText(), last_name.getText());
            String email = GetData.createEmail(username);

            String result = SQLConnector.executeQueryFunction(String.format("select register_teacher('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
                    first_name.getText(),
                    last_name.getText(),
                    password.getText(),
                    dateStr,
                    genderStr,
                    address.getText(),
                    cnic.getText(),
                    phone.getText(),
                    username,
                    email
            ));
            if (result.equals("0"))
                App.getInstance().popWarning("Teacher CNIC already exists in database.");
            else{
                App.getInstance().popSuccess("Teacher has been successfully REGISTERED");
                AdminPortalController.getController().setAnchorScene("admin/create_account.fxml");
            }
        }
    }
}
