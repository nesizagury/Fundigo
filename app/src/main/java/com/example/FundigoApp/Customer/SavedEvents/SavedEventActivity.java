package com.example.FundigoApp.Customer.SavedEvents;

import android.Manifest.permission;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
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
import android.widget.ListView;

import com.example.FundigoApp.Customer.RealTime.RealTimeActivity;
import com.example.FundigoApp.Events.CreateEventActivity;
import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.Events.EventPage;
import com.example.FundigoApp.Events.EventsListAdapter;
import com.example.FundigoApp.FilterPage;
import com.example.FundigoApp.MainActivity;
import com.example.FundigoApp.R;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SavedEventActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    static ArrayList<EventInfo> savedEventsList = new ArrayList<> ();
    static ArrayList<EventInfo> filteredSavedEventsList = new ArrayList<> ();
    static final int REQUEST_CODE_MY_PICK = 1;

    ListView list_view;
    static EventsListAdapter eventsListAdapter;
    Button eventTab;
    Button savedEvent;
    Button realTimeTab;
    static Button currentCityButton;

    LocationManager locationManager;
    static PopupMenu popup;
    LocationListener locationListener;

    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_saved_event);//
        context = SavedEventActivity.this;
        list_view = (ListView) findViewById (R.id.listView);
        eventTab = (Button) findViewById (R.id.BarEvent_button);
        savedEvent = (Button) findViewById (R.id.BarSavedEvent_button);
        realTimeTab = (Button) findViewById (R.id.BarRealTime_button);

        popup = new PopupMenu (SavedEventActivity.this, currentCityButton);
        currentCityButton = (Button) findViewById (R.id.city_item);

        eventsListAdapter = new EventsListAdapter (this, filteredSavedEventsList, true);
        realTimeTab.setOnClickListener (this);
        eventTab.setOnClickListener (this);
        savedEvent.setOnClickListener (this);

        list_view.setAdapter (eventsListAdapter);
        list_view.setSelector (new ColorDrawable (Color.TRANSPARENT));
        list_view.setOnItemClickListener (this);

        getSavedEventsFromJavaList ();
        inflateCityMenu ();
        if (!MainActivity.cityFoundGPS) {
            updateDeviceLocationGPS ();
        }
        MainActivity.savedAcctivityRunnig = true;
    }

    private void inflateCityMenu() {
        popup.getMenuInflater ().inflate (R.menu.popup_city, popup.getMenu ());
        if (MainActivity.namesCity.length == 0) {
            loadCityNamesToPopUp (true);
        } else {
            loadCityNamesToPopUp (false);
        }
        currentCityButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                popup = new PopupMenu (SavedEventActivity.this, currentCityButton);
                //Inflating the Popup using xml file
                popup.getMenuInflater ().inflate (R.menu.popup_city, popup.getMenu ());
                for (int i = 0; i < MainActivity.namesCity.length; i++) {
                    if (i == MainActivity.indexCityGPS && MainActivity.cityFoundGPS) {
                        popup.getMenu ().getItem (i).setTitle (MainActivity.namesCity[i] + "(GPS)");
                    } else {
                        popup.getMenu ().getItem (i).setTitle (MainActivity.namesCity[i]);
                    }
                }
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener (new PopupMenu.OnMenuItemClickListener () {
                    public boolean onMenuItemClick(MenuItem item) {
                        MainActivity.indexCityChossen = MainActivity.popUpIDToCityIndex.get (item.getItemId ());
                        if (MainActivity.cityFoundGPS && item.getTitle ().equals (MainActivity.cityGPS)) {
                            currentCityButton.setText (item.getTitle () + "(GPS)");
                        } else {
                            currentCityButton.setText (item.getTitle ());
                        }
                        filterByCityAndFilterName (MainActivity.namesCity[MainActivity.indexCityChossen],
                                                          MainActivity.currentFilterName);
                        eventsListAdapter.notifyDataSetChanged ();
                        MainActivity.userChoosedCityManually = true;
                        return true;
                    }
                });
                popup.show ();//showing popup menu
            }
        });
    }

    private void loadCityNamesToPopUp(boolean loadCityList) {
        if (loadCityList) {
            Resources rsc = getResources ();
            MainActivity.namesCity = rsc.getStringArray (R.array.popUp);
        }
        try {
            for (int i = 0; i < MainActivity.namesCity.length; i++) {
                if (i == MainActivity.indexCityGPS && MainActivity.cityFoundGPS) {
                    popup.getMenu ().getItem (i).setTitle (MainActivity.namesCity[i] + "(GPS)");
                } else {
                    popup.getMenu ().getItem (i).setTitle (MainActivity.namesCity[i]);
                }
                MainActivity.popUpIDToCityIndex.put (popup.getMenu ().getItem (i).getItemId (), i);
            }
            if (MainActivity.userChoosedCityManually) {
                currentCityButton.setText (popup.getMenu ().getItem (MainActivity.indexCityChossen).getTitle ());
            } else if (MainActivity.cityFoundGPS) {
                currentCityButton.setText (MainActivity.cityGPS + "(GPS)");
            } else {
                currentCityButton.setText (popup.getMenu ().getItem (0).getTitle ());
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public static void filterByCityAndFilterName(String cityName, String currentFilterName) {
        ArrayList<EventInfo> tempEventsList = new ArrayList<> ();
        if (cityName.equals ("All Cities") && currentFilterName.isEmpty ()) {
            tempEventsList.addAll (savedEventsList);
        } else {
            for (int i = 0; i < savedEventsList.size (); i++) {
                String cityEvent = savedEventsList.get (i).getCity ();
                if (cityName.equals ("All Cities") || (cityEvent != null && cityEvent.equals (cityName))) {
                    if (currentFilterName.isEmpty () ||
                                (currentFilterName.equals (savedEventsList.get (i).getFilterName ()))) {
                        tempEventsList.add (savedEventsList.get (i));
                    }
                }
            }
        }
        filteredSavedEventsList.clear ();
        filteredSavedEventsList.addAll (tempEventsList);
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
        if (v.getId () == eventTab.getId ()) {
            newIntent = new Intent (this, MainActivity.class);
            startActivity (newIntent);
        } else if (v.getId () == realTimeTab.getId ()) {
            newIntent = new Intent (this, RealTimeActivity.class);
            startActivity (newIntent);
        }
    }

    public void openFilterPage(View v) {
        Intent filterPageIntent = new Intent (this, FilterPage.class);
        startActivity (filterPageIntent);
    }

    public void openMenuPage(View v) {
        Intent menuPageIntent = new Intent (this, com.example.FundigoApp.Menu.class);
        startActivity (menuPageIntent);
    }

    @Override
    public void onItemClick(AdapterView<?> av, View view, int i, long l) {
        Bundle b = new Bundle ();
        Intent intent = new Intent (this, EventPage.class);
        if (filteredSavedEventsList.get (i).getImageId () != null) {
            Bitmap bmp = filteredSavedEventsList.get (i).getImageId ();
            ByteArrayOutputStream stream = new ByteArrayOutputStream ();
            bmp.compress (Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray ();
            intent.putExtra ("eventImage", byteArray);
        } else
            intent.putExtra ("eventImage", "");
        intent.putExtra ("eventDate", filteredSavedEventsList.get (i).getDate ());
        intent.putExtra ("eventName", filteredSavedEventsList.get (i).getName ());
        intent.putExtra ("eventTags", filteredSavedEventsList.get (i).getTags ());
        intent.putExtra ("eventPrice", filteredSavedEventsList.get (i).getPrice ());
        intent.putExtra ("eventInfo", filteredSavedEventsList.get (i).getInfo ());
        intent.putExtra ("eventPlace", filteredSavedEventsList.get (i).getPlace ());
        intent.putExtra ("toilet", filteredSavedEventsList.get (i).getToilet ());
        intent.putExtra ("parking", filteredSavedEventsList.get (i).getParking ());
        intent.putExtra ("capacity", filteredSavedEventsList.get (i).getCapacity ());
        intent.putExtra ("atm", filteredSavedEventsList.get (i).getAtm ());
        intent.putExtra ("index", filteredSavedEventsList.get (i).getIndexInFullList ());

        b.putString ("customer_id", MainActivity.customer_id);
        if (MainActivity.producerId != null) {
            b.putString ("producer_id", MainActivity.producerId);
        } else {
            b.putString ("producer_id", filteredSavedEventsList.get (i).getProducerId ());
        }
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
                MainActivity.loc = location;
                if (!cityGPS.isEmpty ()) {
                    MainActivity.cityGPS = cityGPS;
                    MainActivity.cityFoundGPS = true;
                    popup.getMenu ().getItem (MainActivity.indexCityGPS).setTitle (MainActivity.namesCity[MainActivity.indexCityGPS]);
                    MainActivity.indexCityGPS = getCityIndexFromName (MainActivity.cityGPS);
                    popup.getMenu ().getItem (MainActivity.indexCityGPS).setTitle (MainActivity.cityGPS + "(GPS)");
                    if (!MainActivity.userChoosedCityManually) {
                        filterByCityAndFilterName (MainActivity.cityGPS,
                                                          MainActivity.currentFilterName);
                        currentCityButton.setText (MainActivity.cityGPS + "(GPS)");
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

    private static int getCityIndexFromName(String name) {
        for (int i = 0; i < MainActivity.namesCity.length; i++) {
            String city = MainActivity.namesCity[i];
            if (city.equals (name)) {
                return i;
            }
        }
        return -1;
    }

    public void createEvent(View view) {
        Intent intent = new Intent (SavedEventActivity.this, CreateEventActivity.class);
        startActivity (intent);
    }

    public static void getSavedEventsFromJavaList() {
        ArrayList<EventInfo> tempEventsList = new ArrayList<> ();
        for (int i = 0; i < MainActivity.all_events_data.size (); i++) {
            if (MainActivity.all_events_data.get (i).getIsSaved ()) {
                tempEventsList.add (MainActivity.all_events_data.get (i));
            }
        }
        savedEventsList.clear ();
        savedEventsList.addAll (tempEventsList);
        filteredSavedEventsList.clear ();
        filteredSavedEventsList.addAll (tempEventsList);
        eventsListAdapter.notifyDataSetChanged ();
        if (MainActivity.userChoosedCityManually) {
            filterByCityAndFilterName (MainActivity.namesCity[MainActivity.indexCityChossen], MainActivity.currentFilterName);
            if (MainActivity.cityFoundGPS && MainActivity.namesCity[MainActivity.indexCityChossen].equals (MainActivity.cityGPS)) {
                currentCityButton.setText (MainActivity.namesCity[MainActivity.indexCityChossen] + "(GPS)");
            } else {
                currentCityButton.setText (MainActivity.namesCity[MainActivity.indexCityChossen]);
            }
        } else if (!MainActivity.cityGPS.isEmpty ()) {
            filterByCityAndFilterName (MainActivity.cityGPS, MainActivity.currentFilterName);
            currentCityButton.setText (MainActivity.cityGPS + "(GPS)");
        }
    }

    @Override
    protected void onResume() {
        super.onResume ();
        if (MainActivity.userChoosedCityManually) {
            filterByCityAndFilterName (MainActivity.namesCity[MainActivity.indexCityChossen], MainActivity.currentFilterName);
            if (MainActivity.cityFoundGPS && MainActivity.namesCity[MainActivity.indexCityChossen].equals (MainActivity.cityGPS)) {
                currentCityButton.setText (MainActivity.namesCity[MainActivity.indexCityChossen] + "(GPS)");
            } else {
                currentCityButton.setText (MainActivity.namesCity[MainActivity.indexCityChossen]);
            }
        } else if (!MainActivity.cityGPS.isEmpty ()) {
            filterByCityAndFilterName (MainActivity.cityGPS, MainActivity.currentFilterName);
            currentCityButton.setText (MainActivity.cityGPS + "(GPS)");
        }
    }
}
