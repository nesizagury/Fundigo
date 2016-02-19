package com.example.FundigoApp;

import android.location.Location;
import android.util.Log;

import com.example.FundigoApp.Events.EventInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by assafbe on 13/02/2016.
 */
public class CityMenu
{

    //7/ call GPS and Distance in differnt Thread
    // present the Menu correct also for Cases of Events less then 10
    //support customer location change
    //support add more events online
    // Bug - when location change the button name is change but filter still the same

    private ArrayList<EventInfo> listOfEventsDetailes = new ArrayList<>();
    private MainActivity mainA = new MainActivity();
    private Location customerDeviceLocation = new Location("customerDevice");
    private List<HashMap<String, String>> totalCityList = new ArrayList<>();
    private String[] cityNames;
    private boolean GPS_ENABLE = false;
    private final int MENU_LENGTH=11;


    public CityMenu(ArrayList<EventInfo> _listOfEventsDetailes) {

        listOfEventsDetailes = _listOfEventsDetailes;
        getCustomerLocation();
    }

    public void getCustomerLocation() {

        // not Equal to Null and Location not changed
        if (MainActivity.loc != null) {
            GPS_ENABLE = true;
            customerDeviceLocation = new Location(MainActivity.loc);
            getSortedListByDistanceFromDevice();

        } else if (MainActivity.loc == null) {
            try {
                GPS_ENABLE = false;
                //if no GPS location do Nothing - //Print all Cities without sorting;
             } catch (Exception e) {
                Log.e(e.getMessage(), "Exception in Location. NO GPS");
            }
        }
    }

    public void getSortedListByDistanceFromDevice() { // Assaf- build ArrayList with Hashmpa of city events and dest. and sort it

        try
        {
                 HashMap<String, String> Data = new HashMap<>();
                Location locationEvent;
                DecimalFormat decimalDistance;
                String distanceString;

                for (int i = 0; i < listOfEventsDetailes.size(); i++) {
                    EventInfo event = listOfEventsDetailes.get(i);
                    double latitude = event.getX();
                    double longitude = event.getY();
                    String cityName = event.getCity();
                    locationEvent = new Location("eventPlace");
                    locationEvent.setLatitude(latitude);
                    locationEvent.setLongitude(longitude);
                    double distance = (double) customerDeviceLocation.distanceTo(locationEvent) / 1000;
                    decimalDistance = new DecimalFormat("#.##");
                    distanceString = decimalDistance.format(distance);
                    Data.put("distance", distanceString);
                    Data.put("cityName", cityName);
                    totalCityList.add(Data);
                    Data = new HashMap<>();
                }
            }

        catch (Exception e) {
            Log.e(e.toString(),"Exception is load data for Menu");
        }

        Collections.sort(totalCityList, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                if (Double.valueOf(lhs.get("distance")) > Double.valueOf(rhs.get("distance")))
                    return 1;
                if (Double.valueOf(lhs.get("distance")) < Double.valueOf(rhs.get("distance")))
                    return -1;
                else
                    return 0;
            }
        });

    }

    public String[] getCityNames() { // Assaf - Present the nearest cities to the cusotmer location
        int i;
        int j;
            if (GPS_ENABLE) { // Sort
                cityNames = new String[totalCityList.size() + 1];

                try {
                    i=1;
                    cityNames[0] = "All Cities";
                    for (HashMap obj : totalCityList) {
                        cityNames[i] = obj.get("cityName").toString();
                        i++;
                    }
                } catch (Exception e) {
                    Log.e(e.toString(), "loadCityNamesToMenuException");
                }
            }
            if (!GPS_ENABLE)// not sort , take all Cities and create a String Array.
            {

                try {
                    j=1;
                    cityNames = new String[listOfEventsDetailes.size() + 1];
                    cityNames[0] = "All Cities";
                    for (EventInfo obj : listOfEventsDetailes) {
                        cityNames[j] = obj.getCity();
                        j++;
                    }
                } catch (Exception e) {
                    Log.e(e.toString(), "Exception in getCityNames");
                }
            }
        return removeDuplicateCity();
    }

    private String[] removeDuplicateCity() {
        // in order to remove City names duplication

        StringBuilder noDupes = new StringBuilder();
        List<String> tempCityNames = new ArrayList<>();
        String[] _cityNames = new String[tempCityNames.size()];
        try {
             for (int j = 0; j < cityNames.length; j++) { // using StringBuilder to check duplications and insert to Array only relevant ones
                String si = cityNames[j];
                if (noDupes.indexOf(si) == -1) {
                    noDupes.append(si);
                    tempCityNames.add(si);
                }
            }
            //convert List to String[] and return.
            _cityNames = new String[tempCityNames.size()];
            tempCityNames.toArray(_cityNames);
        } catch (Exception e) {
            Log.e(e.toString(), "Exception occured in removeDuplication");
        }
        return cityListAsMeunMaxLength(_cityNames);
    }

       public String[] cityListAsMeunMaxLength(String[] cityList)
       {
           String[] tempCityList = new String[MENU_LENGTH];
           try {

               for (int i = 0; i < MENU_LENGTH; i++) {
                   tempCityList[i] = cityList[i];
               }
           }
           catch (Exception e)
           {
               Log.e(e.toString(),"Exception in CitylistShrink");
           }
           return tempCityList;
       }

      }







