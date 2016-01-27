package com.example.events;

import android.graphics.Bitmap;
import android.location.Location;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.File;

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
        return getString("toilet");
    }

    public void setToilet(String toilet) {
        put ("toilet", toilet);
    }

    public String getParking() {
        return getString ("parking");
    }

    public void setParking(String parking) {
        put ("parking", parking);
    }

    public String getCapacity() {
        return getString ("capacity");
    }

    public void setCapacity(String capacity) {
        put ("capacity", capacity);
    }

    public String getAtm() {
        return getString ("atm");
    }

    public void setAtm(String atm) {
        put ("atm", atm);
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

    public String getAccountBalance() {
        return getString ("AccountBalance");
    }

    public void setAccountBalance(String AccountBalance) {
        put ("AccountBalance", AccountBalance);
    }

    public String getPrice() {
        return getString ("Price");
    }

    public void setPrice(String price) {
        put ("Price", price);
    }

    public void setPhoto(File image) {
        put("image", image);
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

    public void setBitmap(Bitmap b){

        this.b = b;

    }

    public  Bitmap getBitmap(){

        return this.b;

    }

    @Override
    public String toString() {
        return getString ("Name") + "\n" +
                       getString ("Price") + "$" + "\n" +
                       getString ("NumOfTicketsLeft");
    }
}