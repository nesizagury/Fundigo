package com.example.FundigoApp.Tickets;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("EventsSeats")
public class EventsSeats extends ParseObject {
    public String getPrice() {
        return getString ("price");
    }

    public void setPrice(int price) {
        put ("price", price);
    }

    public int getEventObjectId() {
        return getInt ("eventObjectId");
    }

    public void setEventObjectId(String eventObjectId) {
        put ("eventObjectId", eventObjectId);
    }

    public String getSeatNumber() {
        return getString ("seatNumber");
    }

    public void setSeatNumber(String seatNumber) {
        put ("seatNumber", seatNumber);
    }
}
