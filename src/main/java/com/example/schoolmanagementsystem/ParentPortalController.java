package com.example.schoolmanagementsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class ParentPortalController {

    @FXML
    private AnchorPane anchor;

    @FXML
    private Button dashboard;

    @FXML
    private Button logout;

    @FXML
    private Label parent_name;

    @FXML
    private ImageView profile_img;

    private static ParentPortalController controller;

    private static String currentParentCnic;

    public static String getCurrentParentCnic() {
        return currentParentCnic;
    }

    public static void setCurrentParentCnic(String currentParentCnic) {
        ParentPortalController.currentParentCnic = currentParentCnic;
    }

    public void initialize() {
        controller = this;
        dashboard.setOnAction(actionEvent -> setAnchorScene("parent/dashboard.fxml"));

        logout.setOnAction(actionEvent -> App.getInstance().gotoLogin());

        setAnchorScene("parent/dashboard.fxml");


        String name = SQLConnector.executeQueryFunction("select name from parent where cnic = " + currentParentCnic);
        parent_name.setText(name);
    }

    public void setAnchorScene(String fxml) {
        anchor.getChildren().clear();
        anchor.getChildren().add(App.loadFxml(fxml));
    }

    public static ParentPortalController getController() {
        return controller;
    }
}
