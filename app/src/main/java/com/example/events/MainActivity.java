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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    static final int REQUEST_CODE_MY_PICK = 1;

    ListView list_view;
    public static List<EventInfo> all_events_data = new ArrayList<EventInfo> ();
    private static List<EventInfo> filtered_events_data = new ArrayList<EventInfo> ();
    public static EventsListAdapter eventsListAdapter;
    Button event, savedEvent, realTime;
    static Button currentCityButton;
    ImageView search,notification;

    static boolean isCustomer = false;
    static boolean isGuest = false;
    public static String customer_id;
    static String producerId;

    public static boolean turnGps = true;
    public static boolean gps_enabled = false;
    public static boolean network_enabled = false;
    public static LocationManager LocationServices;
    private static LocationManager locationManager;
    static PopupMenu popup;
    public static String[] namesCity;
    static Location loc;
    public static String cityGPS = "";
    public static int indexCityGPS = 0;
    public static HashMap<Integer, Integer> popUpIDToCityIndex = new HashMap<Integer, Integer> ();
    LocationListener locationListener;
    public static boolean userChoosedCityManually = false;
    public static int indexCityChossen = 0;
    static boolean cityFoundGPS = false;

    Button create_button;

    public static String currentFilterName = "";

    static boolean savedAcctivityRunnig = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView(R.layout.activity_main);
        list_view = (ListView) findViewById (R.id.listView);
        event = (Button) findViewById (R.id.BarEvent_button);
        savedEvent = (Button) findViewById (R.id.BarSavedEvent_button);
        realTime = (Button) findViewById (R.id.BarRealTime_button);

        popup = new PopupMenu (MainActivity.this, currentCityButton);
        currentCityButton = (Button) findViewById (R.id.city_item);
        eventsListAdapter = new EventsListAdapter (this, filtered_events_data, false);
        realTime.setOnClickListener (this);
        event.setOnClickListener (this);
        savedEvent.setOnClickListener (this);

        search = (ImageView) findViewById (R.id.search);
        notification = (ImageView) findViewById (R.id.notification_item);
        notification.setOnClickListener(this);
        search.setOnClickListener (this);

        list_view.setAdapter (eventsListAdapter);
        list_view.setSelector (new ColorDrawable (Color.TRANSPARENT));
        list_view.setOnItemClickListener (this);

        uploadUserData ();
        inflateCityMenu ();

        Intent intent = getIntent ();
        if (intent.getStringExtra ("chat_id") != null) {
            customer_id = intent.getStringExtra ("chat_id");
            isCustomer = true;
        }
        if (intent.getStringExtra ("is_guest") != null) {
            isGuest = true;
        }
        if (intent.getStringExtra ("is_producer") != null) {
            producerId = intent.getStringExtra ("producerId");
            create_button = (Button) findViewById (R.id.create_button);
            create_button.setVisibility (View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userChoosedCityManually) {
            filterByCity (namesCity[indexCityChossen]);
        } else if (!cityGPS.isEmpty ()) {
            filterByCity (cityGPS);
        }
        if (!currentFilterName.isEmpty ()) {
            filterByFilterChosen (currentFilterName);
        }
    }

    private void uploadUserData() {
        final ArrayList<EventInfo> tempEventsList = new ArrayList<> ();
        ParseQuery<Event> query = new ParseQuery ("Event");
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<Event>() {
            public void done(List<Event> eventParses, ParseException e) {
                if (e == null) {
                    ParseFile imageFile;
                    byte[] data = null;
                    Bitmap bmp;
                    for (int i = 0; i < eventParses.size(); i++) {
                        imageFile = (ParseFile) eventParses.get(i).get("ImageFile");
                        if (imageFile != null) {
                            try {
                                data = imageFile.getData();
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                        } else {
                            bmp = null;
                        }
                        tempEventsList.add(new EventInfo(
                                bmp,
                                eventParses.get(i).getDate(),
                                eventParses.get(i).getName(),
                                eventParses.get(i).getTags(),
                                eventParses.get(i).getPrice(),
                                eventParses.get(i).getDescription(),
                                eventParses.get(i).getAddress(),
                                eventParses.get(i).getEventToiletService(),
                                eventParses.get(i).getEventParkingService(),
                                eventParses.get(i).getEventCapacityService(),
                                eventParses.get(i).getEventATMService(),
                                eventParses.get(i).getCity(),
                                i,
                                eventParses.get(i).getFilterName()));
                        tempEventsList.get(i).setProducerId(eventParses.get(i).getProducerId());
                    }
                    updateSavedEvents(tempEventsList);
                    all_events_data.clear();
                    all_events_data.addAll(tempEventsList);
                    filtered_events_data.clear();
                    filtered_events_data.addAll(tempEventsList);
                    eventsListAdapter.notifyDataSetChanged();
                    updateDeviceLocationGPS();
                    if (userChoosedCityManually) {
                        filterByCity(namesCity[indexCityChossen]);
                    } else if (!cityGPS.isEmpty()) {
                        filterByCity(cityGPS);
                    }
                    if (!currentFilterName.isEmpty()) {
                        filterByFilterChosen(currentFilterName);
                    }
                } else {
                    e.printStackTrace();
                    return;
                }
            }
        });
    }

    private void inflateCityMenu() {
        popup.getMenuInflater ().inflate (R.menu.popup_city, popup.getMenu ());
        loadCityNamesToPopUp ();
        currentCityButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                popup = new PopupMenu (MainActivity.this, currentCityButton);
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
                        indexCityChossen = popUpIDToCityIndex.get (item.getItemId ());
                        currentCityButton.setText (item.getTitle ());
                        filterByCity (namesCity[indexCityChossen]);
                        if (!currentFilterName.isEmpty ()) {
                            filterByFilterChosen (currentFilterName);
                        }
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
                currentCityButton.setText (popup.getMenu ().getItem (indexCityChossen).getTitle ());
            } else {
                currentCityButton.setText (popup.getMenu ().getItem (indexCityGPS).getTitle ());
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public static void filterByCity(String cityName) {
        ArrayList<EventInfo> tempEventsList = new ArrayList<> ();
        if (cityName.equals("All Cities")) {
            tempEventsList.addAll (all_events_data);
        } else {
            for (int i = 0; i < all_events_data.size (); i++) {
                String cityEvent = all_events_data.get (i).getCity ();
                if (cityEvent != null && cityEvent.equals (cityName)) {
                    tempEventsList.add (all_events_data.get (i));
                }
            }
        }
        filtered_events_data.clear ();
        filtered_events_data.addAll (tempEventsList);
        eventsListAdapter.notifyDataSetChanged ();
    }

    public static void filterByFilterChosen(String currentFilterName) {
        ArrayList<EventInfo> tempEventsList = new ArrayList<> ();
        if (currentFilterName.isEmpty ()) {
            tempEventsList.addAll (all_events_data);
        } else {
            for (int i = 0; i < all_events_data.size (); i++) {
                if (currentFilterName.equals (MainActivity.all_events_data.get (i).getFilterName ())) {
                    tempEventsList.add (MainActivity.all_events_data.get (i));
                }
            }
        }
        filtered_events_data.clear ();
        filtered_events_data.addAll (tempEventsList);
        eventsListAdapter.notifyDataSetChanged ();
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
        if (v.getId () == savedEvent.getId ())
        {
            newIntent = new Intent (this, SavedEventActivity.class);
            startActivity (newIntent);
        }
        else if (v.getId () == realTime.getId ())
        {
            newIntent = new Intent (this, RealTime.class);
            startActivity (newIntent);
        }
        else if (v.getId () == search.getId ())
        {
            newIntent = new Intent (this, Search.class);
            startActivity (newIntent);
        }
        else if(v.getId()==notification.getId())
        {
            newIntent=new Intent(this,MyNotification.class);
            startActivity(newIntent);
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
        if (filtered_events_data.get (i).getImageId () != null) {
            Bitmap bmp = filtered_events_data.get (i).getImageId ();
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
        intent.putExtra ("index", filtered_events_data.get (i).getIndexInFullList ());

        b.putString ("customer_id", customer_id);
        if (producerId != null)
            b.putString ("producer_id", producerId);
        else
            b.putString ("producer_id", filtered_events_data.get (i).getProducerId ());
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
                    MainActivity.cityGPS = cityGPS;
                    cityFoundGPS = true;
                    popup.getMenu ().getItem (indexCityGPS).setTitle (namesCity[indexCityGPS]);
                    indexCityGPS = getCityIndexFromName (cityGPS);
                    popup.getMenu ().getItem (indexCityGPS).setTitle (cityGPS + "(GPS)");
                    if (!userChoosedCityManually) {
                        filterByCity (cityGPS);
                        if (!currentFilterName.isEmpty ()) {
                            filterByFilterChosen (currentFilterName);
                        }
                        currentCityButton.setText (cityGPS + "(GPS)");
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

    public void createEvent(View view) {
        Intent intent = new Intent (MainActivity.this, CreateEventActivity.class);
        startActivity (intent);
    }

    private void updateSavedEvents(List<EventInfo> eventsList) {
        try {
            InputStream inputStream = getApplicationContext ().openFileInput ("saves");
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader (inputStream);
                BufferedReader bufferedReader = new BufferedReader (inputStreamReader);
                String receiveString = "";
                while ((receiveString = bufferedReader.readLine ()) != null) {
                    for (int i = 0; i < eventsList.size (); i++) {
                        if (eventsList.get (i).getName ().equals (receiveString)) {
                            eventsList.get (i).setIsSaved (true);
                        }
                    }
                }
                inputStream.close ();
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }
}
