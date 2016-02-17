package com.example.FundigoApp;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by MAMO on 21/01/2016.
 */
public class NotificationHendler extends Application {
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "7FYu4sxa7MuBIdpCmQVWwqNEqX6rxYo6Q0yNKO1v", "cHnuYnSDcfIuM5goHbprCiNxjCWaSaTe22zVzXn9");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

}

