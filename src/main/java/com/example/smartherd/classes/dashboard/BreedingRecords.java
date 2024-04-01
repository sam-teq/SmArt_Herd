package com.example.smartherd.classes.dashboard;

import java.util.Date;

public class BreedingRecords {
    private int breedingId, cowId, bullId, calfId, foreignBullId;
    private Date heatDate, breedingDate, calvingDueDate, pregnancyDiagnosisDate, calvingDate;
    private String cowBreed, bullBreed, calfBreed, foreignBullBreed, breedingMethod, breedingNotes, calvingNotes;

    public int getBreedingId() { 
        return breedingId;
    }

    public void setBreedingId(int breedingId) {
        this.breedingId = breedingId;
    }

    public int getCowId() {
        return cowId;
    }

    public void setCowId(int cowId) {
        this.cowId = cowId;
    }

    public int getBullId() {
        return bullId;
    }

    public void setBullId(int bullId) {
        this.bullId = bullId;
    }

    public int getCalfId() {
        return calfId;
    }

    public void setCalfId(int calfId) {
        this.calfId = calfId;
    }

    public int getForeignBullId() {
        return foreignBullId;
    }

    public void setForeignBullId(int foreignBullId) {
        this.foreignBullId = foreignBullId;
    }

    public String getCowBreed() {
        return cowBreed;
    }

    public void setCowBreed(String cowBreed) {
        this.cowBreed = cowBreed;
    }

    public String getBullBreed() {
        return bullBreed;
    }

    public void setBullBreed(String bullBreed) {
        this.bullBreed = bullBreed;
    }

    public String getCalfBreed() {
        return calfBreed;
    }

    public void setCalfBreed(String calfBreed) {
        this.calfBreed = calfBreed;
    }

    public Date getHeatDate() {
        return heatDate;
    }

    public void setHeatDate(Date heatDate) {
        this.heatDate = heatDate;
    }

    public Date getBreedingDate() {
        return breedingDate;
    }

    public void setBreedingDate(Date breedingDate) {
        this.breedingDate = breedingDate;
    }

    public Date getCalvingDueDate() {
        return calvingDueDate;
    }

    public void setCalvingDueDate(Date calvingDueDate) {
        this.calvingDueDate = calvingDueDate;
    }

    public Date getPregnancyDiagnosisDate() {
        return pregnancyDiagnosisDate;
    }

    public void setPregnancyDiagnosisDate(Date pregnancyDiagnosisDate) {
        this.pregnancyDiagnosisDate = pregnancyDiagnosisDate;
    }

    public Date getCalvingDate() {
        return calvingDate;
    }

    public void setCalvingDate(Date calvingDate) {
        this.calvingDate = calvingDate;
    }

    public String getForeignBullBreed() {
        return foreignBullBreed;
    }

    public void setForeignBullBreed(String foreignBullBreed) {
        this.foreignBullBreed = foreignBullBreed;
    }

    public String getBreedingMethod() {
        return breedingMethod;
    }

    public void setBreedingMethod(String breedingMethod) {
        this.breedingMethod = breedingMethod;
    }

    public String getBreedingNotes() {
        return breedingNotes;
    }

    public void setBreedingNotes(String breedingNotes) {
        this.breedingNotes = breedingNotes;
    }

    public String getCalvingNotes() {
        return calvingNotes;
    }

    public void setCalvingNotes(String calvingNotes) {
        this.calvingNotes = calvingNotes;
    }
}
