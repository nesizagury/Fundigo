package com.example.events;

import android.graphics.Bitmap;
import android.location.Location;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.io.File;

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

    public double getX() {
        return getDouble("X");
    }

    public void setX(double x) {
        put ("X", x);
    }

    public double getY() {
        return getDouble ("Y");
    }

    public void setY(double y) {
        put ("Y", y);
    }

    public String getToilet() {
        return getString("eventToiletService");
    }

    public void setToilet(String toilet) {
        put ("eventToiletService", toilet);
    }

    public String getParking() {
        return getString ("eventParkingService");
    }

    public void setParking(String parking) {
        put ("eventParkingService", parking);
    }

    public String getCapacity() {
        return getString ("eventCapacityService");
    }

    public void setCapacity(String capacity) {
        put ("eventCapacityService", capacity);
    }

    public String getAtm() {
        return getString ("eventATMService");
    }

    public void setAtm(String atm) {
        put ("eventATMService", atm);
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

    public void setPhoto(File image) {
        put ("image", image);
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

    public String getPlace() {
        return getString ("place");
    }

    public void setPlace(String place) {
        put ("place", place);
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

    public void setBitmap(Bitmap b) {

        this.b = b;

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
}