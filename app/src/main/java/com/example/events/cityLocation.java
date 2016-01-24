package com.example.events;

import android.location.Address;
import android.location.Location;

import java.util.List;

/**
 * Created by mirit-binbin on 17/01/2016.
 */
public class cityLocation {



    List<Address> locations;
    Location location=new Location("");
    String nameCity;
    cityLocation(String name,List<Address> list)
    {
        nameCity=name;
        locations=list;
        location.setLatitude(list.get(0).getLatitude());
        location.setLongitude(list.get(0).getLongitude());
    }

    public String getNameCity() {
        return nameCity;
    }

    public void setNameCity(String nameCity) {
        this.nameCity = nameCity;
    }

    public List<Address> getLocations() {
        return locations;
    }

    public void setLocations(List<Address> locations) {
        this.locations = locations;
    }
}

