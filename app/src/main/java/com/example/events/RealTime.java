package com.example.events;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
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

        Event = (Button) findViewById (R.id.BarEvent_button);
        RealTime = (Button) findViewById (R.id.BarRealTime_button);
        SavedEvent = (Button) findViewById (R.id.BarSavedEvent_button);

        Event.setOnClickListener (this);
        SavedEvent.setOnClickListener (this);
        RealTime.setOnClickListener (this);

        RealTime.setTextColor (Color.WHITE);

        if (MainActivity.loc != null) {
            loc.setLatitude (MainActivity.loc.getLatitude ());
            loc.setLongitude (MainActivity.loc.getLongitude ());
        } else {
            loc.setLatitude (y);
            loc.setLongitude (x);
        }
        Toast.makeText (this, "" + loc.getLongitude () + "  " + loc.getLatitude (), Toast.LENGTH_SHORT).show ();
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
            startActivity (newIntent.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP));


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
        ParseQuery<ParseObject> query = ParseQuery.getQuery ("Event");
        List<ParseObject> listObject = query.find ();
        int index = 0;
        for (int i = 0; i < listObject.size (); i++) {
            ParseObject tempObject = listObject.get (i);
            if (!arr.contains (tempObject)) {
                x = tempObject.getDouble ("X");
                y = tempObject.getDouble ("Y");
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

}
