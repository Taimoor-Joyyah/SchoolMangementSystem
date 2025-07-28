package com.example.schoolmanagementsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    private Stage primaryStage;

    private Stage messageStage;
    private static App instance;

    public App() {
        instance = this;
    }

    private static int currentUserID = -1;

    private static UserType userType = UserType.ADMIN;

    public static void setUserType(UserType userType) {
        App.userType = userType;
    }

    public static UserType getUserType() {
        return userType;
    }

    public static int getCurrentUserID() {
        return currentUserID;
    }

    public static void setCurrentUserID(int currentUserID) {
        App.currentUserID = currentUserID;
    }

    public static App getInstance() {
        return instance;
    }

    public static Node loadFxml(String fxml) {
        try {
            return FXMLLoader.load(App.class.getResource(fxml));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void gotoLogin() {
        try {
            primaryStage.setResizable(true);
            primaryStage.setTitle("LOGIN");
            gotoScene("login.fxml");
            primaryStage.setResizable(false);
            primaryStage.setX(600);
            primaryStage.setX(300);
        } catch (IOException e) {
            System.out.println("Login view is unable to load!");
        }
    }

    public void openPortal() {
        switch (userType) {
            case STUDENT -> {
                try {
                    gotoScene("student/portal.fxml");
                    primaryStage.setTitle("Student Portal");
                } catch (IOException e) {
                    System.out.println("Student Portal view is unable to load!");
                }
            }
            case TEACHER -> {
                try {
                    gotoScene("teacher/portal.fxml");
                    primaryStage.setTitle("Teacher Portal");
                } catch (IOException e) {
                    System.out.println("Teacher Portal view is unable to load!");
                }
            }
            case ADMIN -> {
                try {
                    gotoScene("admin/portal.fxml");
                    primaryStage.setTitle("Admin Portal");
                } catch (IOException e) {
                    System.out.println("Admin Portal view is unable to load!");
                    throw new RuntimeException(e);
                }
            }
            case PARENT -> {
                try {
                    gotoScene("parent/portal.fxml");
                    primaryStage.setTitle("Parent Portal");
                } catch (IOException e) {
                    System.out.println("Parent Portal view is unable to load!");
                }
            }
        }
        primaryStage.setMaximized(true);
        primaryStage.setResizable(false);
    }

    public void popWarning(String message) {
        try {
            if (messageStage == null) {
                messageStage = new Stage();
                messageStage.initModality(Modality.APPLICATION_MODAL);
            }
            messageStage.setTitle("WARNING");
            WarningController.setMessage(message);
            gotoScene(messageStage, "warning.fxml");
            messageStage.show();
        } catch (IOException e) {
            System.out.println("Warning is unable to load!");
        } catch (IllegalStateException ignored) {
        }
    }

    public void popSuccess(String message) {
        try {
            if (messageStage == null) {
                messageStage = new Stage();
                messageStage.initModality(Modality.APPLICATION_MODAL);
            }
            messageStage.setTitle("SUCCESS");
            SuccessController.setMessage(message);
            gotoScene(messageStage, "success.fxml");
            messageStage.show();
        } catch (IOException e) {
            System.out.println("Success is unable to load!");
        } catch (IllegalStateException ignored) {
        }
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        gotoLogin();
        this.primaryStage.show();
    }

    private Parent gotoScene(Stage stage, String fxml) throws IOException {
        Parent page = FXMLLoader.load(App.class.getResource(fxml), null, new JavaFXBuilderFactory());
        if (stage.getScene() == null)
            stage.setScene(new Scene(page));
        else
            stage.getScene().setRoot(page);
        stage.sizeToScene();
        return page;
    }

    private Parent gotoScene(String fxml) throws IOException {
        return gotoScene(primaryStage, fxml);
    }

    public void closePrimaryStage() {
        primaryStage.close();
    }

    public void closeMessageStage() {
        if (messageStage != null)
            messageStage.close();
    }

    public static void main(String[] args) {
        launch();
    }
}