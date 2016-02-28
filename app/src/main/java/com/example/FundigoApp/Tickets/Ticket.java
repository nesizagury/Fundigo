package com.example.FundigoApp.Tickets;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by Sprintzin on 28/02/2016.
 */
public class Ticket {
    private String buyer_phone;
    private String eventObjectId;
    private int price;
    private Date purchase_date;
    private String seatNumber;
    private Bitmap bitmap;
    private String objectId;

    public Ticket() {
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Ticket(int price, String seatNumber) {
        this.price = price;
        this.seatNumber = seatNumber;
    }

    public String getBuyer_phone() {
        return buyer_phone;
    }

    public void setBuyer_phone(String buyer_phone) {
        this.buyer_phone = buyer_phone;
    }

    public String getEventObjectId() {
        return eventObjectId;
    }

    public void setEventObjectId(String eventObjectId) {
        this.eventObjectId = eventObjectId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Date getPurchase_date() {
        return purchase_date;
    }

    public void setPurchase_date(Date purchase_date) {
        this.purchase_date = purchase_date;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }
}
