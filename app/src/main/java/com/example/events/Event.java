package com.example.events;

import android.location.Location;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by rufflez on 8/31/14.
 */
@ParseClassName("Event")
public class Event extends ParseObject {

    private Location loc = new Location ("");

    public void setLocation(double x, double y) {
        loc.setLatitude (x);
        loc.setLongitude (y);
    }


    public float getdis() {
        return (RealTime.loc.distanceTo (getLocation ()) / 1000);
    }

    public Location getLocation() {
        return loc;
    }


    public String getName() {
        return getString ("Name");
    }

    public void setName(String name) {
        put ("Name", name);
    }

    public String getNumOfTicketsLeft() {
        return getString ("NumOfTicketsLeft");
    }

    public void setNumOfTicketsLeft(String numOfTicketsLeft) {
        put ("NumOfTicketsLeft", numOfTicketsLeft);
    }

    public String getPrice() {
        return getString ("Price");
    }

    public void setPrice(String price) {
        put ("Price", price);
    }

    public String getAccountBalance() {
        return getString ("AccountBalance");
    }

    public void setAccountBalance(String AccountBalance) {
        put ("AccountBalance", AccountBalance);
    }

    @Override
    public String toString() {
        return getString ("Name") + "\n" +
                       getString ("Price") + "$" + "\n" +
                       getString ("NumOfTicketsLeft");
    }
}