package com.example.asthafood.Util;

public class ReqProductList {

    String Name;
    String Id;

    public String getBatchNo() {
        return BatchNo;
    }

    public void setBatchNo(String batchNo) {
        BatchNo = batchNo;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    String BatchNo;
    String Price;

    public double getQunt() {
        return Qunt;
    }

    public void setQunt(double qunt) {
        Qunt = qunt;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    double Qunt;
}
