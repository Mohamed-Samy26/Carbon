package com.example.myapplication.models;

public class GroupNumbersModel {

    private String text;
    private boolean isSelected = false;

    public GroupNumbersModel(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public boolean isSelected() {
        return isSelected;
    }
}