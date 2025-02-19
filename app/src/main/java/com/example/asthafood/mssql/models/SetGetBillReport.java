package com.example.asthafood.mssql.models;

public class SetGetBillReport {
    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getProductQuantity() {
        return ProductQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        ProductQuantity = productQuantity;
    }

    public String getProductPrice() {
        return ProductPrice;
    }

    public void setProductPrice(String productPrice) {
        ProductPrice = productPrice;
    }

    public String getProductDetails() {
        return ProductDetails;
    }

    public void setProductDetails(String productDetails) {
        ProductDetails = productDetails;
    }

    public String getBillNo() {
        return BillNo;
    }

    public void setBillNo(String billNo) {
        BillNo = billNo;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getBuyer() {
        return Buyer;
    }

    public void setBuyer(String buyer) {
        Buyer = buyer;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    String ProductName,ProductQuantity,ProductPrice,ProductDetails,BillNo,Amount,Buyer,date,ProductId;

    public String getSaleid() {
        return Saleid;
    }

    public void setSaleid(String saleid) {
        Saleid = saleid;
    }

    public String getSaleDate() {
        return SaleDate;
    }

    public void setSaleDate(String saleDate) {
        SaleDate = saleDate;
    }

    public String getPayableAmt() {
        return PayableAmt;
    }

    public void setPayableAmt(String payableAmt) {
        PayableAmt = payableAmt;
    }

    public String getCoustomerPh() {
        return CoustomerPh;
    }

    public void setCoustomerPh(String coustomerPh) {
        CoustomerPh = coustomerPh;
    }

    public String getCoustomerName() {
        return CoustomerName;
    }

    public void setCoustomerName(String coustomerName) {
        CoustomerName = coustomerName;
    }

    public String getItemID() {
        return ItemID;
    }

    public void setItemID(String itemID) {
        ItemID = itemID;
    }

    public String getBatchNo() {
        return BatchNo;
    }

    public void setBatchNo(String batchNo) {
        BatchNo = batchNo;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getSalePrice() {
        return SalePrice;
    }

    public void setSalePrice(String salePrice) {
        SalePrice = salePrice;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    String Saleid,SaleDate,PayableAmt,CoustomerPh,CoustomerName,ItemID,BatchNo,Quantity,SalePrice,ItemName;



}
