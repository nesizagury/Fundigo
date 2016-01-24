package com.example.events;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Benjamin 01/01/2016.
 */
public class SavedEvent extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener
{
    ListView listView;
    ArrayList<EventInfo>arr=new ArrayList<>();
    private Button Event,RealTime,SavedEvent,city;
    List<cityLocation> cityLoc ;

    PopupMenu popup;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_saved_event);
        listView=(ListView)findViewById(R.id.listView);
        Toast.makeText(this,"before readfile",Toast.LENGTH_SHORT).show();
        readFromFile();
        Event=(Button)findViewById(R.id.BarEvent_button);
        RealTime=(Button)findViewById(R.id.BarRealTime_button);
        SavedEvent=(Button)findViewById(R.id.BarSavedEvent_button);

        //Creating the instance of PopupMenu
        popup = new PopupMenu(SavedEvent.this, city);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.popup_city, popup.getMenu());
        city=(Button)findViewById(R.id.city_item);

        if(MainActivity.nameOfCurrentCity!=null) {
            city.setText(MainActivity.nameOfCurrentCity);
            Adapters atpt=new Adapters(SavedEvent.this,city.getText().toString(),2,arr);
            listView.setAdapter(atpt);
        }
        else
        {
            Adapters adapters=new Adapters(this,"SavedEvent",arr);
            listView.setAdapter(adapters);
        }
        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                popup = new PopupMenu(SavedEvent.this, city);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_city, popup.getMenu());
                if(MainActivity.loc!=null) {
                    for (int i = 0; i < cityLoc.size(); i++) {
                        popup.getMenu().getItem(i).setTitle(cityLoc.get(i).getNameCity());
                    }
                }

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        city.setText(item.getTitle());
                        Adapters atpt=new Adapters(SavedEvent.this,item.getTitle().toString(),2,arr);
                        listView.setAdapter(atpt);
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });



        Event.setOnClickListener(this);
        SavedEvent.setOnClickListener (this);
        RealTime.setOnClickListener(this);

        SavedEvent.setTextColor(Color.WHITE);

        listView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        listView.setOnItemClickListener(this);
        addLoccationPerCity();

    }


    private void readFromFile() {
        try {
            // FileInputStream inputStream=new FileInputStream("saves");
            InputStream inputStream = openFileInput("saves");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                while ( (receiveString = bufferedReader.readLine()) != null )
                {
                    for (int i=0;i<MainActivity.events_data.size();i++)
                    {
                        if(MainActivity.events_data.get(i).getName().equals(receiveString))arr.add(MainActivity.events_data.get(i));
                    }
                }
                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
    }

    @Override
    public void onClick(View v)
    {
        int vId=v.getId();
        Intent newIntent=null;
        if(vId==Event.getId())
        {
            newIntent=new Intent(this, MainActivity.class);
            startActivity(newIntent);
        }
        else if(vId==RealTime.getId())
        {
            newIntent=new Intent(this,RealTime.class);
            startActivity(newIntent);
        }

    }
    /**
     * Called when the user clicks the filter button
     */

    public void openFilterPage(View v) {
        Intent filterPageIntent = new Intent (this, FilterPage.class);
        startActivity (filterPageIntent);
    }

//    public void city(MenuItem item) {
//        ArrayList<String> list = new ArrayList<String>();
//
//        String[] locales = Locale.getISOCountries();
//
//        for (String countryCode : locales) {
//
//            Locale obj = new Locale("", countryCode);
//
//            System.out.println("Country Name = " + obj.getDisplayCountry());
//            list.add(obj.getDisplayCountry());
//
//        }
//    }
//
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.Filter:
//               openFilterPage(item);
//                return true;
//
//            default:
//                // If we got here, the user's action was not recognized.
//                // Invoke the superclass to handle it.
//                return super.onOptionsItemSelected(item);
//
//        }
//    }

    @Override
    public void onItemClick(AdapterView<?> av, View view, int i, long l) {
        Bundle b = new Bundle ();
        Intent intent = new Intent (this, EventPage.class);
        Holder holder = (Holder) view.getTag ();
        EventInfo event = (EventInfo) holder.image.getTag ();
        intent.putExtra ("eventImage", MainActivity.events_data.get (i).getImageId());
        intent.putExtra ("eventDate", MainActivity.events_data.get (i).getDate());
        intent.putExtra ("eventName", MainActivity.events_data.get (i).getName());
        intent.putExtra ("eventTags", MainActivity.events_data.get (i).getTags());
        intent.putExtra ("eventPrice", MainActivity.events_data.get (i).getPrice());
        intent.putExtra ("eventInfo", MainActivity.events_data.get (i).getInfo());
        intent.putExtra("eventPlace", MainActivity.events_data.get(i).getPlace());
        b.putInt ("userIndex", i);
        intent.putExtras (b);
        startActivity (intent);
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
            if(MainActivity.loc!=null) {
                Collections.sort(cityLoc, new Comparator<cityLocation>() {
                    @Override
                    public int compare(cityLocation l1, cityLocation l2) {

                        if (MainActivity.loc.distanceTo(l1.location) < MainActivity.loc.distanceTo(l2.location))
                            return -1;
                        if (MainActivity.loc.distanceTo(l1.location) > MainActivity.loc.distanceTo(l2.location))
                            return 1;
                        return 0;
                    }
                });
            }

            for (int i=0;i<cityLoc.size();i++)
            {
                popup.getMenu().getItem(i).setTitle(cityLoc.get(i).getNameCity());
            }


        }catch (Exception e){}
    }

    public void openMenuPage(View v) {
        Intent menuPageIntent = new Intent (this, com.example.events.Menu.class);
        startActivity (menuPageIntent);
    }
}
