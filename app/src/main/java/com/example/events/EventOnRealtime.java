package com.example.events;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by מנהל on 11/02/2016.
 */
public class EventOnRealtime extends Activity {

    TextView eventNameTV;
    TextView enteredTV;
    String event_name;
    String event_date;
   static String sumGuest = "";
    boolean eventDay = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_on_realtime);
        eventNameTV = (TextView) findViewById(R.id.eventTV);
        enteredTV = (TextView) findViewById(R.id.sumGuestTV);
        event_name = getIntent().getStringExtra("eventName");
        event_date = getIntent().getStringExtra("eventDate");
        eventNameTV.setText(event_name + " Real Time");

        Toast.makeText(getApplicationContext(),"artist = " + getIntent().getStringExtra("artist"), Toast.LENGTH_SHORT).show();

        ParseQuery<RealTimeEvent> query = new ParseQuery ("RealTimeEvent");
        query.whereEqualTo("producer", MainActivity.producerId);
        query.orderByAscending("createdAt");

        List <RealTimeEvent> events = null;

        try {
            events = query.find ();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date eventDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        Date todayDate = Calendar.getInstance().getTime();

        eventDate = null;
        try {
            eventDate = dateFormat.parse(event_date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        if(eventDate.getDate() == todayDate.getDate() && eventDate.getMonth() == todayDate.getMonth()) {

            for (int i = 0; i < events.size(); i++) {

                if (events.get(i).getEventName().equals(event_name)) {

                    enteredTV.setText("Guest's Entered: " + events.get(i).getGuestIn());
                    sumGuest = events.get(i).getGuestIn();
                    eventDay = true;

                }

            }
        }

    }

    public void addGuest(View v)
    {
        if(eventDay) {
            Intent intent = new Intent(EventOnRealtime.this, RealTimeGuestAddition.class);
            intent.putExtra("name", event_name);
            intent.putExtra("artist", getIntent().getStringExtra("artist"));
            intent.putExtra("guestIn", sumGuest);
            startActivity(intent);
        }
        else
            Toast.makeText(getApplicationContext(), "Event is on " + event_date, Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onResume() {
        super.onResume();

        enteredTV.setText("Guest's Entered: " + sumGuest);


    }
}
