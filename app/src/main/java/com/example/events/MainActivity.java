package com.example.events;


import android.Manifest.permission;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.events.MyLocation.LocationResult;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private final static String TAG = "MainActivity";
    static final int REQUEST_CODE_MY_PICK = 1;

    ListView list_view;
    public static List<EventInfo> events_data = new ArrayList<EventInfo> ();
    public static List <EventInfo> filtered_events_data = new ArrayList<EventInfo> ();
    EventsListAdapter eventsListAdapter;
    Button event, savedEvent, realTime, currentCityChosen;

    boolean didInit = false;
    static boolean isCustomer = false;
    static boolean isGuest = false;
    String customer_id;
    static Location loc;
    public static boolean turnGps = true;
    public static boolean gps_enabled = false;
    public static boolean network_enabled = false;
    public static LocationManager LocationServices;
    private static LocationManager locationManager;
    PopupMenu popup;
    String[] namesCity;
    static int indexCityGPS = 0;
    HashMap<Integer, Integer> popUpIDToCityIndex = new HashMap<Integer, Integer> ();
    LocationListener locationListener;
    static boolean userChoosedCityManually = false;
    static boolean cityFoundGPS = false;
    Button create_button;
   static boolean isProducer = false;
    static String producerId;
    int toSkip = 0;
    int limit = 0;
    boolean nothingToLoad = false;
    Event lastEvent;
    private ProgressBar spinner;
    ListView artist_listView;
    public static List <String> artist_list = new ArrayList<String>();
    boolean onArtistList = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        Intent intent = getIntent();
        if (intent.getStringExtra("chat_id") != null) {
            customer_id = intent.getStringExtra("chat_id");
            isCustomer = true;
        }
        if (intent.getStringExtra("is_guest") != null) {
            isGuest = true;
        }

        if (intent.getStringExtra("is_producer") != null) {
            isProducer = true;
            producerId = intent.getStringExtra("producerId");
        }

        if(isCustomer || isGuest)
        {
            organizeCustomer();
        }
        else
        {
            organizeProducer();
        }
