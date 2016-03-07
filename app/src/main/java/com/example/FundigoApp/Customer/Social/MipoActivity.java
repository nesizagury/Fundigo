package com.example.FundigoApp.Customer.Social;

import android.content.ComponentName;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MipoActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener {
    ImageButton massage;
    ImageView notification;
    List<MipoUser> mipoUsers = new ArrayList<MipoUser>();
    GridView grid;
    MipoGridAdaptor mipoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mipo);

        massage = (ImageButton) findViewById (R.id.message_Mipo);
        notification = (ImageView) findViewById (R.id.notification_Mipo);

        massage.setOnClickListener(this);
        notification.setOnClickListener(this);

        uploadProfiles();



    }

    private void uploadProfiles() {


        ParseQuery<MipoProfile> query = new ParseQuery ("Profile");
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<MipoProfile>() {
            public void done(List<MipoProfile> profilesList, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < profilesList.size(); i++) {
                        MipoProfile profile = profilesList.get(i);
                        //Date currentDate = new Date();
                        //long diff = currentDate.getTime() - profile.getLastSeen().getTime();
                        //long diffMinutes = diff / (60 * 1000);
                        mipoUsers.add(new MipoUser(null,
                                profile.getName(),profile.getUser().getObjectId()));

                        if(profilesList.get(i).getPic() != null)
                        {
                           mipoUsers.get(i).setPicUrl(profile.getPic().getUrl());
                        }

                        mipoUsers.get(i).setUserLocation(profilesList.get(i).getLocation());

                    }

                    for (int i = 0; i < mipoUsers.size(); i++) {
                    MipoUser user = mipoUsers.get(i);
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

                    Collections.sort (mipoUsers, new Comparator<MipoUser>() {
                        @Override
                        public int compare(MipoUser a, MipoUser b) {
                            if (a.dist < b.dist) return -1;
                            if (a.dist >= b.dist) return 1;
                            return 0;
                        }
                    });

                    grid = (GridView) findViewById (R.id.gridViewMipo);
                    mipoAdapter = new MipoGridAdaptor (MipoActivity.this, mipoUsers, false);
                    grid.setAdapter(mipoAdapter);
                    grid.setOnItemClickListener(MipoActivity.this);

                }



            }
        });

    }

    @Override
    public void onClick(View v) {
        int id = v.getId ();
        Intent intent;
        if (id == massage.getId ()) {
            intent = new Intent (MipoActivity.this, CustomerMessageConversationsListActivity.class);
            startActivity (intent);
        } else if (id == notification.getId ()) {
            intent = new Intent (MipoActivity.this, MyNotificationsActivity.class);
            startActivity (intent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        final ComponentName cn = new ComponentName("com.example.mipo",
                "com.example.mipo.MainPageActivity");
        intent.setComponent(cn);
        intent.putExtra("fundigo","fun");
        intent.putExtra("index",position+"");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
