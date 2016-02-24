package com.example.FundigoApp.Producer;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("RealTimeEvent")
public class RealTimeEvent extends ParseObject {

    public void setEventName(String eventName) {
        put ("eventName", eventName);
    }

    public String getEventName() {
        return getString ("eventName");
    }

    public void setProducer(String producer) {
        put ("producer", producer);
    }

    public String getProducer() {
        return getString ("producer");
    }


    public void setArtist(String artist) {
        put ("artist", artist);
    }

    public String getArtist() {
        return getString ("artist");
    }



    public void setQRCode(String qrCode) {
        put ("qrCode", qrCode);
    }

    public String getQRCode() {
        return getString ("qrCode");
    }

    public void setGuestIn(String guestIn) {
        put ("guestIn", guestIn);
    }

    public String getGuestIn() {
        return getString ("guestIn");
    }

}