//        list_view.setOnScrollListener (new AbsListView.OnScrollListener () {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem,
//                                 int visibleItemCount, int totalItemCount) {
//
//                if (list_view.getLastVisiblePosition () == list_view.getAdapter ().getCount () - 1
//                            && list_view.getChildAt (list_view.getChildCount () - 1).getBottom () <= list_view.getHeight () && !nothingToLoad) {
//
//                    uploadUserData ();
//                    eventsListAdapter.notifyDataSetChanged ();
//
//
//                }
//            }
//        });

    }

    public void organizeProducer() {

        setContentView(R.layout.producer_avtivity_main);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Artists"));
        tabLayout.addTab(tabLayout.newTab().setText("Stats"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final TabPagerAdapter adapter = new TabPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



    }

    public void organizeCustomer() {

        setContentView(R.layout.activity_main);
        list_view = (ListView) findViewById(R.id.listView);
        list_view = (ListView) findViewById(R.id.listView);
        event = (Button) findViewById(R.id.BarEvent_button);
        savedEvent = (Button) findViewById(R.id.BarSavedEvent_button);
        realTime = (Button) findViewById(R.id.BarRealTime_button);

        popup = new PopupMenu(MainActivity.this, currentCityChosen);
        currentCityChosen = (Button) findViewById(R.id.city_item);
        inflateCityMenu();
        eventsListAdapter = new EventsListAdapter(this, filtered_events_data);
        realTime.setOnClickListener(this);
        event.setOnClickListener(this);
        savedEvent.setOnClickListener(this);

        if (!didInit) {
//            lastEvent = getLastEvent ();
            uploadUserData(null);
            didInit = true;
        }

        list_view.setAdapter(eventsListAdapter);
        list_view.setSelector(new ColorDrawable(Color.TRANSPARENT));
        list_view.setOnItemClickListener(this);


        LocationResult locationResult = new LocationResult() {
            @Override
            public void gotLocation(Location location) {
                if (location != null) {
                    final String cityGPS = findCurrentCityGPS(location);
                    if (!cityGPS.isEmpty()) {
                        cityFoundGPS = true;
                        popup.getMenu().getItem(indexCityGPS).setTitle(namesCity[indexCityGPS]);
                        indexCityGPS = getCityIndexFromName(cityGPS);
                        popup.getMenu().getItem(indexCityGPS).setTitle(cityGPS + "(GPS)");
                        if (!userChoosedCityManually) {
                            filterByCity(cityGPS);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    currentCityChosen.setText(cityGPS + "(GPS)");
                                    eventsListAdapter.notifyDataSetChanged();
                                }
                            });

                        }

                    }
                }
            }
        };

        MyLocation myLocation = new MyLocation(this.getApplicationContext());
        myLocation.getLocation(this, locationResult);
        updateDeviceLocationGPS();

    }



    private void uploadUserData(String artist) {
        events_data.clear();
        filtered_events_data.clear();

        ParseQuery<Event> query = new ParseQuery ("Event");
        if(artist != "" && artist != null)
        {
            query.whereEqualTo("artist",artist);
        }
        query.orderByDescending("createdAt");
//        query.setSkip (toSkip);
//        query.setLimit (7);
//        toSkip += 7;
        List<Event> events = null;

        try {

            events = query.find ();
            ParseFile imageFile;
            byte[] data;
            Bitmap bmp;

            for (int i = 0; i < events.size (); i++) {

                imageFile = (ParseFile) events.get (i).get ("ImageFile");
                if (imageFile != null) {
                    data = imageFile.getData ();
                    bmp = BitmapFactory.decodeByteArray (data, 0, data.length);
                } else
                    bmp = null;

                events_data.add (new EventInfo (
                        bmp,
                        events.get (i).getDate (),
                        events.get (i).getName (),
                        events.get (i).getTags (),
                        events.get (i).getPrice (),
                        events.get (i).getDescription (),
                        events.get (i).getAddress (), null, null, null, null, "Tiberias"));
                events_data.get (i).setProducerId(events.get(i).getProducerId());
                events_data.get(i).setArtist(events.get(i).getArtist());
                events_data.get(i).setIncome(events.get(i).getIncome());
                events_data.get(i).setSold(events.get(i).getSold());
                events_data.get(i).setTicketsLeft(events.get(i).getNumOfTicketsLeft());
//                if (lastEvent.equals (events.get (i))) {
//                    nothingToLoad = true;
//                }



            }
            filtered_events_data.addAll(events_data);

        } catch (ParseException e) {
            e.printStackTrace ();
        }
    }

    private void inflateCityMenu() {
        popup.getMenuInflater ().inflate(R.menu.popup_city, popup.getMenu());
        loadCityNamesToPopUp();
        if (userChoosedCityManually) {
            filterByCity (namesCity[0]);
        } else {
            filterByCity (namesCity[indexCityGPS]);
        }
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
            if (userChoosedCityManually) {
                currentCityChosen.setText (popup.getMenu ().getItem (0).getTitle ());
            } else {
                currentCityChosen.setText (popup.getMenu ().getItem (indexCityGPS).getTitle ());
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private void filterByCity(String cityName) {
        filtered_events_data.clear();
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

    public void updateDeviceLocationGPS() {
        boolean gps_enabled = false;
        boolean network_enabled = false;
        boolean passive_enabled = false;

        locationManager = (LocationManager) this.getSystemService (Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener ();
        try {
            gps_enabled = locationManager.isProviderEnabled (LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = locationManager.isProviderEnabled (LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            passive_enabled = locationManager.isProviderEnabled (LocationManager.PASSIVE_PROVIDER);
        } catch (Exception ex) {
        }
        if (gps_enabled) {
            if (ActivityCompat.checkSelfPermission (this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission (this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //do none
            } else {
                locationManager.requestLocationUpdates (LocationManager.GPS_PROVIDER, 10000, 0, locationListener);

            }
        }
        if (network_enabled) {
            locationManager.requestLocationUpdates (LocationManager.NETWORK_PROVIDER, 10000, 0, locationListener);
        }
        if (passive_enabled) {
            locationManager.requestLocationUpdates (LocationManager.PASSIVE_PROVIDER, 10000, 0, locationListener);
        }
    }

    @Override
    public void onClick(View v) {
        Intent newIntent = null;
        if (v.getId () == savedEvent.getId ()) {
            newIntent = new Intent (this, SavedEvent.class);
            startActivity(newIntent);
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
        if (events_data.get (i).getImageId () != null) {
            Bitmap bmp = events_data.get (i).getImageId ();
            ByteArrayOutputStream stream = new ByteArrayOutputStream ();
            bmp.compress (Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray ();
            intent.putExtra ("eventImage", byteArray);
        } else
            intent.putExtra ("eventImage", "");
        intent.putExtra ("eventDate", filtered_events_data.get (i).getDate ());
        intent.putExtra ("eventName", filtered_events_data.get (i).getName ());
        intent.putExtra ("eventTags", filtered_events_data.get (i).getTags ());
        intent.putExtra ("eventPrice", filtered_events_data.get (i).getPrice ());
        intent.putExtra ("eventInfo", filtered_events_data.get (i).getInfo ());
        intent.putExtra ("eventPlace", filtered_events_data.get (i).getPlace ());
        intent.putExtra ("toilet", filtered_events_data.get (i).getToilet ());
        intent.putExtra ("parking", filtered_events_data.get (i).getParking ());
        intent.putExtra ("capacity", filtered_events_data.get (i).getCapacity ());
        intent.putExtra ("atm", filtered_events_data.get (i).getAtm ());
        intent.putExtra ("index", i);

        b.putString ("customer_id", customer_id);
        if (producerId != null)
            b.putInt ("producer_id", Integer.parseInt (producerId));
        else
            b.putInt ("producer_id", Integer.parseInt (events_data.get (i).getProducerId ()));
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

    @Override
    public void onPause() {
        super.onPause();
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission (this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission (this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates (locationListener);
        }
    }

    @Override
    public void onResume() {
        super.onResume ();
        if(isCustomer) {
            updateDeviceLocationGPS();
            eventsListAdapter.notifyDataSetChanged();
        }
    }

    public void createEvent(View view) {

        Intent intent = new Intent (MainActivity.this, CreateEventActivity.class);
        intent.putExtra("create","true");
        startActivity (intent);

    }


//    public Event getLastEvent() {
//
//        ParseQuery<Event> getSizeQuery = new ParseQuery ("Event");
//        getSizeQuery.orderByAscending ("createdAt");
//        lastEvent = null;
//        try {
//            lastEvent = getSizeQuery.getFirst ();
//        } catch (ParseException e) {
//            e.printStackTrace ();
//        }
//
//        return lastEvent;
//
//    }



}
