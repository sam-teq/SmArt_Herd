package com.example.smartherd.classes.dashboard;

import java.sql.Date;

public class Sales {
    private int SaleID, productID, Section;
    private String animalBreed, clientFullName, clientEmail, clientPhone, productName, productInfor;
    private double unitPriceLiveWeight, qtyBought, sellingWeight, totalBill;
    private Date saleDate;

    public int getSaleID() {
        return SaleID;
    }

    public void setSaleID(int saleID) {
        this.SaleID = saleID;
    }

    public int getSection() {
        return Section;
    }

    public void setSection(int section) {
        Section = section;
    }

    public String getAnimalBreed() {
        return animalBreed;
    }

    public void setAnimalBreed(String animalBreed) {
        this.animalBreed = animalBreed;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public double getUnitPriceLiveWeight() {
        return unitPriceLiveWeight;
    }

    public void setUnitPriceLiveWeight(double unitPriceLiveWeight) {
        this.unitPriceLiveWeight = unitPriceLiveWeight;
    }

    public double getSellingWeight() {
        return sellingWeight;
    }

    public void setSellingWeight(double sellingWeight) {
        this.sellingWeight = sellingWeight;
    }

    public double getTotalBill() {
        return totalBill;
    }

    public void setTotalBill(double totalBill) {
        this.totalBill = totalBill;
    }

    public String getClientFullName() {
        return clientFullName;
    }

    public void setClientFullName(String clientFullName) {
        this.clientFullName = clientFullName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductInfor() {
        return productInfor;
    }

    public void setProductInfor(String productInfor) {
        this.productInfor = productInfor;
    }

    public double getQtyBought() {
        return qtyBought;
    }

    public void setQtyBought(double qtyBought) {
        this.qtyBought = qtyBought;
    }
}
