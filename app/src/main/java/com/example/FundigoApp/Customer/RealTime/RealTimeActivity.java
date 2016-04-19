package com.example.FundigoApp.Customer.RealTime;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.FundigoApp.Customer.CustomerMenu.MenuActivity;
import com.example.FundigoApp.Customer.SavedEvents.SavedEventActivity;
import com.example.FundigoApp.Customer.Social.MyNotificationsActivity;
import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.Events.EventPageActivity;
import com.example.FundigoApp.FilterPageActivity;
import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.example.FundigoApp.SearchActivity;
import com.example.FundigoApp.StaticMethod.EventDataMethods;
import com.example.FundigoApp.StaticMethod.GPSMethods;
import com.example.FundigoApp.StaticMethod.GPSMethods.GpsICallback;
import com.example.FundigoApp.StaticMethod.GeneralStaticMethods;
import com.example.FundigoApp.StaticMethod.EventDataMethods.GetEventsDataCallback;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RealTimeActivity extends AppCompatActivity implements View.OnClickListener,
                                                                           AdapterView.OnItemClickListener,
                                                                           GetEventsDataCallback,
                                                                           GpsICallback {

    private GridView gridView;
    private Button Event, RealTime, SavedEvent;
    private TextView turnOnGPS;
    private static List<EventInfo> events_sorted_by_dist_data = new ArrayList<EventInfo> ();
    private static List<EventInfo> events_data_filtered = new ArrayList<EventInfo> ();
    EventsGridAdapter eventsGridAdapter;
    ImageView search, notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_real_time);

        Event = (Button) findViewById (R.id.BarEvent_button);
        RealTime = (Button) findViewById (R.id.BarRealTime_button);
        SavedEvent = (Button) findViewById (R.id.BarSavedEvent_button);
        turnOnGPS = (TextView) findViewById (R.id.turnOnGps);
        Event.setOnClickListener (this);
        SavedEvent.setOnClickListener (this);
        RealTime.setOnClickListener (this);
        notification = (ImageView) findViewById (R.id.notification_item);
        notification.setOnClickListener (this);
        search = (ImageView) findViewById (R.id.search);
        search.setOnClickListener (this);

        RealTime.setTextColor (Color.WHITE);
        if (GlobalVariables.MY_LOCATION == null && !GPSMethods.isLocationEnabled (this)) {
            turnOnGps ();
        }

        if (GlobalVariables.ALL_EVENTS_DATA.size () == 0) {
            Intent intent = new Intent (this, EventPageActivity.class);
            EventDataMethods.downloadEventsData (this, null, this.getApplicationContext (), intent);
        } else {
            if (GlobalVariables.MY_LOCATION != null  && GPSMethods.isLocationEnabled (this)) {
                events_sorted_by_dist_data = getSortedListByDist ();
                List<EventInfo> tempFilteredList =
                        GeneralStaticMethods.filterByFilterName (GlobalVariables.CURRENT_FILTER_NAME, events_sorted_by_dist_data);
                events_data_filtered.clear ();
                events_data_filtered.addAll (tempFilteredList);
            } else {
                GPSMethods.updateDeviceLocationGPS (this.getApplicationContext (), this);
            }
        }

        gridView = (GridView) findViewById (R.id.gridview);
        eventsGridAdapter = new EventsGridAdapter (this, events_data_filtered);
        gridView.setAdapter (eventsGridAdapter);
        gridView.setSelector (new ColorDrawable (Color.TRANSPARENT));
        gridView.setOnItemClickListener (this);
    }

    private void turnOnGps() {
        turnOnGPS.setVisibility (View.VISIBLE);
    }

    @Override
    public void gpsCallback() {
        if (GlobalVariables.ALL_EVENTS_DATA.size () > 0) {
            events_sorted_by_dist_data = getSortedListByDist ();
            List<EventInfo> tempFilteredList =
                    GeneralStaticMethods.filterByFilterName (GlobalVariables.CURRENT_FILTER_NAME, events_sorted_by_dist_data);
            events_data_filtered.clear ();
            events_data_filtered.addAll (tempFilteredList);
            eventsGridAdapter.notifyDataSetChanged ();
        }
        turnOnGPS.setVisibility (View.GONE);
    }

    @Override
    public void eventDataCallback() {
        if (GlobalVariables.MY_LOCATION != null) {
            events_sorted_by_dist_data = getSortedListByDist ();
            List<EventInfo> tempFilteredList =
                    GeneralStaticMethods.filterByFilterName (GlobalVariables.CURRENT_FILTER_NAME, events_sorted_by_dist_data);
            events_data_filtered.clear ();
            events_data_filtered.addAll (tempFilteredList);
            eventsGridAdapter.notifyDataSetChanged ();
            turnOnGPS.setVisibility (View.GONE);
        } else {
            GPSMethods.updateDeviceLocationGPS (this.getApplicationContext (), this);
        }
    }

    public List<EventInfo> getSortedListByDist() {
        List<EventInfo> arr = new ArrayList<> ();
        List<EventInfo> all_events_list = GlobalVariables.ALL_EVENTS_DATA;
        for (int i = 0; i < all_events_list.size (); i++) {
            EventInfo event = all_events_list.get (i);
            double latitude = event.getX ();
            double longitude = event.getY ();
            Location locationEvent = new Location ("eventPlace");
            locationEvent.setLatitude (latitude);
            locationEvent.setLongitude (longitude);
            double distance = (double) GlobalVariables.MY_LOCATION.distanceTo (locationEvent) / 1000;
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
    public void onClick(View v) {
        int vId = v.getId ();
        Intent newIntent = null;
        if (vId == Event.getId ()) {
            finish ();
        } else if (vId == SavedEvent.getId ()) {
            newIntent = new Intent (this, SavedEventActivity.class);
            startActivity (newIntent);
            finish();
        } else if (v.getId () == search.getId ()) {
            newIntent = new Intent (this, SearchActivity.class);
            startActivity (newIntent);
        } else if (v.getId () == notification.getId ()) {
            newIntent = new Intent (this, MyNotificationsActivity.class);
            startActivity (newIntent);
        }
    }

    public void openFilterPage(View v) {
        Intent filterPageIntent = new Intent (this, FilterPageActivity.class);
        startActivity (filterPageIntent);
    }

    @Override
    public void onItemClick(AdapterView<?> av, View view, int i, long l) {
        Bundle b = new Bundle ();
        Intent intent = new Intent (this, EventPageActivity.class);
        EventDataMethods.onEventItemClick (i, events_data_filtered, intent);
        intent.putExtras (b);
        startActivity (intent);
    }

    public void openMenuPage(View v) {
        Intent menuPageIntent = new Intent (this, MenuActivity.class);
        startActivity (menuPageIntent);
    }

    public void openNotificationPage(View v) {
        Intent i = new Intent (this, MyNotificationsActivity.class);
        startActivity (i);
    }

    public void openSearch(View v) {
        Intent i = new Intent (this, SearchActivity.class);
        startActivity (i);
    }


    @Override
    protected void onResume() {
        super.onResume ();
        List<EventInfo> tempFilteredList =
                GeneralStaticMethods.filterByFilterName (GlobalVariables.CURRENT_FILTER_NAME, events_sorted_by_dist_data);
        events_data_filtered.clear ();
        events_data_filtered.addAll (tempFilteredList);
        eventsGridAdapter.notifyDataSetChanged ();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        GeneralStaticMethods.onActivityResult (requestCode,
                                                      data,
                                                      this);
    }
}
