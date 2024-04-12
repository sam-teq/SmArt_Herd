package com.example.smartherd.controllers;

import com.example.smartherd.Login;
import com.example.smartherd.classes.ListData;
import com.example.smartherd.classes.SceneChanger;
import com.example.smartherd.classes.dashboard.*;
import com.example.smartherd.classes.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.lang.reflect.Field;
import java.net.URL;
import java.sql.Date;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;



public class DashboardController implements Initializable {

    FXMLLoader loader;

    public void setLoader(FXMLLoader loader) {
        this.loader = loader;
    }

    public FXMLLoader getLoader() {
        return loader;
    }
    /*---------------------------------------------------------------- START DASHBOARD-----------------------------------------------------------------------------------------------*/

    @FXML
    private Button Logout_BT, Dashboard_BT, Milk_Records_BT, Animal_Records_BT, Animal_Health_BT, Breeding_BT, Animal_Weights_BT, Sales_BT, Administration_BT;

    @FXML
    private AnchorPane Dashboard_AP, Sales_AP, Farm_Finance_AP, Stockfeed_AP, Administration_AP;

    @FXML
    private ImageView logo1;

    @FXML
    private Label Dashboard_Label;

    @FXML
    public Text Logged_In_User, Logged_Role;

    @FXML
    private GridPane Milk_Records_GP, Animal_Records_GP, Animal_Health_GP, Breeding_GP, Animal_Weight_GP;

    Map<Button, Pair<String, Pane>> buttonMap = null;
    Map<Button,String> ButtonCBMap = null;
    private ArrayList<TextField> TextFieldCBMap = new ArrayList<>();

    //set logged in user
    public void setLoggedInUser(String name, int role, int userId){
        Logged_In_User.setText(name.toUpperCase());
        if(role == 0){
            Logged_Role.setText("Admin");
            Administration_BT.setVisible(true);
            logo1.setVisible(false);
        }else{
            Logged_Role.setText("Worker");
            Administration_BT.setVisible(false);
            logo1.setVisible(true);
        }
        userPrivilege(userId);


    }

    @FXML
    void onClickButtonScene(ActionEvent event) {
        if (buttonMap == null) {
            buttonMap = new HashMap<>();
            // Associate each button with its corresponding label text and panel
            buttonMap.put(Dashboard_BT, new Pair<>("Dashboard", Dashboard_AP));
            buttonMap.put(Animal_Records_BT, new Pair<>("Animal Records", Animal_Records_GP));
            buttonMap.put(Milk_Records_BT, new Pair<>("Milk Records", Milk_Records_GP));
            buttonMap.put(Animal_Health_BT, new Pair<>("Animal Health", Animal_Health_GP));
            buttonMap.put(Breeding_BT, new Pair<>("Breeding", Breeding_GP));
            buttonMap.put(Animal_Weights_BT, new Pair<>("Animal Weights", Animal_Weight_GP));
            buttonMap.put(Sales_BT, new Pair<>("Sales", Sales_AP));
            buttonMap.put(Administration_BT, new Pair<>("Administration", Administration_AP));

            System.out.println("map initialized");
        }
        // Iterate through the map and set visibility based on the clicked button
        for (Map.Entry<Button, Pair<String, Pane>> entry : buttonMap.entrySet()) {
            Button button = entry.getKey();
            Pair<String, Pane> pair = entry.getValue();

            if (event.getSource().equals(button)) {
                Dashboard_Label.setText(pair.getKey());
                pair.getValue().setVisible(true);
            } else {
                pair.getValue().setVisible(false);
            }
        }

    }

    // LOGOUT
    @FXML
    void onClickLogOut(ActionEvent event) {
        Stage stage = (Stage) (Logout_BT.getScene().getWindow());
        FXMLLoader loader = new FXMLLoader(Login.class.getResource("fxml/login.fxml"));
        SceneChanger.changeScene(event, "login", loader);
        stage.close();
    }

    //ALL MILK PRODUCTION BARCHART
    @FXML
    private CategoryAxis DxAxis = new CategoryAxis();
    @FXML
    private NumberAxis DyAxis = new NumberAxis();
    @FXML
    private BarChart<String, Number> D_MilkGraph_BC = new BarChart<>(DxAxis, DyAxis);

    //FIVE DAYS MILK PRODUCTION BARCHART
    @FXML
    private CategoryAxis DMxAxis = new CategoryAxis();
    @FXML
    private NumberAxis DMyAxis = new NumberAxis();
    @FXML
    private BarChart<String, Number> D_DailyMilkGraph_BC = new BarChart<>(DMxAxis, DMyAxis);


    //milk barchart
    public void barchartDisplay(){
        // Data
        XYChart.Series<String, Number>[] series = new XYChart.Series[12]; // 12 months
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

        ObservableList<MilkRecords> MilkData = FXCollections.observableArrayList();
        DatabaseConnection db = new DatabaseConnection();
        try {
            MilkData = db.getAllMilkRecords();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        int[] years = new int[MilkData.size()];
        int[] monthTemp = new int[MilkData.size()];
        int count = 0; // Declare count variable outside the loop

        for (MilkRecords M : MilkData) {
            years[count] = M.getMilkingDate().getYear() + 1900;
            count++;
        }
        // Extract distinct years
        years = Arrays.stream(years).distinct()
                .boxed() // Convert int to Integer
                .sorted(Comparator.reverseOrder()) // Sort in descending order
                .limit(3) // Limit to only the first three elements
                .mapToInt(Integer::intValue) // Convert Integer back to int
                .toArray();

        double[][] productionData = new double[3][12];
        
        years = Arrays.stream(years).sorted().toArray();
        for (int i =0; i < years.length; i++){
        for (MilkRecords M : MilkData) {
            String MilkingDate = M.getMilkingDate().toString();
                if(MilkingDate.startsWith(String.valueOf(years[i]))){
                    for(int j = 0; j < 12;j++){
                        if(M.getMilkingDate().getMonth()==j) {
                            productionData[i][j] = productionData[i][j] + (M.getMorningSession()+M.getAfternoonSession()+M.getEveningSession());
                        }
                    }
                    
                }
            count++;
        }}

        // Add data to the series
        for (int i = 0; i < 12; i++) {
            series[i] = new XYChart.Series<>();
            series[i].setName(months[i]);
            //for (int j = 0; j < years.length; j++)
            for (int j = 0; j < years.length; j++) {
                series[i].getData().add(new XYChart.Data<>(String.valueOf(years[j]), productionData[j][i]));
            }
        }

        // Chart
        DxAxis.setLabel("Year");
        DyAxis.setLabel("Production (liters)");

        for (XYChart.Series<String, Number> s : series) {
            D_MilkGraph_BC.getData().add(s);
        }
        // Present Five Days Production

        double[] fiveDaysMilkProduction = new double[7];
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        for(int i =0; i<7;i++){
            for(MilkRecords M : MilkData){
                Date dateOfBirth = (Date) M.getMilkingDate();
                LocalDate localMilkingDate = dateOfBirth.toLocalDate();
                if(localMilkingDate.isAfter(sevenDaysAgo) && localMilkingDate.isBefore(LocalDate.now().plusDays(1))){
                    if(localMilkingDate.equals(sevenDaysAgo.plusDays((i+1)))){
                        double totalMilkProduction = M.getMorningSession() + M.getAfternoonSession() + M.getEveningSession();
                        fiveDaysMilkProduction[i]= fiveDaysMilkProduction[i] + totalMilkProduction;
                        System.out.println(sevenDaysAgo+" COW ID: "+M.getAnimalId()+" "+ localMilkingDate + " Total Production: "+ totalMilkProduction);
                    }
                }
            }
        }

        DMxAxis.setLabel("Day of the Week");

        DMyAxis.setLabel("Production (Liters)");

        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        series2.setName("Production");

        LocalDate today = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            LocalDate day = today.minusDays(7 - (i+1));
            DayOfWeek dayOfWeek = day.getDayOfWeek();
            String dayOfWeekString = dayOfWeek.toString();
            System.out.println(fiveDaysMilkProduction[i]);
            series2.getData().add(new XYChart.Data<>(dayOfWeekString, fiveDaysMilkProduction[i]));
        }

        D_DailyMilkGraph_BC.getData().add(series2);

    }



    @FXML
    private PieChart D_Cattle_PC;
    @FXML
    private Label D_Cow_Label,D_Heifer_Label,D_Bull_Label,D_BullCalf_Label;
    private ObservableList<PieChart.Data> pieChartData;
    public void setPieChartData(){
        // Sample list of cattle types
        DatabaseConnection db = new DatabaseConnection();
        try {
            int cowSize =  db.getAllRecords("COW").size();
            D_Cow_Label.setText(cowSize + " "+(cowSize <= 1 ? "COW" : "COWS")+ " AVAILABLE");
            int heiferSize =  db.getAllRecords("HEIFER").size();
            D_Heifer_Label.setText(heiferSize + " "+(heiferSize <= 1 ? "HEIFER" : "HEIFERS")+ " AVAILABLE");
            int bullSize =  db.getAllRecords("BULL").size();
            D_Bull_Label.setText(bullSize + " "+(bullSize <= 1 ? "BULL" : "BULLS")+ " AVAILABLE");
            int bullCalfSize =  db.getAllRecords("BULL CALF").size();
            D_BullCalf_Label.setText(bullCalfSize + " "+(bullCalfSize <= 1 ? "BULL CALF" : "BULL CALFS")+ " AVAILABLE");

            pieChartData = FXCollections.observableArrayList(
                    new PieChart.Data("COW",cowSize),
                    new PieChart.Data("HEIFER",heiferSize ),
                    new PieChart.Data("BULL", bullSize),
                    new PieChart.Data("BULL CALF", bullCalfSize));

            double total = getTotalCount(pieChartData);

            for (PieChart.Data data : pieChartData) {
                double percentage = (data.getPieValue() / total) * 100;
                data.setName(String.format("%s (%.1f%%)", data.getName(), percentage));
            }
            D_Cattle_PC.getData().addAll(pieChartData);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            db.closeResources();
        }

    }

    private double getTotalCount(ObservableList<PieChart.Data> pieChartData) {
        double totalCount = 0;
        for (PieChart.Data data : pieChartData) {
            totalCount += data.getPieValue();
        }
        return totalCount;
    }


//calculating the cows cycle
    public void CowLifeCycleCalculator (){
            // Example: Calculate the milestones for a calf born on January 1, 2022
            LocalDate birthDate = LocalDate.of(2022, 1, 1);

            // Calculate weaning date (6 months after birth)
            LocalDate weaningDate = birthDate.plusMonths(6);

            // Calculate breeding date (15 months after birth)
            LocalDate breedingDate = birthDate.plusMonths(15);

            // Calculate pregnancy testing date (2 months after insemination)
            LocalDate inseminationDate = breedingDate; // Assuming insemination happens on the same day as breeding
            LocalDate pregnancyTestingDate = inseminationDate.plusMonths(2);

            // Calculate milking start date (305 days after breeding)
            LocalDate milkingStartDate = breedingDate.plusDays(305);

            // Calculate dry period start date (60 days after milking end)
            LocalDate dryPeriodStartDate = milkingStartDate.plusDays(305).plusDays(60);

            // Print the calculated dates
            System.out.println("Weaning Date: " + weaningDate);
            System.out.println("Breeding Date: " + breedingDate);
            System.out.println("Pregnancy Testing Date: " + pregnancyTestingDate);
            System.out.println("Milking Start Date: " + milkingStartDate);
            System.out.println("Dry Period Start Date: " + dryPeriodStartDate);

    }

    /*----------------------------------------------------------------END DASHBOARD-----------------------------------------------------------------------------------------------*/


    /*----------------------------------------------------------------START ANIMAL RECORDS-----------------------------------------------------------------------------------------------*/
    @FXML
    private ComboBox<String> AR_Cbox, AR_Sex_Cbox, AR_HornStatus_Cbox, AR_TailStatus_Cbox, AR_AnimalType_Cbox;

    @FXML
    private TextField AR_Search_TF;
    //Animal Records profile TextField
    @FXML
    private TextField ARP_ID_TF, ARP_Name_TF, ARP_Breed_TF, ARP_Color_TF, ARP_Bweight_TF, ARP_Cweight_TF, ARP_EarNotch_TF,
            ARP_HoofMark_TF, ARP_Tattoo_TF, ARP_TailMark_TF, ARP_SireID_TF, ARP_SireBreed_TF, ARP_SireName_TF, ARP_BthWgtDate_TF,
            ARP_DamID_TF, ARP_DamBreed_TF, ARP_DamName_TF, ARP_AgeFSvc_TF, ARP_weightFSvc_TF, ARP_TempPrf_TF, ARP_DietPrf_TF,
            ARP_CrntWgtDate_TF, ARP_PrvOwnerName_TF, ARP_PrvOwnerNum_TF, ARP_CurrentAge_TF;
    //Animal Records profile DatePicker
    @FXML
    private DatePicker ARP_Date_DP, AW_CrntWgtDate_DP;
    //Animal Records profile TextField
    @FXML
    private VBox ARP_Vbox_TF;
    //TABLE1
    @FXML
    private TableView<AnimalRecords> AR_T1;
    //TABLE1 COLUMNS
    @FXML
    private TableColumn<AnimalRecords, String> AR_AnimalBreed_T1;
    @FXML
    private TableColumn<AnimalRecords, Integer> AR_AnimalID_T1;
    @FXML
    private TableColumn<AnimalRecords, String> AR_AnimalType_T1;
    @FXML
    private TableColumn<AnimalRecords, String> AR_AnimalName_T1;
    //BUTTONS
    @FXML
    private Button AR_AllData_BT, AR_AddNew_BT, AR_Delete_BT, AR_Save_BT, AR_Update_BT, AR_Edit_BT, AR_Export_BT, AR_Cancel_BT;

    @FXML
    private Text AR_Animal_Count_TXT;

    //AnimalRecords class
    AnimalRecords animalRecords = null;

    //Database connections
    DatabaseConnection dbc = null;
    private Connection connect;
    private PreparedStatement statement;
    private CallableStatement callableStatement;
    private ResultSet Result;
    //Observablelist inituilization
    private ObservableList<AnimalRecords> dbData;

    //selected animal id from table
    Integer tableSelectedID;

    //On button click in animal records
    @FXML
    void onAR_BT_Click(ActionEvent event) {
        if (event.getSource() == AR_AddNew_BT) {
            clearAllFields();
            AR_Save_BT.setVisible(true);
            AR_AddNew_BT.setVisible(false);
            AR_Export_BT.setVisible(false);
            AR_Cancel_BT.setVisible(true);
            AR_Delete_BT.setDisable(true);
            AR_Edit_BT.setDisable(true);
        } else if (event.getSource() == AR_Cancel_BT) {
            AR_AddNew_BT.setVisible(true);
            AR_AddNew_BT.setDisable(false);
            AR_Save_BT.setVisible(false);
            AR_Delete_BT.setDisable(false);
            AR_Edit_BT.setDisable(false);
            AR_Edit_BT.setVisible(true);
            AR_Export_BT.setVisible(true);
            AR_Cancel_BT.setVisible(false);
        } else if (event.getSource() == AR_Edit_BT) {
            AR_Update_BT.setVisible(true);
            AR_Update_BT.setDisable(false);
            AR_Edit_BT.setVisible(false);
            AR_Export_BT.setVisible(false);
            AR_Cancel_BT.setVisible(true);
            AR_Delete_BT.setDisable(true);
            AR_AddNew_BT.setDisable(true);
        } else if (event.getSource() == AR_Update_BT) {
            System.out.println("Update Pressed");
            AR_AddNew_BT.setVisible(true);
            AR_AddNew_BT.setDisable(false);
            AR_Save_BT.setVisible(false);
            AR_Delete_BT.setDisable(false);
            AR_Edit_BT.setDisable(false);
            AR_Edit_BT.setVisible(true);
            AR_Export_BT.setVisible(true);
            AR_Cancel_BT.setVisible(false);
            SetTextfieldZero();
            updateIntoTable();
        } else if (event.getSource() == AR_AllData_BT) {
            SetTextfieldZero();
            String query = "query";
            loadFromDB(query);
        } else if (event.getSource() == AR_Save_BT) {
            if (ARP_Breed_TF.getText().isEmpty() || ARP_Date_DP.getValue() == null || AR_AnimalType_Cbox.getValue() == null
                    || AR_Sex_Cbox.getValue() == null) {
                if (ARP_Breed_TF.getText().isEmpty()) {
                    System.out.println("breed is empty");
                }
                if (ARP_Date_DP.getValue() == null) {
                    System.out.println("date of birth is empty");
                }
                if (AR_AnimalType_Cbox.getValue() == null) {
                    System.out.println("animal type is empty");
                }
                if (AR_Sex_Cbox.getValue() == null) {
                    System.out.println("gender is empty");
                }

            } else {
                AR_AddNew_BT.setVisible(true);
                AR_AddNew_BT.setDisable(false);
                AR_Save_BT.setVisible(false);
                AR_Delete_BT.setDisable(false);
                AR_Edit_BT.setDisable(false);
                AR_Edit_BT.setVisible(true);
                AR_Export_BT.setVisible(true);
                AR_Cancel_BT.setVisible(false);
                SetTextfieldZero();
                insertARs();
            }


        } else if (event.getSource() == AR_Delete_BT) {
            dbc.deleteFromAnimalRecords(tableSelectedID);
        }
    }

