package com.parentalert.in.schoolapp.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ToggleButton;

import com.parentalert.in.schoolapp.R;


/**
 * Created by parentalert on 03-12-2016.
 */

public class Recycleviewholder_create extends RecyclerView.ViewHolder implements View.OnClickListener{
    // View holder for gridview recycler view as we used in listview
//    public CheckBox title;
//    public TextView textview;
    public ToggleButton checkbox;


    public Recycleviewholder_create(View view) {
        super(view);
        // Find all views ids

        this.checkbox = (ToggleButton) view
                .findViewById(R.id.toggle_button);

    }



    @Override
    public void onClick(View v) {

    }
}