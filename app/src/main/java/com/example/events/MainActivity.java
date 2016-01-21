package com.example.events;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private final static String TAG = "MainActivity";

    ListView list_view;
    public static List<EventInfo> events_data = new ArrayList<EventInfo> ();
    private Button Event, SavedEvent, RealTime;
    private LinearLayout topToolBar;
    boolean didInit = false;
    static boolean isCustomer = false;
    static boolean isGuest = false;
    int customer_id;
    private TextView mLatitudeText, mLongitudeText;
    private Location mLastLocation;
    public static boolean turnGps = true;
    private AlertDialog.Builder alertDialog;
    private AlertDialog alert;
    public static boolean gps_enabled = false;
    public static boolean network_enabled = false;
    public static LocationManager LocationServices;
    public static Location loc;
    static final int REQUEST_CODE_MY_PICK = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        if (!didInit) {
            uploadUserData ();
            didInit = true;
        }
        setContentView (R.layout.activity_main);
        Intent intent = getIntent ();
        if (intent.getStringExtra ("chat_id") != null) {
            customer_id = Integer.parseInt (intent.getStringExtra ("chat_id"));
            isCustomer = true;

        }
        if (intent.getStringExtra ("is_guest") != null) {
            isGuest = true;
        }

        list_view = (ListView) findViewById (R.id.listView);
        topToolBar = (LinearLayout) findViewById (R.id.toolbar_up);
        Adapters adapts = new Adapters (this);
        list_view.setAdapter (adapts);
        list_view.setSelector (new ColorDrawable (Color.TRANSPARENT));
        list_view.setOnItemClickListener (this);
        Event = (Button) findViewById (R.id.BarEvent_button);
        SavedEvent = (Button) findViewById (R.id.BarSavedEvent_button);
        RealTime = (Button) findViewById (R.id.BarRealTime_button);
        RealTime.setOnClickListener (this);
        Event.setOnClickListener (this);
        SavedEvent.setOnClickListener (this);
        Event.setTextColor (Color.WHITE);
        loc = getLocation ();
        //if (loc != null)
            //Toast.makeText (getApplicationContext (), "" + loc.getLongitude () + " ," + loc.getLatitude (), Toast.LENGTH_LONG).show ();
        list_view.setOnScrollListener (new OnScrollListener () {

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }

            int mPosition = 0;
            int mOffset = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                int position = list_view.getFirstVisiblePosition ();
                View v = list_view.getChildAt (0);
                int offset = (v == null) ? 0 : v.getTop ();

                if (mPosition < position || (mPosition == position && mOffset < offset)) {
                    // Scrolled up
                    topToolBar.setVisibility (View.GONE);

                } else {
                    // Scrolled down
                    topToolBar.setVisibility (View.VISIBLE);
                }
            }
        });
    }


    /**
     * the function return the lastKnowenLocation
     *
     * @return lastKnowenLocation
     */
    public Location getLocation() {
        LocationManager locationManager = (LocationManager) this.getSystemService (Context.LOCATION_SERVICE);
        if (locationManager != null) {
            Location lastKnownLocationGPS = locationManager.getLastKnownLocation (LocationManager.GPS_PROVIDER);
            if (lastKnownLocationGPS != null) {
                return lastKnownLocationGPS;
            } else {
                if (ActivityCompat.checkSelfPermission (this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return null;
                }
                Location loc = locationManager.getLastKnownLocation (LocationManager.PASSIVE_PROVIDER);
                return loc;
            }
        } else {
            return null;
        }
    }


    @Override
    public void onClick(View v) {
        Intent newIntent = null;
        if (v.getId () == SavedEvent.getId ()) {
            newIntent = new Intent (this, SavedEvent.class);
            startActivity(newIntent);
        } else if (v.getId () == RealTime.getId ()) {
            newIntent = new Intent (this, RealTime.class);
            startActivity(newIntent);
        }
    }

//    public void city(MenuItem item) {
//        ArrayList<String> list = new ArrayList<String> ();
//
//        String[] locales = Locale.getISOCountries ();
//
//        for (String countryCode : locales) {
//
//            Locale obj = new Locale ("", countryCode);
//
//            System.out.println ("Country Name = " + obj.getDisplayCountry ());
//            list.add (obj.getDisplayCountry ());
//
//        }
//    }
//
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId ()) {
//            case R.id.Filter:
//                openFilterPage (item);
//                return true;
//            default:
//                return super.onOptionsItemSelected (item);
//
//        }
//    }

    public void openFilterPage(View v) {
        Intent filterPageIntent = new Intent (this, FilterPage.class);
        startActivity (filterPageIntent);
    }

    public void openMenuPage(View v) {
        Intent menuPageIntent = new Intent (this, Menu.class);
        startActivity (menuPageIntent);
    }

    public void uploadUserData() {

        Resources res = this.getResources ();
        String[] eventDate_list;
        String[] eventName_list;
        String[] eventTag_list;
        String[] eventPrice_list;
        String[] eventInfo_list;
        String[] eventPlace_list;

        eventName_list = res.getStringArray (R.array.eventNames);
        eventDate_list = res.getStringArray (R.array.eventDates);
        eventTag_list = res.getStringArray (R.array.eventTags);
        eventPrice_list = res.getStringArray (R.array.eventPrice);
        eventPlace_list = res.getStringArray (R.array.eventPlace);
        eventInfo_list = res.getStringArray (R.array.eventInfo);

        String arrToilet[] = getResources ().getStringArray (R.array.eventToiletService);
        String arrParking[] = getResources ().getStringArray (R.array.eventParkingService);
        String arrCapacity[] = getResources ().getStringArray (R.array.eventCapacityService);
        String arrATM[] = getResources ().getStringArray (R.array.eventATMService);

        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 14; i++) {
                events_data.add (new EventInfo (
                                                       R.mipmap.pic0 + i,
                                                       eventDate_list[i],
                                                       eventName_list[i],
                                                       eventTag_list[i],
                                                       eventPrice_list[i],
                                                       eventInfo_list[i],
                                                       eventPlace_list[i],
                                                       arrToilet[i],
                                                       arrParking[i],
                                                       arrCapacity[i],
                                                       arrATM[i])
                );
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> av, View view, int i, long l) {
        Bundle b = new Bundle ();
        Intent intent = new Intent (this, EventPage.class);
        Holder holder = (Holder) view.getTag ();
        intent.putExtra ("eventImage", events_data.get (i).getImageId ());
        intent.putExtra ("eventDate", events_data.get (i).getDate ());
        intent.putExtra ("eventName", events_data.get (i).getName ());
        intent.putExtra ("eventTags", events_data.get (i).getTags ());
        intent.putExtra ("eventPrice", events_data.get (i).getPrice ());
        intent.putExtra ("eventInfo", events_data.get (i).getInfo ());
        intent.putExtra ("eventPlace", events_data.get (i).getPlace ());

        intent.putExtra ("toilet", events_data.get (i).getToilet ());
        intent.putExtra ("parking", events_data.get (i).getParking ());
        intent.putExtra ("capacity", events_data.get (i).getCapacity ());
        intent.putExtra ("atm", events_data.get (i).getAtm ());


        b.putInt ("customer_id", customer_id);
        b.putInt ("producer_id", i + 1);
        intent.putExtras (b);
        startActivity (intent);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == REQUEST_CODE_MY_PICK) {
            String appName = data.getComponent ().flattenToShortString ();
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences (this);
            String name = sp.getString ("name", null);
            String date = sp.getString ("date", null);
            String place = sp.getString ("place", null);
            Log.e (TAG, "" + name + " " + date + " " + place);
            Log.e (TAG, "" + appName);
            if (appName.equals ("com.facebook.katana/com.facebook.composer.shareintent.ImplicitShareIntentHandlerDefaultAlias")) {
                ShareDialog shareDialog;
                shareDialog = new ShareDialog (this);

                ShareLinkContent linkContent = new ShareLinkContent.Builder ()
                                                       .setContentTitle ("I`m going to " + name)
                                                       .setImageUrl (Uri.parse ("https://lh3.googleusercontent.com/-V5wz7jKaQW8/VpvKq0rwEOI/AAAAAAAAB6Y/cZoicmGpQpc/s279-Ic42/pic0.jpg"))
                                                       .setContentDescription (
                                                                                      "C u there at " + date + " !" + "\n" + "At " + place)
                                                       .setContentUrl (Uri.parse ("http://eventpageURL.com/here"))
                                                       .build ();

                shareDialog.show (linkContent);
            } else {
                startActivity (data);
            }
        }
    }

}
