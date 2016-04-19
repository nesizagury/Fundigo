package com.example.FundigoApp.Customer.Social;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.example.FundigoApp.Verifications.SmsSignUpActivity;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MipoActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    ImageButton massage;
    ImageView notification;
    List<MipoUser> mipoUsers = new ArrayList<MipoUser> ();
    GridView grid;
    MipoGridAdaptor mipoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_mipo);

        massage = (ImageButton) findViewById (R.id.message_Mipo);
        notification = (ImageView) findViewById (R.id.notification_Mipo);

        massage.setOnClickListener (this);
        notification.setOnClickListener (this);
        if (GlobalVariables.IS_CUSTOMER_GUEST) {
            dialogForGuestToRegister ();
        }
    }

    private void downloadProfiles() {
        ParseQuery<Profile> query = new ParseQuery ("Profile");
        query.orderByDescending ("createdAt");
        query.whereExists ("lastSeen");
        List<Profile> profilesList = null;
        try {
            profilesList = query.find ();
            for (int i = 0; i < profilesList.size (); i++) {
                Profile profile = profilesList.get (i);
                mipoUsers.add (new MipoUser (null,
                                                    profile.getName (), profile.getNumber ()));

                mipoUsers.get (i).setPicUrl (profile.getPic ().getUrl ());
                mipoUsers.get (i).setUserLocation (profilesList.get (i).getLocation ());
            }

            for (int i = 0; i < mipoUsers.size (); i++) {
                MipoUser user = mipoUsers.get (i);
                if (user.getUserPhone ().equals (GlobalVariables.CUSTOMER_PHONE_NUM)) {
                    user.setDist (-1);
                } else {
                    ParseGeoPoint parseGeoPoint = user.getUserLocation ();
                    Location userLocation = new Location ("GPS");
                    userLocation.setLatitude (parseGeoPoint.getLatitude ());
                    userLocation.setLongitude (parseGeoPoint.getLongitude ());
                    double distance = (double) GlobalVariables.MY_LOCATION.distanceTo (userLocation) / 1000;
                    DecimalFormat df = new DecimalFormat ("#.##");
                    String dx = df.format (distance);
                    distance = Double.valueOf (dx);
                    user.setDist (distance);
                }
            }

            Collections.sort (mipoUsers, new Comparator<MipoUser> () {
                @Override
                public int compare(MipoUser a, MipoUser b) {
                    if (a.dist < b.dist) return -1;
                    if (a.dist >= b.dist) return 1;
                    return 0;
                }
            });

            grid = (GridView) findViewById (R.id.gridViewMipo);
            mipoAdapter = new MipoGridAdaptor (MipoActivity.this, mipoUsers, false);
            grid.setAdapter (mipoAdapter);
            grid.setOnItemClickListener (MipoActivity.this);
        } catch (ParseException e) {
            e.printStackTrace ();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId ();
        Intent intent;
        if (id == massage.getId ()) {
            intent = new Intent (MipoActivity.this, CustomerMessageConversationsListActivity.class);
            startActivity (intent);
            finish ();
        } else if (id == notification.getId ()) {
            finish ();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        final Intent intent = new Intent (Intent.ACTION_MAIN, null);
        intent.addCategory (Intent.CATEGORY_LAUNCHER);
        final ComponentName cn = new ComponentName ("com.example.mipo",
                                                           "com.example.mipo.MainPageActivity");
        intent.setComponent (cn);
        intent.putExtra ("fundigo", "fun");
        intent.putExtra ("index", mipoUsers.get (position).getUserPhone ());
        intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity (intent);
    }

    @Override
    protected void onResume() {
        super.onResume ();
        if (GlobalVariables.IS_CUSTOMER_REGISTERED_USER &&
                    GlobalVariables.MY_LOCATION != null &&
                    mipoUsers.size () == 0) {
            downloadProfiles ();
        } else if (GlobalVariables.IS_CUSTOMER_REGISTERED_USER &&
                           GlobalVariables.MY_LOCATION == null &&
                           mipoUsers.size () == 0) {
            Toast.makeText (this, "Turn on gps and try again", Toast.LENGTH_LONG).show ();
        }
    }

    public boolean dialogForGuestToRegister() {
        //Assaf:show dialog in case  Guest want to Chat
        final AlertDialog.Builder builder = new AlertDialog.Builder (this);
        builder.setMessage ("In order to Chat or Send Message you have to pass Registration First")
                .setCancelable (true)
                .setNeutralButton ("Register by SMS", new DialogInterface.OnClickListener () {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent smsRegister = new Intent (MipoActivity.this, SmsSignUpActivity.class);
                        startActivity (smsRegister);
                    }
                });

        builder.setPositiveButton ("Cancel", new DialogInterface.OnClickListener () {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel ();
            }
        });
        AlertDialog smsAlert = builder.create ();
        smsAlert.show ();
        return true;
    }
}
