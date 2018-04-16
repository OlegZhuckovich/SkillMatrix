package com.epam.zhuckovich.controller.entity;

import java.util.ArrayList;

public class Category {

    private String categoryName;
    private int rowIndex;
    private int columnIndex;
    private ArrayList<Category> subcategoryList;

    public Category(String categoryName){
        this.categoryName = categoryName;
    }

    public boolean addCategory(Category newCategory){
        if(subcategoryList == null){
            subcategoryList = new ArrayList<>();
        }
        return subcategoryList.add(newCategory);
    }

    public int getSubcategoryQuantity(){
        return subcategoryList.size();
    }

    public void setCategoryName(String categoryName){
        this.categoryName = categoryName;
    }

    public void setRowIndex(int rowIndex){
        this.rowIndex = rowIndex;
    }

    public void setColumnIndex(int columnIndex){
        this.columnIndex = columnIndex;
    }

    public String getCategoryName(){
        return categoryName;
    }

    public int getRowIndex(){
        return rowIndex;
    }

    public int getColumnIndex(){
        return columnIndex;
    }

    public ArrayList<Category> getSubcategoryList(){
        return subcategoryList;
    }

}
