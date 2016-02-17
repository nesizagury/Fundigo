package com.example.events;

import android.app.Application;
import android.content.SharedPreferences;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(formUri = "https://collector.tracepot.com/99efad05")
public class Fundigo extends Application {
    @Override
    public void onCreate() {
        super.onCreate ();
        ACRA.init (this);
        Parse.enableLocalDatastore (this);
        Parse.initialize (this);
        try {
            ParseInstallation.getCurrentInstallation().save ();
        } catch (ParseException e) {
            e.printStackTrace ();
        }

        ParseObject.registerSubclass (Event.class);
        ParseObject.registerSubclass(com.example.events.Message.class);
        ParseObject.registerSubclass(com.example.events.Room.class);
        ParseObject.registerSubclass (com.example.events.Numbers.class);
        FacebookSdk.sdkInitialize (getApplicationContext ());
        ParseUser.enableAutomaticUser ();
        ParseACL defaultAcl = new ParseACL();
        defaultAcl.setPublicReadAccess(true);
        defaultAcl.setPublicWriteAccess (true);
        ParseACL.setDefaultACL(defaultAcl, true);
        SharedPreferences ratePrefs = getSharedPreferences ("First Update", 0);
        if (!ratePrefs.getBoolean ("FrstTime", false)) {
            ParsePush.subscribeInBackground ("All_Users");
            SharedPreferences.Editor edit = ratePrefs.edit ();
            edit.putBoolean ("FrstTime", true);
            edit.commit ();
        }
    }

}