package com.example.asthafood.mssql.models;

public class SetGetIncAssignProduct {
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

    public String getReamingQuenty() {
        return ReamingQuenty;
    }

    public void setReamingQuenty(String reamingQuenty) {
        ReamingQuenty = reamingQuenty;
    }

    public String getSellQnty() {
        return SellQnty;
    }

    public void setSellQnty(String sellQnty) {
        SellQnty = sellQnty;
    }

    public String getRemaningQnty() {
        return RemaningQnty;
    }

    public void setRemaningQnty(String remaningQnty) {
        RemaningQnty = remaningQnty;
    }

    String ReamingQuenty,SellQnty,RemaningQnty;


}
