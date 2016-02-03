package com.example.events;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private final static String TAG = "MainActivity";
    List<cityLocation> cityLoc ;
    ImageView search;
    ListView list_view;
    public static List<EventInfo> events_data = new ArrayList<EventInfo> ();
    private Button Event, SavedEvent, RealTime,city;
    private LinearLayout topToolBar;
    boolean didInit = false;
    static boolean isCustomer = false;
    static boolean isGuest = false;
    int customer_id;

    public static boolean turnGps = true;
    public static boolean gps_enabled = false;
    public static boolean network_enabled = false;
    public static LocationManager LocationServices;
    public static Location loc;
    static final int REQUEST_CODE_MY_PICK = 1;
    PopupMenu popup;
    static String nameOfCurrentCity="";
    FileOutputStream file;

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
        Event = (Button) findViewById (R.id.BarEvent_button);
        SavedEvent = (Button) findViewById (R.id.BarSavedEvent_button);
        RealTime = (Button) findViewById (R.id.BarRealTime_button);
        search=(ImageView)findViewById(R.id.search);
        search.setOnClickListener(this);

        //Creating the instance of PopupMenu
        popup = new PopupMenu (MainActivity.this, city);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate (R.menu.popup_city, popup.getMenu ());
        city=(Button)findViewById(R.id.city_item);
        addLoccationPerCity();
        city.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                popup = new PopupMenu (MainActivity.this, city);
                //Inflating the Popup using xml file
                popup.getMenuInflater ().inflate (R.menu.popup_city, popup.getMenu ());
                if (loc != null) {
                    for (int i = 0; i < cityLoc.size (); i++) {
                        popup.getMenu ().getItem (i).setTitle (cityLoc.get (i).getNameCity ());
                    }
                }

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener (new PopupMenu.OnMenuItemClickListener () {
                    public boolean onMenuItemClick(MenuItem item) {
                        city.setText (item.getTitle ());
                        nameOfCurrentCity = item.getTitle ().toString ();
                        Adapters atpt = new Adapters (MainActivity.this, item.getTitle ().toString (), 1, null);
                        list_view.setAdapter (atpt);
                        saveCurrentCity ();
                        return true;
                    }
                });

                popup.show ();//showing popup menu
            }
        });

        RealTime.setOnClickListener (this);
        Event.setOnClickListener (this);
        SavedEvent.setOnClickListener (this);
        Event.setTextColor (Color.WHITE);
        loc = getLocation ();
        if (loc != null){
            city();
            saveCurrentCity();
        }

        ArrayList<EventInfo> arr=(ArrayList<EventInfo>)getIntent().getSerializableExtra("List");
        ArrayList<EventInfo> search=(ArrayList<EventInfo>)getIntent().getSerializableExtra("search");
        Adapters adapter;
        if(arr!=null)
        {
            if(loc!=null)
            {
                adapter=new Adapters(this, city.getText().toString(), arr);
            }
            else
            {
                adapter = new Adapters(this, "filter", arr);
            }
        }
        else if(search!=null)
        {
            adapter = new Adapters(this, "filter", search);
        }
        else if(loc!=null)
        {
            adapter=setCurrentCity();
            if(adapter==null)adapter=new Adapters(this);
        }else
        {
            adapter = new Adapters(this);
        }

        list_view.setAdapter(adapter);
        list_view.setSelector(new ColorDrawable(Color.TRANSPARENT));
        list_view.setOnItemClickListener(this);
        list_view.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }

            int mPosition = 0;
            int mOffset = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                int position = list_view.getFirstVisiblePosition();
                View v = list_view.getChildAt(0);
                int offset = (v == null) ? 0 : v.getTop();

                if (mPosition < position || (mPosition == position && mOffset < offset)) {
                    // Scrolled up
                    topToolBar.setVisibility(View.GONE);

                } else {
                    // Scrolled down
                    topToolBar.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    public void city() {
        Geocoder gcd = new Geocoder(this, Locale.getDefault ());
        if(loc!=null) {
            try {
                List<Address> addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(),1);
                if(addresses.size()>0)city.setText(addresses.get(0).getAdminArea());
            } catch (IOException e) {
            }
        }
    }
    private void addLoccationPerCity()
    {
        Resources rsc=getResources();
        String [] namesCity=rsc.getStringArray(R.array.popUp);
        try {
            Geocoder geocoder = new Geocoder(this);
            cityLoc = new ArrayList<>();
            for (int i = 0; i < namesCity.length; i++) {
                cityLoc.add(new cityLocation(namesCity[i], geocoder.getFromLocationName(namesCity[i], 1)));
            }
            if(loc!=null) {
                Collections.sort (cityLoc, new Comparator<cityLocation> () {
                    @Override
                    public int compare(cityLocation l1, cityLocation l2) {
                        if (loc.distanceTo (l1.location) < loc.distanceTo (l2.location)) return -1;
                        if (loc.distanceTo (l1.location) > loc.distanceTo (l2.location)) return 1;
                        return 0;
                    }
                });
            }

            for (int i=0;i<cityLoc.size();i++)
            {
                popup.getMenu().getItem(i).setTitle(cityLoc.get(i).getNameCity());
            }

            city.setText(popup.getMenu().getItem(0).getTitle());
            nameOfCurrentCity=city.getText().toString();
            Adapters atpt=new Adapters(MainActivity.this,city.getText().toString(),1,null);
            list_view.setAdapter(atpt);

        }catch (Exception e){}
    }

    public void saveCurrentCity()
    {
        try
        {
            file=openFileOutput("currentCity", Context.MODE_PRIVATE);
            file.write(nameOfCurrentCity.getBytes());
            file.close();
        }catch(IOException e){}
    }
    public Adapters setCurrentCity()
    {
        try{
            Adapters adapter=null;
            InputStream inputStream = openFileInput("currentCity");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                if ( (receiveString = bufferedReader.readLine()) != null )
                {
                    nameOfCurrentCity=receiveString;
                    city.setText(nameOfCurrentCity);
                    adapter=new Adapters(MainActivity.this,city.getText().toString(),1,null);
                }

                inputStream.close();
                return adapter;
            }
        }
        catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
        return null;
    }

    public void changeButtonColorToSaveEvent()
    {
        try {
            File inputFile = new File("saves");
            BufferedReader reader = new BufferedReader(new FileReader (inputFile));
            String currentLine;
            boolean flag=true;
            while((currentLine = reader.readLine()) != null)
            {
                for (int i=0;i<events_data.size() && flag;i++)
                {
                    if(events_data.get(i).getName().equals(currentLine))
                    {
                        Toast.makeText (this, currentLine, Toast.LENGTH_SHORT).show();
                        events_data.get(i).setPress(true);
                        flag =false;
                    }
                }
                flag=true;
            }
            reader.close();

        }catch (FileNotFoundException e) {}
        catch (IOException e) {}
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
        }else if(v.getId()==search.getId())
        {
            newIntent = new Intent (this, Search.class);
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
        Intent menuPageIntent = new Intent (this, com.example.events.Menu.class);
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
        String[] eventFilter_list;

        eventName_list = res.getStringArray (R.array.eventNames);
        eventDate_list = res.getStringArray (R.array.eventDates);
        eventTag_list = res.getStringArray (R.array.eventTags);
        eventPrice_list = res.getStringArray (R.array.eventPrice);
        eventPlace_list = res.getStringArray (R.array.eventPlace);
        eventInfo_list = res.getStringArray (R.array.eventInfo);
        eventFilter_list=res.getStringArray(R.array.filterTags);

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
                                                       arrATM[i],
                                                       eventFilter_list[i])
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

}
