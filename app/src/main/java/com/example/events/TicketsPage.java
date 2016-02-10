package com.example.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
    static Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.tickets_page);

        intent = getIntent ();
        final String eventName = intent.getStringExtra ("eventName");

        eventsList.clear ();
        final List<Event> eventsList1 = new ArrayList<Event> ();
        ParseQuery<Event> query = new ParseQuery<Event> ("Event");
        List<Event> list = null;
        try {
            list = query.find ();
            for (Event eventParse : list) {
                if (eventName.equals (eventParse.getName ())) {
                    Event newEventParse = new Event ();
                    newEventParse.setName ("Event Name is : " + eventParse.getName ());
                    newEventParse.setPrice ("Event Price is : " + eventParse.getPrice ());
                    newEventParse.setNumOfTicketsLeft ("Num of Tickets available : " + eventParse.getNumOfTicketsLeft ());
                    eventsList1.add (newEventParse);
                    break;
                }
            }
            ArrayAdapter<Event> adapter = new ArrayAdapter<Event> (TicketsPage.this, android.R.layout.simple_list_item_1, eventsList1);
            list_view = (ListView) findViewById (R.id.list_tickets);
            list_view.setAdapter (adapter);
        } catch (ParseException e) {
            e.printStackTrace ();
        }
    }

    public void buyTicket(View view) {
        Bundle b = new Bundle ();
        Intent intent = new Intent (TicketsPage.this, VerifyCard.class);
        Intent intentHere = getIntent ();
        intent.putExtra ("eventName", intentHere.getStringExtra ("eventName"));
        intent.putExtra ("eventPrice", intentHere.getStringExtra ("eventPrice"));
        intent.putExtra("eventDate",intentHere.getStringExtra("eventDate"));// added to get the date of each Event into the Tickets table
        intent.putExtras (b);
        startActivity (intent);
    }

    @Override
    protected void onResume() {
        super.onResume ();
        final String eventName = intent.getStringExtra ("eventName");

        final List<Event> eventsList1 = new ArrayList<Event> ();
        ParseQuery<Event> query = new ParseQuery<Event> ("Event");
        List<Event> list = null;
        try {
            list = query.find ();
            for (Event eventParse : list) {
                if (eventName.equals (eventParse.getName ())) {
                    Event newEventParse = new Event ();
                    newEventParse.setName ("Event Name is : " + eventParse.getName ());
                    newEventParse.setPrice ("Event Price is : " + eventParse.getPrice ());
                    newEventParse.setNumOfTicketsLeft ("Num of Tickets available : " + eventParse.getNumOfTicketsLeft ());
                    eventsList1.add (newEventParse);
                    break;
                }
            }
            ArrayAdapter<Event> adapter = new ArrayAdapter<Event> (TicketsPage.this, android.R.layout.simple_list_item_1, eventsList1);
            list_view = (ListView) findViewById (R.id.list_tickets);
            list_view.setAdapter (adapter);
        } catch (ParseException e) {
            e.printStackTrace ();
        }
    }
}
