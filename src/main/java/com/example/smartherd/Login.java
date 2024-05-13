package com.example.smartherd;

import com.example.smartherd.controllers.LoginController;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class Login extends Application {
    double x, y = 0;
    ActionEvent event;

    @Override
    public void start(Stage stage) throws IOException {
        /*FXMLLoader loader = new FXMLLoader(Login.class.getResource("fxml/dashboard.fxml"));
        SceneChanger.changeScene(event,"dashboard",loader);
        Notification notification = new Notification(loader);*/
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("fxml/login.fxml")));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        // Pass the login stage reference to the controller
        LoginController loginController = loader.getController();
        loginController.setPrimaryStage(stage);

        //make windows responsive

        stage.initStyle(StageStyle.UNDECORATED);
        //move around
        root.setOnMousePressed(evt ->{
            x = evt.getSceneX();
            y = evt.getSceneY();
        });

        root.setOnMouseDragged(evt ->{
            stage.setX(evt.getScreenX() -x);
            stage.setY(evt.getScreenY() -y);
        });
        stage.setTitle("Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {launch();}
}