package com.example.FundigoApp.Customer;

import android.graphics.Bitmap;

public class CustomerDetails {
    String faceBookId;
    String picUrl;
    Bitmap bmp;

    public CustomerDetails(String faceBookId, String picUrl, Bitmap bmp) {
        this.faceBookId = faceBookId;
        this.picUrl = picUrl;
        this.bmp = bmp;
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

    public Bitmap getBmp() {
        return bmp;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }
}
