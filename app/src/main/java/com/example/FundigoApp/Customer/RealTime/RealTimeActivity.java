package com.example.FundigoApp.Customer.RealTime;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.Events.EventPage;
import com.example.FundigoApp.FilterPage;
import com.example.FundigoApp.MainActivity;
import com.example.FundigoApp.R;
import com.example.FundigoApp.Customer.SavedEvents.SavedEventActivity;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RealTimeActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private GridView gridView;
    private Button Event, RealTime, SavedEvent;
    private TextView turnOnGPS;
    public static Location loc = new Location ("");
    public static List<EventInfo> events_data = new ArrayList<EventInfo> ();
    public static List<EventInfo> events_data_filtered = new ArrayList<EventInfo> ();
    EventsGridAdapter eventsGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_real_time);

        Event = (Button) findViewById (R.id.BarEvent_button);
        RealTime = (Button) findViewById (R.id.BarRealTime_button);
        SavedEvent = (Button) findViewById (R.id.BarSavedEvent_button);
        turnOnGPS = (TextView) findViewById (R.id.textView8);
        Event.setOnClickListener (this);
        SavedEvent.setOnClickListener (this);
        RealTime.setOnClickListener (this);

        RealTime.setTextColor (Color.WHITE);
        if (MainActivity.loc == null) {
            turnOnGps ();
        }

        if (MainActivity.loc != null) {
            loc.setLatitude (MainActivity.loc.getLatitude ());
            loc.setLongitude (MainActivity.loc.getLongitude ());
            events_data = getSortedListByDist ();
            events_data_filtered.addAll (events_data);
        }

        gridView = (GridView) findViewById (R.id.gridview);
        eventsGridAdapter = new EventsGridAdapter (this, events_data_filtered);
        gridView.setAdapter (eventsGridAdapter);
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
            newIntent = new Intent (this, SavedEventActivity.class);
        }
        if (vId != RealTime.getId ()) {
            startActivity (newIntent);
        }
    }

    /**
     * Called when the user clicks the filter button
     */
    public void openFilterPage(View v) {
        Intent filterPageIntent = new Intent (this, FilterPage.class);
        startActivity (filterPageIntent);
    }

    public List<EventInfo> getSortedListByDist() {
        List<EventInfo> arr = new ArrayList<> ();
        List<EventInfo> all_events_list = MainActivity.all_events_data;
        for (int i = 0; i < all_events_list.size (); i++) {
            EventInfo event = all_events_list.get (i);
            double latitude = event.getX ();
            double longitude = event.getY ();
            Location locationEvent = new Location ("eventPlace");
            locationEvent.setLatitude (latitude);
            locationEvent.setLongitude (longitude);
            double distance = (double) MainActivity.loc.distanceTo (locationEvent) / 1000;
            DecimalFormat df = new DecimalFormat ("#.##");
            String dx = df.format (distance);
            distance = Double.valueOf (dx);
            event.setDist (distance);
            arr.add (event);
        }
        Collections.sort (arr, new Comparator<EventInfo> () {
            @Override
            public int compare(EventInfo a, EventInfo b) {
                if (a.getDist () < b.getDist ()) return -1;
                if (a.getDist () >= b.getDist ()) return 1;
                return 0;
            }
        });
        return arr;
    }

    @Override
    public void onItemClick(AdapterView<?> av, View view, int i, long l) {
        Bundle b = new Bundle ();
        Intent intent = new Intent (this, EventPage.class);
        if (events_data_filtered.get (i).getImageId () != null) {
            Bitmap bmp = events_data_filtered.get (i).getImageId ();
            ByteArrayOutputStream stream = new ByteArrayOutputStream ();
            bmp.compress (Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray ();
            intent.putExtra ("eventImage", byteArray);
        } else
            intent.putExtra ("eventImage", "");
        intent.putExtra ("eventDate", events_data_filtered.get (i).getDate ());
        intent.putExtra ("eventName", events_data_filtered.get (i).getName ());
        intent.putExtra ("eventTags", events_data_filtered.get (i).getTags ());
        intent.putExtra ("eventPrice", events_data_filtered.get (i).getPrice ());
        intent.putExtra ("eventInfo", events_data_filtered.get (i).getInfo ());
        intent.putExtra ("eventPlace", events_data_filtered.get (i).getPlace ());
        intent.putExtra ("toilet", events_data_filtered.get (i).getToilet ());
        intent.putExtra ("parking", events_data_filtered.get (i).getParking ());
        intent.putExtra ("capacity", events_data_filtered.get (i).getCapacity ());
        intent.putExtra ("atm", events_data_filtered.get (i).getAtm ());
        intent.putExtra ("index", events_data_filtered.get (i).getIndexInFullList ());

        b.putString ("customer_id", MainActivity.customer_id);
        if (MainActivity.producerId != null)
            b.putString ("producer_id", MainActivity.producerId);
        else
            b.putString ("producer_id", events_data_filtered.get (i).getProducerId ());
        intent.putExtras (b);
        startActivity (intent);
    }

    private void turnOnGps() {
        turnOnGPS.setVisibility (View.VISIBLE);
    }

    public void openMenuPage(View v) {
        Intent menuPageIntent = new Intent (this, com.example.FundigoApp.Menu.class);
        startActivity (menuPageIntent);
    }

    @Override
    protected void onResume() {
        super.onResume ();
        filterByFilterName (MainActivity.currentFilterName);
    }

    public void filterByFilterName(String currentFilterName) {
        ArrayList<EventInfo> tempEventsList = new ArrayList<> ();
        if (currentFilterName.isEmpty ()) {
            tempEventsList.addAll (events_data);
        } else {
            for (int i = 0; i < events_data.size (); i++) {
                if (currentFilterName.isEmpty () ||
                            (currentFilterName.equals (events_data.get (i).getFilterName ()))) {
                    tempEventsList.add (events_data.get (i));
                }
            }
        }
        events_data_filtered.clear ();
        events_data_filtered.addAll (tempEventsList);
        eventsGridAdapter.notifyDataSetChanged ();
    }
}
