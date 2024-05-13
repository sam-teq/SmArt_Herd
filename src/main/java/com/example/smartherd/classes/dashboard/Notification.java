package com.example.smartherd.classes.dashboard;

import com.example.smartherd.classes.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

public class Notification {
    DatabaseConnection db = new DatabaseConnection();
    ObservableList <AnimalRecords> animalRecords = FXCollections.observableArrayList();
    public Notification(FXMLLoader loader){
        TableView<AnimalRecords> D_HeatNotification_T ;
        TableColumn<AnimalRecords, String> D_Id_TC, D_Breed_TC;
        TableColumn<?, String>  D_HeatDate_TC;


        try {
            this.animalRecords = db.getAllRecords("all animals");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for(AnimalRecords A : this.animalRecords){
                if(A.getAnimalType().equals("COW")){
                    if(A.getDaysLived() >= 30 && A.getDaysLived() <= 100){
                        System.out.println(A.getBreed() + " is :"+A.getDaysLived()+" days old");
                    }
                }else if (A.getAnimalType().equals("HEIFER")){

                }else{
                        System.out.println(A.getBreed() + " is :"+A.getDaysLived()+" days old");
                }

        }

        D_HeatNotification_T = (TableView<AnimalRecords>) loader.getNamespace().get("D_HeatNote_T");
        D_Id_TC = (TableColumn<AnimalRecords, String>) loader.getNamespace().get("D_Id_TC");
        D_Breed_TC = (TableColumn<AnimalRecords, String>) loader.getNamespace().get("D_Breed_TC");
        D_HeatDate_TC = (TableColumn<AnimalRecords, String>) loader.getNamespace().get("D_HeatDate_TC");

// Ensure elements are not null

        D_Id_TC.setCellValueFactory(new PropertyValueFactory<>("animalId"));
        D_Breed_TC.setCellValueFactory(new PropertyValueFactory<>("breed"));
        //D_HeatDate_TC.setCellValueFactory(new PropertyValueFactory<>("daysLived"));
        System.out.println("List Size = "+animalRecords.size());
        // Set data to table
        D_HeatNotification_T.setItems(animalRecords);

    }

    public ObservableList<AnimalRecords> getAnimalRecords() {
        return animalRecords;
    }
}
