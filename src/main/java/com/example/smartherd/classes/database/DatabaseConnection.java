package com.example.smartherd.classes.database;

import com.example.smartherd.classes.ListData;
import com.example.smartherd.classes.dashboard.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class DatabaseConnection {
    //database tools
    private Connection connect;
    private PreparedStatement statement;
    private CallableStatement callableStatement;
    private ResultSet Result;

    private AnimalRecords animalRecords= null;
    private WeightRecords weightRecords= null;
    private ObservableList<AnimalRecords> dbData = FXCollections.observableArrayList();
    private ObservableList<WeightRecords> dbWeightData = FXCollections.observableArrayList();
    private ObservableList<MilkRecords> dbMilktData = FXCollections.observableArrayList();
    private ObservableList<HealthRecords> dbHealth = FXCollections.observableArrayList();
    private ObservableList<BreedingRecords> dbBreedingData = FXCollections.observableArrayList();
    private ObservableList<Sales> dbSales = FXCollections.observableArrayList();
    private ObservableList<AdminDetails> dbUserDetails = FXCollections.observableArrayList();
    /*------------------------------------------------------------------------CONNECTS-TO-SMART-HERD-DATABASE----------------------------------------------------------------*/

    public DatabaseConnection() {
        try{
            connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/smart-herd","root","RUTH+260968+MYSQL");
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    /*-------------------------------------------------------------------------------RETRIEVES-USER-EMAIL-----------------------------------------------------------------*/
    public ResultSet getResult(String sql, String email) throws SQLException {

        try {
            statement = connect.prepareStatement(sql);
            statement.setString(1, email);
            Result = statement.executeQuery();
            return Result;
        } catch (SQLException e) {
            // Log the exception or handle it as needed
            e.printStackTrace();
            throw e; // Optionally, throw a custom exception here
        }
    }

    /*---------------------------------------------------------------------------RETRIEVES-USER-LOGIN-CREDENTIALS-------------------------------------------------------*/
    public ResultSet getResult(String sql, String username, String password) throws SQLException {

        try {
            statement = connect.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            Result = statement.executeQuery();
            return Result;
        } catch (SQLException e) {
            // Log the exception or handle it as needed
            e.printStackTrace();
            throw e; // Optionally, throw a custom exception here
        }
    }


    public ResultSet getResult(String sql) throws SQLException {

        try {

            statement = connect.prepareStatement(sql);
            Result = statement.executeQuery();
            return Result;
        } catch (SQLException e) {
            // Log the exception or handle it as needed
            e.printStackTrace();
            throw e; // Optionally, throw a custom exception here
        }
    }

    /*-------------------------------------------------------------------------------CALL-ALL-ANIMAL-RECORDS--------------------------------------------------------*/
    public ResultSet getStatementResult() throws SQLException {
        try {
             callableStatement = connect.prepareCall("{call GetAnimalDetails()}");

            // Execute the stored procedure
            callableStatement.execute();

            // Retrieve the result set
            Result = callableStatement.getResultSet();

            return Result;
        } catch (SQLException e) {
            // Log the exception or handle it as needed
            e.printStackTrace();
            throw e; // Optionally, throw a custom exception here
        }
    }

    /*--------------------------------------------------------------------------CALL-ANIMAL-RECORDS-BY-ANIMAL-TYPE-------------------------------------------------------*/
///
    public ResultSet getStatementResult(String AnimalType) throws SQLException {

        try {
            // Prepare the stored procedure call
            callableStatement = connect.prepareCall("{call GetAnimalDetailsByType(?)}");

            // Set the parameter for the stored procedure
            callableStatement.setString(1, AnimalType);

            // Execute the stored procedure
            Result = callableStatement.executeQuery();

            // Returning all results
            return Result;
        } catch (SQLException e) {
            // Log the exception or handle it as needed
            e.printStackTrace();
            throw e; // Optionally, throw a custom exception here
        }
    }


    /*---------------------------------------------------------------------------------CALL-ANIMAL-RECORDS-BY-ANIMAL-TYPE-------------------------------------------------------*/

    public ResultSet getHealthRecords(int animalID) throws SQLException {

        try {
            // Prepare the stored procedure call
            callableStatement = connect.prepareCall("{call GetAnimalHealthRecords(?)}");

            // Set the parameter for the stored procedure
            callableStatement.setInt(1, animalID);

            // Execute the stored procedure
            Result = callableStatement.executeQuery();

            // Returning all results
            return Result;
        } catch (SQLException e) {
            // Log the exception or handle it as needed
            e.printStackTrace();
            throw e; // Optionally, throw a custom exception here
        }
    }



    /*--------------------------------------------------------------------------CALL-ANIMAL-RECORDS-BY-ANIMAL-TYPE-------------------------------------------------------*/

    public ResultSet getWeightRecResult(int AnimalID) throws SQLException {

        try {
            // Prepare the stored procedure call
            callableStatement = connect.prepareCall("{call GetWeightRecords(?)}");

            // Set the parameter for the stored procedure
            callableStatement.setInt(1, AnimalID);

            // Execute the stored procedure
            Result = callableStatement.executeQuery();

            // Returning all results
            return Result;
        } catch (SQLException e) {
            // Log the exception or handle it as needed
            e.printStackTrace();
            throw e; // Optionally, throw a custom exception here
        }
    }


    /*---------------------------------------------------------------------------------LOAD-ANIMAL-RECORDS-TABLE-------------------------------------------------------*/

    String animalTypeData[] = ListData.AnimalType;
    public ObservableList<AnimalRecords> getAllRecords(String query) throws SQLException {
        dbData.clear();
        if(query.equals(animalTypeData[0]) || query.equals(animalTypeData[1]) ||
                query.equals(animalTypeData[2]) || query.equals(animalTypeData[3])){
            //returns results from database
            Result=getStatementResult(query);
        }else {
            Result=getStatementResult();
        }
        //adding result data to AnimalRecords class
        // NOTE fields should be named exactly as in database
        while(Result.next()){
            animalRecords = new AnimalRecords();
            animalRecords.setAnimalId(Result.getInt("Animal_ID"));
            animalRecords.setAnimalName(Result.getString("Animal_Name"));
            animalRecords.setAnimalType(Result.getString("Animal_Type"));
            animalRecords.setBreed(Result.getString("Breed"));
            animalRecords.setGender(Result.getString("Gender"));
            animalRecords.setDateOfBirth(Result.getDate("D.O.B"));
            animalRecords.setCurrentAge(Result.getString("Current_Age"));
            animalRecords.setColor(Result.getString("color"));
            animalRecords.setTattoo(Result.getString("tattoo"));
            animalRecords.setHornStatus(Result.getString("Horn_Status"));
            animalRecords.setTailDockStatus(Result.getString("Tail_Dock_Status"));
            animalRecords.setHoofMark(Result.getString("Hoof_Mark"));
            animalRecords.setTailMark(Result.getString("Tail_mark"));
            animalRecords.setEarNotches(Result.getString("Ear_Notches"));
            animalRecords.setBirthWeight(Result.getDouble("Birth_Weight"));
            animalRecords.setTempPreference(Result.getString("Temp_Preference"));
            animalRecords.setDietPreference(Result.getString("Diet_Preference"));
            animalRecords.setSireId(Result.getInt("Sire_ID"));
            animalRecords.setForeign_Sire_ID(Result.getInt("Foreign_Sire_ID"));
            animalRecords.setDamId(Result.getInt("Dam_ID"));
            animalRecords.setForeign_Dam_ID(Result.getInt("Foreign_Dam_ID"));
            animalRecords.setSireBreed(Result.getString("Sire_Breed"));
            animalRecords.setForeign_Sire_Breed(Result.getString("Foreign_Sire_Breed"));
            animalRecords.setSireName(Result.getString("Sire_Name"));
            animalRecords.setForeign_Sire_Name(Result.getString("Foreign_Sire_Name"));
            animalRecords.setDamBreed(Result.getString("Dam_Breed"));
            animalRecords.setForeign_Dam_Breed(Result.getString("Foreign_Dam_Breed"));
            animalRecords.setDamName(Result.getString("Dam_Name"));
            animalRecords.setForeign_Dam_Name(Result.getString("Foreign_Dam_Name"));
            animalRecords.setF_S_Age(Result.getString("F_S_Age"));
            animalRecords.setF_S_weight(Result.getDouble("Service_Weight"));
            animalRecords.setServiceDate(Result.getTimestamp("Service_Date"));
            animalRecords.setCurrentWeight(Result.getDouble("Current_Weight"));
            animalRecords.setCurrentWeightDate(Result.getTimestamp("Current_Weight_Date"));
            animalRecords.setBirthWeightDate(Result.getTimestamp("Birth_Weight_Date"));
            animalRecords.setPre_Owner_Name(Result.getString("Pre_Owner_Name"));
            animalRecords.setPre_Owner_Number(Result.getString("Pre_Owner_Number"));
            animalRecords.setDaysLived(Result.getInt("Days_Lived"));
            dbData.add(animalRecords);

        }
        return dbData;
    }

    /*------------------------------------------------------------------------------INSERT-INTO-ANIMAL-RECORDS-TABLE-------------------------------------------------------*/

    public int insertAnimalRecord(String animalName, String animalType, String breed, String gender, Date dateOfBirth,
                                  String color, String tattoo, String hornStatus, String tailDockStatus,
                                  String hoofMark, String tailMark, String earNotches, String tempPreference,
                                  String dietPreference, String prevOwnerName, String prevOwnerNum, int sireId, int damId,
                                  String foreignSireBreed, String foreignSireName,String foreignDamBreed, String foreignDamName) {

        int autoGeneratedAnimalId = 0;
        // JDBC connection and PreparedStatement
        String insertQuery = "INSERT INTO animal_records " +
                "(Animal_Name, Animal_Type, Breed, Gender, `D.O.B`, Color, Tattoo, Horn_Status, Tail_Dock_Status, " +
                "Hoof_Mark, Tail_mark, Ear_Notches, Temp_Preference, Diet_Preference, Pre_Owner_Name, Pre_Owner_Number, " +
                "Sire_ID, Dam_ID, Foreign_Sire_Breed, Foreign_Sire_Name, Foreign_Dam_Breed, Foreign_Dam_Name) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            // Create a prepared statement with the insert query
            statement = connect.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);

            // Set values for the placeholders in the prepared statement
            statement.setString(1, animalName);
            statement.setString(2, animalType);
            statement.setString(3, breed);
            statement.setString(4, gender);
            statement.setDate(5, new Date(dateOfBirth.getTime()));
            statement.setString(6, color);
            statement.setString(7, tattoo);
            statement.setString(8, hornStatus);
            statement.setString(9, tailDockStatus);
            statement.setString(10, hoofMark);
            statement.setString(11, tailMark);
            statement.setString(12, earNotches);
            statement.setString(13, tempPreference);
            statement.setString(14, dietPreference);
            statement.setString(15, prevOwnerName);
            statement.setString(16, prevOwnerNum);
            statement.setInt(17, sireId);
            statement.setInt(18, damId);
            statement.setString(19, foreignSireBreed);
            statement.setString(20, foreignSireName);
            statement.setString(21, foreignDamBreed);
            statement.setString(22, foreignDamName);

            // Execute the insert query
            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                // Retrieve the auto-generated keys (including the auto-incremented animalId)
                ResultSet generatedKeys = statement.getGeneratedKeys();

                if (generatedKeys.next()) {
                    autoGeneratedAnimalId = generatedKeys.getInt(1);
                    System.out.println("Auto-generated Animal_ID: " + autoGeneratedAnimalId);
                }
            }

            System.out.println("Data inserted successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return autoGeneratedAnimalId;

    }
    /*------------------------------------------------------------------------------UPDATE-INTO-ANIMAL-RECORDS-TABLE-------------------------------------------------------*/

    public int updateAnimalRecord(int animalId, String animalName, String animalType, String breed, String gender,
                                  Date dateOfBirth, String color, String tattoo, String hornStatus,
                                  String tailDockStatus, String hoofMark, String tailMark, String earNotches,
                                  String tempPreference, String dietPreference, String prevOwnerName,
                                  String prevOwnerNum, int sireId, int damId) {
        int rowsAffected = 0;

        String updateAnimalRecordQuery = "UPDATE animal_records SET " +
                "Animal_Name = ?, Animal_Type = ?, Breed = ?, Gender = ?, " +
                "`D.O.B` = ?, Color = ?, Tattoo = ?, Horn_Status = ?, Tail_Dock_Status = ?, " +
                "Hoof_Mark = ?, Tail_mark = ?, Ear_Notches = ?, Temp_Preference = ?, " +
                "Diet_Preference = ?, Pre_Owner_Name = ?, Pre_Owner_Number = ?, " +
                "Sire_ID = ?, Dam_ID = ? WHERE Animal_ID = ?";

        try {

            statement = connect.prepareStatement(updateAnimalRecordQuery);

            statement.setString(1, animalName);
            statement.setString(2, animalType);
            statement.setString(3, breed);
            statement.setString(4, gender);
            statement.setDate(5, new Date(dateOfBirth.getTime()));
            statement.setString(6, color);
            statement.setString(7, tattoo);
            statement.setString(8, hornStatus);
            statement.setString(9, tailDockStatus);
            statement.setString(10, hoofMark);
            statement.setString(11, tailMark);
            statement.setString(12, earNotches);
            statement.setString(13, tempPreference);
            statement.setString(14, dietPreference);
            statement.setString(15, prevOwnerName);
            statement.setString(16, prevOwnerNum);
            statement.setInt(17, sireId);
            statement.setInt(18, damId);
            statement.setInt(19, animalId);

            rowsAffected = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rowsAffected;
    }

    /*-----------------------------------------------------------------------------DELETING-FROM-ANIMAL-RECORDS-TABLE-------------------------------------------------------------*/
    public void deleteFromAnimalRecords(Integer animalId) {
        String deleteAnimalRecordQuery = "DELETE FROM animal_records WHERE Animal_ID = ?";
        try {
            statement = connect.prepareStatement(deleteAnimalRecordQuery);
            statement.setInt(1, animalId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }





    /*--------------------------------------------------------------------------CALL-BREED-RECORDS-BY-ANIMAL-ID-------------------------------------------------------*/

    public ResultSet getBreedingRecResult(int AnimalID) throws SQLException {

        try {
            // Prepare the stored procedure call
            callableStatement = connect.prepareCall("{call GetBreedingInfo(?)}");

            // Set the parameter for the stored procedure
            callableStatement.setInt(1, AnimalID);

            // Execute the stored procedure
            Result = callableStatement.executeQuery();

            // Returning all results
            return Result;
        } catch (SQLException e) {
            // Log the exception or handle it as needed
            e.printStackTrace();
            throw e; // Optionally, throw a custom exception here
        }
    }
    public ObservableList<BreedingRecords> getAllBreedingRecords(int AnimalID) throws SQLException {
        dbBreedingData.clear();
        Result=getBreedingRecResult(AnimalID);
        //adding result data to Weight Records class
        // NOTE fields should be named exactly as in database
        while(Result.next()){
            BreedingRecords breedingRecords = new BreedingRecords();
            breedingRecords.setBreedingId(Result.getInt("Breeding_ID"));
            breedingRecords.setCowId(Result.getInt("Cow_ID"));
            breedingRecords.setCowBreed(Result.getString("Cow_Breed"));
            breedingRecords.setHeatDate(Result.getDate("Heat_Date"));
            breedingRecords.setBreedingDate(Result.getDate("Breading_Date"));
            breedingRecords.setBullId(Result.getInt("Bull_ID"));
            breedingRecords.setBullBreed(Result.getString("Bull_Breed"));
            breedingRecords.setForeignBullId(Result.getInt("Foreign_Bull_ID"));
            breedingRecords.setForeignBullBreed(Result.getString("Foreign_Bull_Breed"));
            breedingRecords.setBreedingMethod(Result.getString("Breading_Method"));
            breedingRecords.setPregnancyDiagnosisDate(Result.getDate("Pregnance_Diagnosis_Date"));
            breedingRecords.setCalvingDueDate(Result.getDate("Calving_Due_Date"));
            breedingRecords.setCalvingDate(Result.getDate("Calving_Date"));
            breedingRecords.setCalfId(Result.getInt("Calf_ID"));
            breedingRecords.setCalfBreed(Result.getString("Calf_Breed"));
            breedingRecords.setBreedingNotes(Result.getString("Breeding_Notes"));
            breedingRecords.setCalvingNotes(Result.getString("Calving_Notes"));
            dbBreedingData.add(breedingRecords);

        }
        return dbBreedingData;
    }

    /*-----------------------------------------------------------------------------RETRIEVING-FROM-MILK-RECORDS-TABLE-------------------------------------------------------------*/

    public ObservableList<MilkRecords>  getAllMilkRecords() throws SQLException {
        dbMilktData.clear();
        String query = "SELECT * FROM milk_records";

        statement = connect.prepareStatement(query);
        Result = statement.executeQuery();
        while (Result.next()) {
            MilkRecords milkRecords = new MilkRecords();
            milkRecords.setMilkId(Result.getInt("Milk_ID"));
            milkRecords.setAnimalId(Result.getInt("Animal_ID"));
            milkRecords.setMilkingDate(Result.getDate("Milking_Date"));
            milkRecords.setMorningSession(Result.getDouble("Morning_Session"));
            milkRecords.setAfternoonSession(Result.getDouble("Afternoon_Session"));
            milkRecords.setEveningSession(Result.getDouble("Evening_Session"));
            dbMilktData.add(milkRecords);
        }
        return dbMilktData;
    }

    public ObservableList<MilkRecords>  getAllMilkRecords(int AnimalID) throws SQLException {
        dbMilktData.clear();
        String query = "SELECT * FROM milk_records WHERE Animal_ID = ?";

        statement = connect.prepareStatement(query);
        statement.setInt(1, AnimalID); // Set the value for the first parameter
        Result = statement.executeQuery();
        while (Result.next()) {
            MilkRecords milkRecords = new MilkRecords();
            milkRecords.setMilkId(Result.getInt("Milk_ID"));
            milkRecords.setAnimalId(Result.getInt("Animal_ID"));
            milkRecords.setMilkingDate(Result.getDate("Milking_Date"));
            milkRecords.setMorningSession(Result.getDouble("Morning_Session"));
            milkRecords.setAfternoonSession(Result.getDouble("Afternoon_Session"));
            milkRecords.setEveningSession(Result.getDouble("Evening_Session"));
            dbMilktData.add(milkRecords);
        }


        return dbMilktData;
    }
    /*-----------------------------------------------------------------------------INPUT-INTO-MILK-RECORDS-TABLE-------------------------------------------------------------*/


    // Function to insert data into the milk_records table
    public void insertMilkRecord(int animalId, Date milkingDate, double morningSession, double afternoonSession, double eveningSession) {
        try{

            if (milkingDateExists(animalId, milkingDate)) {
                System.out.println("Record for Animal_ID and Milking_Date already exists. Aborting insertion.");
                return;
            }

            // SQL query to insert data
            String insertQuery = "INSERT INTO milk_records (Animal_ID, Milking_Date, Morning_Session, Afternoon_Session, Evening_Session) " +
                    "VALUES (?, ?, ?, ?, ?)";

            statement = connect.prepareStatement(insertQuery);
            // Set values for placeholders in the SQL query
            statement.setInt(1, animalId);
            statement.setDate(2, milkingDate);
            statement.setDouble(3, morningSession);
            statement.setDouble(4, afternoonSession);
            statement.setDouble(5, eveningSession);

            // Execute the query
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Data inserted successfully!");
            } else {
                System.out.println("Failed to insert data.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /*-----------------------------------------------------------------------------CHECK-RESOURCES--------------------------------------------------------------*/

    // Function to check if Milking_Date already exists
    private boolean milkingDateExists(int animalId,Date milkingDate) throws SQLException {

        String checkQuery = "SELECT COUNT(*) FROM milk_records WHERE Animal_ID = ? AND Milking_Date = ?";
        try{  statement = connect.prepareStatement(checkQuery);
            statement.setInt(1, animalId);
            statement.setDate(2, milkingDate);
            Result = statement.executeQuery();
            Result.next();
            int count = Result.getInt(1);
            return count > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /*-----------------------------------------------------------------------------UPDATE-INTO-MILK-RECORDS-TABLE-------------------------------------------------------------*/


    // Function to update data in the milk_records table
    public void updateMilkRecord(int milkId, int animalId, Date milkingDate, double morningSession,
                                 double afternoonSession, double eveningSession) {

        // SQL query to update data
        String updateQuery = "UPDATE milk_records SET Animal_ID = ?, Milking_Date = ?, Morning_Session = ?, " +
                "Afternoon_Session = ?, Evening_Session = ? WHERE Milk_ID = ?";

        try {
            statement = connect.prepareStatement(updateQuery);
            // Set values for placeholders in the SQL query
            statement.setInt(1, animalId);
            statement.setDate(2, milkingDate);
            statement.setDouble(3, morningSession);
            statement.setDouble(4, afternoonSession);
            statement.setDouble(5, eveningSession);
            statement.setInt(6, milkId);

            // Execute the query
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Data updated successfully!");
            } else {
                System.out.println("Failed to update data. Milk record with ID " + milkId + " not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /*-----------------------------------------------------------------------------DELETE-FROM-MILK-RECORDS-TABLE-------------------------------------------------------------*/

    // Function to delete data from the milk_records table
    public void deleteMilkRecord(int Id,String toDelete) {
        String deleteQuery;
        if(toDelete.equals("Milk")){
            // SQL query to delete data
            deleteQuery = "DELETE FROM milk_records WHERE Milk_ID = ?";

        }else{
            deleteQuery = "DELETE FROM milk_records WHERE Animal_ID = ?";
        }

        try{
            PreparedStatement statement = connect.prepareStatement(deleteQuery);
            // Set value for the placeholder in the SQL query
            statement.setInt(1, Id);

            // Execute the query
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Data deleted successfully!");
            } else {
                System.out.println("Failed to delete data. Milk record with ID " + Id + " not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*---------------------------------------------------------------------------------LOAD-HEALTH-RECORDS-TABLE-------------------------------------------------------*/

    public ObservableList<HealthRecords> getAllHealthRecords(int animalID) throws SQLException {
        dbHealth.clear();
        Result=getHealthRecords(animalID);
        //adding result data to Weight Records class
        // NOTE fields should be named exactly as in database
        while(Result.next()){
            HealthRecords healthRecords = new HealthRecords();

            healthRecords.setHealthId(Result.getInt("Health_ID"));
            healthRecords.setAnimalId(Result.getInt("Animal_ID"));
            healthRecords.setBreed(Result.getString("Breed"));
            healthRecords.setServiceDate(Result.getDate("Date"));
            healthRecords.setWeightKg(Result.getDouble("Weight_Kg"));
            healthRecords.setWeightNote(Result.getString("Weight_Note"));
            healthRecords.setSymptoms(Result.getString("Symptoms"));
            healthRecords.setDiagnosis(Result.getString("Diagnosis"));
            healthRecords.setTreatmentCost(Result.getDouble("Treatment_Cost"));
            healthRecords.setTreatment_Plan(Result.getString("Treatment_Plan"));
            healthRecords.setServiceNote(Result.getString("Note"));
            healthRecords.setService_Age(Result.getString("Service_Age"));
            healthRecords.setFirstName(Result.getString("Vet_First_Name"));
            healthRecords.setLastName(Result.getString("Vet_Last_Name"));
            healthRecords.setEmail(Result.getString("Email"));
            healthRecords.setPhone(Result.getString("Phone"));
            healthRecords.setHomeAddress(Result.getString("Home_Address"));
            healthRecords.setWorkAddress(Result.getString("Work_Address"));

            dbHealth.add(healthRecords);

        }
        return dbHealth;
    }
    /*---------------------------------------------------------------------------INSERT-INTO-HEALTH-RECORDS-TABLE-------------------------------------------------------*/

    public  int insertHealthRecord(int animalId, Date date, String symptoms, String diagnosis,
                                   String treatmentPlan, double treatmentCost, String note,
                                   String vetFirstName, String vetLastName, String email,
                                   String phone, String homeAddress, String workAddress) {
        int autoGeneratedAnimalId = 0;
        // SQL query to insert a record into the health_records table
        String sql = "INSERT INTO health_records (Animal_ID, Date, Symptoms, Diagnosis, Treatment_Plan, " +
                "Treatment_Cost, Note, Vet_First_Name, Vet_Last_Name, Email, Phone, " +
                "Home_Address, Work_Address) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try{

            // Create a prepared statement
            statement = connect.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            // Set parameters for the prepared statement
            statement.setInt(1, animalId);
            statement.setDate(2, new Date(date.getTime()));
            statement.setString(3, symptoms);
            statement.setString(4, diagnosis);
            statement.setString(5, treatmentPlan);
            statement.setDouble(6, treatmentCost);
            statement.setString(7, note);
            statement.setString(8, vetFirstName);
            statement.setString(9, vetLastName);
            statement.setString(10, email);
            statement.setString(11, phone);
            statement.setString(12, homeAddress);
            statement.setString(13, workAddress);

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                // Retrieve the auto-generated keys (including the auto-incremented animalId)
                ResultSet generatedKeys = statement.getGeneratedKeys();

                if (generatedKeys.next()) {
                    autoGeneratedAnimalId = generatedKeys.getInt(1);
                    System.out.println("Auto-generated Animal_ID: " + autoGeneratedAnimalId);
                }
            }

            System.out.println("Data inserted successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return autoGeneratedAnimalId;
    }

    /*------------------------------------------------------------------------------UPDATE-INTO-ANIMAL-HEALTH-RECORDS-TABLE-------------------------------------------------------*/
    public  void updateHealthRecord(int healthID, int animalId, Date date, String symptoms, String diagnosis,
                                    String treatmentPlan, double treatmentCost, String note,
                                    String vetFirstName, String vetLastName, String email,
                                    String phone, String homeAddress, String workAddress) {

        // SQL query to update a record into the health_records table
        String sql = "UPDATE health_records SET  Animal_ID = ?, Date = ?, Symptoms = ?, Diagnosis = ?, Treatment_Plan = ?, " +
                "Treatment_Cost = ?, Note = ?, Vet_First_Name = ?, Vet_Last_Name = ?, Email = ?, Phone = ?, " +
                "Home_Address = ?, Work_Address = ? WHERE Health_ID = ?";

        try{

            // Create a prepared statement
            statement = connect.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            // Set parameters for the prepared statement
            statement.setInt(1, animalId);
            statement.setDate(2, new Date(date.getTime()));
            statement.setString(3, symptoms);
            statement.setString(4, diagnosis);
            statement.setString(5, treatmentPlan);
            statement.setDouble(6, treatmentCost);
            statement.setString(7, note);
            statement.setString(8, vetFirstName);
            statement.setString(9, vetLastName);
            statement.setString(10, email);
            statement.setString(11, phone);
            statement.setString(12, homeAddress);
            statement.setString(13, workAddress);

            statement.setInt(14, healthID);

            // Execute the prepared statement
            statement.executeUpdate();

            System.out.println("Health record updated successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*-----------------------------------------------------------------------------DELETING-FROM-HEALTH-RECORDS-TABLE-------------------------------------------------------------*/
    public void deleteFromHealthRecords( Integer health_Id) throws SQLException {
        String deleteWeightRecordsQuery = "DELETE FROM health_records WHERE Health_ID = ?";

        statement = connect.prepareStatement(deleteWeightRecordsQuery);
        statement.setInt(1, health_Id);
        statement.executeUpdate();
    }

    /*------------------------------------------------------------------------------INSERT-INTO-BREEDING-RECORDS-TABLE-------------------------------------------------------*/
    public void insertBreedingData(int cowId, int bullId, Date heatDate, Date breedingDate, String breedingMethod, Date calvingDueDate,
                                   Date pregnancyDiagnosisDate, Date calvingDate, int calfId, String breedingNotes, String calvingNotes) {
        try{
            // SQL query for insertion
            String query = "INSERT INTO breeding (Cow_ID, Bull_ID, Heat_Date, Breading_Date, " +
                    " Breading_Method, Pregnance_Diagnosis_Date, Calving_Due_Date, Calving_Date, " +
                    "Calf_ID, Breeding_Notes, Calving_Notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            statement = connect.prepareStatement(query);
            // Set values for placeholders in the SQL query
            statement.setInt(1, cowId);
            statement.setInt(2, bullId);
            statement.setDate(3, heatDate);
            statement.setDate(4, breedingDate);
            statement.setString(5, breedingMethod);
            statement.setDate(6, pregnancyDiagnosisDate);
            statement.setDate(7, calvingDueDate);
            statement.setDate(8, calvingDate);
            statement.setInt(9, calfId);
            statement.setString(10, breedingNotes);
            statement.setString(11, calvingNotes);

            // Execute the insertion
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Data inserted successfully!");
            } else {
                System.out.println("Failed to insert data.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //TODO --> Creating update into and delete from breeding table methods


    /*---------------------------------------------------------------------------------LOAD-WEIGHT-RECORDS-TABLE-------------------------------------------------------*/
    public ObservableList<WeightRecords> getAllWeightRecords(int AnimalID) throws SQLException {
        dbWeightData.clear();
        Result=getWeightRecResult(AnimalID);
        //adding result data to Weight Records class
        // NOTE fields should be named exactly as in database
        while(Result.next()){
            weightRecords = new WeightRecords();

            weightRecords.setWeightId(Result.getInt("Weight_ID"));
            weightRecords.setAnimalId(Result.getInt("Animal_ID"));
            weightRecords.setAnimalName(Result.getString("Animal_Name"));
            weightRecords.setAnimalBreed(Result.getString("Breed"));
            weightRecords.setWeightKg(Result.getDouble("Weight_Kg"));
            weightRecords.setAgeWeight(Result.getString("Weighing_Age"));
            weightRecords.setWeightDate(Result.getTimestamp("Weight_Date"));
            weightRecords.setWeightNote(Result.getString("Weight_Note"));
            dbWeightData.add(weightRecords);

        }
        return dbWeightData;
    }

    /*---------------------------------------------------------------------------INSERT-INTO-WEIGHT-RECORDS-TABLE-------------------------------------------------------*/

    public void insertWeightRecord(int animalId,int healthId, double weightKg, Date weightDate, String weightNote) {
        String insertQuery;
        if(weightNote.equals("SERVICE WEIGHT") || weightNote.equals("FIRST SERVICE WEIGHT")){
            insertQuery = "INSERT INTO weight_records " +
                    "(Animal_ID, Weight_Kg, Weight_Date, Weight_Note, Health_ID) " +
                    "VALUES (?, ?, ?, ?, ?)";
        }else {
            insertQuery = "INSERT INTO weight_records " +
                    "(Animal_ID, Weight_Kg, Weight_Date, Weight_Note) " +
                    "VALUES (?, ?, ?, ?)";
        }

        try {
            // Create a prepared statement with the insert query
            statement = connect.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);

            // Set values for the placeholders in the prepared statement
            statement.setInt(1, animalId);

            statement.setDouble(2, weightKg);

            // Set Weight_Date to provided value or CURRENT_TIMESTAMP if not provided
            if (weightDate != null) {
                statement.setDate(3, new Date(weightDate.getTime()));
            } else {
                statement.setObject(3, null);
            }

            statement.setString(4, weightNote);
            if(healthId != 0){

                statement.setInt(5, healthId);
            }


            // Execute the insert query
            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                // Retrieve the auto-generated keys (including the auto-incremented Weight_ID)
                ResultSet generatedKeys = statement.getGeneratedKeys();

                if (generatedKeys.next()) {
                    int autoGeneratedWeightId = generatedKeys.getInt(1);
                    System.out.println("Auto-generated Weight_ID: " + autoGeneratedWeightId);
                }
            }

            System.out.println("Weight record inserted successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /*------------------------------------------------------------------------------UPDATE-INTO-ANIMAL-WEIGHT-RECORDS-TABLE-------------------------------------------------------*/

    public void updateWeightRecord(String from, int Id, double updatedWeightKg, Date updatedWeightDate, String updatedWeightNote) {

        String updateQuery = null;

        switch (from)
        {
            case "Animal_Records":
                updateQuery = "UPDATE weight_records SET " +
                        "Weight_Kg = ?, " +
                        "Weight_Date = ? " +
                        "WHERE Animal_ID = ? AND Weight_Note = ?";
                break;

            case "Weight_Records":
                updateQuery = "UPDATE weight_records SET " +
                        "Weight_Kg = ?, " +
                        "Weight_Date = ? " +
                        "WHERE Weight_ID = ? AND Weight_Note = ?";
                break;
            case "health_Records":
                updateQuery = "UPDATE weight_records SET " +
                        "Weight_Kg = ?, " +
                        "Weight_Date = ? " +
                        "WHERE Health_ID = ? AND Weight_Note = ?";
                break;
        }

        try {
            statement = connect.prepareStatement(updateQuery);

            // Set values for the placeholders
            statement.setDouble(1, updatedWeightKg);
            statement.setDate(2, new Date(updatedWeightDate.getTime()));

            // Set values for the WHERE clause
            statement.setInt(3, Id);
            statement.setString(4, updatedWeightNote);

            // Execute the update query
            statement.executeUpdate();

            System.out.println("Weight record updated successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*-----------------------------------------------------------------------------DELETING-FROM-WEIGHT-RECORDS-TABLE-------------------------------------------------------------*/
    public void deleteFromWeightRecords( Integer animal_weight_Id) throws SQLException {

        String deleteWeightRecordsQuery = "DELETE FROM weight_records WHERE Animal_ID = ?";

        statement = connect.prepareStatement(deleteWeightRecordsQuery);
        statement.setInt(1, animal_weight_Id);
        statement.executeUpdate();

    }




    /*---------------------------------------------------------------------------------LOAD-SALES-RECORDS-TABLE-------------------------------------------------------*/
    public ObservableList<Sales> getAllSales(String section) {
        dbSales.clear();
        String SELECT_QUERY = switch (section) {
            case "Milk" -> "SELECT * FROM sales WHERE Section = 1;";
            case "Livestock" -> "SELECT * FROM sales WHERE Section = 2;";
            case "ByProduct" -> "SELECT * FROM sales WHERE Section = 3;";
            default -> throw new IllegalStateException("Unexpected value: " + section);
        };
        // MySQL SELECT query
        try {
            // Creating a statement for executing SQL queries
            statement = connect.prepareStatement(SELECT_QUERY);
            // Executing the query and getting the result set
            Result = statement.executeQuery();

            // Processing the result set
            while (Result.next()) {
                Sales sales = new Sales();
                sales.setSaleID(Result.getInt("Sale_ID"));
                sales.setSection(Result.getInt("Section"));
                sales.setProductID(Result.getInt("Product_ID"));
                sales.setProductName(Result.getString("Product_Name"));
                sales.setProductInfor(Result.getString("Product_Infor"));
                sales.setAnimalBreed(Result.getString("Animal_Breed"));
                sales.setSaleDate(Result.getDate("Sale_Date"));
                sales.setUnitPriceLiveWeight(Result.getDouble("UnitPrice/Live_Weight"));
                sales.setSellingWeight(Result.getDouble("Selling_Weight"));
                sales.setQtyBought(Result.getDouble("Quantity_Bought"));
                sales.setTotalBill(Result.getDouble("Total_Bill"));
                sales.setClientFullName(Result.getString("Client_Full_Name"));
                sales.setClientEmail(Result.getString("Client_Email"));
                sales.setClientPhone(Result.getString("Client_Phone"));
                dbSales.add(sales);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dbSales;
    }

    /*------------------------------------------------------------------------------INSERT-INTO-SALES-RECORDS-TABLE-------------------------------------------------------*/
    public void insertSalesData(int AnimalID, String ProductName, String ProducInfor, String AnimalBreed, Date SaleDate, double Price_LiveWeight, double SellingWeight,
                                double QtyBought, double TotalBill, String ClientFullName, String ClientEmail, String ClientPhone, int Section) {
        try{
            // SQL query for insertion
            String query = "INSERT sales (Product_ID, Product_Name, Product_Infor, Animal_Breed, Sale_Date, `UnitPrice/Live_Weight`, Selling_Weight, Quantity_Bought, Total_Bill," +
                    " Client_Full_Name, Client_Email, Client_Phone, Section) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            statement = connect.prepareStatement(query);
            // Set values for placeholders in the SQL query
            statement.setInt(1, AnimalID);
            statement.setString(2, ProductName);
            statement.setString(3, ProducInfor);
            statement.setString(4, AnimalBreed);
            statement.setDate(5, SaleDate);
            statement.setDouble(6, Price_LiveWeight);
            statement.setDouble(7, SellingWeight);
            statement.setDouble(8, QtyBought);
            statement.setDouble(9, TotalBill);
            statement.setString(10, ClientFullName);
            statement.setString(11, ClientEmail);
            statement.setString(12, ClientPhone);
            statement.setInt(13, Section);

            // Execute the insertion
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Data inserted successfully!");
            } else {
                System.out.println("Failed to insert data.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*---------------------------------------------------------------------------------UPDATE-INTO-SALES-TABLE-------------------------------------------------------------------*/
    public void updateToSale(int AnimalID, String ProductName, String ProducInfor, String AnimalBreed, Date SaleDate, double Price_LiveWeight, double SellingWeight,
                             double QtyBought, double TotalBill, String ClientFullName, String ClientEmail, String ClientPhone, int Section, int SalesID) {
        // MySQL UPDATE query
        String UPDATE_QUERY = "UPDATE sales SET Product_ID = ?, Product_Name = ?, Product_Infor = ?, Animal_Breed = ?, Sale_Date = ?, `UnitPrice/Live_Weight` = ?,"+
                " Selling_Weight = ?, Quantity_Bought = ?, Total_Bill = ?, Client_Full_Name = ?, Client_Email = ?, Client_Phone = ?, Section = ?  WHERE Sale_ID=?";

        try{
            // Creating a prepared statement for executing SQL queries with parameters
            statement = connect.prepareStatement(UPDATE_QUERY, Statement.RETURN_GENERATED_KEYS);

            // Setting parameters for the prepared statement
            statement.setInt(1, AnimalID);
            statement.setString(2, ProductName);
            statement.setString(3, ProducInfor);
            statement.setString(4, AnimalBreed);
            statement.setDate(5, SaleDate);
            statement.setDouble(6, Price_LiveWeight);
            statement.setDouble(7, SellingWeight);
            statement.setDouble(8, QtyBought);
            statement.setDouble(9, TotalBill);
            statement.setString(10, ClientFullName);
            statement.setString(11, ClientEmail);
            statement.setString(12, ClientPhone);
            statement.setInt(13, Section);
            statement.setInt(14, SalesID);

            // Executing the update query
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Update successful!");
            } else {
                System.out.println("No rows were updated!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /*-----------------------------------------------------------------------------DELETE-FROM-SALES-TABLE-------------------------------------------------------------*/

    // Function to delete data from the milk_records table
    public void deleteFromSales(int SaleID) {

        // SQL query to delete data
        String deleteQuery = "DELETE FROM sales WHERE Sale_ID = ?";

        try{
            PreparedStatement statement = connect.prepareStatement(deleteQuery);
            // Set value for the placeholder in the SQL query
            statement.setInt(1, SaleID);

            // Execute the query
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Data deleted successfully!");
            } else {
                System.out.println("Failed to delete data. Livestock Sale with ID " + SaleID + " not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*---------------------------------------------------------------------------------LOAD-USER-RECORDS-TABLE-------------------------------------------------------*/
    public ObservableList<AdminDetails> getAllUserDetails(String manage) {
        dbUserDetails.clear();
        String SELECT_QUERY;
        if(manage.equals("ADMIN")) {
            // MySQL SELECT query
            SELECT_QUERY = "SELECT * FROM user WHERE role = 0";
        }else{
            // MySQL SELECT query
            SELECT_QUERY = "SELECT * FROM user WHERE role = 1";
        }
            try {
                // Creating a statement for executing SQL queries
                statement = connect.prepareStatement(SELECT_QUERY);
                // Executing the query and getting the result set
                Result = statement.executeQuery();

                // Processing the result set
                while (Result.next()) {
                    AdminDetails adminDetails  = new AdminDetails();
                    adminDetails.setId(Result.getInt("id"));
                    adminDetails.setFirst_name(Result.getString("first_name"));
                    adminDetails.setLast_name(Result.getString("last_name"));
                    adminDetails.setUsername(Result.getString("username"));
                    adminDetails.setPassword(Result.getString("password"));
                    adminDetails.setEmail(Result.getString("email"));
                    // Retrieving other admin details
                    adminDetails.setAdminAR(Result.getString("UserAR"));
                    adminDetails.setAdminFF(Result.getString("UserFF"));
                    adminDetails.setAdminHR(Result.getString("UserHR"));
                    adminDetails.setAdminMR(Result.getString("UserMR"));
                    adminDetails.setAdminSF(Result.getString("UserSF"));
                    adminDetails.setAdminS(Result.getString("UserS"));
                    adminDetails.setAdminAW(Result.getString("UserAW"));
                    adminDetails.setAdminB(Result.getString("UserB"));
                    adminDetails.setAdminAddAR(Result.getString("UserAddAR"));
                    adminDetails.setAdminAddAW(Result.getString("UserAddAW"));
                    adminDetails.setAdminAddB(Result.getString("UserAddB"));
                    adminDetails.setAdminAddFF(Result.getString("UserAddFF"));
                    adminDetails.setAdminAddHR(Result.getString("UserAddHR"));
                    adminDetails.setAdminAddMR(Result.getString("UserAddMR"));
                    adminDetails.setAdminAddSF(Result.getString("UserAddSF"));
                    adminDetails.setAdminAddS(Result.getString("UserAddS"));
                    adminDetails.setAdminDeleteAR(Result.getString("UserDeleteAR"));
                    adminDetails.setAdminDeleteAW(Result.getString("UserDeleteAW"));
                    adminDetails.setAdminDeleteB(Result.getString("UserDeleteB"));
                    adminDetails.setAdminDeleteFF(Result.getString("UserDeleteFF"));
                    adminDetails.setAdminDeleteHR(Result.getString("UserDeleteHR"));
                    adminDetails.setAdminDeleteMR(Result.getString("UserDeleteMR"));
                    adminDetails.setAdminDeleteSF(Result.getString("UserDeleteSF"));
                    adminDetails.setAdminDeleteS(Result.getString("UserDeleteS"));
                    adminDetails.setAdminEditAR(Result.getString("UserEditAR"));
                    adminDetails.setAdminEditAW(Result.getString("UserEditAW"));
                    adminDetails.setAdminEditFF(Result.getString("UserEditFF"));
                    adminDetails.setAdminEditHR(Result.getString("UserEditHR"));
                    adminDetails.setAdminEditMR(Result.getString("UserEditMR"));
                    adminDetails.setAdminEditSF(Result.getString("UserEditSF"));
                    adminDetails.setAdminEditB(Result.getString("UserEditB"));
                    adminDetails.setAdminEditS(Result.getString("UserEditS"));
                    adminDetails.setAdminAddLSS(Result.getString("UserAddLSS"));
                    adminDetails.setAdminEditLSS(Result.getString("UserEditLSS"));
                    adminDetails.setAdminDeleteLSS(Result.getString("UserDeleteLSS"));
                    adminDetails.setAdminAddBPS(Result.getString("UserAddBPS"));
                    adminDetails.setAdminEditBPS(Result.getString("UserEditBPS"));
                    adminDetails.setAdminDeleteBPS(Result.getString("UserDeleteBPS"));
                    dbUserDetails.add(adminDetails);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return dbUserDetails;
    }
    /*---------------------------------------------------------------------------------INSERT-INTO-ADMIN-TABLE-------------------------------------------------------*/
    public void insertUserDetails(String manage, String first_name, String last_name, String username, String password, String email,
                                  String AdminAR, String AdminFF, String AdminHR, String AdminMR, String AdminSF, String AdminS,
                                  String AdminAW, String AdminB, String AdminAddAR, String AdminAddAW, String AdminAddB,
                                  String AdminAddFF, String AdminAddHR, String AdminAddMR, String AdminAddSF, String AdminAddS,
                                  String AdminDeleteAR, String AdminDeleteAW, String AdminDeleteB, String AdminDeleteFF,
                                  String AdminDeleteHR, String AdminDeleteMR, String AdminDeleteSF, String AdminDeleteS,
                                  String AdminEditAR, String AdminEditAW, String AdminEditFF, String AdminEditHR,
                                  String AdminEditMR, String AdminEditSF, String AdminEditB, String AdminEditS, String AdminAddLSS,
                                  String AdminAddBPS, String AdminEditLSS, String AdminEditBPS, String AdminDeleteLSS, String AdminDeleteBPS) {
        int role;
        String query;
        try {

            if(manage.equals("ADMIN")){
                role = 0;}else {
                role = 1;}

            // SQL query for user insertion
            query = "INSERT INTO `user` (role, first_name, last_name, username, password, email, UserAR, UserFF, UserHR, " +
                    "UserMR, UserSF, UserS, UserAW, UserB, UserAddAR, UserAddAW, UserAddB, UserAddFF, " +
                    "UserAddHR, UserAddMR, UserAddSF, UserAddS, UserDeleteAR, UserDeleteAW, UserDeleteB, " +
                    "UserDeleteFF, UserDeleteHR, UserDeleteMR, UserDeleteSF, UserDeleteS, UserEditAR, " +
                    "UserEditAW, UserEditFF, UserEditHR, UserEditMR, UserEditSF, UserEditB, UserEditS,"+
                    "UserAddLSS, UserAddBPS, UserEditLSS, UserEditBPS, UserDeleteLSS, UserDeleteBPS) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


            statement = connect.prepareStatement(query);

            // Set values for placeholders in the SQL query
            statement.setInt(1, role);
            statement.setString(2, first_name);
            statement.setString(3, last_name);
            statement.setString(4, username);
            statement.setString(5, password);
            statement.setString(6, email);
            statement.setString(7, AdminAR);
            statement.setString(8, AdminFF);
            statement.setString(9, AdminHR);
            statement.setString(10, AdminMR);
            statement.setString(11, AdminSF);
            statement.setString(12, AdminS);
            statement.setString(13, AdminAW);
            statement.setString(14, AdminB);
            statement.setString(15, AdminAddAR);
            statement.setString(16, AdminAddAW);
            statement.setString(17, AdminAddB);
            statement.setString(18, AdminAddFF);
            statement.setString(19, AdminAddHR);
            statement.setString(20, AdminAddMR);
            statement.setString(21, AdminAddSF);
            statement.setString(22, AdminAddS);
            statement.setString(23, AdminDeleteAR);
            statement.setString(24, AdminDeleteAW);
            statement.setString(25, AdminDeleteB);
            statement.setString(26, AdminDeleteFF);
            statement.setString(27, AdminDeleteHR);
            statement.setString(28, AdminDeleteMR);
            statement.setString(29, AdminDeleteSF);
            statement.setString(30, AdminDeleteS);
            statement.setString(31, AdminEditAR);
            statement.setString(32, AdminEditAW);
            statement.setString(33, AdminEditFF);
            statement.setString(34, AdminEditHR);
            statement.setString(35, AdminEditMR);
            statement.setString(36, AdminEditSF);
            statement.setString(37, AdminEditB);
            statement.setString(38, AdminEditS);
            statement.setString(39, AdminAddLSS);
            statement.setString(40, AdminAddBPS);
            statement.setString(41, AdminEditLSS);
            statement.setString(42, AdminEditBPS);
            statement.setString(43, AdminDeleteLSS);
            statement.setString(44, AdminDeleteBPS);

            // Execute the insertion
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Data inserted successfully!");
            } else {
                System.out.println("Failed to insert data.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /*---------------------------------------------------------------------------------UPDATE-INTO-USER-TABLE-------------------------------------------------------*/

    public void updateAdminDetails(String manage, int UserID, String first_name, String last_name, String username, String password, String email,
                                   String UserAR, String UserFF, String UserHR, String UserMR, String UserSF, String UserS,
                                   String UserAW, String UserB, String UserAddAR, String UserAddAW, String UserAddB,
                                   String UserAddFF, String UserAddHR, String UserAddMR, String UserAddSF, String UserAddS,
                                   String UserDeleteAR, String UserDeleteAW, String UserDeleteB, String UserDeleteFF,
                                   String UserDeleteHR, String UserDeleteMR, String UserDeleteSF, String UserDeleteS,
                                   String AdminEditAR, String AdminEditAW, String AdminEditFF, String UserEditHR,
                                   String UserEditMR, String UserEditSF, String UserEditB, String UserEditS, String UserAddLSS,
                                   String UserAddBPS, String UserEditLSS, String UserEditBPS, String UserDeleteLSS, String UserDeleteBPS) {
        String UPDATE_QUERY;
        int role;
        try {
            if(manage.equals("ADMIN")){
                role = 0;}else{
                                role = 1;}
                // MySQL UPDATE query
                UPDATE_QUERY = "UPDATE user SET first_name = ?, last_name = ?, username = ?, password = ?, email = ?," +
                        " UserAR = ?, UserFF = ?, UserHR = ?, UserMR = ?, UserSF = ?, UserS = ?, UserAW = ?, UserB = ?," +
                        " UserAddAR = ?, UserAddAW = ?, UserAddB = ?, UserAddFF = ?, UserAddHR = ?, UserAddMR = ?, UserAddSF = ?," +
                        " UserAddS = ?, UserDeleteAR = ?, UserDeleteAW = ?, UserDeleteB = ?, UserDeleteFF = ?, UserDeleteHR = ?," +
                        " UserDeleteMR = ?, UserDeleteSF = ?, UserDeleteS = ?, UserEditAR = ?, UserEditAW = ?, UserEditFF = ?," +
                        " UserEditHR = ?, UserEditMR = ?, UserEditSF = ?, UserEditB = ?, UserEditS = ?, UserAddLSS = ?, UserAddBPS = ?"+
                        ", UserEditLSS = ?, UserEditBPS = ?, UserDeleteLSS = ?, UserDeleteBPS = ? WHERE id = ? and role = ?;";

            // Creating a prepared statement for executing SQL queries with parameters
            statement = connect.prepareStatement(UPDATE_QUERY);

            // Set values for placeholders in the SQL query
            statement.setString(1, first_name);
            statement.setString(2, last_name);
            statement.setString(3, username);
            statement.setString(4, password);
            statement.setString(5, email);
            statement.setString(6, UserAR);
            statement.setString(7, UserFF);
            statement.setString(8, UserHR);
            statement.setString(9, UserMR);
            statement.setString(10, UserSF);
            statement.setString(11, UserS);
            statement.setString(12, UserAW);
            statement.setString(13, UserB);
            statement.setString(14, UserAddAR);
            statement.setString(15, UserAddAW);
            statement.setString(16, UserAddB);
            statement.setString(17, UserAddFF);
            statement.setString(18, UserAddHR);
            statement.setString(19, UserAddMR);
            statement.setString(20, UserAddSF);
            statement.setString(21, UserAddS);
            statement.setString(22, UserDeleteAR);
            statement.setString(23, UserDeleteAW);
            statement.setString(24, UserDeleteB);
            statement.setString(25, UserDeleteFF);
            statement.setString(26, UserDeleteHR);
            statement.setString(27, UserDeleteMR);
            statement.setString(28, UserDeleteSF);
            statement.setString(29, UserDeleteS);
            statement.setString(30, AdminEditAR);
            statement.setString(31, AdminEditAW);
            statement.setString(32, AdminEditFF);
            statement.setString(33, UserEditHR);
            statement.setString(34, UserEditMR);
            statement.setString(35, UserEditSF);
            statement.setString(36, UserEditB);
            statement.setString(37, UserEditS);
            statement.setString(38, UserAddLSS);
            statement.setString(39, UserAddBPS);
            statement.setString(40, UserEditLSS);
            statement.setString(41, UserEditBPS);
            statement.setString(42, UserDeleteLSS);
            statement.setString(43, UserDeleteBPS);
            statement.setInt(44, UserID);
            statement.setInt(45, role);

            // Executing the update query
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Update successful!");
            } else {
                System.out.println("No rows were updated!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*-----------------------------------------------------------------------------DELETE-FROM-USER-TABLE-------------------------------------------------------------*/

    // Function to delete data from the milk_records table
    public void deleteAdminRecords(String manage, int userID) {
        String deleteQuery = "DELETE FROM `user` WHERE id = ?";
        try{
            PreparedStatement statement = connect.prepareStatement(deleteQuery);
            // Set value for the placeholder in the SQL query
            statement.setInt(1, userID);

            // Execute the query
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Data deleted successfully!");
            } else {
                System.out.println("Failed to delete data "+manage+" with ID " + userID + " not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /*-----------------------------------------------------------------------------CLOSES-DATABASE-RESOURCES--------------------------------------------------------------*/

    public void closeResources(){

        // Close the resources in a
        if (Result != null) {
            try {
                Result.close();
                System.out.println("Result Closed");
            } catch (SQLException e) {
                e.printStackTrace(); // Log or handle the exception
            }
        }
        if (statement != null) {
            try {
                statement.close();
                System.out.println("Statement Closed");
            } catch (SQLException e) {
                e.printStackTrace(); // Log or handle the exception
            }
        }
        if (callableStatement != null) {
            try {
                callableStatement.close();
                System.out.println("callableStatement Closed");
            } catch (SQLException e) {
                e.printStackTrace(); // Log or handle the exception
            }
        }
    }
}