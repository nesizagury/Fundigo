package com.example.events;

public class EventInfo {

    int imageId;
    String date;
    String name;
    String tags;
    String price;
    String info;
    String place;
    String toilet;
    String parking;
    String capacity;
    String atm;

    public EventInfo(int imageId, String date, String name, String tags, String price, String info, String place, String toilet,String parking,String capacity,String atm) {
        this.imageId = imageId;
        this.date = date;
        this.name = name;
        this.tags = tags;
        this.price = price;
        this.info = info;
        this.place = place;
        this.toilet=toilet;
        this.parking=parking;
        this.capacity=capacity;
        this.atm=atm;
    }


    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
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
}
