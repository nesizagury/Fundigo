package com.example.FundigoApp;

import android.app.Application;

import com.example.FundigoApp.Chat.Message;
import com.example.FundigoApp.Chat.MsgRealTime;
import com.example.FundigoApp.Chat.Room;
import com.example.FundigoApp.Events.Event;
import com.example.FundigoApp.Verifications.Numbers;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
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
            ParseInstallation.getCurrentInstallation ().save ();
        } catch (ParseException e) {
            e.printStackTrace ();
        }
        ParseObject.registerSubclass (Event.class);
        ParseObject.registerSubclass (Message.class);
        ParseObject.registerSubclass (Room.class);
        ParseObject.registerSubclass (Numbers.class);
        ParseObject.registerSubclass (MsgRealTime.class);
        FacebookSdk.sdkInitialize (getApplicationContext ());
        ParseUser.enableAutomaticUser ();
        ParseACL defaultAcl = new ParseACL ();
        defaultAcl.setPublicReadAccess (true);
        defaultAcl.setPublicWriteAccess (true);
        ParseACL.setDefaultACL (defaultAcl, true);
        AccessToken.refreshCurrentAccessTokenAsync ();
    }
}