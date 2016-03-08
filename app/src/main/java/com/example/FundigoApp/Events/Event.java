package com.example.FundigoApp.Events;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.Date;

@ParseClassName("Event")
public class Event extends ParseObject {

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

    public int getNumOfTickets() {
        return getInt("NumOfTickets");
    }

    public void setNumOfTickets(int numOfTickets) {
        put ("NumOfTickets", numOfTickets);
    }

    public String getPrice() {
        return getString("Price");
    }

    public void setPrice(String price) {
        put ("Price", price);
    }

    public double getX() {
        return getDouble("X");
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

    public Date getRealDate() {
        return getDate("realDate");
    }

    public void setRealDate(Date date) {
        put ("realDate", date);
    }

    public String getPlace() {
        return getString("place");
    }

    public void setPlace(String place) {
        put ("place", place);
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

    public String getFbUrl() { //link saved in Parse for link to Even FB page
        return getString ("FaceBookUrl");
    }

    public void setIsStadium(boolean IsStadium) {
        put ("isStadium", IsStadium);
    }

    public boolean getIsStadium() { //link saved in Parse for link to Even FB page
        return getBoolean("isStadium");
    }

    public ParseFile getPic() {
        return getParseFile("ImageFile");
    }

    public void setPic(ParseFile file) {
        put ("ImageFile", file);
    }

}