package com.example.events;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Message")
public class Message extends ParseObject {

    public String getUserId() {
        return getString("userId");
    }

    public String getBody() {
        return getString("body");
    }

    public void setUserId(String userId) {
        put("userId", userId);
    }

    public void setBody(String body) {
        put("body", body);
    }

    public void setProducer(String producer) {
        put("producer", producer);
    }

    public String getProducer() {
        return getString("producer");
    }

    public void setCustomer(String customer) {
        put("customer", customer);
    }

    public String getCustomer() {
        return getString("customer");
    }

    public String getEventName() {
        return getString("eventName");
    }

    public void setEventName(String eventName) {
        put("eventName", eventName);
    }

    public void setSenderName(String senderName) {
        put("senderName", senderName);
    }

    public String getSenderName() {
        return getString("senderName");
    }

    public boolean isProducer() {
        return getBoolean("isProducer");
    }

    public void setIsProducer(boolean isProducer) {
        put("isProducer", isProducer);
    }

    public String getFbId() {
        return getString("fbId");
    }

    public void setFbId(String fbId) {
        put("fbId", fbId);
    }

    public String getPicUrl() {
        return getString("picUrl");
    }

    public void setPicUrl(String picUrl) {
        put("picUrl", picUrl);
    }
}