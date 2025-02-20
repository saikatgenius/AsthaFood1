package com.example.asthafood.mssql.models;

public class ItemCategoryItems {

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    String ProductName;
    String ProductID;

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    String Price;

    public String getBatchNo() {
        return BatchNo;
    }

    public void setBatchNo(String batchNo) {
        BatchNo = batchNo;
    }

    String BatchNo;

}
