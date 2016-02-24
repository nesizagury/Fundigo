package com.example.FundigoApp.Events;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.FundigoApp.R;

public class EventListHolder {

    ImageView image;
    TextView date;
    TextView name;
    TextView tags;
    TextView price;
    TextView place;
    ImageView saveEvent;
    TextView artist;

    public EventListHolder(View v) {
        artist = (TextView) v.findViewById(R.id.tv_artist_eventAdapter);
        image = (ImageView) v.findViewById (R.id.imageView);
        date = (TextView) v.findViewById (R.id.event_date);
        name = (TextView) v.findViewById (R.id.event_name_tv);
        tags = (TextView) v.findViewById (R.id.tags);
        price = (TextView) v.findViewById (R.id.event_price);
        place = (TextView) v.findViewById (R.id.event_location);
        saveEvent = (ImageView) v.findViewById (R.id.imageView3);
    }
}