package com.example.FundigoApp.Chat;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Message")
public class Message extends ParseObject {
    public String getUserId() {
        return getString ("userId");
    }

    public String getBody() {
        return getString ("body");
    }

    public void setUserId(String userId) {
        put ("userId", userId);
    }

    public void setBody(String body) {
        put ("body", body);
    }

    public void setProducer(String producer) {
        put ("producer", producer);
    }

    public void setCustomer(String customer) {
        put ("customer", customer);
    }

    public String getCustomer() {
        return getString ("customer");
    }

    public String getEventObjectId() {
        return getString ("eventObjectId");
    }

    public void setEventObjectId(String eventObjectId) {
        put ("eventObjectId", eventObjectId);
    }
}