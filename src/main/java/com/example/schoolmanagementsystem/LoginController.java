package com.example.schoolmanagementsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class LoginController {
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button login;

    @FXML
    public void initialize() {
        login.setOnAction(this::login);
//        login.setOnAction(this::testAdminLogin);
        password.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER)
                login(null);
        });
    }

    public void testAdminLogin(ActionEvent event) {
        verifyCredentials("root", "root");
        App.getInstance().openPortal();
    }

    public void testStudentLogin(ActionEvent event) {
        verifyCredentials("nomanyahya1", "3QP8MXZBmnT_2");
        App.getInstance().openPortal();
    }

    public void testTeacherLogin(ActionEvent event) {
        verifyCredentials("mansoorsheikh1", "5sYwLL-Mj$@wu-Ay");
        App.getInstance().openPortal();
    }

    public void testParentLogin(ActionEvent event) {
        verifyCredentials("Imran Ashraf", "hrMRIgvhQW7z");
        App.getInstance().openPortal();
    }



    private void login(ActionEvent actionEvent) {
        StringBuilder builder = new StringBuilder();
        if (username.getText().isBlank())
            builder.append("Username is missing.\n");
        if (password.getText().isBlank())
            builder.append("Password is missing.\n");

        if (!builder.isEmpty())
            App.getInstance().popWarning(builder.toString());
        else if (verifyCredentials(username.getText(), password.getText()))
            App.getInstance().openPortal();
        else
            App.getInstance().popWarning("Credentials does not exists!");
    }

    private static boolean verifyCredentials(String username, String password) {
        int admin_id = Integer.parseInt(SQLConnector.executeQueryFunction(String.format("select login_admin('%s', '%s')", username, password)));
        if (admin_id > -1) {
            App.setUserType(UserType.ADMIN);
            App.setCurrentUserID(admin_id);
            return true;
        }
        int student_id = Integer.parseInt(SQLConnector.executeQueryFunction(String.format("select login_student('%s', '%s')", username, password)));
        if (student_id > -1) {
            App.setUserType(UserType.STUDENT);
            App.setCurrentUserID(student_id);
            return true;
        }
        int teacher_id = Integer.parseInt(SQLConnector.executeQueryFunction(String.format("select login_teacher('%s', '%s')", username, password)));
        if (teacher_id > -1) {
            App.setUserType(UserType.TEACHER);
            App.setCurrentUserID(teacher_id);
            return true;
        }
        String cnic = SQLConnector.executeQueryFunction(String.format("select login_parent('%s', '%s')", username, password));
        if (!cnic.equals("xxxxxxxxxxxxx")) {
            App.setUserType(UserType.PARENT);
            ParentPortalController.setCurrentParentCnic(cnic);
            return true;
        }
        return false;
    }
}