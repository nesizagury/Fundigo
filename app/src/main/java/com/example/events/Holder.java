package com.example.events;


import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Holder {

    ImageView image;
    TextView date;
    TextView name;
    TextView tags;


    public Holder(View v) {

        image = (ImageView) v.findViewById(R.id.imageView);
        date = (TextView) v.findViewById(R.id.event_date);
        name = (TextView) v.findViewById(R.id.event_name);
        tags = (TextView) v.findViewById(R.id.tags);
    }


}