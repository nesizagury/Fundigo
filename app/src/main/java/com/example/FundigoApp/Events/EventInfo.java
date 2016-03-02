package com.example.FundigoApp.Events;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;

public class EventInfo implements Serializable, Parcelable {

    Bitmap imageId;
    String date;
    String name;
    String tags;
    String price;
    String info;
    String place;
    String city;
    String toilet;
    String parking;
    String capacity;
    String atm;
    String filterName;
    boolean isSaved;
    String producerId;
    int indexInFullList;
    double dist;
    double x;
    double y;
    String artist;
    String sold;
    String income;
    String TicketsLeft;
    String parseObjectId;

    public EventInfo(Bitmap imageId,
                     String date,
                     String name,
                     String tags,
                     String price,
                     String info,
                     String place,
                     String toilet,
                     String parking,
                     String capacity,
                     String atm,
                     String city,
                     int indexInFullList,
                     String filterName) {
        this.imageId = imageId;
        this.date = date;
        this.name = name;
        this.tags = tags;
        this.price = price;
        this.info = info;
        this.place = place;
        this.toilet = toilet;
        this.parking = parking;
        this.capacity = capacity;
        this.atm = atm;
        this.city = city;
        this.indexInFullList = indexInFullList;
        this.filterName = filterName;
    }

    public EventInfo(Parcel in) {
        String[] data = new String[11];
        in.readStringArray (data);
        this.date = data[0];
        this.name = data[1];
        this.tags = data[2];
        this.price = data[3];
        this.info = data[4];
        this.place = data[5];
        this.toilet = data[6];
        this.parking = data[7];
        this.capacity = data[8];
        this.atm = data[9];
        this.filterName = data[10];

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray (new String[]{
                                                   this.date, this.name, this.tags, this.price, this.info, this.place,
                                                   this.toilet, this.parking, this.capacity, this.atm, this.filterName});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator () {
        public EventInfo createFromParcel(Parcel in) {
            return new EventInfo (in);
        }

        public EventInfo[] newArray(int size) {
            return new EventInfo[size];
        }
    };

    public Bitmap getImageBitmap() {
        return imageId;
    }

    public void setImageId(Bitmap imageId) {
        this.imageId = imageId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getToilet() {
        return toilet;
    }

    public void setToilet(String toilet) {
        this.toilet = toilet;
    }

    public String getParking() {
        return parking;
    }

    public void setParking(String parking) {
        this.parking = parking;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getAtm() {
        return atm;
    }

    public void setAtm(String atm) {
        this.atm = atm;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public void setIsSaved(boolean t) {
        isSaved = t;
    }

    public boolean getIsSaved() {
        return isSaved;
    }

    public String getCity() {
        return city;
    }

    public String getProducerId() {
        return producerId;
    }

    public void setProducerId(String producerId) {
        this.producerId = producerId;
    }

    public int getIndexInFullList() {
        return indexInFullList;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSold() {
        return sold;
    }

    public void setSold(String sold) {
        this.sold = sold;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getTicketsLeft() {
        return TicketsLeft;
    }

    public void setTicketsLeft(String ticketsLeft) {
        TicketsLeft = ticketsLeft;
    }

    public double getDist() {
        return dist;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getParseObjectId() {
        Log.d("m1234","get: "+ parseObjectId);
        return parseObjectId;
    }

    public void setParseObjectId(String parseObjectId) {

        this.parseObjectId = parseObjectId;
    }
}
