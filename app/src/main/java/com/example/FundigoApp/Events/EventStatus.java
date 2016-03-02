package com.example.FundigoApp.Events;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;

public class EventStatus extends Activity {

    TextView eventNameTV;
    TextView soldTV;
    TextView leftTV;
    TextView incomeTV;
    TextView futureTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_event_status);

        eventNameTV = (TextView) findViewById (R.id.eventNameTV);
        eventNameTV.setText ("" + getIntent ().getStringExtra ("name"));
        soldTV = (TextView) findViewById (R.id.soldTV);
        leftTV = (TextView) findViewById (R.id.leftTV);
        incomeTV = (TextView) findViewById (R.id.incomeTV);
        futureTV = (TextView) findViewById (R.id.futureTV);

        for (int i = 0; i < GlobalVariables.ALL_EVENTS_DATA.size (); i++) {
            EventInfo event = GlobalVariables.ALL_EVENTS_DATA.get (i);
            if (event.getParseObjectId ().equals (getIntent ().getStringExtra ("eventObjectId"))) {
                soldTV.setText (R.string.tickets_sold + event.getSold ());
                leftTV.setText (R.string.tickets_left + event.getTicketsLeft ());
                incomeTV.setText (R.string.sum_income + event.getIncome ());
                StringBuilder sb = new StringBuilder (event.getPrice ());
                sb.deleteCharAt (sb.length () - 1);
                int ticketsLeft = Integer.parseInt (event.getTicketsLeft ());
                int price = Integer.parseInt (sb.toString ());
                futureTV.setText (R.string.future_income + (price * ticketsLeft));
                break;
            }
        }
    }
}
