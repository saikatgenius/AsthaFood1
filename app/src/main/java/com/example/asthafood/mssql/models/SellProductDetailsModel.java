package com.example.asthafood.mssql.models;

public class SellProductDetailsModel {

    String ProductID;

    public String getBatchNo() {
        return BatchNo;
    }

    public void setBatchNo(String batchNo) {
        BatchNo = batchNo;
    }

    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    public String getAssingQnty() {
        return AssingQnty;
    }

    public void setAssingQnty(String assingQnty) {
        AssingQnty = assingQnty;
    }

    public String getSellQunty() {
        return SellQunty;
    }

    public void setSellQunty(String sellQunty) {
        SellQunty = sellQunty;
    }

    public String getReturnQunt() {
        return ReturnQunt;
    }

    public void setReturnQunt(String returnQunt) {
        ReturnQunt = returnQunt;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public int getRemainingQuntity() {
        return RemainingQuntity;
    }

    public void setRemainingQuntity(int remainingQuntity) {
        RemainingQuntity = remainingQuntity;
    }

    String BatchNo;
    String AssingQnty;
    String SellQunty;
    String ReturnQunt;
    String Price ;
    int RemainingQuntity;

    public int getSellingQnty() {
        return SellingQnty;
    }

    public void setSellingQnty(int sellingQnty) {
        SellingQnty = sellingQnty;
    }

    int SellingQnty;

    public double getSellingQntyFinalPrice() {
        return SellingQntyFinalPrice;
    }

    public void setSellingQntyFinalPrice(double sellingQntyFinalPrice) {
        SellingQntyFinalPrice = sellingQntyFinalPrice;
    }

    double SellingQntyFinalPrice;

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    String ProductName;





}
