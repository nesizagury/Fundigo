package com.example.FundigoApp.Tickets;

import android.graphics.Bitmap;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

@ParseClassName("EventsSeats")
public class EventsSeats extends ParseObject {
    private Bitmap b;

    public Bitmap getBitmap() {
        return b;
    }

    public void setBitmap(Bitmap b) {
        this.b = b;
    }

    public int getPrice() {
        return getInt ("price");
    }

    public void setPrice(int price) {
        put ("price", price);
    }

    public String getEventObjectId() {
        return getString("eventObjectId");
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
    public String getBuyerPhone() {
        return getString ("buyer_phone");
    }

    public void setBuyerPhone(String buyer_phone) {
        put ("buyer_phone", buyer_phone);
    }

    public Date getPurchaseDate(){
        return getDate("purchase_date");
    }
    public void setPurchaseDate(Date date){
        put("purchase_date", date);
    }

}
