package com.example.events;

import android.view.View;
import android.widget.TextView;

/**
 * Created by mirit-binbin on 11/02/2016.
 */
public class PushHolder
{
    TextView message,date,eventName;

    public PushHolder(View view)
    {
        message=(TextView) view.findViewById(R.id.MaessagePushList);
        date=(TextView)view.findViewById(R.id.eventDatePushList);
        eventName=(TextView)view.findViewById(R.id.eventNamePushList);
    }

}