    @FXML
    public void onChooseAnimalType(ActionEvent event){
        if(AR_AnimalType_Cbox.getValue().equals("COW") || AR_AnimalType_Cbox.getValue().equals("HEIFER")){
            AR_Sex_Cbox.setValue("FEMALE");
        }else{
            AR_Sex_Cbox.setValue("MALE");
        }


    }

    /*---------------------------------------------------INSERT INTO TABLES-----*/
    //method inserting into database animal records table

    private void insertARs() {


        // Call method to insert animal record
        Integer DamID = null, SireID = null;
        if (!ARP_DamID_TF.getText().isEmpty()) {
            try {
                DamID = Integer.parseInt(ARP_DamID_TF.getText());
                // Continue processing with the parsed value
            } catch (NumberFormatException e) {
                // Handle the case where the string is not a valid integer
                e.printStackTrace(); // Or log the exception
            }
        } else {
            DamID = 0;
        }
        if (!ARP_SireID_TF.getText().isEmpty()) {
            try {
                SireID = Integer.parseInt(ARP_SireID_TF.getText());
                // Continue processing with the parsed value
            } catch (NumberFormatException e) {
                // Handle the case where the string is not a valid integer
                e.printStackTrace(); // Or log the exception
            }
        } else {
            SireID = 0;
        }


        int newAnimalID = dbc.insertAnimalRecord(
                ARP_Name_TF.getText(), AR_AnimalType_Cbox.getValue(), ARP_Breed_TF.getText(), AR_Sex_Cbox.getValue(),
                ARP_Date_DP.getValue() != null ? Date.valueOf(ARP_Date_DP.getValue()) : null, ARP_Color_TF.getText(),
                ARP_Tattoo_TF.getText(), AR_HornStatus_Cbox.getValue(), AR_TailStatus_Cbox.getValue(), ARP_HoofMark_TF.getText(),
                ARP_TailMark_TF.getText(), ARP_EarNotch_TF.getText(), ARP_TempPrf_TF.getText(), ARP_DietPrf_TF.getText(),
                ARP_PrvOwnerName_TF.getText(), ARP_PrvOwnerNum_TF.getText(), SireID, DamID, ARP_SireBreed_TF.getText(),
                ARP_SireName_TF.getText(), ARP_DamBreed_TF.getText(), ARP_DamName_TF.getText());
        // Call method to insert weight record

        dbc.insertWeightRecord(newAnimalID, 0, Double.parseDouble(ARP_Bweight_TF.getText()), Date.valueOf(ARP_Date_DP.getValue())
                , "BIRTH WEIGHT");

    }

    private void updateIntoTable() {

        int rowsAffected = dbc.updateAnimalRecord(Integer.parseInt(ARP_ID_TF.getText()), ARP_Name_TF.getText(), AR_AnimalType_Cbox.getValue(), ARP_Breed_TF.getText(), AR_Sex_Cbox.getValue(),
                ARP_Date_DP.getValue() != null ? Date.valueOf(ARP_Date_DP.getValue()) : null, ARP_Color_TF.getText(),
                ARP_Tattoo_TF.getText(), AR_HornStatus_Cbox.getValue(), AR_TailStatus_Cbox.getValue(), ARP_HoofMark_TF.getText(),
                ARP_TailMark_TF.getText(), ARP_EarNotch_TF.getText(), ARP_TempPrf_TF.getText(), ARP_DietPrf_TF.getText(),
                ARP_PrvOwnerName_TF.getText(), ARP_PrvOwnerNum_TF.getText(), Integer.parseInt(ARP_SireID_TF.getText()), Integer.parseInt(ARP_DamID_TF.getText()));
        // Call the function
        dbc.updateWeightRecord("Animal_Records", Integer.parseInt(ARP_ID_TF.getText()), Double.parseDouble(ARP_Bweight_TF.getText()), Date.valueOf(ARP_Date_DP.getValue())
                , "BIRTH WEIGHT");

    }

    // loads data into dbData observablelist from DatabaseConnection class
    private String [] animalTypeData = ListData.AnimalType;

