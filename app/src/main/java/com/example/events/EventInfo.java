package com.example.events;

import android.graphics.Bitmap;

public class EventInfo {

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
                     int indexInFullList) {
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
    }

    public Bitmap getImageId() {
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
}
