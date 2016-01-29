package com.example.events;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.io.File;

@ParseClassName("Numbers")
public class Numbers extends ParseObject {

    public String getNumber() {
        return getString("number");
    }

    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        put("name", name);
    }

    public void setNumber(String number) {
        put("number", number);
    }

    public void setPhoto(File image) {
        put("image", image);
    }
}