package com.example.FundigoApp.Chat;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by מנהל on 21/12/2015.
 */@ParseClassName("Room")
   public class Room extends ParseObject{



    public void setCustomer_id(String customer_id) {

        put("customer_id", customer_id);
    }

    public String getCustomer_id(){
        return getString("customer_id");
    }

    public void setProducer_id(String producer_id) {
        put("producer_id", producer_id);
    }

    public String getProducer_id(){
        return getString("producer_id");
    }

    public void setName(String name) {
        put("name", name);
    }

    public String getName(){
        return getString("name");
    }


    public void setConversationId(String ConversationId){

        put("ConversationId",ConversationId);
    }


    public String getConversationId(){

        return getString("ConversationId");

    }

}
