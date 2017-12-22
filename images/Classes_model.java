package com.parentalert.in.schoolapp.Model;


/**
 * Created by SONU on 25/09/15.
 */
public class Classes_model {

    // Getter and Setter model for recycler view items
    private String title;
    private boolean checkbox;

    public Classes_model(String title, boolean checkbox) {

        this.title = title;
        this.checkbox = checkbox;
    }

    public String getTitle() {
        return title;
    }


    public boolean isSelected() {
        return checkbox;
    }

    public void setSelected(boolean checkbox) {
        this.checkbox = checkbox;
    }
}