package com.example.asthafood.mssql.models;

public class ItemCategory {

    public String getCategoriyNo() {
        return CategoriyNo;
    }

    public void setCategoriyNo(String categoriyNo) {
        CategoriyNo = categoriyNo;
    }

    public String getCategoriName() {
        return CategoriName;
    }

    public void setCategoriName(String categoriName) {
        CategoriName = categoriName;
    }

    public String getCategoryQuantity() {
        return CategoryQuantity;
    }

    public void setCategoryQuantity(String categoryQuantity) {
        CategoryQuantity = categoryQuantity;
    }

    String CategoriyNo,CategoriName,CategoryQuantity;
}
