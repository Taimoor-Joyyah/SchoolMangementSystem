package com.example.schoolmanagementsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class AdminPortalController {

    public AnchorPane anchor;
    @FXML
    private Label admin_name;

    @FXML
    private Button create_account;

    @FXML
    private Button dashboard;

    @FXML
    private Button fee_vouchers;

    @FXML
    private Button logout;

    @FXML
    private Button memos;

    @FXML
    private ImageView profile_img;

    @FXML
    private Button timetable;

    private static AdminPortalController controller;

    public static AdminPortalController getController() {
        return controller;
    }

    public void initialize() {
        controller = this;
        dashboard.setOnAction(actionEvent -> setAnchorScene("admin/dashboard.fxml"));
        fee_vouchers.setOnAction(actionEvent -> setAnchorScene("admin/fee_vouchers.fxml"));
        memos.setOnAction(actionEvent -> setAnchorScene("admin/memos.fxml"));
        create_account.setOnAction(actionEvent -> setAnchorScene("admin/create_account.fxml"));
        timetable.setOnAction(actionEvent -> setAnchorScene("admin/timetable.fxml"));

        logout.setOnAction(actionEvent -> App.getInstance().gotoLogin());

        setAnchorScene("admin/dashboard.fxml");

        String name = SQLConnector.executeQueryFunction("select username from admin where id = " + App.getCurrentUserID());

        admin_name.setText(name);
    }

    public void setAnchorScene(String fxml) {
        anchor.getChildren().clear();
        anchor.getChildren().add(App.loadFxml(fxml));
    }

}
