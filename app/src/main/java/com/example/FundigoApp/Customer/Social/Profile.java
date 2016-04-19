package com.example.FundigoApp.Customer.Social;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;

@ParseClassName("Profile")
public class Profile extends ParseObject {

    public String getName() {
        return getString ("name");
    }

    public void setName(String name) {
        put ("name", name);
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser user) {
        put ("user", user);
    }

    public ParseFile getPic() {
        return getParseFile("pic");
    }

    public void setPic(ParseFile file) {
        put ("pic", file);
    }

    public void setNumber(String number) {
        put ("number", number);
    }

    public String getNumber() {
        return getString ("number");
    }

    public void setLocation(ParseGeoPoint location) {
        put ("location", location);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public String getAbout() {
        return getString ("about");
    }

    public void setAbout(String about) {
        put ("about", about);
    }

    public String getAge() {
        return getString ("age");
    }

    public void setAge(String age) {
        put ("age", age);
    }

    public String getBody_type() {
        return getString ("body_type");
    }

    public void setBody_type(String body_type) {
        put ("body_type", body_type);
    }

    public String getHeight() {
        return getString ("height");
    }

    public void setHeight(String height) {
        put ("height", height);
    }

    public String getLooking_for() {
        return getString ("looking_for");
    }

    public void setLooking_for(String looking_for) {
        put ("looking_for", looking_for);
    }

    public String getNation() {
        return getString ("nation");
    }

    public void setNation(String nation) {
        put ("nation", nation);
    }

    public String getRelationship_status() {
        return getString ("relationship_status");
    }

    public void setRelationship_status(String relationship_status) {
        put ("relationship_status", relationship_status);
    }

    public String getStatus() {
        return getString ("status");
    }

    public void setStatus(String status) {
        put ("status", status);
    }


    public String getWeight() {
        return getString ("weight");
    }

    public void setWeight(String weight) {
        put ("weight", weight);
    }

    public Date getLastSeen() {
        return getDate("lastSeen");
    }

    public void setLastSeen(Date lastSeen) {
        put ("lastSeen", lastSeen);
    }

    public void setFbId(String fbId) {
        put ("fbId", fbId);
    }

    public String getFbId() {
        return getString ("fbId");
    }

    public List getBlocked() {
        return getList("blocked");
    }

    public void setBlocked(List blocked) {
        put ("blocked", blocked);
    }

    public void setFbUrl(String fbUrl) {
        put ("fbUrl", fbUrl);
    }

    public String getFbUrl() {
        return getString ("fbUrl");
    }

    public void setCanels(String chanel) {
        add ("Chanels", chanel);
    }

    public List<String> getChanels() {
        return getList ("Chanels");
    }

    public void addChanels(String str) {
        add("Chanels", str);
    }
}
