package com.example.events;

import java.io.Serializable;

public class MessageRoomBean implements Serializable {

    int imageId;
    String name;
    String body;
    String customer_id;
    String producer_id;


    public MessageRoomBean(){

    }

    public MessageRoomBean(int imageId, String name, String body,String customer_id,String producer_id) {

        this.imageId = imageId;
        this.name = name;
        this.customer_id = customer_id;
        this.body = body;
        this.producer_id = producer_id;


    }

    public int getImageId(){
        return imageId;
    }

    public void setImageId(int imageId){

        this.imageId = imageId;

    }

    public void setBody(String body)
    {
        this.body = body;
    }

    public String getBody()
    {
        return  body;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;



    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String id) {
        this.customer_id = customer_id;
    }

    public String getProducer_id() {
        return producer_id;
    }

    public void setProducer_id(String id) {
        this.producer_id = producer_id;
    }
}
