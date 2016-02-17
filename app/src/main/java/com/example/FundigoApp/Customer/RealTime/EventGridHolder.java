package com.example.FundigoApp.Customer.RealTime;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.FundigoApp.R;

public class EventGridHolder {

    ImageView image;
    TextView date;
    TextView name;
    TextView tags;
    TextView price;
    TextView place;
    ImageView saveEvent;

    public EventGridHolder(View v) {
        image = (ImageView) v.findViewById(R.id.imageView_grid);
        date = (TextView) v.findViewById(R.id.event_date_grid);
        name = (TextView) v.findViewById(R.id.event_name_tv_grid);
        tags = (TextView) v.findViewById(R.id.tags_grid);
        price = (TextView) v.findViewById(R.id.event_price_grid);
        place = (TextView) v.findViewById(R.id.event_location_grid);
        saveEvent = (ImageView) v.findViewById(R.id.imageView3_grid);
    }
}