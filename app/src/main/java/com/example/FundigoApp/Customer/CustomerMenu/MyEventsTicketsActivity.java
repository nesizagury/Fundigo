package com.example.FundigoApp.Customer.CustomerMenu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethod.EventDataMethods;
import com.example.FundigoApp.Tickets.CustomerTicketsListAdapter;
import com.example.FundigoApp.Tickets.EventsSeats;
import com.example.FundigoApp.Tickets.EventsSeatsInfo;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyEventsTicketsActivity extends AppCompatActivity {
    static List<EventInfo> my_tickets_events_list = new ArrayList<EventInfo> ();
    static ArrayList<EventsSeatsInfo> my_tickets_list = new ArrayList<EventsSeatsInfo> ();

    ListView listT;
    TextView noTickets;
    ListAdapter _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_customer_events_tickets);
        noTickets = (TextView) findViewById (R.id.noTickets);
        listT = (ListView) findViewById (R.id.listOfEventsTickets);

        _adapter = new CustomerTicketsListAdapter (this, R.layout.content_events_tickets, my_tickets_list);
        listT.setAdapter (_adapter);

        getListOfEventsTickets ();
    }

    public void getListOfEventsTickets() {
        my_tickets_events_list.clear ();
        my_tickets_list.clear ();
        String _userPhoneNumber = GlobalVariables.CUSTOMER_PHONE_NUM;
        List<EventsSeats> list;
        try {
            ParseQuery<EventsSeats> query = ParseQuery.getQuery ("EventsSeats");
            query.whereEqualTo ("CustomerPhone", _userPhoneNumber).whereEqualTo ("sold", true).orderByDescending ("updatedAt");
            list = query.find ();
            if (list.size () != 0) {
                for (EventsSeats eventsSeats : list) {
                    Bitmap qrCode;
                    byte[] data = null;
                    ParseFile imageFile = (ParseFile) eventsSeats.get ("QR_Code");
                    if (imageFile != null) {
                        try {
                            data = imageFile.getData ();
                        } catch (ParseException e1) {
                            e1.printStackTrace ();
                        }
                        qrCode = BitmapFactory.decodeByteArray (data, 0, data.length);
                    } else {
                        qrCode = null;
                    }
                    EventInfo eventInfo = EventDataMethods.getEventFromObjID (eventsSeats.getString ("eventObjectId"), GlobalVariables.ALL_EVENTS_DATA);
                    Date current_date = new Date ();
                    Date event_date = eventInfo.getDate ();
                    eventInfo.setIsFutureEvent (event_date.after (current_date));
                    ParseObject soldTickets = eventsSeats.getSoldTicketsPointer ().fetch ();
                    my_tickets_events_list.add (eventInfo);
                    my_tickets_list.add (new EventsSeatsInfo (eventsSeats.getSeatNumber (),
                                                                     qrCode,
                                                                     soldTickets.getCreatedAt (),
                                                                     eventsSeats.getIntPrice (),
                                                                     eventInfo,
                                                                     soldTickets));
                }
                listT.deferNotifyDataSetChanged ();
            } else {
                noTickets.setText (R.string.no_tickets_to_display);
                noTickets.setVisibility (View.VISIBLE);
            }
        } catch (ParseException e) {
            e.printStackTrace ();
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    public void onClickButton(View v) {
        final Intent intent = new Intent (this, CustomerTicketsMoreDetailesActivity.class);
        try {
            View parentRow = (View) v.getParent ();
            ListView _listView = (ListView) parentRow.getParent ();
            int _position = _listView.getPositionForView (parentRow);
            intent.putExtra ("index", _position);
            startActivity (intent);
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}

