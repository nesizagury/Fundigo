package com.example.FundigoApp.Customer.Social;

import com.parse.ParseGeoPoint;

/**
 * Created by מנהל on 01/03/2016.
 */
public class MipoUser {

    String picUrl;
    String name;
    String objectId;
    ParseGeoPoint userLocation;
    double dist;


    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public ParseGeoPoint getUserLocation() {
        return userLocation;
    }

    public double getDist() {
        return dist;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }

    public void setUserLocation(ParseGeoPoint userLocation) {
        this.userLocation = userLocation;
    }


    public MipoUser(String picUrl, String name,String objectId) {

        this.picUrl = picUrl;
        this.name = name;
        this.objectId = objectId;

    }
}
