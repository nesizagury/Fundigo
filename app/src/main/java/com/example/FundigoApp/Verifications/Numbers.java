package com.example.FundigoApp.Verifications;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.File;
import java.util.List;

@ParseClassName("Numbers")
public class Numbers extends ParseObject {
    public void setName(String name) {
        put ("name", name);
    }

    public void setCanels(String chanel) {
        add("Chanels", chanel);
    }

    public void setNumber(String number) {
        put("number", number);
    }

    public void setPhoto(File image) {
        put ("image", image);
    }

    public ParseFile getImageFile() {
        return getParseFile ("ImageFile");
    }

    public List<String> getChanels() {
        return getList("Chanels");
    }

    public void addChanels(String str) {
        add("Chanels", str);
    }


    public void setFbId(String fbId) {
        put ("fbId", fbId);
    }

    public String getFbId() {
        return getString ("fbId");
    }

    public String getNumber() {
        return getString ("number");
    }

    public String getName() {
        return getString ("name");
    }

    public void setFbUrl(String fbUrl) {
        put ("fbUrl", fbUrl);
    }

    public String getFbUrl() {
        return getString ("fbUrl");
    }
}