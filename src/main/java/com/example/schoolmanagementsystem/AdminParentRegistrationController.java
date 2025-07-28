package com.example.schoolmanagementsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AdminParentRegistrationController {

    @FXML
    private TextField cnic;

    @FXML
    private PasswordField confirm_password;

    @FXML
    private TextField email;

    @FXML
    private TextField name;

    @FXML
    private PasswordField password;

    @FXML
    private TextField phone;

    @FXML
    private Button submit;
    @FXML
    private Button back;

    public void initialize() {
        back.setOnAction(event -> AdminPortalController.getController().setAnchorScene("admin/create_account.fxml"));
        submit.setOnAction(this::submit);
    }

    private void submit(ActionEvent event) {
        StringBuilder builder = new StringBuilder();
        if (name.getText().isBlank())
            builder.append("Name is missing.\n");
        else if (name.getText().length() < 4)
            builder.append("Name must be at least 4 character long.");

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
            String result = SQLConnector.executeQueryFunction(String.format("select register_parent('%s', '%s', '%s', '%s', '%s')",
                    name.getText(),
                    password.getText(),
                    cnic.getText(),
                    phone.getText(),
                    email.getText()
            ));
            if (result.equals("0"))
                App.getInstance().popWarning("Parent CNIC already exists in database.");
            else {
                App.getInstance().popSuccess("Parent has been successfully REGISTERED");
                AdminPortalController.getController().setAnchorScene("admin/create_account.fxml");
            }
        }
    }

}
