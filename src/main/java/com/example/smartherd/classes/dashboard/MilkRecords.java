package com.example.smartherd.classes.dashboard;

import java.util.Date;

public class MilkRecords {
    private int milkId;
    private int animalId;
    private Date milkingDate;
    private double morningSession;
    private double afternoonSession;
    private double eveningSession;


    public int getMilkId() {
        return milkId;
    }

    public void setMilkId(int milkId) {
        this.milkId = milkId;
    }

    public int getAnimalId() {
        return animalId;
    }

    public void setAnimalId(int animalId) {
        this.animalId = animalId;
    }

    public Date getMilkingDate() {
        return milkingDate;
    }

    public void setMilkingDate(Date milkingDate) {
        this.milkingDate = milkingDate;
    }

    public double getMorningSession() {
        return morningSession;
    }

    public void setMorningSession(double morningSession) {
        this.morningSession = morningSession;
    }

    public double getAfternoonSession() {
        return afternoonSession;
    }

    public void setAfternoonSession(double afternoonSession) {
        this.afternoonSession = afternoonSession;
    }

    public double getEveningSession() {
        return eveningSession;
    }

    public void setEveningSession(double eveningSession) {
        this.eveningSession = eveningSession;
    }
}
