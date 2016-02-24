package com.example.FundigoApp.Customer.Social;

import android.view.View;
import android.widget.TextView;

import com.example.FundigoApp.R;

public class NotificationHolder {
    TextView message, date, eventName;

    public NotificationHolder(View view) {
        message = (TextView) view.findViewById (R.id.MaessagePushList);
        date = (TextView) view.findViewById (R.id.eventDatePushList);
        eventName = (TextView) view.findViewById (R.id.eventNamePushList);
    }
}
