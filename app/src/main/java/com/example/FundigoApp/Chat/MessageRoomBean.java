package com.example.FundigoApp.Chat;

import android.graphics.Bitmap;

import java.io.Serializable;

public class MessageRoomBean implements Serializable {
    String customerImageUrl;
    String customerImageFacebookUrl;
    String lastMessage;
    String customer_id;
    String producer_id;

    public MessageRoomBean(String lastMessage, String customer_id, String producer_id) {
        this.lastMessage = lastMessage;
        this.customer_id = customer_id;
        this.producer_id = producer_id;
    }

    public String getCustomerImage() {
        return customerImageUrl;
    }

    public void setCustomerImage(String customerImage) {
        this.customerImageUrl = customerImage;
    }

    public String getCustomerImageFacebookUrl() {
        return customerImageFacebookUrl;
    }

    public void setCustomerImageFacebookUrl(String customerImageFacebookUrl) {
        this.customerImageFacebookUrl = customerImageFacebookUrl;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getProducer_id() {
        return producer_id;
    }

    public void setProducer_id(String producer_id) {
        this.producer_id = producer_id;
    }
}
