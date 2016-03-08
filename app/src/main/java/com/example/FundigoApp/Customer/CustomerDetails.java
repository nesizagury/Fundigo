package com.example.FundigoApp.Customer;

import android.graphics.Bitmap;

public class CustomerDetails {
    String faceBookId;
    String picUrl;
    String customerImage;
    String customerName;


    public CustomerDetails(String faceBookId,
                           String picUrl,
                           String customerImage,
                           String customerName) {
        this.faceBookId = faceBookId;
        this.picUrl = picUrl;
        this.customerImage = customerImage;
        this.customerName = customerName;
    }

    public String getFaceBookId() {
        return faceBookId;
    }

    public void setFaceBookId(String faceBookId) {
        this.faceBookId = faceBookId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getCustomerImage() {
        return customerImage;
    }

    public void setCustomerImage(String customerImage) {
        this.customerImage = customerImage;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