    private void loadFromDB(String query) {
        try {
            //clearing data in list
            dbData.clear();
            if (query.equals(animalTypeData[0]) || query.equals(animalTypeData[1]) ||
                    query.equals(animalTypeData[2]) || query.equals(animalTypeData[3])) {
                //returns results from database
                dbData = dbc.getAllRecords(query);
            } else {
                //returns results from database
                dbData = dbc.getAllRecords(query);
            }

            //puts values in specific columns
            //use the same name that corresponds to the object class
            AR_AnimalID_T1.setCellValueFactory(new PropertyValueFactory<>("animalId"));
            AR_AnimalName_T1.setCellValueFactory(new PropertyValueFactory<>("animalName"));
            AR_AnimalBreed_T1.setCellValueFactory(new PropertyValueFactory<>("breed"));
            AR_AnimalType_T1.setCellValueFactory(new PropertyValueFactory<>("animalType"));

            //set data to table
            AR_T1.setItems(dbData);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // Close the resources in a

        dbc.closeResources();
        if (Result != null) {
            try {
                Result.close();
            } catch (SQLException e) {
                e.printStackTrace(); // Log or handle the exception
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace(); // Log or handle the exception
            }
        }
        if (callableStatement != null) {
            try {
                callableStatement.close();
            } catch (SQLException e) {
                e.printStackTrace(); // Log or handle the exception
            }
        }

    }

    public void loadToClass(ResultSet Result) {
    }

    // Method called when value is selected in combo-box
    @FXML
    void onChooseType(ActionEvent event) {
        if (AR_Cbox.getValue() == null) {
            AR_Animal_Count_TXT.setText("NO ANIMAL FOUND");
        } else {

            loadFromDB(AR_Cbox.getValue());
            int NumberReturned = dbData.size();
            String AnimalType = AR_Cbox.getValue();
            AR_Animal_Count_TXT.setText(NumberReturned + " "+(NumberReturned < 1 ? AnimalType : AnimalType+"S")+ " AVAILABLE");

        }
    }


    //when row is clicked on table method

    @FXML
    private void onMouseTableItemClick(MouseEvent event) {
        if (event.getClickCount() == 1) { // Check if it's a single click
            AnimalRecords selectedRecord = AR_T1.getSelectionModel().getSelectedItem();
            if (selectedRecord != null) {
                tableSelectedID = selectedRecord.getAnimalId();

                ARP_ID_TF.setText(String.valueOf(selectedRecord.getAnimalId()));
                ARP_Name_TF.setText(selectedRecord.getAnimalName());
                ARP_Breed_TF.setText(selectedRecord.getBreed());
                ARP_Color_TF.setText(selectedRecord.getColor());
                ARP_CurrentAge_TF.setText(selectedRecord.getCurrentAge());
                ARP_Bweight_TF.setText(String.valueOf(selectedRecord.getBirthWeight()));
                ARP_Cweight_TF.setText(String.valueOf(selectedRecord.getCurrentWeight()));
                ARP_EarNotch_TF.setText(selectedRecord.getEarNotches());
                ARP_HoofMark_TF.setText(selectedRecord.getHoofMark());
                ARP_Tattoo_TF.setText(selectedRecord.getTattoo());
                ARP_TailMark_TF.setText(selectedRecord.getTailMark());

                if (selectedRecord.getSireId() == 0 && selectedRecord.getForeign_Sire_ID() != 0) {
                    ARP_SireID_TF.setText(String.valueOf(selectedRecord.getForeign_Sire_ID()));
                    ARP_SireBreed_TF.setText(selectedRecord.getForeign_Sire_Breed());
                    ARP_SireName_TF.setText(selectedRecord.getForeign_Sire_Name());
                } else {
                    ARP_SireID_TF.setText(String.valueOf(selectedRecord.getSireId()));
                    ARP_SireBreed_TF.setText(selectedRecord.getSireBreed());
                    ARP_SireName_TF.setText(selectedRecord.getSireName());
                }

                if (selectedRecord.getDamId() == 0 && selectedRecord.getForeign_Dam_ID() != 0) {
                    ARP_DamID_TF.setText(String.valueOf(selectedRecord.getForeign_Dam_ID()));
                    ARP_DamBreed_TF.setText(selectedRecord.getForeign_Dam_Breed());
                    ARP_DamName_TF.setText(selectedRecord.getForeign_Dam_Name());
                } else {
                    ARP_DamID_TF.setText(String.valueOf(selectedRecord.getDamId()));
                    ARP_DamBreed_TF.setText(selectedRecord.getDamBreed());
                    ARP_DamName_TF.setText(selectedRecord.getDamName());
                }

                ARP_BthWgtDate_TF.setText(String.valueOf(selectedRecord.getBirthWeightDate()));
                ARP_AgeFSvc_TF.setText(String.valueOf(selectedRecord.getF_S_Age()));
                ARP_weightFSvc_TF.setText(String.valueOf(selectedRecord.getF_S_weight()));
                ARP_TempPrf_TF.setText(selectedRecord.getTempPreference());
                ARP_DietPrf_TF.setText(selectedRecord.getDietPreference());
                ARP_CrntWgtDate_TF.setText(String.valueOf(selectedRecord.getCurrentWeightDate()));
                ARP_PrvOwnerName_TF.setText(selectedRecord.getPre_Owner_Name());
                ARP_PrvOwnerNum_TF.setText(selectedRecord.getPre_Owner_Number());
                // Update ComboBox fields
                AR_Sex_Cbox.setValue(selectedRecord.getGender());
                AR_HornStatus_Cbox.setValue(selectedRecord.getHornStatus());
                AR_TailStatus_Cbox.setValue(selectedRecord.getTailDockStatus());
                AR_AnimalType_Cbox.setValue(selectedRecord.getAnimalType());
                // Update DatePicker field
                if (selectedRecord.getDateOfBirth() != null) {
                    Date dateOfBirth = (Date) selectedRecord.getDateOfBirth();
                    LocalDate localDateOfBirth = dateOfBirth.toLocalDate();
                    ARP_Date_DP.setValue(localDateOfBirth);

                } else {
                    ARP_Date_DP.setValue(null);
                }


                // ... update other fields ...
            }
        }

    }

    private void clearAllFields() {
        // Clear the text fields
        //AR_Search_TF.clear();
        ARP_ID_TF.clear();
        ARP_ID_TF.setDisable(true);
        ARP_Name_TF.clear();
        ARP_Breed_TF.clear();
        ARP_Color_TF.clear();
        ARP_Bweight_TF.clear();
        ARP_Cweight_TF.clear();
        ARP_Cweight_TF.setDisable(true);
        ARP_BthWgtDate_TF.clear();
        ARP_BthWgtDate_TF.setDisable(true);
        ARP_CrntWgtDate_TF.clear();
        ARP_CrntWgtDate_TF.setDisable(true);
        ARP_EarNotch_TF.clear();
        ARP_HoofMark_TF.clear();
        ARP_Tattoo_TF.clear();
        ARP_TailMark_TF.clear();
        ARP_SireID_TF.clear();
        ARP_SireBreed_TF.clear();
        ARP_SireBreed_TF.setDisable(true);
        ARP_SireName_TF.clear();
        ARP_SireName_TF.setDisable(true);
        ARP_DamID_TF.clear();
        ARP_DamBreed_TF.clear();
        ARP_DamBreed_TF.setDisable(true);
        ARP_DamName_TF.clear();
        ARP_DamName_TF.setDisable(true);
        ARP_AgeFSvc_TF.clear();
        ARP_AgeFSvc_TF.setDisable(true);
        ARP_weightFSvc_TF.clear();
        ARP_weightFSvc_TF.setDisable(true);
        ARP_TempPrf_TF.clear();
        ARP_DietPrf_TF.clear();
        ARP_PrvOwnerName_TF.clear();
        ARP_PrvOwnerNum_TF.clear();
        ARP_CurrentAge_TF.clear();
        ARP_CurrentAge_TF.setDisable(true);

        // Clear the combo boxes
        AR_Cbox.getSelectionModel().clearSelection();
        AR_Cbox.setPromptText("Choose Type");
        AR_Sex_Cbox.getSelectionModel().clearSelection();
        AR_HornStatus_Cbox.getSelectionModel().clearSelection();
        AR_TailStatus_Cbox.getSelectionModel().clearSelection();
        AR_AnimalType_Cbox.getSelectionModel().clearSelection();

        // Clear the date pickers
        ARP_Date_DP.setValue(null);
        if (AW_CrntWgtDate_DP != null) {
            AW_CrntWgtDate_DP.setValue(null);
        }
    }

    /*---------------------------------------------------------------------------END-ANIMAL-RECORDS-----------------------------------------------------------------------------------------------*/


    /*---------------------------------------------------------------------------START-MILk-RECORDS-----------------------------------------------------------------------------------------------*/
    @FXML
    private Text MR_Animal_Count_TXT;
    @FXML
    private TextField MR_SearchAnimal_TF, MR_SearchRecords_TF, MR_CowID_TF, MR_Afternoon_TF, MR_Morning_TF, MR_Evening_TF;
    @FXML
    private Button MR_AddNew_BT, MR_AllAnimalData_BT, MR_Cancel_BT, MR_Delete_BT, MR_DeleteAll_BT, MR_Edit_BT, MR_Update_BT, MR_Export_BT, MR_LoadRecords_BT, MR_Save_BT;
    @FXML
    private TableView<AnimalRecords> MR_AnimalTable_T1;
    @FXML
    private TableColumn<AnimalRecords, String> MR_AnimalBreed_T1, MR_AnimalID_T1, MR_AnimalName_T1, MR_AnimalType_T1;
    @FXML
    private TableView<MilkRecords> MR_MilkTable_T2;
    @FXML
    private TableColumn<?, ?> MR_MilkingDate_TC2, MR_Morning_TC2, MR_Afternoon_TC2, MR_Evening_TC2, MR_CowID_TC2, MR_CowName_TC2;
    @FXML
    private ComboBox<String> MR_MilkingMonth_Cbox;
    @FXML
    private ComboBox<Integer> MR_MilkingYear_Cbox;
    @FXML
    private DatePicker MR_MilkingDate_DP;
    @FXML
    private StackPane MR_Add_Save_SP, MR_Edit_Update_SP;
    @FXML
    private CategoryAxis MRxAxis = new CategoryAxis();
    @FXML
    private NumberAxis MRyAxis = new NumberAxis();
    @FXML
    private LineChart<String, Number> MR_MilkGraph_LC = new LineChart<>(MRxAxis, MRyAxis);

    private ObservableList<MilkRecords> dbObMilk;

    @FXML
    public void onMR_BT_Click(ActionEvent event) {
        if (event.getSource() == MR_AddNew_BT) {
            MR_Edit_Update_SP.setDisable(true);
            MR_Save_BT.setVisible(true);
            MR_AddNew_BT.setVisible(false);
        } else if (event.getSource() == MR_Edit_BT) {
            MR_Add_Save_SP.setDisable(true);
            MR_Update_BT.setVisible(true);
            MR_Edit_BT.setVisible(false);
        } else if (event.getSource() == MR_Save_BT) {
            MR_Edit_Update_SP.setDisable(false);
            MR_Save_BT.setVisible(false);
            MR_AddNew_BT.setVisible(true);
            SetTextfieldZero();
            insertIntoMilkTable();
            SetTextfieldZero();
            loadMRTable2();
            MR_MilkTable_T2.setItems(dbObMilk);
        } else if (event.getSource() == MR_Update_BT) {
            MR_Add_Save_SP.setDisable(false);
            MR_Update_BT.setVisible(false);
            MR_Edit_BT.setVisible(true);
            SetTextfieldZero();
            updateIntoMilkTable();
            loadMRTable2();

        } else if (event.getSource() == MR_Cancel_BT) {
            MR_Edit_Update_SP.setDisable(false);
            MR_Save_BT.setVisible(false);
            MR_AddNew_BT.setVisible(true);
            MR_Add_Save_SP.setDisable(false);
            MR_Update_BT.setVisible(false);
            MR_Edit_BT.setVisible(true);

        } else if (event.getSource() == MR_Delete_BT) {
            deleteFromMilkTable();
            loadMRTable2();
        }else if (event.getSource() == MR_DeleteAll_BT) {
            deleteAllFromMilkTable();
            loadMRTable2();
        } else if (event.getSource() == AW_Export_BT) {

        }  else if (event.getSource() == AW_AllData_BT) {
            String query = "query";
            loadFromDB(query);
        }
    }

    private void loadDataMR_AR_Table() {
        //puts values in specific columns
        //use the same name that corresponds to the object class
        MR_AnimalID_T1.setCellValueFactory(new PropertyValueFactory<>("animalId"));
        MR_AnimalName_T1.setCellValueFactory(new PropertyValueFactory<>("animalName"));
        MR_AnimalBreed_T1.setCellValueFactory(new PropertyValueFactory<>("breed"));
        MR_AnimalType_T1.setCellValueFactory(new PropertyValueFactory<>("animalType"));
        //set data to table
        MR_AnimalTable_T1.setItems(dbData);
    }
    Integer MR_tableAnimalSelectedID;
    public void onARMRMouseTableItemClick1(MouseEvent event) {
        dbObMilk.clear();
        if (event.getClickCount() == 1) { // Check if it's a single click
            AnimalRecords selectedRecord = MR_AnimalTable_T1.getSelectionModel().getSelectedItem();
            if (selectedRecord != null) {
                MR_tableAnimalSelectedID = selectedRecord.getAnimalId();
                MR_CowID_TF.setText(String.valueOf(selectedRecord.getAnimalId()));
                loadMRTable2();
                initializeMRYearMonthCbox();
                initualizeMRChart(MR_tableAnimalSelectedID);
            }
        }
    }

    private void loadMRTable2() {
        try {
            dbObMilk = dbc.getAllMilkRecords(MR_tableAnimalSelectedID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (MR_tableAnimalSelectedID != null) {
            MR_CowID_TC2.setCellValueFactory(new PropertyValueFactory<>("animalId"));
            MR_CowName_TC2.setCellValueFactory(new PropertyValueFactory<>("animalId"));
            MR_MilkingDate_TC2.setCellValueFactory(new PropertyValueFactory<>("milkingDate"));
            MR_Morning_TC2.setCellValueFactory(new PropertyValueFactory<>("morningSession"));
            MR_Afternoon_TC2.setCellValueFactory(new PropertyValueFactory<>("afternoonSession"));
            MR_Evening_TC2.setCellValueFactory(new PropertyValueFactory<>("eveningSession"));
            //set data to table
            MR_MilkTable_T2.setItems(dbObMilk);
        }
    }

    int milkIDFromMRTable;
    public void onARMRMouseTableItemClick2(MouseEvent event) {

        if (event.getClickCount() == 1) { // Check if it's a single click
            MilkRecords selectedRecord = MR_MilkTable_T2.getSelectionModel().getSelectedItem();

            if (selectedRecord != null) {
                milkIDFromMRTable = selectedRecord.getMilkId();
                MR_CowID_TF.setText(String.valueOf(selectedRecord.getAnimalId()));
                MR_Morning_TF.setText(String.valueOf(selectedRecord.getMorningSession()));
                MR_Afternoon_TF.setText(String.valueOf(selectedRecord.getAfternoonSession()));
                MR_Evening_TF.setText(String.valueOf(selectedRecord.getEveningSession()));
                if (selectedRecord.getMilkingDate() != null) {
                    Date dateOfBirth = (Date) selectedRecord.getMilkingDate();
                    LocalDate localMilkingDate = dateOfBirth.toLocalDate();
                    MR_MilkingDate_DP.setValue(LocalDate.from(localMilkingDate));
                } else {
                    MR_MilkingDate_DP.setValue(null);

                }

            }
        }
    }

    private void insertIntoMilkTable() {
        dbc.insertMilkRecord(Integer.parseInt(MR_CowID_TF.getText()), MR_MilkingDate_DP.getValue() != null ? Date.valueOf(MR_MilkingDate_DP.getValue()) : null,
                Double.parseDouble(MR_Morning_TF.getText()), Double.parseDouble(MR_Afternoon_TF.getText()), Double.parseDouble(MR_Evening_TF.getText()));

    }

    private void updateIntoMilkTable() {
//TODO handle double values with if so that ("") should not be passed
        dbc.updateMilkRecord(milkIDFromMRTable, Integer.parseInt(MR_CowID_TF.getText()), MR_MilkingDate_DP.getValue() != null ? Date.valueOf(MR_MilkingDate_DP.getValue()) : null,
                Double.parseDouble(MR_Morning_TF.getText()), Double.parseDouble(MR_Afternoon_TF.getText()), Double.parseDouble(MR_Evening_TF.getText()));


    }

    private void deleteFromMilkTable() {
        dbc.deleteMilkRecord(milkIDFromMRTable,"Milk");

    }
    private void deleteAllFromMilkTable() {
        dbc.deleteMilkRecord(milkIDFromMRTable,"Animal");

    }

    private void initualizeMRChart(int animalID){
        MR_MilkGraph_LC.getData().clear();
        try {
            // Define axes
            MRxAxis.setLabel("Milking Date");
            MRyAxis.setLabel("Milk Production");

            // Create the line chart
            MR_MilkGraph_LC.setTitle("Milk Production Chart");

            // Create a series for the data
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Milk Production");

            // Populate the series with sample data
            for (MilkRecords MR : dbObMilk) {

                String mDate = String.valueOf(MR.getMilkingDate());
                String mYear = String.valueOf(MR_MilkingYear_Cbox.getValue());
                int monthNumber = Month.valueOf(String.valueOf(MR_MilkingMonth_Cbox.getValue()).toUpperCase()).getValue();
                String mMonth = (monthNumber < 10) ? "0" + monthNumber : String.valueOf(monthNumber);
                String yearMonth = (mYear+"-"+mMonth);
                // Generate random milk production for each day (for demonstration purposes)
                if(animalID==MR.getAnimalId() && mDate.startsWith(yearMonth)){
                    double totalMilkProduction = (MR.getMorningSession()+MR.getAfternoonSession()+MR.getEveningSession());
                    series.getData().add(new XYChart.Data<>(MR.getMilkingDate().toString(), totalMilkProduction));
                }


            }
            // Add series to chart
            MR_MilkGraph_LC.getData().add(series);


        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions here
        }
    }
    public void initializeMRYearMonthCbox() {

        int[] yearTemp = new int[dbObMilk.size()];
        int[] monthTemp = new int[dbObMilk.size()];
        int count = 0; // Declare count variable outside the loop

        for (MilkRecords M : dbObMilk) {
            yearTemp[count] = M.getMilkingDate().getYear() + 1900;
            monthTemp[count] = M.getMilkingDate().getMonth() + 1;
            //System.out.println(yearTemp[count]);
            count++;
        }
        // Extract distinct years
        ObservableList<Integer> distinctYears = Arrays.stream(yearTemp)
                .distinct()
                .mapToObj(num -> num)
                .sorted() // Optional: Sort the years
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        // Extract distinct Month
        ObservableList<String> distinctMonth = Arrays.stream(monthTemp)
                .distinct()
                .mapToObj(month -> Month.of(month).toString()) // Map month number to month name
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        // Assuming MR_MilkingMonth_Cbox is the ComboBox you want to populate
        MR_MilkingMonth_Cbox.setItems(distinctMonth);
        // Assuming comboBox is the ComboBox you want to populate
        MR_MilkingYear_Cbox.setItems(distinctYears);

        if(!distinctMonth.isEmpty() && !distinctYears.isEmpty()){
            MR_MilkingMonth_Cbox.setValue(distinctMonth.getLast());
            MR_MilkingYear_Cbox.setValue(distinctYears.getLast());
        }else{
            System.out.println("i want to set prompt");
            MR_MilkingMonth_Cbox.getItems().clear();
            MR_MilkingYear_Cbox.getItems().clear();
            MR_MilkingMonth_Cbox.setPromptText("Months");
            MR_MilkingYear_Cbox.setPromptText("Years");
        }
    }

    // Event handler for when both year and month are selected
    @FXML
    public void handleMRYearMonthSelection(ActionEvent event) {
        Integer selectedYear = MR_MilkingYear_Cbox.getValue();
        String selectedMonth = MR_MilkingMonth_Cbox.getValue();

        if (selectedYear != null && selectedMonth != null) {
            try{
                // Perform the action here
                System.out.println("Selected Year: " + selectedYear + ", Selected Month: " + selectedMonth);
                int monthNumber = Month.valueOf(selectedMonth.toUpperCase()).getValue();
                // Perform the action here
                System.out.println("Selected Year: " + selectedYear + ", Selected Month: " + monthNumber);
            }catch(Exception e){
                System.out.println("error occured");
            }

            initualizeMRChart(MR_tableAnimalSelectedID);
        }
    }

    /*---------------------------------------------------------------------------END-MILK-RECORDS-----------------------------------------------------------------------------------------------*/


    /*--------------------------------------------------------------------------START-HEALTH-RECORDS-----------------------------------------------------------------------------------------------*/
    @FXML
    private Button H_AddNew_BT, H_Cancel_BT, H_Delete_BT, H_Edit_BT, H_Export_BT, H_LoadAnimals_BT, H_Save_BT, H_Update_BT;
    @FXML
    private TextField H_VetEmail_TF, H_VetFName_TF, H_VetHomeL_TF, H_VetLName_TF, H_VetPhone_TF, H_VetWorkL_TF, H_TreatmentCost_TF,
            H_SearchAnimal_TF, H_SearchRecord_TF, H_Breed_TF, H_AnimalID_TF, H_AgeAtService_TF, H_ServiceWeight_TF;

    @FXML
    private TextArea H_Diangnosis_TA, H_Symptoms_TA, H_TreatmentPlan_TA, H_Notes_TA;
    @FXML
    private TableView<AnimalRecords> H_Animal_T1;
    @FXML
    private TableColumn<AnimalRecords, String> H_ID_TC1, H_AnimalName_TC1, H_Breed_TC1, H_AnimalType_TC1;
    @FXML
    private TableView<HealthRecords> H_Record_T2;
    @FXML
    private TableColumn<HealthRecords, String> H_RecordDate_TC2, H_ServicedBy_TC2, H_AnimalID_TC2;
    @FXML
    private ComboBox<?> H_SearchType_Cbox;
    @FXML
    private DatePicker H_ServiceDate_DP;
    @FXML
    private StackPane H_Add_Save_SP, H_Edit_Update_SP, H_Delete_Cancel_SP;
    private ObservableList<HealthRecords> dbObHealth;

    @FXML
    public void onH_BT_Click(ActionEvent event) throws SQLException {
        if (event.getSource() == H_AddNew_BT) {
            H_Edit_Update_SP.setDisable(true);
            H_Save_BT.setVisible(true);
            H_AddNew_BT.setVisible(false);
            H_Delete_BT.setVisible(false);
            H_Cancel_BT.setVisible(true);
        } else if (event.getSource() == H_Edit_BT) {
            H_Add_Save_SP.setDisable(true);
            H_Update_BT.setVisible(true);
            H_Edit_BT.setVisible(false);
            H_Delete_BT.setVisible(false);
            H_Cancel_BT.setVisible(true);
        } else if (event.getSource() == H_Save_BT) {
            H_Edit_Update_SP.setDisable(false);
            H_Save_BT.setVisible(false);
            H_AddNew_BT.setVisible(true);
            H_Delete_BT.setVisible(true);
            H_Cancel_BT.setVisible(false);
            SetTextfieldZero();
            insertIntoHealth();
            AddToHTable2();
        } else if (event.getSource() == H_Update_BT) {
            H_Add_Save_SP.setDisable(false);
            H_Update_BT.setVisible(false);
            H_Edit_BT.setVisible(true);
            H_Delete_BT.setVisible(true);
            H_Cancel_BT.setVisible(false);
            SetTextfieldZero();
            updateIntoHealth();
            AddToHTable2();
        } else if (event.getSource() == H_Cancel_BT) {
            H_Edit_Update_SP.setDisable(false);
            H_Add_Save_SP.setDisable(false);
            H_Save_BT.setVisible(false);
            H_AddNew_BT.setVisible(true);
            H_Update_BT.setVisible(false);
            H_Edit_BT.setVisible(true);
            H_Delete_BT.setVisible(true);
            H_Cancel_BT.setVisible(false);

        } else if (event.getSource() == H_Delete_BT) {
            deleteFromHealth();
            AddToHTable2();
        } else if (event.getSource() == H_Export_BT) {

        } else if (event.getSource() == H_LoadAnimals_BT) {
            String query = "query";
            loadFromDB(query);
        }
    }

    private void loadDataH_AR_Table() {

        //puts values in specific columns
        //use the same name that corresponds to the object class
        H_ID_TC1.setCellValueFactory(new PropertyValueFactory<>("animalId"));
        H_AnimalName_TC1.setCellValueFactory(new PropertyValueFactory<>("animalName"));
        H_Breed_TC1.setCellValueFactory(new PropertyValueFactory<>("breed"));
        H_AnimalType_TC1.setCellValueFactory(new PropertyValueFactory<>("animalType"));

        //set data to table
        H_Animal_T1.setItems(dbData);
    }

    int animalIDFromHTable;

    @FXML
    public void onARHMouseTableItemClick1(MouseEvent event) {
        dbObHealth.clear();
        if (event.getClickCount() == 1) { // Check if it's a single click
            AnimalRecords selectedRecord = H_Animal_T1.getSelectionModel().getSelectedItem();
            animalIDFromHTable = selectedRecord.getAnimalId();
            H_AnimalID_TF.setText(String.valueOf(selectedRecord.getAnimalId()));
            H_Breed_TF.setText(selectedRecord.getBreed());

            if (selectedRecord != null) {
                tableSelectedID = selectedRecord.getAnimalId();
                AddToHTable2();
            }
        }

    }

    private void AddToHTable2() {
        try {
            dbObHealth = dbc.getAllHealthRecords(animalIDFromHTable);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        H_AnimalID_TC2.setCellValueFactory(new PropertyValueFactory<>("animalId"));
        H_ServicedBy_TC2.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        H_RecordDate_TC2.setCellValueFactory(new PropertyValueFactory<>("serviceDate"));
        //set data to table
        H_Record_T2.setItems(dbObHealth);

    }


    int animalIDFromHTable2, selectedHealthId;
    String tableSelecteWeghtNote;
    @FXML
    public void onARHMouseTableItemClick2(MouseEvent event){
        if (event.getClickCount() == 1) { // Check if it's a single click
            HealthRecords selectedRecord = H_Record_T2.getSelectionModel().getSelectedItem();
            animalIDFromHTable2 = selectedRecord.getAnimalId();
            selectedHealthId = selectedRecord.getHealthId();

            if (selectedRecord != null) {
                tableSelecteWeghtNote = selectedRecord.getWeightNote();
                H_AnimalID_TF.setText(String.valueOf(selectedRecord.getAnimalId()));
                H_Breed_TF.setText(selectedRecord.getBreed());
                H_Symptoms_TA.setText(selectedRecord.getSymptoms());
                H_Diangnosis_TA.setText(selectedRecord.getDiagnosis());
                H_TreatmentPlan_TA.setText(selectedRecord.getTreatment_Plan());
                H_Notes_TA.setText(selectedRecord.getServiceNote());
                H_ServiceWeight_TF.setText(String.valueOf(selectedRecord.getWeightKg()));
                H_AgeAtService_TF.setText(selectedRecord.getService_Age());
                H_TreatmentCost_TF.setText(String.valueOf(selectedRecord.getTreatmentCost()));
                H_VetFName_TF.setText(selectedRecord.getFirstName());
                H_VetLName_TF.setText(selectedRecord.getLastName());
                H_VetEmail_TF.setText(selectedRecord.getEmail());
                H_VetPhone_TF.setText(selectedRecord.getPhone());
                H_VetWorkL_TF.setText(selectedRecord.getWorkAddress());
                H_VetHomeL_TF.setText(selectedRecord.getHomeAddress());

                if (selectedRecord.getServiceDate() != null) {
                    Date dateOfBirth = (Date) selectedRecord.getServiceDate();
                    LocalDate localDateOfBirth = dateOfBirth.toLocalDate();
                    H_ServiceDate_DP.setValue(localDateOfBirth);

                } else {
                    H_ServiceDate_DP.setValue(null);
                }


                // ... update other fields ...
            }else{
                System.out.println("EMPTY");
            }
        }
    }

 int healthGeneratedId;
    private void insertIntoHealth(){
        // TODO(developer) - add if statement to check if ID exists and make all fields receiving numeric values to receive only numeric values
        healthGeneratedId = dbc.insertHealthRecord(Integer.parseInt(H_AnimalID_TF.getText()), H_ServiceDate_DP.getValue() != null ? Date.valueOf(H_ServiceDate_DP.getValue()) : null,
                               H_Symptoms_TA.getText(),H_Diangnosis_TA.getText(),H_TreatmentPlan_TA.getText(),Double.parseDouble(H_TreatmentCost_TF.getText()),
                               H_Notes_TA.getText(),H_VetFName_TF.getText(),H_VetLName_TF.getText(),H_VetEmail_TF.getText(),H_VetPhone_TF.getText(),
                               H_VetHomeL_TF.getText(),H_VetWorkL_TF.getText());

        dbc.insertWeightRecord( Integer.parseInt(H_AnimalID_TF.getText()), healthGeneratedId, Double.parseDouble(H_ServiceWeight_TF.getText()),Date.valueOf(H_ServiceDate_DP.getValue())
                , "SERVICE WEIGHT");
    }

    private void updateIntoHealth(){
        // TODO(developer) - add if statement to check if ID exists and make all fields receiving numeric values to receive only numeric values
        dbc.updateHealthRecord(selectedHealthId,Integer.parseInt(H_AnimalID_TF.getText()), H_ServiceDate_DP.getValue() != null ? Date.valueOf(H_ServiceDate_DP.getValue()) : null,
                H_Symptoms_TA.getText(),H_Diangnosis_TA.getText(),H_TreatmentPlan_TA.getText(),Double.parseDouble(H_TreatmentCost_TF.getText()),
                H_Notes_TA.getText(),H_VetFName_TF.getText(),H_VetLName_TF.getText(),H_VetEmail_TF.getText(),H_VetPhone_TF.getText(),
                H_VetHomeL_TF.getText(),H_VetWorkL_TF.getText());

        dbc.updateWeightRecord("health_Records", selectedHealthId, Double.parseDouble(H_ServiceWeight_TF.getText()),Date.valueOf(H_ServiceDate_DP.getValue())
                , tableSelecteWeghtNote);

    }
    private void deleteFromHealth() throws SQLException {
        dbc.deleteFromHealthRecords(selectedHealthId);
    }



    /*--------------------------------------------------------------------------END-HEALTH-RECORDS-----------------------------------------------------------------------------------------------*/



    /*------------------------------------------------------------------------START-BREEDING-RECORDS-----------------------------------------------------------------------------------------------*/
    @FXML
    private Button B_AddNew_BT, B_Cancel_BT, B_Delete_BT, B_Edit_BT, B_Export_BT, B_LoadAnimals_BT, B_Save_BT, B_Update_BT;
    @FXML
    private TableView<AnimalRecords> B_Animal_T;
    @FXML
    private TableColumn<AnimalRecords,String> B_Breed_TC,B_AnimalName_TC,B_AnimalType_TC, B_ID_TC;
    @FXML
    private TableView<BreedingRecords> B_Record_T1;
    @FXML
    private TableColumn<BreedingRecords,String> B_BreedingMethod_TC1, B_AnimalID_TC1, B_RecordDate_TC1, B_HeatDate_TC1;
    @FXML
    private TextField B_BreedingMethod_TF, B_BullBreed_TF, B_BullID_TF, B_CalfBreed_TF, B_CalfID_TF, B_CowBreed_TF, B_CowID_TF,
                      B_SearchAnimal_TF, B_SearchRecord_TF;
    @FXML
    private TextArea B_BreedingNotes_TA,B_CalvingNotes_TA;
    @FXML
    private DatePicker B_CalvingDueDate_DP, B_BreedingDate_DP, B_DateCalved_DP, B_HeatDate_DP, B_PregDiagDate_DP;
    @FXML
    private StackPane B_Add_Save_SP, B_Edit_Update_SP, B_Delete_Cancel_SP;
    private ObservableList<BreedingRecords> dbObBreeding;


    @FXML
    public void onB_BT_Click(ActionEvent event) throws SQLException {
        if (event.getSource() == B_AddNew_BT) {
            B_Edit_Update_SP.setDisable(true);
            B_Save_BT.setVisible(true);
            B_AddNew_BT.setVisible(false);
            B_Delete_BT.setVisible(false);
            B_Cancel_BT.setVisible(true);
        } else if (event.getSource() == B_Edit_BT) {
            B_Add_Save_SP.setDisable(true);
            B_Update_BT.setVisible(true);
            B_Edit_BT.setVisible(false);
            B_Delete_BT.setVisible(false);
            B_Cancel_BT.setVisible(true);
        } else if (event.getSource() == B_Save_BT) {
            B_Edit_Update_SP.setDisable(false);
            B_Save_BT.setVisible(false);
            B_AddNew_BT.setVisible(true);
            B_Delete_BT.setVisible(true);
            B_Cancel_BT.setVisible(false);
            SetTextfieldZero();
            insertBreedingDataFromUI();
        } else if (event.getSource() == B_Update_BT) {
            B_Add_Save_SP.setDisable(false);
            B_Update_BT.setVisible(false);
            B_Edit_BT.setVisible(true);
            B_Delete_BT.setVisible(true);
            B_Cancel_BT.setVisible(false);
            SetTextfieldZero();
        } else if (event.getSource() == B_Cancel_BT) {
            B_Edit_Update_SP.setDisable(false);
            B_Add_Save_SP.setDisable(false);
            B_Save_BT.setVisible(false);
            B_AddNew_BT.setVisible(true);
            B_Update_BT.setVisible(false);
            B_Edit_BT.setVisible(true);
            B_Delete_BT.setVisible(true);
            B_Cancel_BT.setVisible(false);

        } else if (event.getSource() == B_Delete_BT) {

        } else if (event.getSource() == B_Export_BT) {

        } else if (event.getSource() == B_LoadAnimals_BT) {
            String query = "query";
            loadFromDB(query);
        }
    }

    private void loadDataB_AR_Table() {

        //puts values in specific columns
        //use the same name that corresponds to the object class
        B_ID_TC.setCellValueFactory(new PropertyValueFactory<>("animalId"));
        B_AnimalName_TC.setCellValueFactory(new PropertyValueFactory<>("animalName"));
        B_Breed_TC.setCellValueFactory(new PropertyValueFactory<>("breed"));
        B_AnimalType_TC.setCellValueFactory(new PropertyValueFactory<>("animalType"));

        //set data to table
        B_Animal_T.setItems(dbData);
    }

    int animalIDFromBTable;

    @FXML
    public void onARBMouseTableItemClick(MouseEvent event) {
        dbObBreeding.clear();
        if (event.getClickCount() == 1) { // Check if it's a single click
            AnimalRecords selectedRecord = B_Animal_T.getSelectionModel().getSelectedItem();
            animalIDFromBTable = selectedRecord.getAnimalId();
            B_CowID_TF.setText(String.valueOf(selectedRecord.getAnimalId()));
            B_CowBreed_TF.setText(selectedRecord.getBreed());

            if (selectedRecord != null) {
                tableSelectedID = selectedRecord.getAnimalId();
                AddToBTable2();
            }
        }

    }

    private void AddToBTable2() {
        try {
            dbObBreeding = dbc.getAllBreedingRecords(animalIDFromBTable);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        B_AnimalID_TC1.setCellValueFactory(new PropertyValueFactory<>("cowId"));
        B_BreedingMethod_TC1.setCellValueFactory(new PropertyValueFactory<>("breedingMethod"));
        B_HeatDate_TC1.setCellValueFactory(new PropertyValueFactory<>("heatDate"));
        B_RecordDate_TC1.setCellValueFactory(new PropertyValueFactory<>("breedingDate"));
        //set data to table
        B_Record_T1.setItems(dbObBreeding);

    }

    // New method to get values from JavaFX components and call insertBreedingData
    public void insertBreedingDataFromUI() {
        //TODO check if the text fields have values before inserting to databade
       dbc.insertBreedingData(Integer.parseInt(B_CowID_TF.getText()),Integer.parseInt(B_BullID_TF.getText()),Date.valueOf(B_HeatDate_DP.getValue()),
                              Date.valueOf(B_BreedingDate_DP.getValue()),B_BreedingMethod_TF.getText(), Date.valueOf(B_CalvingDueDate_DP.getValue()),
                              Date.valueOf(B_PregDiagDate_DP.getValue()), Date.valueOf(B_DateCalved_DP.getValue()),Integer.parseInt(B_CalfID_TF.getText()),
                              B_BreedingNotes_TA.getText(),B_CalvingNotes_TA.getText());
    }


    int CowIDFromBTable1, selectedBreedingId;
    @FXML
    public void onBMouseTableItemClick(MouseEvent event){
        if (event.getClickCount() == 1) { // Check if it's a single click
            BreedingRecords selectedRecord = B_Record_T1.getSelectionModel().getSelectedItem();
            CowIDFromBTable1 = selectedRecord.getCowId();
            selectedBreedingId = selectedRecord.getBreedingId();

            if (selectedRecord != null) {
                B_CowID_TF.setText(String.valueOf(selectedRecord.getCowId()));
                B_CowBreed_TF.setText(selectedRecord.getCowBreed());
                B_BullID_TF.setText(String.valueOf(selectedRecord.getBullId()));
                B_BullBreed_TF.setText(selectedRecord.getBullBreed());
                B_CalfID_TF.setText(String.valueOf(selectedRecord.getCalfId()));
                B_CalfBreed_TF.setText(selectedRecord.getCalfBreed());
                B_BreedingMethod_TF.setText(selectedRecord.getBreedingMethod());
                B_BreedingNotes_TA.setText(selectedRecord.getBreedingNotes());
                B_CalvingNotes_TA.setText(selectedRecord.getCalvingNotes());

                // Set date values if not null
                setDatePickerValue(B_CalvingDueDate_DP, ((Date)selectedRecord.getCalvingDueDate()));
                setDatePickerValue(B_BreedingDate_DP, ((Date)selectedRecord.getBreedingDate()));
                setDatePickerValue(B_DateCalved_DP, ((Date)selectedRecord.getCalvingDate()));
                setDatePickerValue(B_HeatDate_DP, ((Date)selectedRecord.getHeatDate()));
                setDatePickerValue(B_PregDiagDate_DP, ((Date)selectedRecord.getPregnancyDiagnosisDate()));

                // ... update other fields ...
            } else {
                System.out.println("EMPTY");
            }
        }
    }

    // A method to set DatePicker value if the date is not null
    private void setDatePickerValue(DatePicker datePicker, Date date) {
        if (date != null) {
            LocalDate localDateOfBirth = date.toLocalDate();
            datePicker.setValue(localDateOfBirth);
        } else {
            datePicker.setValue(null);
        }
    }

    /*------------------------------------------------------------------------END-BREEDING-RECORDS-----------------------------------------------------------------------------------------------*/



    /*--------------------------------------------------------------------------START-WEIGHT-RECORDS-----------------------------------------------------------------------------------------------*/

    @FXML
    private Text AW_Animal_Count_TXT;
    @FXML
    private Button AW_AllData_BT, AW_AddNew_BT, AW_Delete_BT, AW_Edit_BT, AW_Update_BT, AW_Export_BT, AW_Save_BT, AW_Cancel_BT;

    @FXML
    private TableView<WeightRecords> AW_WeightTable_T1;
    @FXML
    private TableColumn<AnimalRecords, String> AW_AnimalID_T, AW_AnimalName_T, AW_AnimalType_T, AW_AnimalBreed_T;
    @FXML
    private TableView<AnimalRecords> AW_ARTABLE_T;
    @FXML
    private TableColumn<WeightRecords, String> AW_AnimalID_T1, AW_AnimalName_T1 , AW_AnimalBreed_T1, AW_Weight_T1, AW_WeighDate_T1, AW_AgeAtWeight_T1 ,AW_WeightNote_T1 ;
    @FXML
    private TextField AW_Search_TF, AW_Weight_TF;
    @FXML
    private DatePicker AW_WgtDate_DP;

    @FXML
    private HBox AW_Button_VB, AW_InputWeight_VB;

    @FXML
    private ComboBox<String> AW_WeighingMonth_Cbox;
    @FXML
    private ComboBox<Integer> AW_WeighingYear_Cbox;

    @FXML
    private CategoryAxis AWxAxis = new CategoryAxis();
    @FXML
    private NumberAxis AWyAxis = new NumberAxis();
    @FXML
    private LineChart<String, Number> AW_WeightGraph_LC = new LineChart<>(AWxAxis, AWyAxis);
    private ObservableList<WeightRecords> dbObWeight;
    @FXML
    //private DatePicker AW_WgtDate_DP;

    void onAW_BT_Click(ActionEvent event) {
        if (event.getSource() == AW_AddNew_BT) {
            AW_InputWeight_VB.setVisible(true);
            AW_Button_VB.setVisible(false);
            AW_Save_BT.setVisible(true);
            AW_Update_BT.setVisible(false);
        } else if (event.getSource() == AW_Edit_BT) {
            AW_InputWeight_VB.setVisible(true);
            AW_Button_VB.setVisible(false);
            AW_Save_BT.setVisible(false);
            AW_Update_BT.setVisible(true);
        }  else if (event.getSource() == AW_Save_BT) {
            AW_InputWeight_VB.setVisible(false);
            AW_Button_VB.setVisible(true);
            SetTextfieldZero();
            addWeightToDb();
            loadDataAW_AR_Table();
        } else if (event.getSource() == AW_Update_BT) {
            AW_InputWeight_VB.setVisible(false);
            AW_Button_VB.setVisible(true);
            SetTextfieldZero();
            updateWeightToDb();
            loadDataAW_AR_Table();
        } else if (event.getSource() == AW_Cancel_BT) {
            AW_InputWeight_VB.setVisible(false);
            AW_Button_VB.setVisible(true);
        }else if (event.getSource() == AW_Delete_BT) {
            System.out.println("DELETE PRESSED");
                deleteWeightToDb();
                loadDataAW_AR_Table();
        }else if (event.getSource() == AW_Export_BT) {

        }else if (event.getSource() == AW_AllData_BT) {
            String query = "query";
            loadFromDB(query);
        }

    }

    private void loadDataAW_AR_Table(){

        //puts values in specific columns
        //use the same name that corresponds to the object class
        AW_AnimalID_T.setCellValueFactory(new PropertyValueFactory<>("animalId"));
        AW_AnimalName_T.setCellValueFactory(new PropertyValueFactory<>("animalName"));
        AW_AnimalBreed_T.setCellValueFactory(new PropertyValueFactory<>("breed"));
        AW_AnimalType_T.setCellValueFactory(new PropertyValueFactory<>("animalType"));

        //set data to table
        AW_ARTABLE_T.setItems(dbData);
    }

    //get weight from database
    int animalIDFromAWTable;
    @FXML
    public void onARAWMouseTableItemClick(MouseEvent event){
        if (event.getClickCount() == 1) { // Check if it's a single click
            AnimalRecords selectedRecord = AW_ARTABLE_T.getSelectionModel().getSelectedItem();
             animalIDFromAWTable = selectedRecord.getAnimalId();

            if (selectedRecord != null) {

                try {
                    dbObWeight = dbc.getAllWeightRecords(selectedRecord.getAnimalId());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                tableSelectedID = selectedRecord.getAnimalId();
                AW_AnimalID_T1.setCellValueFactory(new PropertyValueFactory<>("animalId"));
                AW_AnimalName_T1.setCellValueFactory(new PropertyValueFactory<>("animalName"));
                AW_AnimalBreed_T1.setCellValueFactory(new PropertyValueFactory<>("animalBreed"));
                AW_Weight_T1.setCellValueFactory(new PropertyValueFactory<>("weightKg"));
                AW_WeighDate_T1.setCellValueFactory(new PropertyValueFactory<>("weightDate"));
                AW_AgeAtWeight_T1.setCellValueFactory(new PropertyValueFactory<>("ageWeight"));
                AW_WeightNote_T1.setCellValueFactory(new PropertyValueFactory<>("weightNote"));

                //set data to table
                AW_WeightTable_T1.setItems(dbObWeight);
                initializeAWYearMonthCbox();
                initualizeAWChart(animalIDFromAWTable);



            }
        }
    }


    private void addWeightToDb(){
        System.out.println("SELECTED WEIGHT ID = "+animalIDFromAWTable);
        dbc.insertWeightRecord(animalIDFromAWTable,0, Double.parseDouble(AW_Weight_TF.getText()),AW_WgtDate_DP.getValue() != null ? Date.valueOf(AW_WgtDate_DP.getValue()) : null,
                "USUAL WEIGHING");

    }
    int weightIDFromTable, WeightIDFromAWTable;
    String weightNote;
    private void updateWeightToDb(){

        dbc.updateWeightRecord("Weight_Records",WeightIDFromAWTable,Double.parseDouble(AW_Weight_TF.getText()),AW_WgtDate_DP.getValue() != null ? Date.valueOf(AW_WgtDate_DP.getValue()) : null,
                weightNote);
    }

    private void deleteWeightToDb()  {

        try {
            dbc.deleteFromWeightRecords(WeightIDFromAWTable );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void onAWMouseTableItemClick(MouseEvent event) {
        if (event.getClickCount() == 1) { // Check if it's a single click
            WeightRecords selectedRecord = AW_WeightTable_T1.getSelectionModel().getSelectedItem();
            WeightIDFromAWTable = selectedRecord.getWeightId();

            weightNote = selectedRecord.getWeightNote();
            AW_Weight_TF.setText(String.valueOf(selectedRecord.getWeightKg()));

            if (selectedRecord.getWeightDate() != null) {
                Timestamp timestamp = (Timestamp) selectedRecord.getWeightDate();
                // Convert Timestamp to LocalDateTime
                LocalDateTime localDateTime = timestamp.toLocalDateTime();
                AW_WgtDate_DP.setValue(LocalDate.from(localDateTime));

            } else {
                AW_WgtDate_DP.setValue(null);
            }
        }
    }

    private void initualizeAWChart(int animalID){
        AW_WeightGraph_LC.getData().clear();
        try {
            // Define axes
            AWxAxis.setLabel("Weight Date");
            AWyAxis.setLabel("Weight");

            // Create the line chart
            AW_WeightGraph_LC.setTitle("Animal Weight Chart");

            // Create a series for the data
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Milk Production");

            // Populate the series with sample data
            for (WeightRecords AW : dbObWeight) {

                String wDate = String.valueOf(AW.getWeightDate());
                String mYear = String.valueOf(AW_WeighingYear_Cbox.getValue());
                int monthNumber = Month.valueOf(String.valueOf(AW_WeighingMonth_Cbox.getValue()).toUpperCase()).getValue();
                String wMonth = (monthNumber < 10) ? "0" + monthNumber : String.valueOf(monthNumber);
                String yearMonth = (mYear+"-"+wMonth);
                // Generate random milk production for each day (for demonstration purposes)
                if(animalID==AW.getAnimalId() && wDate.startsWith(yearMonth)){
                    double animalWeight = (AW.getWeightKg());
                    series.getData().add(new XYChart.Data<>(AW.getWeightDate().toString(), animalWeight));
                }
            }
            // Add series to chart
            AW_WeightGraph_LC.getData().add(series);


        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions here
        }
    }
    public void initializeAWYearMonthCbox() {

        int[] yearTemp = new int[dbObWeight.size()];
        int[] monthTemp = new int[dbObWeight.size()];
        int count = 0; // Declare count variable outside the loop

        for (WeightRecords M : dbObWeight) {
            yearTemp[count] = M.getWeightDate().getYear() + 1900;
            monthTemp[count] = M.getWeightDate().getMonth() + 1;
            //System.out.println(yearTemp[count]);
            count++;
        }
        // Extract distinct years
        ObservableList<Integer> distinctAWYears = Arrays.stream(yearTemp)
                .distinct()
                .mapToObj(num -> num)
                .sorted() // Optional: Sort the years
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        // Extract distinct Month
        ObservableList<String> distinctAWMonth = Arrays.stream(monthTemp)
                .distinct()
                .mapToObj(month -> Month.of(month).toString()) // Map month number to month name
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        // Assuming MR_MilkingMonth_Cbox is the ComboBox you want to populate
        AW_WeighingMonth_Cbox.setItems(distinctAWMonth);
        // Assuming comboBox is the ComboBox you want to populate
        AW_WeighingYear_Cbox.setItems(distinctAWYears);

        if(!distinctAWMonth.isEmpty() && !distinctAWYears.isEmpty()){
            AW_WeighingMonth_Cbox.setValue(distinctAWMonth.getLast());
            AW_WeighingYear_Cbox.setValue(distinctAWYears.getLast());
        }else{
            System.out.println("i want to set prompt");
            AW_WeighingMonth_Cbox.getItems().clear();
            AW_WeighingYear_Cbox.getItems().clear();
            AW_WeighingMonth_Cbox.setPromptText("Months");
            AW_WeighingYear_Cbox.setPromptText("Years");
        }
    }

    // Event handler for when both year and month are selected
    @FXML
    public void handleAWYearMonthSelection(ActionEvent event) {
        Integer selectedYear = AW_WeighingYear_Cbox.getValue();
        String selectedMonth = AW_WeighingMonth_Cbox.getValue();

        if (selectedYear != null && selectedMonth != null) {
            try{
                // Perform the action here
                System.out.println("Selected Year: " + selectedYear + ", Selected Month: " + selectedMonth);
                int monthNumber = Month.valueOf(selectedMonth.toUpperCase()).getValue();
                // Perform the action here
                System.out.println("Selected Year: " + selectedYear + ", Selected Month: " + monthNumber);
            }catch(Exception e){
                System.out.println("error occured");
            }

            initualizeAWChart(animalIDFromAWTable);
        }
    }
    /*----------------------------------------------------------------------------END-WEIGHT-RECORDS-----------------------------------------------------------------------------------------------*/




    /*----------------------------------------------------------------------------START-SALES-RECORDS-----------------------------------------------------------------------------------------------*/

    //MILK SALES
    @FXML
    private Button S_MilkAddNew_BT, S_MilkCancel_BT, S_MilkDelete_BT, S_MilkEdit_BT, S_MilkExport_BT, S_MilkSave_BT, S_MilkUpdate_BT, S_Milk_Load_BT;
    @FXML
    private TextField S_MilkClientEmail_TF, S_MilkClientName_TF, S_MilkClientPhone_TF, S_MilkQBought_TF, S_MilkTPayment_TF, S_MilkUnitPrice_TF,
            S_Milk_SoldMilkSearch_TF;
    @FXML
    private TableView<Sales> S_Milk_Table;
    @FXML
    private TableColumn<Sales, String> S_Milk_ClientEmail_TC, S_Milk_ClientName_TC, S_Milk_ClientPhone_TC, S_Milk_PDate_TC, S_Milk_UnitPrice_TC, S_Milk_QtyBought_TC, S_Milk_TotalPrice_TC;
    @FXML
    private DatePicker S_Milk_CDate_DP;
    @FXML
    private StackPane S_Milk_Add_Save_SP, S_Milk_Edit_Update_SP, S_Milk_Delete_Cancel_SP;
    private ObservableList<Sales> dbMilkSalesData;

    @FXML
    public void onS_Milk_BT_Click(ActionEvent event) throws SQLException {
        if (event.getSource() == S_MilkAddNew_BT) {
            S_Milk_Edit_Update_SP.setDisable(true);
            S_MilkSave_BT.setVisible(true);
            S_MilkAddNew_BT.setVisible(false);
            S_MilkDelete_BT.setVisible(false);
            S_MilkCancel_BT.setVisible(true);
            S_MilkExport_BT.setDisable(true);
        } else if (event.getSource() == S_MilkEdit_BT) {
            S_Milk_Add_Save_SP.setDisable(true);
            S_MilkUpdate_BT.setVisible(true);
            S_MilkEdit_BT.setVisible(false);
            S_MilkDelete_BT.setVisible(false);
            S_MilkCancel_BT.setVisible(true);
            S_MilkExport_BT.setDisable(true);
        } else if (event.getSource() == S_MilkSave_BT) {
            S_Milk_Edit_Update_SP.setDisable(false);
            S_MilkSave_BT.setVisible(false);
            S_MilkAddNew_BT.setVisible(true);
            S_MilkDelete_BT.setVisible(true);
            S_MilkCancel_BT.setVisible(false);
            S_MilkExport_BT.setDisable(false);
            SetTextfieldZero();
            insertMilkSalesData();
            loadDataS_Milk_Table();
        } else if (event.getSource() == S_MilkUpdate_BT) {
            S_Milk_Add_Save_SP.setDisable(false);
            S_MilkUpdate_BT.setVisible(false);
            S_MilkEdit_BT.setVisible(true);
            S_MilkDelete_BT.setVisible(true);
            S_MilkCancel_BT.setVisible(false);
            S_MilkExport_BT.setDisable(false);
            SetTextfieldZero();
            updateMilkSalesData();
            loadDataS_Milk_Table();
        } else if (event.getSource() == S_MilkCancel_BT) {
            S_Milk_Edit_Update_SP.setDisable(false);
            S_Milk_Add_Save_SP.setDisable(false);
            S_MilkSave_BT.setVisible(false);
            S_MilkAddNew_BT.setVisible(true);
            S_MilkUpdate_BT.setVisible(false);
            S_MilkEdit_BT.setVisible(true);
            S_MilkDelete_BT.setVisible(true);
            S_MilkCancel_BT.setVisible(false);
            S_MilkExport_BT.setDisable(false);

        } else if (event.getSource() == S_MilkDelete_BT) {
            DeleteMilkSales();
            loadDataS_Milk_Table();
        } else if (event.getSource() == S_MilkExport_BT) {

        } else if (event.getSource() == S_Milk_Load_BT) {
            loadDataS_Milk_Table();
        }
    }

    private void loadDataS_Milk_Table(){
        dbMilkSalesData.clear();
        DatabaseConnection db = new DatabaseConnection();
        dbMilkSalesData = db.getAllSales("Milk");
        S_Milk_ClientName_TC.setCellValueFactory(new PropertyValueFactory<>("clientFullName"));
        S_Milk_ClientEmail_TC.setCellValueFactory(new PropertyValueFactory<>("clientEmail"));
        S_Milk_ClientPhone_TC.setCellValueFactory(new PropertyValueFactory<>("clientPhone"));
        S_Milk_UnitPrice_TC.setCellValueFactory(new PropertyValueFactory<>("unitPriceLiveWeight"));
        S_Milk_QtyBought_TC.setCellValueFactory(new PropertyValueFactory<>("qtyBought"));
        S_Milk_TotalPrice_TC.setCellValueFactory(new PropertyValueFactory<>("totalBill"));
        S_Milk_PDate_TC.setCellValueFactory(new PropertyValueFactory<>("saleDate"));

        //set data to table
        S_Milk_Table.setItems(dbMilkSalesData);
        db.closeResources();
    }


    int MilkSaleIDFromAWTable;
    @FXML
    public void onSMouseTableItemClick(MouseEvent event){
        if (event.getClickCount() == 1) { // Check if it's a single click
            Sales selectedRecord = S_Milk_Table.getSelectionModel().getSelectedItem();
            MilkSaleIDFromAWTable = selectedRecord.getSaleID();

            if (selectedRecord != null) {
                S_MilkClientName_TF.setText(selectedRecord.getClientFullName());
                S_MilkClientEmail_TF.setText(selectedRecord.getClientEmail());
                S_MilkClientPhone_TF.setText(selectedRecord.getClientPhone());
                S_MilkQBought_TF.setText(String.valueOf(selectedRecord.getQtyBought()));
                S_MilkTPayment_TF.setText(String.valueOf(selectedRecord.getTotalBill()));
                // Assuming unit price is not stored in MilkSale object
                S_MilkUnitPrice_TF.setText(String.valueOf(selectedRecord.getUnitPriceLiveWeight()));

                if (selectedRecord.getSaleDate() != null) {
                    Date dateOfBirth = (Date) selectedRecord.getSaleDate();
                    LocalDate localDateOfBirth = dateOfBirth.toLocalDate();
                    S_Milk_CDate_DP.setValue(localDateOfBirth);

                } else {
                    S_Milk_CDate_DP.setValue(null);
                }
            } else {
                // Clear text fields if no selection
                S_MilkClientName_TF.clear();
                S_MilkClientEmail_TF.clear();
                S_MilkClientPhone_TF.clear();
                S_MilkQBought_TF.clear();
                S_MilkTPayment_TF.clear();
                S_MilkUnitPrice_TF.clear();
            }
        }
    }

    private void insertMilkSalesData(){
        dbc.insertSalesData(0,"Milk", "null", "null", S_Milk_CDate_DP.getValue()!= null ? Date.valueOf(S_Milk_CDate_DP.getValue()) : null,
                            Double.parseDouble(S_MilkUnitPrice_TF.getText()),0.0, Double.parseDouble(S_MilkQBought_TF.getText()),Double.parseDouble(S_MilkTPayment_TF.getText()),
                            S_MilkClientName_TF.getText(),S_MilkClientEmail_TF.getText(), S_MilkClientPhone_TF.getText(), 1);

    }
    private void updateMilkSalesData(){
        dbc.updateToSale(0,"Milk", "null", "null", S_Milk_CDate_DP.getValue()!= null ? Date.valueOf(S_Milk_CDate_DP.getValue()) : null,
                          Double.parseDouble(S_MilkUnitPrice_TF.getText()), 0.0, Double.parseDouble(S_MilkQBought_TF.getText()), Double.parseDouble(S_MilkTPayment_TF.getText()),
                          S_MilkClientName_TF.getText(), S_MilkClientEmail_TF.getText(), S_MilkClientPhone_TF.getText(),1, MilkSaleIDFromAWTable);
    }
    private void DeleteMilkSales(){
        dbc.deleteFromSales(MilkSaleIDFromAWTable);
        loadDataS_Milk_Table();
    }




    // LIVESTOCK SALES
    @FXML
    private Button S_LS_LoadAnimals_BT, S_LS_LoadSold_BT, S_LS_AddNew_BT, S_LS_Cancel_BT, S_LS_Delete_BT, S_LS_Edit_BT, S_LS_Export_BT, S_LS_Save_BT, S_LS_Update_BT ;
    @FXML
    private TextField S_LS_AnimalID_TF, S_LS_AnimalBreed_TF, S_LS_AnimalSearch_TF, S_LS_AnimalWeight_TF, S_LS_ClientEmail_TF, S_LS_ClientName_TF,
            S_LS_ClientPhone_TF, S_LS_PriceLiveWeight_TF, S_LS_SoldAnimalSearch_TF, S_LS_TotalPrice_TF;
    @FXML
    private TableView<AnimalRecords> S_LS_AnimalTable;
    @FXML
    private TableColumn<AnimalRecords, String> S_LS_AnimalID_TC, S_LS_AnimalBreed_TC, S_LS_AnimalType_TC, S_LS_AnimalName_TC;
    @FXML
    private TableView<Sales> S_LS_AnimalSalesTable;
    @FXML
    private TableColumn<Sales, String> S_LS_ClientEmail_TC, S_LS_ClientName_TC, S_LS_ClientPhone_TC, S_LS_UnitPrice_TC, S_LS_QtyBought_TC, S_LS_PriceSold_TC, S_LS_PurchaseDate_TC, S_LS_SoldAnimalID_TC;
    @FXML
    private DatePicker S_LS_CDate_DP;

    @FXML
    private StackPane S_LS_Add_Save_SP, S_LS_Edit_Update_SP, S_LS_Delete_Cancel_SP;
    private ObservableList<Sales> dbLivestockSalesData;

    @FXML
    public void onS_LS_BT_Click(ActionEvent event) {
        if (event.getSource() == S_LS_AddNew_BT) {
            S_LS_Edit_Update_SP.setDisable(true);
            S_LS_Save_BT.setVisible(true);
            S_LS_AddNew_BT.setVisible(false);
            S_LS_Delete_BT.setVisible(false);
            S_LS_Cancel_BT.setVisible(true);
            S_LS_Export_BT.setDisable(true);
        } else if (event.getSource() == S_LS_Edit_BT) {
            S_LS_Add_Save_SP.setDisable(true);
            S_LS_Update_BT.setVisible(true);
            S_LS_Edit_BT.setVisible(false);
            S_LS_Delete_BT.setVisible(false);
            S_LS_Cancel_BT.setVisible(true);
            S_LS_Export_BT.setDisable(true);
        } else if (event.getSource() == S_LS_Save_BT) {
            S_LS_Edit_Update_SP.setDisable(false);
            S_LS_Save_BT.setVisible(false);
            S_LS_AddNew_BT.setVisible(true);
            S_LS_Delete_BT.setVisible(true);
            S_LS_Cancel_BT.setVisible(false);
            S_LS_Export_BT.setDisable(false);
            SetTextfieldZero();
            insertLivestockSalesData();
            loadDataS_LivestockSales_Table();
        } else if (event.getSource() == S_LS_Update_BT) {
            S_LS_Add_Save_SP.setDisable(false);
            S_LS_Update_BT.setVisible(false);
            S_LS_Edit_BT.setVisible(true);
            S_LS_Delete_BT.setVisible(true);
            S_LS_Cancel_BT.setVisible(false);
            S_LS_Export_BT.setDisable(false);
            SetTextfieldZero();
            updateLivestockSalesData();
            loadDataS_LivestockSales_Table();
        } else if (event.getSource() == S_LS_Cancel_BT) {
            S_LS_Edit_Update_SP.setDisable(false);
            S_LS_Add_Save_SP.setDisable(false);
            S_LS_Save_BT.setVisible(false);
            S_LS_AddNew_BT.setVisible(true);
            S_LS_Update_BT.setVisible(false);
            S_LS_Edit_BT.setVisible(true);
            S_LS_Delete_BT.setVisible(true);
            S_LS_Cancel_BT.setVisible(false);
            S_LS_Export_BT.setDisable(false);

        } else if (event.getSource() == S_LS_Delete_BT) {
            DeleteLivestockSalesSales();
            loadDataS_LivestockSales_Table();
        } else if (event.getSource() == S_LS_Export_BT) {

        } else if (event.getSource() == S_LS_LoadAnimals_BT) {

        } else if (event.getSource() == S_LS_LoadSold_BT) {
            loadDataS_LivestockSales_Table();
        }
    }

    private void loadDataS_AR_Table(){

        //puts values in specific columns
        //use the same name that corresponds to the object class
        S_LS_AnimalID_TC.setCellValueFactory(new PropertyValueFactory<>("animalId"));
        S_LS_AnimalName_TC.setCellValueFactory(new PropertyValueFactory<>("animalName"));
        S_LS_AnimalBreed_TC.setCellValueFactory(new PropertyValueFactory<>("breed"));
        S_LS_AnimalType_TC.setCellValueFactory(new PropertyValueFactory<>("animalType"));

        //set data to table
        S_LS_AnimalTable.setItems(dbData);
    }


    String animalBreedFromSTable;
    @FXML
    public void onARSMouseTableItemClick(MouseEvent event){
        if (event.getClickCount() == 1) { // Check if it's a single click
            AnimalRecords selectedRecord = S_LS_AnimalTable.getSelectionModel().getSelectedItem();
            animalBreedFromSTable = selectedRecord.getBreed();
            if (selectedRecord != null) {
                S_LS_AnimalID_TF.setText(String.valueOf(selectedRecord.getAnimalId()));
                S_LS_AnimalBreed_TF.setText(String.valueOf(selectedRecord.getBreed()));
            }
        }
    }

    private void loadDataS_LivestockSales_Table(){
        dbLivestockSalesData.clear();
        DatabaseConnection db = new DatabaseConnection();
        dbLivestockSalesData = db.getAllSales("Livestock");

        S_LS_ClientName_TC.setCellValueFactory(new PropertyValueFactory<>("clientFullName"));
        S_LS_ClientEmail_TC.setCellValueFactory(new PropertyValueFactory<>("clientEmail"));
        S_LS_ClientPhone_TC.setCellValueFactory(new PropertyValueFactory<>("clientPhone"));
        S_LS_UnitPrice_TC.setCellValueFactory(new PropertyValueFactory<>("unitPriceLiveWeight"));
        S_LS_QtyBought_TC.setCellValueFactory(new PropertyValueFactory<>("sellingWeight"));
        S_LS_PriceSold_TC.setCellValueFactory(new PropertyValueFactory<>("totalBill"));
        S_LS_SoldAnimalID_TC.setCellValueFactory(new PropertyValueFactory<>("productID"));
        S_LS_PurchaseDate_TC.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
        //set data to table
        S_LS_AnimalSalesTable.setItems(dbLivestockSalesData);
        db.closeResources();
    }


    int livestockSaleIDFromLVTable;
    @FXML
    public void onSLSMouseTableItemClick(MouseEvent event){
        if (event.getClickCount() == 1) { // Check if it's a single click
            Sales selectedRecord = S_LS_AnimalSalesTable.getSelectionModel().getSelectedItem();
            livestockSaleIDFromLVTable = selectedRecord.getSaleID();

            if (selectedRecord != null) {
                S_LS_ClientName_TF.setText(selectedRecord.getClientFullName());
                S_LS_ClientEmail_TF.setText(selectedRecord.getClientEmail());
                S_LS_ClientPhone_TF.setText(selectedRecord.getClientPhone());
                S_LS_AnimalID_TF.setText(String.valueOf(selectedRecord.getProductID()));
                S_LS_AnimalBreed_TF.setText(selectedRecord.getAnimalBreed());
                S_LS_AnimalWeight_TF.setText(String.valueOf(selectedRecord.getSellingWeight()));
                S_LS_TotalPrice_TF.setText(String.valueOf(selectedRecord.getTotalBill()));
                S_LS_PriceLiveWeight_TF.setText(String.valueOf(selectedRecord.getUnitPriceLiveWeight() ));

                if (selectedRecord.getSaleDate() != null) {
                    Date dateOfBirth = (Date) selectedRecord.getSaleDate();
                    LocalDate localDateOfBirth = dateOfBirth.toLocalDate();
                    S_LS_CDate_DP.setValue(localDateOfBirth);

                } else {
                    S_BP_CDate_DP.setValue(null);
                }
            } else {
                // Clear text fields if no selection
                S_BP_ClientName_TF.clear();
                S_BP_ClientEmail_TF.clear();
                S_BP_ClientPhone_TF.clear();
                S_BP_ProductName_TF.clear();
                S_BP_QBought_TF.clear();
                S_BP_TotalPayment_TF.clear();
                S_BP_Infor_TA.clear();
                S_BP_PPUnit_TF.clear();
            }
        }
    }

    private void insertLivestockSalesData(){
        dbc.insertSalesData((int) Double.parseDouble(S_LS_AnimalID_TF.getText()),"CATTLE","null",animalBreedFromSTable, S_LS_CDate_DP.getValue()!= null ? Date.valueOf(S_LS_CDate_DP.getValue()) : null, Double.parseDouble(S_LS_PriceLiveWeight_TF.getText()),
                        Double.parseDouble(S_LS_AnimalWeight_TF.getText()),0.0, Double.parseDouble(S_LS_TotalPrice_TF.getText()), S_LS_ClientName_TF.getText(), S_LS_ClientEmail_TF.getText(), S_LS_ClientPhone_TF.getText(),2);
    }
    private void updateLivestockSalesData(){
        dbc.updateToSale((int) Double.parseDouble(S_LS_AnimalID_TF.getText()),"CATTLE","NULL",animalBreedFromSTable, S_LS_CDate_DP.getValue()!= null ? Date.valueOf(S_LS_CDate_DP.getValue()) : null, Double.parseDouble(S_LS_PriceLiveWeight_TF.getText()),
                Double.parseDouble(S_LS_AnimalWeight_TF.getText()),0.0, Double.parseDouble(S_LS_TotalPrice_TF.getText()), S_LS_ClientName_TF.getText(), S_LS_ClientEmail_TF.getText(), S_LS_ClientPhone_TF.getText(),2,livestockSaleIDFromLVTable);

    }

    private void DeleteLivestockSalesSales(){
        dbc.deleteFromSales(livestockSaleIDFromLVTable);
        loadDataS_LivestockSales_Table();
    }


    //BY PRODUCTS SALES
    @FXML
    private Button S_BPAddNew_BT, S_BPCancel_BT, S_BPDelete_BT, S_BPEdit_BT, S_BPExport_BT, S_BPSave_BT, S_BPUpdate_BT, S_BP_Load_BT;
    @FXML
    private TextField S_BP_ProductName_TF, S_BP_ByProductSearch_TF, S_BP_ClientEmail_TF, S_BP_ClientName_TF,
                      S_BP_ClientPhone_TF, S_BP_PPUnit_TF, S_BP_QBought_TF, S_BP_TotalPayment_TF;
    @FXML
    private TextArea S_BP_Infor_TA;
    @FXML
    private TableView<Sales> S_BP_ByProductTable;
    @FXML
    private TableColumn<Sales, String> S_BP_ClientEmail_TC, S_BP_ClientName_TC, S_BP_ClientPhone_TC, S_BP_ProductName_TC, S_BP_PurchaseDate_TC,
                              S_BP_QtyBought_TC, S_BP_TotalPay_TC,S_BP_UnitPrice_TC;
    @FXML
    private DatePicker S_BP_CDate_DP;

    @FXML
    private StackPane S_BP_Add_Save_SP, S_BP_Edit_Update_SP, S_BP_Delete_Cancel_SP;
    private ObservableList<Sales> dbByProductSalesData;

    @FXML
    public void onS_BP_BT_Click(ActionEvent event) throws SQLException {
        if (event.getSource() == S_BPAddNew_BT) {
            S_BP_Edit_Update_SP.setDisable(true);
            S_BPSave_BT.setVisible(true);
            S_BPAddNew_BT.setVisible(false);
            S_BPDelete_BT.setVisible(false);
            S_BPCancel_BT.setVisible(true);
            S_BPExport_BT.setDisable(true);
        } else if (event.getSource() == S_BPEdit_BT) {
            S_BP_Add_Save_SP.setDisable(true);
            S_BPUpdate_BT.setVisible(true);
            S_BPEdit_BT.setVisible(false);
            S_BPDelete_BT.setVisible(false);
            S_BPCancel_BT.setVisible(true);
            S_BPExport_BT.setDisable(true);
        } else if (event.getSource() == S_BPSave_BT) {
            S_BP_Edit_Update_SP.setDisable(false);
            S_BPSave_BT.setVisible(false);
            S_BPAddNew_BT.setVisible(true);
            S_BPDelete_BT.setVisible(true);
            S_BPCancel_BT.setVisible(false);
            S_BPExport_BT.setDisable(false);
            SetTextfieldZero();
            insertByProductSalesData();
            loadDataS_ByProducts_Table();
        } else if (event.getSource() == S_BPUpdate_BT) {
            S_BP_Add_Save_SP.setDisable(false);
            S_BPUpdate_BT.setVisible(false);
            S_BPEdit_BT.setVisible(true);
            S_BPDelete_BT.setVisible(true);
            S_BPCancel_BT.setVisible(false);
            S_BPExport_BT.setDisable(false);
            SetTextfieldZero();
            updateByProductSalesData();
            loadDataS_ByProducts_Table();
        } else if (event.getSource() == S_BPCancel_BT) {
            S_BP_Edit_Update_SP.setDisable(false);
            S_BP_Add_Save_SP.setDisable(false);
            S_BPSave_BT.setVisible(false);
            S_BPAddNew_BT.setVisible(true);
            S_BPUpdate_BT.setVisible(false);
            S_BPEdit_BT.setVisible(true);
            S_BPDelete_BT.setVisible(true);
            S_BPCancel_BT.setVisible(false);
            S_BPExport_BT.setDisable(false);

        } else if (event.getSource() == S_BPDelete_BT) {
            DeleteByProductSales();
            loadDataS_ByProducts_Table();
        } else if (event.getSource() == S_BPExport_BT) {

        } else if (event.getSource() == S_BP_Load_BT) {
            loadDataS_ByProducts_Table();
        }
    }
    private void insertByProductSalesData(){
        dbc.insertSalesData(0, S_BP_ProductName_TF.getText(), S_BP_Infor_TA.getText(), "null", S_BP_CDate_DP.getValue()!= null ? Date.valueOf(S_BP_CDate_DP.getValue()) : null,
                            Double.parseDouble(S_BP_PPUnit_TF.getText()),0.0, Double.parseDouble(S_BP_QBought_TF.getText()),Double.parseDouble(S_BP_TotalPayment_TF.getText()),
                            S_BP_ClientName_TF.getText(), S_BP_ClientEmail_TF.getText(), S_BP_ClientPhone_TF.getText(), 3);
    }

    private void loadDataS_ByProducts_Table(){
        dbByProductSalesData.clear();
        DatabaseConnection db = new DatabaseConnection();
        dbByProductSalesData = db.getAllSales("ByProduct");
        S_BP_ClientName_TC.setCellValueFactory(new PropertyValueFactory<>("clientFullName"));
        S_BP_ClientEmail_TC.setCellValueFactory(new PropertyValueFactory<>("clientEmail"));
        S_BP_ClientPhone_TC.setCellValueFactory(new PropertyValueFactory<>("clientPhone"));
        S_BP_ProductName_TC.setCellValueFactory(new PropertyValueFactory<>("ProductName"));
        S_BP_UnitPrice_TC.setCellValueFactory(new PropertyValueFactory<>("unitPriceLiveWeight"));
        S_BP_QtyBought_TC.setCellValueFactory(new PropertyValueFactory<>("qtyBought"));
        S_BP_TotalPay_TC.setCellValueFactory(new PropertyValueFactory<>("totalBill"));
        S_BP_PurchaseDate_TC.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
        //set data to table
        S_BP_ByProductTable.setItems(dbByProductSalesData);
        db.closeResources();
    }

    int ByprodyctIDFromAWTable;
    @FXML
    public void onSBPMouseTableItemClick(MouseEvent event){
        if (event.getClickCount() == 1) { // Check if it's a single click
            Sales selectedRecord = S_BP_ByProductTable.getSelectionModel().getSelectedItem();
            ByprodyctIDFromAWTable = selectedRecord.getSaleID();

            if (selectedRecord != null) {

                S_BP_ClientName_TF.setText(selectedRecord.getClientFullName());
                S_BP_ClientEmail_TF.setText(selectedRecord.getClientEmail());
                S_BP_ClientPhone_TF.setText(selectedRecord.getClientPhone());
                S_BP_ProductName_TF.setText(selectedRecord.getProductName());
                S_BP_QBought_TF.setText(String.valueOf(selectedRecord.getQtyBought()));
                S_BP_TotalPayment_TF.setText(String.valueOf(selectedRecord.getTotalBill()));
                S_BP_Infor_TA.setText(selectedRecord.getProductInfor());
                // Assuming unit price is not stored in MilkSale object
                S_BP_PPUnit_TF.setText(String.valueOf(selectedRecord.getUnitPriceLiveWeight() ));

                if (selectedRecord.getSaleDate() != null) {
                    Date dateOfBirth = (Date) selectedRecord.getSaleDate();
                    LocalDate localDateOfBirth = dateOfBirth.toLocalDate();
                    S_BP_CDate_DP.setValue(localDateOfBirth);

                } else {
                    S_BP_CDate_DP.setValue(null);
                }
            } else {
                // Clear text fields if no selection
                S_BP_ClientName_TF.clear();
                S_BP_ClientEmail_TF.clear();
                S_BP_ClientPhone_TF.clear();
                S_BP_ProductName_TF.clear();
                S_BP_QBought_TF.clear();
                S_BP_TotalPayment_TF.clear();
                S_BP_Infor_TA.clear();
                S_BP_PPUnit_TF.clear();
            }
        }
    }
    private void updateByProductSalesData(){
        dbc.updateToSale(0, S_BP_ProductName_TF.getText(), S_BP_Infor_TA.getText(), "null", S_BP_CDate_DP.getValue()!= null ? Date.valueOf(S_BP_CDate_DP.getValue()) : null,
                Double.parseDouble(S_BP_PPUnit_TF.getText()),0.0, Double.parseDouble(S_BP_QBought_TF.getText()), Double.parseDouble(S_BP_TotalPayment_TF.getText()),
                S_BP_ClientName_TF.getText(), S_BP_ClientEmail_TF.getText(), S_BP_ClientPhone_TF.getText(), 3, ByprodyctIDFromAWTable);
    }
    private void DeleteByProductSales(){
        dbc.deleteFromSales(ByprodyctIDFromAWTable);
        loadDataS_ByProducts_Table();
    }

    /*-----------------------------------------------------------------------------END-SALES-RECORDS-----------------------------------------------------------------------------------------------*/


    /*-----------------------------------------------------------------------------START-ADMIN-RECORDS-----------------------------------------------------------------------------------------------*/
    //admin section
    @FXML
    private Button AD_AddNew_BT, AD_Save_BT, AD_Edit_BT, AD_Update_BT, AD_Cancel_BT, AD_Delete_BT, AD_Export_BT, AD_LoadData_BT;
    @FXML
    private TextField AD_AdminFirstName_TF, AD_AdminLastName_TF, AD_AdminUserName_TF, AD_AdminPassword_TF, AD_AdminEmail_TF;
    @FXML
    private ComboBox<String> AD_Manage_CB, AD_AdminAR_CB, AD_AdminFF_CB, AD_AdminHR_CB, AD_AdminMR_CB, AD_AdminSF_CB, AD_AdminS_CB, AD_AdminAW_CB, AD_AdminB_CB,
                        AD_AdminAddAR_CB, AD_AdminAddAW_CB, AD_AdminAddB_CB, AD_AdminAddFF_CB, AD_AdminAddHR_CB, AD_AdminAddMR_CB, AD_AdminAddSF_CB,
                        AD_AdminAddS_CB, AD_AdminDeleteAR_CB, AD_AdminDeleteAW_CB, AD_AdminDeleteB_CB, AD_AdminDeleteFF_CB, AD_AdminDeleteHR_CB,
                        AD_AdminAddLSS_CB, AD_AdminAddBPS_CB,AD_AdminDeleteMR_CB, AD_AdminDeleteSF_CB, AD_AdminDeleteS_CB, AD_AdminDeleteLSS_CB,
                        AD_AdminDeleteBPS_CB, AD_AdminEditAR_CB, AD_AdminEditAW_CB, AD_AdminEditFF_CB, AD_AdminEditHR_CB, AD_AdminEditMR_CB,
                        AD_AdminEditSF_CB, AD_AdminEditB_CB, AD_AdminEditS_CB, AD_AdminEditLSS_CB, AD_AdminEditBPS_CB;



    @FXML
    private TableView<AdminDetails> AD_Admin_T;
    @FXML
    private TableColumn<AdminDetails, String> AD_AdminID_TC, AD_AdminFirstName_TC, AD_AdminLastName_TC, AD_AdminUserName_TC, AD_AdminPassword_TC, AD_AdminEmail_TC;
    //User section
    @FXML
    private TableView<AdminDetails> AD_Worker_T;
    @FXML
    private TableColumn<AdminDetails, String> AD_WorkerID_TC, AD_WorkerFirstName_TC, AD_WorkerLastName_TC, AD_WorkerUserName_TC, AD_WorkerPassword_TC, AD_WorkerEmail_TC;
    @FXML
    private StackPane AD_Add_Save_SP, AD_Delete_Cancel_SP, AD_Edit_Update_SP;
    @FXML
    private StackPane AD_Admin_User_SP;
    private ObservableList<AdminDetails> dbUserDetails;

    @FXML
    public void onAD_BT_Click(ActionEvent event) {
        if (event.getSource() == AD_AddNew_BT) {
            AD_Edit_Update_SP.setDisable(true);
            AD_Save_BT.setVisible(true);
            AD_AddNew_BT.setVisible(false);
            AD_Delete_BT.setVisible(false);
            AD_Cancel_BT.setVisible(true);
        } else if (event.getSource() == AD_Edit_BT) {
            AD_Add_Save_SP.setDisable(true);
            AD_Update_BT.setVisible(true);
            AD_Edit_BT.setVisible(false);
            AD_Delete_BT.setVisible(false);
            AD_Cancel_BT.setVisible(true);
        } else if (event.getSource() == AD_Save_BT) {
            AD_Edit_Update_SP.setDisable(false);
            AD_Save_BT.setVisible(false);
            AD_AddNew_BT.setVisible(true);
            AD_Delete_BT.setVisible(true);
            AD_Cancel_BT.setVisible(false);
            String role = AD_Manage_CB.getValue();
            if(role.equals("ADMIN")){
                insertNewUser(role);
                loadDataAD_Admin_Table();
            }else{
                insertNewUser(role);
                loadDataAD_Worker_Table();
            }
        } else if (event.getSource() == AD_Update_BT) {
            AD_Add_Save_SP.setDisable(false);
            AD_Update_BT.setVisible(false);
            AD_Edit_BT.setVisible(true);
            AD_Delete_BT.setVisible(true);
            AD_Cancel_BT.setVisible(false);

            String role = AD_Manage_CB.getValue();
            if(role.equals("ADMIN")){
                updateUserDetails(role);
                loadDataAD_Admin_Table();
            }else{
                updateUserDetails(role);
                loadDataAD_Worker_Table();
            }
        } else if (event.getSource() == AD_Cancel_BT) {
            AD_Edit_Update_SP.setDisable(false);
            AD_Add_Save_SP.setDisable(false);
            AD_Save_BT.setVisible(false);
            AD_AddNew_BT.setVisible(true);
            AD_Update_BT.setVisible(false);
            AD_Edit_BT.setVisible(true);
            AD_Delete_BT.setVisible(true);
            AD_Cancel_BT.setVisible(false);

        } else if (event.getSource() == AD_Delete_BT) {
            if(AD_Manage_CB.getValue().equals("ADMIN")){
                DeleteAdmin(AD_Manage_CB.getValue());
                loadDataAD_Admin_Table();
            }else{
                DeleteAdmin(AD_Manage_CB.getValue());
                loadDataAD_Worker_Table();
            }
        } else if (event.getSource() == AD_Export_BT) {

        } else if (event.getSource() == AD_LoadData_BT) {
            if(AD_Manage_CB.getValue().equals("ADMIN")){
                loadDataAD_Admin_Table();
            }else{
                loadDataAD_Worker_Table();
            }
        }
    }
    private void insertNewUser(String role){
        dbc.insertUserDetails(role,
                AD_AdminFirstName_TF.getText(), AD_AdminLastName_TF.getText(), AD_AdminUserName_TF.getText(), AD_AdminPassword_TF.getText(), AD_AdminEmail_TF.getText(),
                AD_AdminAR_CB.getValue(), AD_AdminFF_CB.getValue(), AD_AdminHR_CB.getValue(), AD_AdminMR_CB.getValue(), AD_AdminSF_CB.getValue(), AD_AdminS_CB.getValue(),
                AD_AdminAW_CB.getValue(), AD_AdminB_CB.getValue(), AD_AdminAddAR_CB.getValue(), AD_AdminAddAW_CB.getValue(), AD_AdminAddB_CB.getValue(), AD_AdminAddFF_CB.getValue(),
                AD_AdminAddHR_CB.getValue(), AD_AdminAddMR_CB.getValue(), AD_AdminAddSF_CB.getValue(), AD_AdminAddS_CB.getValue(), AD_AdminDeleteAR_CB.getValue(), AD_AdminDeleteAW_CB.getValue(),
                AD_AdminDeleteB_CB.getValue(), AD_AdminDeleteFF_CB.getValue(), AD_AdminDeleteHR_CB.getValue(), AD_AdminDeleteMR_CB.getValue(), AD_AdminDeleteSF_CB.getValue(), AD_AdminDeleteS_CB.getValue(),
                AD_AdminEditAR_CB.getValue(), AD_AdminEditAW_CB.getValue(), AD_AdminEditFF_CB.getValue(), AD_AdminEditHR_CB.getValue(), AD_AdminEditMR_CB.getValue(), AD_AdminEditSF_CB.getValue(),
                AD_AdminEditB_CB.getValue(), AD_AdminEditS_CB.getValue(),AD_AdminAddLSS_CB.getValue(), AD_AdminAddBPS_CB.getValue(),AD_AdminEditLSS_CB.getValue(), AD_AdminEditBPS_CB.getValue(),
                AD_AdminDeleteLSS_CB.getValue(), AD_AdminDeleteBPS_CB.getValue()
        );
    }

    private void loadDataAD_Admin_Table(){
        dbUserDetails.clear();
        dbUserDetails = dbc.getAllUserDetails("ADMIN");
        AD_AdminID_TC.setCellValueFactory(new PropertyValueFactory<>("id"));
        AD_AdminFirstName_TC.setCellValueFactory(new PropertyValueFactory<>("first_name"));
        AD_AdminLastName_TC.setCellValueFactory(new PropertyValueFactory<>("last_name"));
        AD_AdminUserName_TC.setCellValueFactory(new PropertyValueFactory<>("username"));
        AD_AdminPassword_TC.setCellValueFactory(new PropertyValueFactory<>("password"));
        AD_AdminEmail_TC.setCellValueFactory(new PropertyValueFactory<>("email"));
        //set data to table
        AD_Admin_T.setItems(dbUserDetails);
    }

    private void loadDataAD_Worker_Table(){
        dbUserDetails.clear();
        dbUserDetails = dbc.getAllUserDetails("WORKER");
        AD_WorkerID_TC.setCellValueFactory(new PropertyValueFactory<>("id"));
        AD_WorkerFirstName_TC.setCellValueFactory(new PropertyValueFactory<>("first_name"));
        AD_WorkerLastName_TC.setCellValueFactory(new PropertyValueFactory<>("last_name"));
        AD_WorkerUserName_TC.setCellValueFactory(new PropertyValueFactory<>("username"));
        AD_WorkerPassword_TC.setCellValueFactory(new PropertyValueFactory<>("password"));
        AD_WorkerEmail_TC.setCellValueFactory(new PropertyValueFactory<>("email"));
        //set data to table
        AD_Worker_T.setItems(dbUserDetails);
    }



    int adminIDFromAWTable;
    @FXML
    public void onADadminMouseTableItemClick(MouseEvent event){
        if (event.getClickCount() == 1) { // Check if it's a single click
            AdminDetails selectedRecord;
            if(AD_Manage_CB.getValue().equals("ADMIN")){
                 selectedRecord = AD_Admin_T.getSelectionModel().getSelectedItem();
            }else{
                 selectedRecord = AD_Worker_T.getSelectionModel().getSelectedItem();
            }

            adminIDFromAWTable = selectedRecord.getId();
            if (selectedRecord != null) {
                AD_AdminFirstName_TF.setText(selectedRecord.getFirst_name());
                AD_AdminLastName_TF.setText(selectedRecord.getLast_name());
                AD_AdminUserName_TF.setText(selectedRecord.getUsername());
                AD_AdminPassword_TF.setText(selectedRecord.getPassword());
                AD_AdminEmail_TF.setText(selectedRecord.getEmail());

                // Set ComboBox texts
                AD_AdminAR_CB.setValue(selectedRecord.getAdminAR());
                AD_AdminFF_CB.setValue(selectedRecord.getAdminFF());
                AD_AdminHR_CB.setValue(selectedRecord.getAdminHR());
                AD_AdminMR_CB.setValue(selectedRecord.getAdminMR());
                AD_AdminSF_CB.setValue(selectedRecord.getAdminSF());
                AD_AdminS_CB.setValue(selectedRecord.getAdminS());
                AD_AdminAW_CB.setValue(selectedRecord.getAdminAW());
                AD_AdminB_CB.setValue(selectedRecord.getAdminB());
                AD_AdminAddAR_CB.setValue(selectedRecord.getAdminAddAR());
                AD_AdminAddAW_CB.setValue(selectedRecord.getAdminAddAW());
                AD_AdminAddB_CB.setValue(selectedRecord.getAdminAddB());
                AD_AdminAddFF_CB.setValue(selectedRecord.getAdminAddFF());
                AD_AdminAddHR_CB.setValue(selectedRecord.getAdminAddHR());
                AD_AdminAddMR_CB.setValue(selectedRecord.getAdminAddMR());
                AD_AdminAddSF_CB.setValue(selectedRecord.getAdminAddSF());
                AD_AdminAddS_CB.setValue(selectedRecord.getAdminAddS());
                AD_AdminDeleteAR_CB.setValue(selectedRecord.getAdminDeleteAR());
                AD_AdminDeleteAW_CB.setValue(selectedRecord.getAdminDeleteAW());
                AD_AdminDeleteB_CB.setValue(selectedRecord.getAdminDeleteB());
                AD_AdminDeleteFF_CB.setValue(selectedRecord.getAdminDeleteFF());
                AD_AdminDeleteHR_CB.setValue(selectedRecord.getAdminDeleteHR());
                AD_AdminDeleteMR_CB.setValue(selectedRecord.getAdminDeleteMR());
                AD_AdminDeleteSF_CB.setValue(selectedRecord.getAdminDeleteSF());
                AD_AdminDeleteS_CB.setValue(selectedRecord.getAdminDeleteS());
                AD_AdminEditAR_CB.setValue(selectedRecord.getAdminEditAR());
                AD_AdminEditAW_CB.setValue(selectedRecord.getAdminEditAW());
                AD_AdminEditFF_CB.setValue(selectedRecord.getAdminEditFF());
                AD_AdminEditHR_CB.setValue(selectedRecord.getAdminEditHR());
                AD_AdminEditMR_CB.setValue(selectedRecord.getAdminEditMR());
                AD_AdminEditSF_CB.setValue(selectedRecord.getAdminEditSF());
                AD_AdminEditB_CB.setValue(selectedRecord.getAdminEditB());
                AD_AdminEditS_CB.setValue(selectedRecord.getAdminEditS());
                AD_AdminAddLSS_CB.setValue(selectedRecord.getAdminAddLSS());
                AD_AdminAddBPS_CB.setValue(selectedRecord.getAdminAddBPS());
                AD_AdminDeleteLSS_CB.setValue(selectedRecord.getAdminDeleteLSS());
                AD_AdminDeleteBPS_CB.setValue(selectedRecord.getAdminDeleteBPS());
                AD_AdminEditLSS_CB.setValue(selectedRecord.getAdminEditLSS());
                AD_AdminEditBPS_CB.setValue(selectedRecord.getAdminEditBPS());
            } else {
                // Clear text fields if no selection
                AD_AdminFirstName_TF.clear();
                AD_AdminLastName_TF.clear();
                AD_AdminUserName_TF.clear();
                AD_AdminPassword_TF.clear();
                AD_AdminEmail_TF.clear();
                // Clear ComboBoxes if no selection
                AD_AdminAR_CB.setValue(null);
                AD_AdminFF_CB.setValue(null);
                AD_AdminHR_CB.setValue(null);
                AD_AdminMR_CB.setValue(null);
                AD_AdminSF_CB.setValue(null);
                AD_AdminS_CB.setValue(null);
                AD_AdminAW_CB.setValue(null);
                AD_AdminB_CB.setValue(null);
                AD_AdminAddAR_CB.setValue(null);
                AD_AdminAddAW_CB.setValue(null);
                AD_AdminAddB_CB.setValue(null);
                AD_AdminAddFF_CB.setValue(null);
                AD_AdminAddHR_CB.setValue(null);
                AD_AdminAddMR_CB.setValue(null);
                AD_AdminAddSF_CB.setValue(null);
                AD_AdminAddS_CB.setValue(null);
                AD_AdminDeleteAR_CB.setValue(null);
                AD_AdminDeleteAW_CB.setValue(null);
                AD_AdminDeleteB_CB.setValue(null);
                AD_AdminDeleteFF_CB.setValue(null);
                AD_AdminDeleteHR_CB.setValue(null);
                AD_AdminDeleteMR_CB.setValue(null);
                AD_AdminDeleteSF_CB.setValue(null);
                AD_AdminDeleteS_CB.setValue(null);
                AD_AdminEditAR_CB.setValue(null);
                AD_AdminEditAW_CB.setValue(null);
                AD_AdminEditFF_CB.setValue(null);
                AD_AdminEditHR_CB.setValue(null);
                AD_AdminEditMR_CB.setValue(null);
                AD_AdminEditSF_CB.setValue(null);
                AD_AdminEditB_CB.setValue(null);
                AD_AdminEditS_CB.setValue(null);
                AD_AdminAddLSS_CB.setValue(null);
                AD_AdminAddBPS_CB.setValue(null);
                AD_AdminDeleteLSS_CB.setValue(null);
                AD_AdminDeleteBPS_CB.setValue(null);
                AD_AdminEditLSS_CB.setValue(null);
                AD_AdminEditBPS_CB.setValue(null);
            }
        }
    }
    private void updateUserDetails(String role){
        dbc.updateAdminDetails(role, adminIDFromAWTable,
                AD_AdminFirstName_TF.getText(), AD_AdminLastName_TF.getText(), AD_AdminUserName_TF.getText(), AD_AdminPassword_TF.getText(),
                AD_AdminEmail_TF.getText(), AD_AdminAR_CB.getValue(), AD_AdminFF_CB.getValue(), AD_AdminHR_CB.getValue(), AD_AdminMR_CB.getValue(),
                AD_AdminSF_CB.getValue(), AD_AdminS_CB.getValue(), AD_AdminAW_CB.getValue(), AD_AdminB_CB.getValue(), AD_AdminAddAR_CB.getValue(),
                AD_AdminAddAW_CB.getValue(), AD_AdminAddB_CB.getValue(), AD_AdminAddFF_CB.getValue(), AD_AdminAddHR_CB.getValue(), AD_AdminAddMR_CB.getValue(),
                AD_AdminAddSF_CB.getValue(), AD_AdminAddS_CB.getValue(), AD_AdminDeleteAR_CB.getValue(), AD_AdminDeleteAW_CB.getValue(), AD_AdminDeleteB_CB.getValue(),
                AD_AdminDeleteFF_CB.getValue(), AD_AdminDeleteHR_CB.getValue(), AD_AdminDeleteMR_CB.getValue(), AD_AdminDeleteSF_CB.getValue(), AD_AdminDeleteS_CB.getValue(),
                AD_AdminEditAR_CB.getValue(), AD_AdminEditAW_CB.getValue(), AD_AdminEditFF_CB.getValue(), AD_AdminEditHR_CB.getValue(), AD_AdminEditMR_CB.getValue(),
                AD_AdminEditSF_CB.getValue(), AD_AdminEditB_CB.getValue(), AD_AdminEditS_CB.getValue(),AD_AdminAddLSS_CB.getValue(), AD_AdminAddBPS_CB.getValue(),
                AD_AdminEditLSS_CB.getValue(), AD_AdminEditBPS_CB.getValue(), AD_AdminDeleteLSS_CB.getValue(), AD_AdminDeleteBPS_CB.getValue());
    }
    private void DeleteAdmin(String manage){
        dbc.deleteAdminRecords(manage, adminIDFromAWTable);
        loadDataS_ByProducts_Table();
    }

    @FXML
    void onManageChangedCB(ActionEvent event) {
    if(AD_Manage_CB.getValue().equals("ADMIN")){
        loadDataAD_Admin_Table();
        AD_Worker_T.setVisible(false);
        AD_Admin_T.setVisible(true);
    }else{
        loadDataAD_Worker_Table();
        AD_Worker_T.setVisible(true);
        AD_Admin_T.setVisible(false);
    }

    }



    /*-----------------------------------------------------------------------------END-ADMIN-RECORDS-----------------------------------------------------------------------------------------------*/


    /*----------------------------------------------------------------START INITIALIZEBLES-----------------------------------------------------------------------------------------------*/
// method putting data in combo-box Type
    private void roleList () {
        // Using Arrays.asList to create a fixed-size list backed by the specified array
        ObservableList<String> AnimalTypeData = FXCollections.observableArrayList(Arrays.asList(ListData.AnimalType));
        ObservableList<String> GenderData = FXCollections.observableArrayList(Arrays.asList(ListData.Gender));
        ObservableList<String> HornStatusData = FXCollections.observableArrayList(Arrays.asList(ListData.HornStatus));
        ObservableList<String> TailStatusData = FXCollections.observableArrayList(Arrays.asList(ListData.TailStatus));
        ObservableList<String> Privilege = FXCollections.observableArrayList(Arrays.asList(ListData.Privilege));
        ObservableList<String> Manage = FXCollections.observableArrayList(Arrays.asList(ListData.Manage));
        AR_Cbox.setItems(AnimalTypeData);
        AR_AnimalType_Cbox.setItems(AnimalTypeData);
        AR_Sex_Cbox.setItems(GenderData);
        AR_HornStatus_Cbox.setItems(HornStatusData);
        AR_TailStatus_Cbox.setItems(TailStatusData);

        AD_Manage_CB.setItems(Manage);
        AD_AdminAR_CB.setItems(Privilege);
        AD_AdminFF_CB.setItems(Privilege);
        AD_AdminHR_CB.setItems(Privilege);
        AD_AdminMR_CB.setItems(Privilege);
        AD_AdminSF_CB.setItems(Privilege);
        AD_AdminS_CB.setItems(Privilege);
        AD_AdminAW_CB.setItems(Privilege);
        AD_AdminB_CB.setItems(Privilege);
        AD_AdminAddAR_CB.setItems(Privilege);
        AD_AdminAddAW_CB.setItems(Privilege);
        AD_AdminAddB_CB.setItems(Privilege);
        AD_AdminAddFF_CB.setItems(Privilege);
        AD_AdminAddHR_CB.setItems(Privilege);
        AD_AdminAddMR_CB.setItems(Privilege);
        AD_AdminAddSF_CB.setItems(Privilege);
        AD_AdminAddS_CB.setItems(Privilege);
        AD_AdminDeleteAR_CB.setItems(Privilege);
        AD_AdminDeleteAW_CB.setItems(Privilege);
        AD_AdminDeleteB_CB.setItems(Privilege);
        AD_AdminDeleteFF_CB.setItems(Privilege);
        AD_AdminDeleteHR_CB.setItems(Privilege);
        AD_AdminDeleteMR_CB.setItems(Privilege);
        AD_AdminDeleteSF_CB.setItems(Privilege);
        AD_AdminDeleteS_CB.setItems(Privilege);
        AD_AdminEditAR_CB.setItems(Privilege);
        AD_AdminEditAW_CB.setItems(Privilege);
        AD_AdminEditFF_CB.setItems(Privilege);
        AD_AdminEditHR_CB.setItems(Privilege);
        AD_AdminEditMR_CB.setItems(Privilege);
        AD_AdminEditSF_CB.setItems(Privilege);
        AD_AdminEditB_CB.setItems(Privilege);
        AD_AdminEditS_CB.setItems(Privilege);
        AD_AdminAddLSS_CB.setItems(Privilege);
        AD_AdminAddBPS_CB.setItems(Privilege);
        AD_AdminDeleteLSS_CB.setItems(Privilege);
        AD_AdminDeleteBPS_CB.setItems(Privilege);
        AD_AdminEditLSS_CB.setItems(Privilege);
        AD_AdminEditBPS_CB.setItems(Privilege);
    }

    private void userPrivilege(int userId){
        dbUserDetails = dbc.getAllUserDetails(AD_Manage_CB.getValue());
        ButtonCBMap = new HashMap<>();
        // Get all fields of the SampleController class
        Field[] fields = DashboardController.class.getDeclaredFields();
        // Fetch admin details once

        for (AdminDetails adminDetails : dbUserDetails){
            if(adminDetails.getId() == userId){
                //TODO -> add if statement to implement privilege to the user logged in
                for (Field field : fields) {
                    if (field.getType() == Button.class) {
                        try {
                            // Make private fields accessible
                            field.setAccessible(true);
                            Button button = (Button) field.get(this); // Get the button instance
                            String buttonName = field.getName();
                            // Extract the corresponding privilege status from AdminDetails
                            String buttonStatus = extractButtonStatus(adminDetails, buttonName);
                            if(buttonStatus==null){
                                buttonStatus="DISABLED";
                            }
                            // Put button and status into the ButtonCBMap
                            ButtonCBMap.put(button, buttonStatus);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            }
        }

        // Set button statuses based on ButtonCBMap
        for (Map.Entry<Button, String> entry : ButtonCBMap.entrySet()) {
            Button button = entry.getKey();
            String buttonStatus = entry.getValue();

            if (buttonStatus.equals("DISABLED")) {
                button.setDisable(true);
            } else {
                button.setDisable(false);
            }
        }
    }

    private String extractButtonStatus(AdminDetails adminDetails, String buttonName) {
        switch (buttonName) {

            case "Milk_Records_BT":
                return adminDetails.getAdminMR();
            case "MR_AddNew_BT":
                return adminDetails.getAdminAddMR();
            case "MR_Edit_BT":
                return adminDetails.getAdminEditMR();
            case "MR_Delete_BT":
                return adminDetails.getAdminDeleteMR();

            case "Animal_Records_BT":
                return adminDetails.getAdminAR();
            case "AR_AddNew_BT":
                return adminDetails.getAdminAddAR();
            case "AR_Edit_BT":
                return adminDetails.getAdminEditAR();
            case "AR_Delete_BT":
                return adminDetails.getAdminDeleteAR();

            case "Animal_Health_BT":
                return adminDetails.getAdminHR();
            case "H_AddNew_BT":
                return adminDetails.getAdminAddHR();
            case "H_Edit_BT":
                return adminDetails.getAdminEditHR();
            case "H_Delete_BT":
                return adminDetails.getAdminDeleteHR();

            case "Breeding_BT":
                return adminDetails.getAdminB();
            case "B_AddNew_BT":
                return adminDetails.getAdminAddB();
            case "B_Edit_BT":
                return adminDetails.getAdminEditB();
            case "B_Delete_BT":
                return adminDetails.getAdminDeleteB();

            case "Animal_Weights_BT":
                return adminDetails.getAdminAW();
            case "AW_AddNew_BT":
                return adminDetails.getAdminAddAW();
            case "AW_Edit_BT":
                return adminDetails.getAdminEditAW();
            case "AW_Delete_BT":
                return adminDetails.getAdminDeleteAW();

            case "Sales_BT":
                return adminDetails.getAdminS();
            case "S_MilkAddNew_BT":
                return adminDetails.getAdminAddS();
            case "S_MilkEdit_BT":
                return adminDetails.getAdminEditS();
            case "S_MilkDelete_BT":
                return adminDetails.getAdminDeleteS();
            case "Farm_Finance_BT":
                return adminDetails.getAdminFF();
            case "Stockfeed_BT":
                return adminDetails.getAdminSF();
            case "S_LS_AddNew_BT":
                return adminDetails.getAdminAddLSS();
            case "S_BPAddNew_BT":
                return adminDetails.getAdminAddBPS();
            case "S_LS_Edit_BT":
                return adminDetails.getAdminEditLSS();
            case "S_BPEdit_BT":
                return adminDetails.getAdminEditBPS();
            case "S_LS_Delete_BT":
                return adminDetails.getAdminDeleteLSS();
            case "S_BPDelete_BT":
                return adminDetails.getAdminDeleteBPS();



            /*
            case "Administration_BT":
                return adminDetails.getAdminSF();
                case "AD_AddNew_BT":

                return adminDetails.getAdminFF();
            case "AD_Edit_BT":
                return adminDetails.getAdminFF();
            case "AD_Delete_BT":
                return adminDetails.getAdminFF();*/

            default:
                // Handle the default case if needed
                break;
        }
        // Return a default value if none of the cases match


        return "ENABLED";
    }


    private void inputFilters(){
        // Add listener to filter data based on user input in Animal Records
        AR_Search_TF.textProperty().addListener((observable, oldValue, newValue) -> filterData(newValue,"AR"));
        // Add listener to filter data based on user input in Animal Weight Records
        AW_Search_TF.textProperty().addListener((observable, oldValue, newValue) -> filterData(newValue,"AW"));
        // Add listener to filter data based on user input in Milk Records
        MR_SearchAnimal_TF.textProperty().addListener((observable, oldValue, newValue) -> filterData(newValue,"MR"));
        // Add listener to filter data based on user input in sales livestock sales
        S_LS_AnimalSearch_TF.textProperty().addListener((observable, oldValue, newValue) -> filterData(newValue,"SLS"));
        // Add listener to filter data based on user input in sales livestock sales
        B_SearchAnimal_TF.textProperty().addListener((observable, oldValue, newValue) -> filterData(newValue,"B"));
            if(TextFieldCBMap.isEmpty()){
            // filters textfields that allows numbers
            TextFieldCBMap.add(ARP_SireID_TF);
            TextFieldCBMap.add(ARP_DamID_TF);
            TextFieldCBMap.add(ARP_Bweight_TF);
            TextFieldCBMap.add(MR_CowID_TF);
            TextFieldCBMap.add(MR_Morning_TF);
            TextFieldCBMap.add(MR_Afternoon_TF);
            TextFieldCBMap.add(MR_Evening_TF);
            TextFieldCBMap.add(H_AnimalID_TF);
            TextFieldCBMap.add(H_ServiceWeight_TF);
            TextFieldCBMap.add(H_TreatmentCost_TF);
            TextFieldCBMap.add(S_MilkUnitPrice_TF);
            TextFieldCBMap.add(S_MilkQBought_TF);
            TextFieldCBMap.add(S_MilkTPayment_TF);
            TextFieldCBMap.add(S_LS_AnimalID_TF);
            TextFieldCBMap.add(S_LS_AnimalWeight_TF);
            TextFieldCBMap.add(S_LS_PriceLiveWeight_TF);
            TextFieldCBMap.add(S_LS_TotalPrice_TF);
            TextFieldCBMap.add(S_BP_PPUnit_TF);
            TextFieldCBMap.add(S_BP_QBought_TF);
            TextFieldCBMap.add(S_BP_TotalPayment_TF);
            //TextFieldCBMap.add(ARP_Bweight_TF);
        }

            // Loop through the ArrayList
            for (TextField textField : TextFieldCBMap) {
                // Here, we're just printing the text of each TextField
                textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
                    String character = event.getCharacter();
                    if (!character.matches("[0-9.]")) {
                        event.consume(); // Ignore non-numeric input
                    }
                });
            }
            ARP_SireID_TF.textProperty().addListener((observable, oldValue, newValue) -> {
                boolean isMatch = false;
                for (AnimalRecords a : dbData) {
                    if (a.getAnimalId().toString().equals(newValue) && a.getAnimalType().equalsIgnoreCase("Bull")) {
                        isMatch = true;
                        break; // Exit the loop if a match is found
                    }}
                if (!isMatch) {
                    ARP_SireID_TF.setStyle("-fx-border-color: red;");
                    ARP_SireBreed_TF.setDisable(false);
                    ARP_SireName_TF.setDisable(false);
                    ARP_DamBreed_TF.setDisable(false);
                    ARP_DamName_TF.setDisable(false);
                } else {
                    ARP_SireID_TF.setStyle(""); // Reset border color
                    ARP_SireBreed_TF.setDisable(true);
                    ARP_SireName_TF.setDisable(true);
                    ARP_DamBreed_TF.setDisable(true);
                    ARP_DamName_TF.setDisable(true);
                }
            });

        ARP_DamID_TF.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isMatch = false;
            for (AnimalRecords a : dbData) {
                if (a.getAnimalId().toString().equals(newValue) && a.getAnimalType().equalsIgnoreCase("Cow")) {
                    isMatch = true;
                    break; // Exit the loop if a match is found
                }}
            if (!isMatch) {
                ARP_DamID_TF.setStyle("-fx-border-color: red;");
                ARP_SireBreed_TF.setDisable(false);
                ARP_SireName_TF.setDisable(false);
                ARP_DamBreed_TF.setDisable(false);
                ARP_DamName_TF.setDisable(false);

            } else {
                ARP_DamID_TF.setStyle(""); // Reset border color
                ARP_SireBreed_TF.setDisable(true);
                ARP_SireName_TF.setDisable(true);
                ARP_DamBreed_TF.setDisable(true);
                ARP_DamName_TF.setDisable(true);
            }
        });

    }
    // Method to filter data based on the search term
    private void filterData(String searchTerm, String textField) {
        ObservableList<AnimalRecords> filteredData = FXCollections.observableArrayList();

        // If the search term is empty, show all records
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            switch (textField){
                case "AR":
                    AR_T1.setItems(dbData);
                    break;
                case "AW":
                    AW_ARTABLE_T.setItems(dbData);
                    break;
                case "MR":
                    MR_AnimalTable_T1.setItems(dbData);
                    break;
                case "SLS":
                    S_LS_AnimalTable.setItems(dbData);
                    break;
                case "B":
                    B_Animal_T.setItems(dbData);
                    break;
            }

        } else {
            // Filter records based on the search term
            for (AnimalRecords record : dbData) {
                if (record.getAnimalName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        String.valueOf(record.getAnimalId()).toLowerCase().contains(searchTerm.toLowerCase()) ||
                        record.getBreed().toLowerCase().contains(searchTerm.toLowerCase())) {
                    filteredData.add(record);
                }
            }

            switch (textField){
                case "AR":
                    // Set filtered data to the animal record table
                    AR_T1.setItems(filteredData);
                    break;
                case "AW":
                    AW_ARTABLE_T.setItems(filteredData);
                    break;
                case "MR":
                    // Set filtered data to the milk record table
                    MR_AnimalTable_T1.setItems(filteredData);
                    break;
                case "SLS":
                    // Set filtered data to the sales by_product sales record table
                    S_LS_AnimalTable.setItems(filteredData);
                    break;
                case "B":
                    // Set filtered data to the Breeding record table
                    B_Animal_T.setItems(filteredData);
                    break;
            }



        }

    }

    //set numerical textfield to 0 instead of empty string
    private void SetTextfieldZero(){
        for(TextField textField : TextFieldCBMap){
            if(textField.getText().isEmpty()){
                textField.setText("0");
            }
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //------------------------------------
        dbc = new DatabaseConnection();
        //--------------------------------
        dbData = FXCollections.observableArrayList();
        dbObWeight = FXCollections.observableArrayList();
        dbObMilk = FXCollections.observableArrayList();
        dbObHealth = FXCollections.observableArrayList();
        dbObBreeding = FXCollections.observableArrayList();
        dbMilkSalesData = FXCollections.observableArrayList();
        dbByProductSalesData = FXCollections.observableArrayList();
        dbLivestockSalesData = FXCollections.observableArrayList();
        dbUserDetails = FXCollections.observableArrayList();

        //set AD_Manage_CB to USER
        AD_Manage_CB.setValue("WORKER");
        //--------------------
        String query = "query";
        loadFromDB(query);
        //-----------------------------------
        inputFilters();
        //--------------------------
        roleList ();
        //--------------------
        //initualizeDBarChart();
        barchartDisplay();
        setPieChartData();
        loadDataAW_AR_Table();
        loadDataMR_AR_Table();
        loadDataH_AR_Table();
        loadDataB_AR_Table();
        loadDataS_AR_Table();
        loadDataS_Milk_Table();
        loadDataS_ByProducts_Table();
        loadDataS_LivestockSales_Table();
        loadDataAD_Worker_Table();

    }

}
/*----------------------------------------------------------------END INITIALIZABLES-----------------------------------------------------------------------------------------------*/

