package com.example.FundigoApp.Events;

import android.graphics.Bitmap;
import android.location.Location;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Event")
public class Event extends ParseObject {

    private Location loc = new Location ("");
    private Bitmap b;

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

    public double getY() {
        return getDouble ("Y");
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

    public String getArtist() {
        return getString ("artist");
    }

    public void setArtist(String artist) {
        put ("artist", artist);
    }

    public String getIncome() {
        return getString ("income");
    }

    public void setIncome(String income) {
        put ("income", income);
    }

    public String getSold() {
        return getString ("sold");
    }

    public void setSold(String sold) {
        put ("sold", sold);
    }


    public Bitmap getBitmap() {
        return this.b;
    }

    @Override
    public String toString() {
        return getString ("Name") + "\n" +
                       getString ("Price") + "$" + "\n" +
                       getString ("NumOfTicketsLeft");
    }

    public String getFbUrl () { // Assaf added. link saved in Parse for link to Even FB page
        return getString("FaceBookUrl");
    }
}