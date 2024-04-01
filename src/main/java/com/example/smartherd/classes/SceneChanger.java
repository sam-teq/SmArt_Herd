package com.example.smartherd.classes;

import com.example.smartherd.controllers.LoginController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class SceneChanger {
    static double x, y = 0;

    public static void changeScene (ActionEvent event, String Stagetype, FXMLLoader loader){
        Stage stage = new Stage();
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();

        if(Stagetype.equals("login")){
            Scene scene = new Scene(root);
            // Pass the login stage reference to the controller
            LoginController loginController = loader.getController();
            loginController.setPrimaryStage(stage);
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
            stage.setScene(scene);
            stage.show();
        }else if(Stagetype.equals("dashboard")){
            stage.setTitle("Dashboard");
            stage.setResizable(true);
            //move around
            root.setOnMousePressed(evt ->{
                x = evt.getSceneX();
                y = evt.getSceneY();
            });

            root.setOnMouseDragged(evt ->{
                stage.setX(evt.getScreenX() -x);
                stage.setY(evt.getScreenY() -y);
            });

            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();
        }


    }
}


