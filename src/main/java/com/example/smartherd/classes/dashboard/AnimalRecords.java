package com.example.smartherd.classes.dashboard;
import java.sql.Timestamp;
import java.util.Date;

public class AnimalRecords {
    private Integer animalId, sireId, damId, foreign_Sire_ID, foreign_Dam_ID;
    private String animalName, animalType, breed, gender, color, tattoo, hornStatus, tailDockStatus, hoofMark, tailMark,
            earNotches, tempPreference, dietPreference ,sireName, sireBreed,damName, damBreed, F_S_Age, Pre_Owner_Name,
            Pre_Owner_Number, currentAge, foreign_Sire_Breed, foreign_Dam_Breed,foreign_Sire_Name, foreign_Dam_Name;
    private Date dateOfBirth;
    private Timestamp currentWeightDate, birthWeightDate, serviceDate;

    private double birthWeight, F_S_weight, currentWeight;


    public String getAnimalName() {
        return animalName;
    }

    public void setAnimalName(String animalName) {
        this.animalName = animalName;
    }

    public String getAnimalType() {
        return animalType;
    }

    public void setAnimalType(String animalType) {
        this.animalType = animalType;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHornStatus() {
        return hornStatus;
    }

    public void setHornStatus(String hornStatus) {
        this.hornStatus = hornStatus;
    }

    public String getTailDockStatus() {
        return tailDockStatus;
    }

    public void setTailDockStatus(String tailDockStatus) {
        this.tailDockStatus = tailDockStatus;
    }

    public String getHoofMark() {
        return hoofMark;
    }

    public void setHoofMark(String hoofMark) {
        this.hoofMark = hoofMark;
    }

    public String getTailMark() {
        return tailMark;
    }

    public void setTailMark(String tailMark) {
        this.tailMark = tailMark;
    }

    public String getEarNotches() {
        return earNotches;
    }

    public void setEarNotches(String earNotches) {
        this.earNotches = earNotches;
    }

    public String getTempPreference() {
        return tempPreference;
    }

    public void setTempPreference(String tempPreference) {
        this.tempPreference = tempPreference;
    }

    public String getDietPreference() {
        return dietPreference;
    }

    public void setDietPreference(String dietPreference) {
        this.dietPreference = dietPreference;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public double getBirthWeight() {
        return birthWeight;
    }

    public void setBirthWeight(double birthWeight) {
        this.birthWeight = birthWeight;
    }

    public void setAnimalId(Integer animalId) {
        this.animalId = animalId;
    }

    public Integer getAnimalId() {
        return animalId;
    }

    public Integer getSireId() {
        return sireId;
    }

    public void setSireId(Integer sireId) {
        this.sireId = sireId;
    }

    public Integer getDamId() {
        return damId;
    }

    public void setDamId(Integer damId) {
        this.damId = damId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTattoo() {
        return tattoo;
    }

    public void setTattoo(String tattoo) {
        this.tattoo = tattoo;
    }

    public String getSireName() {
        return sireName;
    }

    public void setSireName(String sireName) {
        this.sireName = sireName;
    }

    public String getSireBreed() {
        return sireBreed;
    }

    public void setSireBreed(String sireBreed) {
        this.sireBreed = sireBreed;
    }

    public String getDamName() {
        return damName;
    }

    public void setDamName(String damName) {
        this.damName = damName;
    }

    public String getDamBreed() {
        return damBreed;
    }

    public void setDamBreed(String damBreed) {
        this.damBreed = damBreed;
    }

    public String getF_S_Age() {
        return F_S_Age;
    }

    public void setF_S_Age(String f_S_Age) {
        F_S_Age = f_S_Age;
    }

    public Timestamp getCurrentWeightDate() {
        return currentWeightDate;
    }

    public void setCurrentWeightDate(Timestamp currentWeightDate) {
        this.currentWeightDate = currentWeightDate;
    }

    public Timestamp getBirthWeightDate() {
        return birthWeightDate;
    }

    public void setBirthWeightDate(Timestamp birthWeightDate) {
        this.birthWeightDate = birthWeightDate;
    }

    public double getF_S_weight() {
        return F_S_weight;
    }

    public void setF_S_weight(double f_S_weight) {
        F_S_weight = f_S_weight;
    }

    public double getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(double currentWeight) {
        this.currentWeight = currentWeight;
    }

    public String getPre_Owner_Name() {
        return Pre_Owner_Name;
    }

    public void setPre_Owner_Name(String pre_Owner_Name) {
        Pre_Owner_Name = pre_Owner_Name;
    }

    public String getPre_Owner_Number() {
        return Pre_Owner_Number;
    }

    public void setPre_Owner_Number(String pre_Owner_Number) {
        Pre_Owner_Number = pre_Owner_Number;
    }

    public String getCurrentAge() {
        return currentAge;
    }

    public void setCurrentAge(String currentAge) {
        this.currentAge = currentAge;
    }

    public Integer getForeign_Sire_ID() {
        return foreign_Sire_ID;
    }

    public void setForeign_Sire_ID(Integer foreign_Sire_ID) {
        this.foreign_Sire_ID = foreign_Sire_ID;
    }

    public Integer getForeign_Dam_ID() {
        return foreign_Dam_ID;
    }

    public void setForeign_Dam_ID(Integer foreign_Dam_ID) {
        this.foreign_Dam_ID = foreign_Dam_ID;
    }

    public String getForeign_Sire_Breed() {
        return foreign_Sire_Breed;
    }

    public void setForeign_Sire_Breed(String foreign_Sire_Breed) {
        this.foreign_Sire_Breed = foreign_Sire_Breed;
    }

    public String getForeign_Dam_Breed() {
        return foreign_Dam_Breed;
    }

    public void setForeign_Dam_Breed(String foreign_Dam_Breed) {
        this.foreign_Dam_Breed = foreign_Dam_Breed;
    }

    public String getForeign_Sire_Name() {
        return foreign_Sire_Name;
    }

    public void setForeign_Sire_Name(String foreign_Sire_Name) {
        this.foreign_Sire_Name = foreign_Sire_Name;
    }

    public String getForeign_Dam_Name() {
        return foreign_Dam_Name;
    }

    public void setForeign_Dam_Name(String foreign_Dam_Name) {
        this.foreign_Dam_Name = foreign_Dam_Name;
    }

    public Timestamp getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(Timestamp serviceDate) {
        this.serviceDate = serviceDate;
    }
}
