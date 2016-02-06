package com.example.events;

/**
 * Created by מנהל on 02/02/2016.
 */
public class Artist {

    String name;
    String ticketsSold;

    public Artist (String name, String ticketsSold){

        this.name = name;
        this.ticketsSold = ticketsSold;

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTicketsSold() {
        return ticketsSold;
    }

    public void setTicketsSold(String ticketsSold) {
        this.ticketsSold = ticketsSold;
    }
}
