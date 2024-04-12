package com.example.smartherd.controllers;

import com.example.smartherd.Login;
import com.example.smartherd.classes.CodeTimer;
import com.example.smartherd.classes.ListData;
import com.example.smartherd.classes.SceneChanger;
import com.example.smartherd.classes.dashboard.Notification;
import com.example.smartherd.classes.database.DatabaseConnection;
import com.example.smartherd.classes.mailer.Gmailer;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private AnchorPane AnchorPane, Login_Ap, Recovery_Ap1, Recovery_Ap2, Recovery_Ap3;

    @FXML
    private Button Login_Bt, Recovery1_Email_Submit_Bt, Recovery1_Back_Bt, Recovery2_Back_Bt, Recovery2_Code_Submit_Bt;

    @FXML
    private ComboBox<String> Combo_box, Recovery_combo_box;

    @FXML
    private TextField Username_Tf, Recovery1_Email_Tf, Recovery2_Code_Tf;

    @FXML
    private PasswordField Password_Pf;

    @FXML
    private Hyperlink Forgot_Hl, Recovery3_Login_Hl;

    @FXML
    private Text login_txt, Recovery1_login_txt, Recovery2_login_txt, Recovery3_Username_T, Recovery3_Password_T;

    @FXML
    private FontAwesomeIconView Minimize_icon;

    //stage
    private Stage primaryStage;

    //getting username and password from database
    private String usernameResult, passwordResult, userEmail, selectedValue;

    // initializing Codetimer class
    private final CodeTimer codeTimer =new CodeTimer();
    Gmailer gmailer = new Gmailer();

    //Database connections
    DatabaseConnection dbc = new DatabaseConnection();
    private Connection connect;
    private PreparedStatement statement;
    private ResultSet Result;

    private String code;

    public LoginController() throws GeneralSecurityException, IOException {
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public String getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = String.valueOf(code);
    }

    //Exit and Minimize icon functionality
    @FXML
    void mouseClicked(MouseEvent event) {
        if(event.getSource().equals(Minimize_icon))
        {
            Stage stage = (Stage) Minimize_icon.getScene().getWindow();
            stage.setIconified(true);
        }else{
            javafx.application.Platform.exit();
        }
    }

    //method switching, login_anchorpanes and Recovery_anchorpanes
    public void switchForm(ActionEvent event){

        System.out.println("hyperlink clicked");
    if(event.getSource().equals(Forgot_Hl) || event.getSource().equals(Recovery2_Back_Bt)){
        Login_Ap.setVisible(false);
        Recovery_Ap1.setVisible(true);
        Recovery_Ap2.setVisible(false);
        Recovery_Ap3.setVisible(false);
        }else if(event.getSource().equals(Recovery3_Login_Hl)|| event.getSource().equals(Recovery1_Back_Bt)){
            Login_Ap.setVisible(true);
            Recovery_Ap1.setVisible(false);
            Recovery_Ap2.setVisible(false);
            Recovery_Ap3.setVisible(false);
            }
    }

    // method putting data in combo-box role
    public void roleList (){
        List<String> ListR = new ArrayList<>();
        // Collections work like for loop, its adding all data from class ListData into combo-box
        Collections.addAll(ListR, ListData.role);
        // Creating an ObservableList named ListData and initializing it with an ObservableArrayList
        // The ObservableArrayList is populated with the elements from the existing ListR
        ObservableList<String> ListData = FXCollections.observableArrayList(ListR);
        Combo_box.setItems(ListData);
        Recovery_combo_box.setItems(ListData);
    }

    @FXML
    public void onClickLoginButton(ActionEvent event) {
        // checking password and username is entered and login in
                    if (!Username_Tf.getText().isBlank() && !Password_Pf.getText().isBlank()) {
                        validateUser(event);
                    } else {
                        login_txt.setText("blank Username or Password");
                    }
    }

    //Method connecting to database checking entered username and password
    int role,userId;
    String firstName, lastName;
    public void validateUser(ActionEvent event){
        try {
           String sqlQuery = "SELECT * FROM user WHERE username = ? and password = ?";
           Result = dbc.getResult(sqlQuery,Username_Tf.getText(),Password_Pf.getText());
            while(Result.next()){
                if(Username_Tf.getText().equals(Result.getString("username"))){
                    role = Result.getInt("role");
                    firstName = Result.getString("first_name");
                    lastName = Result.getString("last_name");
                    userId = Result.getInt("id");
                    String userName = (firstName+" "+lastName);
                    System.out.println("the name is :"+userName+" role is :"+ role);

                    FXMLLoader loader = new FXMLLoader(Login.class.getResource("fxml/dashboard.fxml"));
                    SceneChanger.changeScene(event,"dashboard", loader);
                    DashboardController dashboardController = loader.getController();
                    Notification notification = new Notification(loader);
                    dashboardController.setLoggedInUser(userName, role, userId);


                    /*// Access FXML elements
                    Text label = (Text) loader.getNamespace().get("Logged_In_User");
                    Text label1 = (Text) loader.getNamespace().get("Logged_Role");

                    // Manipulate FXML elements
                    label.setText("SAMSON NYONI");
                    label1.setText("ADMIN");*/
                    //Notification notification = new Notification(loader);




                    // Close the login scene
                    this.primaryStage.close();
                }else{
                    login_txt.setText("invalid username or password");}
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    //when submit button in anchorpane recovery1 is pressed
    @FXML
    public void onEmailSubmit(ActionEvent event){
        // checking password and username is entered and login in
                if (!Recovery1_Email_Tf.getText().isBlank()) {
                    Recovery(event);
                } else {
                    Recovery1_login_txt.setText("blank the email field");
                }
    }
    public void Recovery(ActionEvent event){
        userEmail = Recovery1_Email_Tf.getText();
        try {
            String sqlQuery ="SELECT * FROM user WHERE email = ?";
            Result = dbc.getResult(sqlQuery,userEmail);
            if(Result.next()){
                try {
                    codeTimer.codeTimer(this);
                    try {
                        Thread.sleep(900);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sendRecoveryCode();
                } catch (GeneralSecurityException | IOException e) {
                    throw new RuntimeException(e);
                }
                Login_Ap.setVisible(false);
                Recovery_Ap1.setVisible(false);
                Recovery_Ap2.setVisible(true);
                Recovery_Ap3.setVisible(false);
            }else{
                Recovery1_login_txt.setText("Invalid Email");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendRecoveryCode() throws GeneralSecurityException, IOException {
        String fname = "Sm-Art Herd", lname ="User";
        gmailer.setRecipientEmail(userEmail);
        try {
            String sqlQuery = "SELECT first_name, last_name FROM user WHERE email = ?";
            Result = dbc.getResult(sqlQuery,userEmail);
            while (Result.next()) {
                fname = Result.getString("first_name");
                lname = Result.getString("last_name");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String message = "Dear "+fname+" "+ lname+".\n\n"
                + "To recover your Sm-Art Herd account, use the following one-time code within the next 5 minutes:\n\n"
                + "Verification Code: " + code + "\n\n"
                + "If you didn't request this, ignore this message. For assistance, contact us at:\nEmail: samtech.nm02@gmail.com \nPhone: +2690 968-161-486.\n\n"
                + "Thank you,\nSm-Art Herd Team";



       try {

            gmailer.sendMail("Sm-Art Herd Account Recovery", message);
            /*Path[] attachments = {Paths.get("F:\\PROGRAMMING\\INTELLIJ PROJ\\Sm-Art Herd\\src\\main\\resources\\com\\example\\smartherd\\pdf\\samtech.pdf"),
                    Paths.get("F:\\PROGRAMMING\\INTELLIJ PROJ\\Sm-Art Herd\\src\\main\\resources\\com\\example\\smartherd\\pdf\\samtech.rtf")};
            gmailer.sendMail("Sm-Art Herd Account Recovery", message,attachments);*/
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    public void onCodeSubmit(ActionEvent event){
        String RecoveryCode = Recovery2_Code_Tf.getText();
        if (!RecoveryCode.isBlank()){
            if(RecoveryCode.equals(code)){
                codeTimer.codeTimer(this);
                try {
                    String sqlQuery = "SELECT password, username FROM user WHERE email = ?";
                        // Process the results
                    Result = dbc.getResult(sqlQuery,Recovery1_Email_Tf.getText());
                        while (Result.next()) {
                            usernameResult = Result.getString("username");
                            passwordResult = Result.getString("password");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                      }

                //setting username and password to recovery3 texts
                Recovery3_Username_T.setText(usernameResult);
                Recovery3_Password_T.setText(passwordResult);

                //making recovery 3 visible
                Login_Ap.setVisible(false);
                Recovery_Ap1.setVisible(false);
                Recovery_Ap2.setVisible(false);
                Recovery_Ap3.setVisible(true);

            }else{ Recovery2_login_txt.setText("incorrect code entered");}
        }else{ Recovery2_login_txt.setText("field black, please enter recovery code sent to your email");}
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        roleList();
       // Recovery2_Code_Submit_Bt
    }
}