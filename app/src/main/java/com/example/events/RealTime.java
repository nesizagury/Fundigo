package com.example.events;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by benjamin on 01/01/2016.
 */
public class RealTime extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private GridView gridView;
    private Button Event, RealTime, SavedEvent;
    private Toolbar toolbar2;
    private double x = 32.0408830, y = 34.46055;
    protected static Location loc = new Location ("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_real_time);
        Calendar c = Calendar.getInstance();
        long first = c.getTimeInMillis();
        Event = (Button) findViewById (R.id.BarEvent_button);
        RealTime = (Button) findViewById (R.id.BarRealTime_button);
        SavedEvent = (Button) findViewById (R.id.BarSavedEvent_button);

        Event.setOnClickListener (this);
        SavedEvent.setOnClickListener (this);
        RealTime.setOnClickListener (this);

        RealTime.setTextColor (Color.WHITE);
        if (MainActivity.loc == null) turnOnGps ();

        if (MainActivity.loc != null) {
            loc.setLatitude (MainActivity.loc.getLatitude ());
            loc.setLongitude (MainActivity.loc.getLongitude ());
        } else {
            loc.setLatitude (y);
            loc.setLongitude (x);
        }
        //Toast.makeText (this, "" + loc.getLongitude () + "  " + loc.getLatitude (), Toast.LENGTH_SHORT).show ();
        ArrayList<Event> arrayList = new ArrayList<> ();
        try {
            arrayList = sortList ();
        } catch (ParseException e) {
        }


        gridView = (GridView) findViewById (R.id.gridview);
        Adapters adapters = new Adapters (this, arrayList);
        gridView.setAdapter (adapters);
        gridView.setSelector (new ColorDrawable (Color.TRANSPARENT));
        gridView.setOnItemClickListener (this);
        long seconds = c.getTimeInMillis();
        Toast.makeText(this, ""+(seconds-first), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId ();
        Intent newIntent = null;
        if (vId == Event.getId ()) {
            newIntent = new Intent (this, MainActivity.class);
        } else if (vId == SavedEvent.getId ()) {
            newIntent = new Intent (this, com.example.events.SavedEvent.class);
        }
        if (vId != RealTime.getId ())
            startActivity (newIntent);


    }

    /**
     * Called when the user clicks the filter button
     */
    public void openFilterPage(View v) {
        Intent filterPageIntent = new Intent (this, FilterPage.class);
        startActivity (filterPageIntent);
    }

    public ArrayList<Event> sortList() throws ParseException {
        double x, y;
        ArrayList<Event> arr = new ArrayList<> ();
        ParseQuery<Event> query = ParseQuery.getQuery ("Event");
        List<Event> listObject = query.find ();
        int index = 0;
        for (int i = 0; i < listObject.size (); i++) {
            Event tempObject = listObject.get (i);
            if (!arr.contains (tempObject)) {
                x = tempObject.getX();
                y = tempObject.getY();
                Event tempEvent = new Event ();
                tempEvent.setLocation (x, y);
                tempEvent.setName (tempObject.getString ("Name"));
                arr.add (index++, tempEvent);
            }
        }

        Collections.sort (arr, new Comparator<Event> () {
            @Override
            public int compare(Event a, Event b) {
                if (a.getdis () < b.getdis ()) return -1;
                if (a.getdis () >= b.getdis ()) return 1;
                return 0;
            }
        });
        return arr;
    }


    @Override
    public void onItemClick(AdapterView<?> av, View view, int i, long l) {
        Bundle b = new Bundle ();
        Intent intent = new Intent (this, EventPage.class);
        Holder holder = (Holder) view.getTag ();
        EventInfo event = (EventInfo) holder.image.getTag ();
        intent.putExtra ("eventImage", MainActivity.events_data.get (i).getImageId ());
        intent.putExtra ("eventDate", MainActivity.events_data.get (i).getDate ());
        intent.putExtra ("eventName", MainActivity.events_data.get (i).getName ());
        intent.putExtra ("eventTags", MainActivity.events_data.get (i).getTags ());
        intent.putExtra ("eventPrice", MainActivity.events_data.get (i).getPrice ());
        intent.putExtra ("eventInfo", MainActivity.events_data.get (i).getInfo ());
        intent.putExtra ("eventPlace", MainActivity.events_data.get (i).getPlace ());
        b.putInt ("userIndex", i);
        intent.putExtras (b);
        startActivity (intent);
    }

    private void turnOnGps() {
        MainActivity.turnGps = false;
        try {
            MainActivity.gps_enabled = MainActivity.LocationServices.isProviderEnabled (LocationManager.GPS_PROVIDER);
            MainActivity.network_enabled = MainActivity.LocationServices.isProviderEnabled (LocationManager.NETWORK_PROVIDER);
        } catch (Exception x) {

        }
        if (!MainActivity.gps_enabled && !MainActivity.network_enabled) {
//            alertDialog = new AlertDialog.Builder (this);
//            alertDialog.setMessage ("turn on your GPS").setPositiveButton ("OK", new DialogInterface.OnClickListener () {
//
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    Intent intent = new Intent (Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                    startActivity (intent);
//                    dialog.dismiss ();
//                }
//            }).setNegativeButton ("Cancel", new DialogInterface.OnClickListener () {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss ();
//                }
//            });
//            alertDialog.create ();
//            alertDialog.show ();
            Toast.makeText (getApplicationContext (), "GPS off, turn on to see real-time events", Toast.LENGTH_LONG).show ();
        }
    }

    public void openMenuPage(View v) {
        Intent menuPageIntent = new Intent (this, com.example.events.Menu.class);
        startActivity (menuPageIntent);
    }

}
