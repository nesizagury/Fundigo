package com.example.events;

import android.graphics.Bitmap;
import android.location.Location;

import com.parse.ParseClassName;
import com.parse.ParseObject;


/**
 * Created by rufflez on 8/31/14.
 */
@ParseClassName("Event")
public class Event extends ParseObject {

    private Location loc = new Location ("");
    private Bitmap b;

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

    public String getCity() {
        return getString ("city");
    }

    public void setCity(String city) {
        put ("city", city);
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

    public double getX() {
        return getDouble ("X");
    }

    public void setX(double x) {
        put ("X", x);
    }

    public void setY(double y) {
        put ("Y", y);
    }

    public String getTags() {
        return getString ("tags");
    }

    public void setTags(String tags) {
        put ("tags", tags);
    }

    public String getDescription() {
        return getString ("description");
    }

    public void setDescription(String description) {
        put ("description", description);
    }

    public String getAddress() {
        return getString ("address");
    }

    public void setAddress(String address) {
        put ("address", address);
    }

    public String getProducerId() {
        return getString ("producerId");
    }

    public void setProducerId(String producerId) {
        put ("producerId", producerId);
    }

    public String getDate() {
        return getString ("date");
    }

    public void setDate(String date) {
        put ("date", date);
    }

    public void setPlace(String place) {
        put ("place", place);
    }

    public void setBitmap(Bitmap b) {
        this.b = b;
    }

    public String getEventToiletService() {
        return getString ("eventToiletService");
    }

    public void setEventToiletService(String eventToiletService) {
        put ("eventToiletService", eventToiletService);
    }

    public String getEventParkingService() {
        return getString ("eventParkingService");
    }

    public void setEventParkingService(String eventParkingService) {
        put ("eventParkingService", eventParkingService);
    }

    public String getEventCapacityService() {
        return getString ("eventCapacityService");
    }

    public void setEventCapacityService(String eventCapacityService) {
        put ("eventCapacityService", eventCapacityService);
    }

    public String getEventATMService() {
        return getString ("eventATMService");
    }

    public void setEventATMService(String eventATMService) {
        put ("eventATMService", eventATMService);
    }

    public String getFilterName() {
        return getString ("filterName");
    }

    public void setFilterName(String filterName) {
        put ("filterName", filterName);
    }
    @Override
    public String toString() {
        return getString ("Name") + "\n" +
                       getString ("Price") + "$" + "\n" +
                       getString ("NumOfTicketsLeft");
    }
}