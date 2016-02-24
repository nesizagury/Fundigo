package com.example.FundigoApp.Chat;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Room")
public class Room extends ParseObject {
    public void setCustomer_id(String customer_id) {
        put ("customer_id", customer_id);
    }

    public String getCustomer_id() {
        return getString ("customer_id");
    }

    public void setProducer_id(String producer_id) {
        put ("producer_id", producer_id);
    }

    public String getProducer_id() {
        return getString ("producer_id");
    }

    public void setLastMessage(String lastMessage) {
        put ("lastMessage", lastMessage);
    }

    public String getLastMessage() {
        return getString ("lastMessage");
    }

    public void setEventObjId(String eventObjId) {
        put ("eventObjId", eventObjId);
    }

    public String getEventObjId() {
        return getString ("eventObjId");
    }
}
