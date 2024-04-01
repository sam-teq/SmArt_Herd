package com.example.smartherd.classes.dashboard;

import java.util.Date;

public class WeightRecords {
   private Integer weightId, animalId;
    private double weightKg;
    private Date weightDate;
    private String ageWeight, weightNote, animalBreed, animalName;

    public Integer getWeightId() {
        return weightId;
    }

    public void setWeightId(Integer weightId) {
        this.weightId = weightId;
    }

    public Integer getAnimalId() {
        return animalId;
    }

    public void setAnimalId(Integer animalId) {
        this.animalId = animalId;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    public String getWeightNote() {
        return weightNote;
    }

    public void setWeightNote(String weightNote) {
        this.weightNote = weightNote;
    }

    public Date getWeightDate() {
        return weightDate;
    }

    public void setWeightDate(Date weightDate) {
        this.weightDate = weightDate;
    }

    public String getAgeWeight() {
        return ageWeight;
    }

    public void setAgeWeight(String ageWeight) {
        this.ageWeight = ageWeight;
    }

    public String getAnimalBreed() {
        return animalBreed;
    }

    public void setAnimalBreed(String animalBreed) {
        this.animalBreed = animalBreed;
    }

    public String getAnimalName() {
        return animalName;
    }

    public void setAnimalName(String animalName) {
        this.animalName = animalName;
    }


}
