package com.example.FundigoApp.Tickets;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by assafbe on 24/02/2016.
 */
public class EventsSeatsInfo {

    private String eventName;
    private String ticketName;
    private Bitmap QR;
    private Date purchaseDate;
    private int price;
    private String eventDate;


    public EventsSeatsInfo (String _eventName, String _ticketName,Date _purchaseDate,
                                 int _price,String _eventDate,Bitmap _QR)
    {
      this.eventName=_eventName;
      this.eventDate=_eventDate;
      this.QR = _QR;
      this.purchaseDate = _purchaseDate;
      this.price = _price;
      this.ticketName = _ticketName;
    }

    public String getEventName() {
        return eventName;
    }

    public String getTicketName() {
        return ticketName;
    }

    public Bitmap getQR() {
        return QR;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public int getPrice() {
        return price;
    }

    public String getEventDate() {
        return eventDate;
    }


}
