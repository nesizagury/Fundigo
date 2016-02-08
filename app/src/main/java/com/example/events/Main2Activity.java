package com.example.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class Main2Activity extends AppCompatActivity {
    private final static String TAG = "Main2Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(com.example.events.Message.class);
        ParseObject.registerSubclass(com.example.events.Room.class);
        ParseObject.registerSubclass(com.example.events.Numbers.class);
        FacebookSdk.sdkInitialize(getApplicationContext());
        ParseUser.enableAutomaticUser();
        ParseACL defaultAcl = new ParseACL();
        defaultAcl.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultAcl, true);
        if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            Intent intent = new Intent(this, LoginActivity2.class);
            startActivity(intent);
            finish();
        } else {
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser != null && true == false) {
                Toast.makeText(getApplicationContext(), "Successfully Signed in", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(this, LoginActivity2.class);
                startActivity(intent);
                finish();
            }
        }
    }
}
