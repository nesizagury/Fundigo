package com.example.FundigoApp.Events;

import android.graphics.Bitmap;

import com.example.FundigoApp.Tickets.EventsSeats;

import java.util.Date;
import java.util.List;

public class EventInfo {

    Bitmap imageId;
    Date date;
    String dateAsString;
    String name;
    String tags;
    String price;
    String info;
    String place;
    String address;
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
    int NumOfTickets;
    String parseObjectId;
    boolean isFutureEvent;
    String fbUrl;
    boolean isStadium;
    List<EventsSeats> eventsSeatsList;
    String picUrl;

    public EventInfo(String picUrl,
                     Date date,
                     String dateAsString,
                     String name,
                     String tags,
                     String price,
                     String info,
                     String place,
                     String address,
                     String city,
                     String toilet,
                     String parking,
                     String capacity,
                     String atm,
                     String filterName,
                     boolean isSaved,
                     String producerId,
                     int indexInFullList,
                     double x,
                     double y,
                     String artist,
                     int numOfTickets,
                     String parseObjectId,
                     String fbUrl,
                     boolean isStadium) {
        this.picUrl = picUrl;
        this.date = date;
        this.dateAsString = dateAsString;
        this.name = name;
        this.tags = tags;
        this.price = price;
        this.info = info;
        this.place = place;
        this.address = address;
        this.city = city;
        this.toilet = toilet;
        this.parking = parking;
        this.capacity = capacity;
        this.atm = atm;
        this.filterName = filterName;
        this.isSaved = isSaved;
        this.producerId = producerId;
        this.indexInFullList = indexInFullList;
        this.x = x;
        this.y = y;
        this.artist = artist;
        NumOfTickets = numOfTickets;
        this.parseObjectId = parseObjectId;
        this.fbUrl = fbUrl;
        this.isStadium = isStadium;
    }

    public Bitmap getImageBitmap() {
        return imageId;
    }

    public void setBitmap(Bitmap imageId) {
        this.imageId = imageId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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

    public void setDescription(String info) {
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

    public int getNumOfTickets() {
        return NumOfTickets;
    }

    public void setNumOfTickets(int numOfTickets) {
        NumOfTickets = numOfTickets;
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
        return parseObjectId;
    }

    public void setParseObjectId(String parseObjectId) {
        this.parseObjectId = parseObjectId;
    }

    public boolean isFutureEvent() {
        return isFutureEvent;
    }

    public void setIsFutureEvent(boolean isFutureEvent) {
        this.isFutureEvent = isFutureEvent;
    }

    public String getFbUrl() // return FB URL is Event Info
    {
        return this.fbUrl;
    }

    public boolean isStadium() {
        return isStadium;
    }

    public void setIsStadium(boolean isStadium) {
        this.isStadium = isStadium;
    }

    public String getDateAsString() {
        return dateAsString;
    }

    public void setDateAsString(String dateAsString) {
        this.dateAsString = dateAsString;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<EventsSeats> getEventsSeatsList() {
        return eventsSeatsList;
    }

    public void setEventsSeatsList(List<EventsSeats> eventsSeatsList) {
        this.eventsSeatsList = eventsSeatsList;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

}
