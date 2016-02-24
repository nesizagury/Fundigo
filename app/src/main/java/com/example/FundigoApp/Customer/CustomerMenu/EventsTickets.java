package com.example.FundigoApp.Customer.CustomerMenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.Events.EventPage;
import com.example.FundigoApp.Events.EventsListAdapter;
import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethods;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class EventsTickets extends AppCompatActivity {
    private List<EventInfo> my_tickets_events_list = new ArrayList<EventInfo> ();
    boolean TICKETS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_events_tickets);
        ListView listT = (ListView) findViewById (R.id.listOfEventsTickets);

        getListOfEventsTickets ();
        if (TICKETS) {
            ListAdapter _adapter = new EventsListAdapter (EventsTickets.this, my_tickets_events_list, false);
            listT.setAdapter (_adapter);
        } else
            Toast.makeText (getApplicationContext (), "No Tickets to Display", Toast.LENGTH_SHORT).show ();

        final Intent intent = new Intent (this, EventPage.class); // When click on one of the events that in the list, it will be presented
        listT.setOnItemClickListener (new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Bundle b = new Bundle ();
                StaticMethods.onEventItemClick (i, my_tickets_events_list, intent);
                intent.putExtras (b);
                startActivity (intent);
            }
        });
    }

    //this method build a Arraylist of HashMaps from "Tickets" list in parse
    public void getListOfEventsTickets() {
        my_tickets_events_list.clear ();
        String _userPhoneNumber = GlobalVariables.CUSTOMER_PHONE_NUM;
        List<ParseObject> list;
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery ("EventsSeats");
            query.orderByDescending ("purchase_date").whereEqualTo ("buyer_phone", _userPhoneNumber);
            list = query.find ();
            if (list.size () != 0) {
                for (ParseObject obj : list) {
                    my_tickets_events_list.add (StaticMethods.getEventFromObjID (
                                                                                        obj.getString ("eventObjectId"),
                                                                                        GlobalVariables.ALL_EVENTS_DATA));
                }
                TICKETS = true;
            } else
                TICKETS = false;
        } catch (ParseException e) {
            Log.e ("Exception catch", e.toString ());
        } catch (Exception e) {
            Log.e ("Exception catch", e.toString ());
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        StaticMethods.onActivityResult (requestCode,
                                               data,
                                               this);
    }
}

