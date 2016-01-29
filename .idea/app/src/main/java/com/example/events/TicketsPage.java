package com.example.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nesi on 31/12/2015.
 */
public class TicketsPage extends AppCompatActivity {

    List<Event> eventsList = new ArrayList<Event> ();
    ListView list_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.events_list);

        Intent intent = getIntent ();
        final String eventName = intent.getStringExtra ("eventName");

        ParseQuery<Event> query = new ParseQuery<> ("Event");
        try {
            List<Event> list = query.find ();
            for (Event event : list) {
                if (eventName.equals (event.getName ())) {
                    Event newEvent = new Event ();
                    newEvent.setName ("Event Name is : " + event.getName ());
                    newEvent.setPrice ("Event Price is : " + event.getPrice ());
                    newEvent.setNumOfTicketsLeft ("Tickets left after you bought your ticket : " + event.getNumOfTicketsLeft ());
                    eventsList.add (newEvent);
                    int tickets = Integer.parseInt (event.getNumOfTicketsLeft ());
                    int t = tickets - 1;
                    String left = Integer.toString (t);
                    Toast.makeText (TicketsPage.this, "Enjoy Yout Ticket!", Toast.LENGTH_SHORT).show ();
                    event.put ("NumOfTicketsLeft", left);
                    try {
                        event.save ();
                    } catch (Exception e1) {
                        e1.printStackTrace ();
                    }
                }
            }
            ArrayAdapter<Event> adapter = new ArrayAdapter<Event> (TicketsPage.this, android.R.layout.simple_list_item_1, eventsList);
            list_view = (ListView) findViewById (R.id.list_tickets);
            list_view.setAdapter (adapter);
        } catch (ParseException e) {
            e.printStackTrace ();
        }

    }
}
