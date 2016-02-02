package com.example.events;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.events.MyLocation.LocationResult;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private final static String TAG = "MainActivity";
    static final int REQUEST_CODE_MY_PICK = 1;

    ListView list_view;
    public static List<EventInfo> events_data = new ArrayList<EventInfo> ();
    public static List<EventInfo> filtered_events_data = new ArrayList<EventInfo> ();
    EventsListAdapter eventsListAdapter;
    private Button event, savedEvent, realTime, currentCityChosen;

    boolean didInit = false;
    static boolean isCustomer = false;
    static boolean isGuest = false;
    String customer_id;
    static Location loc;
    public static boolean turnGps = true;
    public static boolean gps_enabled = false;
    public static boolean network_enabled = false;
    public static LocationManager LocationServices;
    PopupMenu popup;
    String[] namesCity;
    int indexCityGPS = 0;
    HashMap<Integer, Integer> popUpIDToCityIndex = new HashMap<Integer, Integer> ();
    LocationListener locationListener;
    boolean userChoosedCityManually = false;
    boolean cityFoundGPS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);//
        if (!didInit) {
            uploadUserData ();
            didInit = true;
        }
        list_view = (ListView) findViewById (R.id.listView);
        event = (Button) findViewById (R.id.BarEvent_button);
        savedEvent = (Button) findViewById (R.id.BarSavedEvent_button);
        realTime = (Button) findViewById (R.id.BarRealTime_button);

        popup = new PopupMenu (MainActivity.this, currentCityChosen);
        currentCityChosen = (Button) findViewById (R.id.city_item);
        inflateCityMenu ();
        eventsListAdapter = new EventsListAdapter (this, filtered_events_data);
        realTime.setOnClickListener (this);
        event.setOnClickListener (this);
        savedEvent.setOnClickListener (this);

        list_view.setAdapter (eventsListAdapter);
        list_view.setSelector (new ColorDrawable (Color.TRANSPARENT));
        list_view.setOnItemClickListener (this);

        Intent intent = getIntent ();
        if (intent.getStringExtra ("chat_id") != null) {
            customer_id = intent.getStringExtra ("chat_id");
            isCustomer = true;
        }
        if (intent.getStringExtra ("is_guest") != null) {
            isGuest = true;
        }
        LocationResult locationResult = new LocationResult () {
            @Override
            public void gotLocation(Location location) {
                if (location != null) {
                    final String cityGPS = findCurrentCityGPS (location);
                    if (!cityGPS.isEmpty ()) {
                        cityFoundGPS = true;
                        popup.getMenu ().getItem (indexCityGPS).setTitle (namesCity[indexCityGPS]);
                        indexCityGPS = getCityIndexFromName (cityGPS);
                        popup.getMenu ().getItem (indexCityGPS).setTitle (cityGPS + "(GPS)");
                        if (!userChoosedCityManually) {
                            filterByCity (cityGPS);
                            runOnUiThread (new Runnable () {
                                @Override
                                public void run() {
                                    currentCityChosen.setText (cityGPS + "(GPS)");
                                    eventsListAdapter.notifyDataSetChanged ();
                                }
                            });

                        }

                    }
                }
            }
        };
        MyLocation myLocation = new MyLocation (this.getApplicationContext ());
        myLocation.getLocation (this, locationResult);
     //   updateDeviceLocationGPS ();
    }

    private void uploadUserData() {
        events_data.clear ();
        filtered_events_data.clear ();
        Resources res = this.getResources ();
        String[] eventDate_list;
        String[] eventName_list;
        String[] eventTag_list;
        String[] eventPrice_list;
        String[] eventInfo_list;
        String[] eventPlace_list;
        String[] eventCity_list;

        eventName_list = res.getStringArray (R.array.eventNames);
        eventDate_list = res.getStringArray (R.array.eventDates);
        eventTag_list = res.getStringArray (R.array.eventTags);
        eventPrice_list = res.getStringArray (R.array.eventPrice);
        eventPlace_list = res.getStringArray (R.array.eventPlace);
        eventInfo_list = res.getStringArray (R.array.eventInfo);
        eventCity_list = res.getStringArray (R.array.eventCity);

        String arrToilet[] = getResources ().getStringArray (R.array.eventToiletService);
        String arrParking[] = getResources ().getStringArray (R.array.eventParkingService);
        String arrCapacity[] = getResources ().getStringArray (R.array.eventCapacityService);
        String arrATM[] = getResources ().getStringArray (R.array.eventATMService);

        for (int i = 0; i < 15; i++) {
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
                                                   arrATM[i],
                                                   eventCity_list[i])
            );
        }
        filtered_events_data.addAll (events_data);
    }

    private void inflateCityMenu() {
        popup.getMenuInflater ().inflate (R.menu.popup_city, popup.getMenu ());
        loadCityNamesToPopUp ();
        filterByCity (namesCity[indexCityGPS]);
        currentCityChosen.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                popup = new PopupMenu (MainActivity.this, currentCityChosen);
                //Inflating the Popup using xml file
                popup.getMenuInflater ().inflate (R.menu.popup_city, popup.getMenu ());
                for (int i = 0; i < namesCity.length; i++) {
                    if (i == indexCityGPS && cityFoundGPS) {
                        popup.getMenu ().getItem (i).setTitle (namesCity[i] + "(GPS)");
                    } else {
                        popup.getMenu ().getItem (i).setTitle (namesCity[i]);
                    }
                }
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener (new PopupMenu.OnMenuItemClickListener () {
                    public boolean onMenuItemClick(MenuItem item) {
                        int i = popUpIDToCityIndex.get (item.getItemId ());
                        currentCityChosen.setText (item.getTitle ());
                        filterByCity (namesCity[i]);
                        eventsListAdapter.notifyDataSetChanged ();
                        userChoosedCityManually = true;
                        return true;
                    }
                });
                popup.show ();//showing popup menu
            }
        });
    }

    private void loadCityNamesToPopUp() {
        Resources rsc = getResources ();
        namesCity = rsc.getStringArray (R.array.popUp);
        try {
            for (int i = 0; i < namesCity.length; i++) {
                if (i == indexCityGPS && cityFoundGPS) {
                    popup.getMenu ().getItem (i).setTitle (namesCity[i] + "(GPS)");
                } else {
                    popup.getMenu ().getItem (i).setTitle (namesCity[i]);
                }
                popUpIDToCityIndex.put (popup.getMenu ().getItem (i).getItemId (), i);
            }
            if (!cityFoundGPS) {
                currentCityChosen.setText (popup.getMenu ().getItem (0).getTitle ());
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private void filterByCity(String cityName) {
        filtered_events_data.clear ();
        if (cityName.equals ("All Cities")) {
            filtered_events_data.addAll (events_data);
            return;
        } else {
            for (int i = 0; i < events_data.size (); i++) {
                if (events_data.get (i).getCity ().equals (cityName)) {
                    filtered_events_data.add (events_data.get (i));
                }
            }
        }
    }

   // public void updateDeviceLocationGPS() {
       // LocationManager locationManager = (LocationManager) this.getSystemService (Context.LOCATION_SERVICE);
       // locationListener = new MyLocationListener ();
       // if (ActivityCompat.checkSelfPermission (this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission (this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
         //   return;
       // }
       // locationManager.requestLocationUpdates (LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        //locationManager.requestLocationUpdates (LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
      //  locationManager.requestLocationUpdates (LocationManager.PASSIVE_PROVIDER, 0, 0, locationListener);
    //}

    @Override
    public void onClick(View v) {
        Intent newIntent = null;
        if (v.getId () == savedEvent.getId ()) {
            newIntent = new Intent (this, SavedEvent.class);
            startActivity (newIntent);
        } else if (v.getId () == realTime.getId ()) {
            newIntent = new Intent (this, RealTime.class);
            startActivity (newIntent);
        }
    }

    public void openFilterPage(View v) {
        Intent filterPageIntent = new Intent (this, FilterPage.class);
        startActivity (filterPageIntent);
    }

    public void openMenuPage(View v) {
        Intent menuPageIntent = new Intent (this, com.example.events.Menu.class);
        startActivity (menuPageIntent);
    }

    @Override
    public void onItemClick(AdapterView<?> av, View view, int i, long l) {
        Bundle b = new Bundle ();
        Intent intent = new Intent (this, EventPage.class);
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

        b.putString ("customer_id", customer_id);
        b.putString ("producer_id", Integer.toString (i + 1));
        intent.putExtras (b);
        startActivity (intent);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (data != null && requestCode == REQUEST_CODE_MY_PICK) {
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

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                String cityGPS = findCurrentCityGPS (location);
                if (!cityGPS.isEmpty ()) {
                    cityFoundGPS = true;
                    popup.getMenu ().getItem (indexCityGPS).setTitle (namesCity[indexCityGPS]);
                    indexCityGPS = getCityIndexFromName (cityGPS);
                    popup.getMenu ().getItem (indexCityGPS).setTitle (cityGPS + "(GPS)");
                    if (!userChoosedCityManually) {
                        filterByCity (cityGPS);
                        currentCityChosen.setText (cityGPS + "(GPS)");
                        eventsListAdapter.notifyDataSetChanged ();
                    }

                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    public String findCurrentCityGPS(Location loc) {
        Geocoder gcd = new Geocoder (this, Locale.ENGLISH);
        if (loc != null) {
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation (loc.getLatitude (), loc.getLongitude (), 1);
            } catch (IOException e) {
                e.printStackTrace ();
            }
            if (addresses != null && addresses.size () > 0) {
                String city = addresses.get (0).getLocality ();
                for (int i = 0; i < popup.getMenu ().size (); i++) {
                    MenuItem menuItem = popup.getMenu ().getItem (i);
                    if (menuItem.getTitle ().equals (city)) {
                        return city;
                    }
                }
            }
        }
        return "";
    }

    private int getCityIndexFromName(String name) {
        for (int i = 0; i < namesCity.length; i++) {
            String city = namesCity[i];
            if (city.equals (name)) {
                return i;
            }
        }
        return -1;
    }
}
