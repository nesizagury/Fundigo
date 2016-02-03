package com.example.events;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class EventInfo implements Serializable,Parcelable{

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
    String filterName;
    boolean press;
    public EventInfo(EventInfo event)
    {
        this.imageId=event.imageId;
        this.name = event.name;
        this.tags = event.tags;
        this.price = event.price;
        this.info = event.info;
        this.place = event.place;
        this.toilet = event.toilet;
        this.parking = event.parking;
        this.capacity = event.capacity;
        this.atm = event.atm;
        this.filterName = event.filterName;

    }
    public EventInfo(int imageId, String date, String name, String tags, String price, String info, String place, String toilet,String parking,String capacity,String atm,String filterName) {
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
        this.filterName=filterName;
    }
    public EventInfo(int imageId, String date, String name, String tags, String price, String info, String place,String filterName) {
        this.imageId = imageId;
        this.date = date;
        this.name = name;
        this.tags = tags;
        this.price = price;
        this.info = info;
        this.place = place;
        this.filterName=filterName;
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
    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {this.filterName = filterName;}
    public void setPress(boolean t){press=t;};
    public boolean getPress(){return press;};

    /*public static final Parcelable.Creator<MyParcelable> CREATOR
            = new Parcelable.Creator<MyParcelable>() {
        public MyParcelable createFromParcel(Parcel in) {
            return new MyParcelable(in);
        }

        public MyParcelable[] newArray(int size) {
            return new MyParcelable[size];
        }
    };*/
    public EventInfo(Parcel in){
        String[] data = new String[11];
        in.readStringArray(data);
        this.date = data[0];
        this.name = data[1];
        this.tags = data[2];
        this.price = data[3];
        this.info = data[4];
        this.place = data[5];
        this.toilet= data[6];
        this.parking = data[7];
        this.capacity = data[8];
        this.atm = data[9];
        this.filterName = data[10];

    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.date, this.name, this.tags, this.price, this.info, this.place,
                this.toilet, this.parking, this.capacity, this.atm, this.filterName});
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public EventInfo createFromParcel(Parcel in) {
            return new EventInfo(in);
        }

        public EventInfo[] newArray(int size) {
            return new EventInfo[size];
        }
    };

}
