package com.example.events;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.SaveCallback;

public class RealTimeGuestAddition extends Activity {


    int GuestIn = 0;
    EditText qrCodeET;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        setContentView (R.layout.activity_guest_addition);
        DisplayMetrics ma = new DisplayMetrics ();
        getWindowManager ().getDefaultDisplay ().getMetrics (ma);

        qrCodeET = (EditText) findViewById(R.id.codeET);

        int width = ma.widthPixels;
        int height = ma.heightPixels;

        getWindow ().setLayout ((int) (width * .9), (int) (height * .350));

        intent = getIntent();

        Toast.makeText(getApplicationContext(),"artist = " + intent.getStringExtra("artist"), Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(),"guest = " + intent.getStringExtra("guestIn"), Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(),"producer = " + intent.getStringExtra("producer"), Toast.LENGTH_SHORT).show();



    }

    public void Add(View view)
    {

        RealTimeEvent rte = new RealTimeEvent ();
        ParseACL parseAcl = new ParseACL ();
        parseAcl.setPublicReadAccess(true);
        parseAcl.setPublicWriteAccess(true);
        rte.setACL(parseAcl);
        rte.setQRCode(qrCodeET.getText().toString());
        rte.setProducer(MainActivity.producerId);
        rte.setArtist(intent.getStringExtra("artist"));
        rte.setEventName(intent.getStringExtra("name"));
        int guestIn = Integer.parseInt(intent.getStringExtra("guestIn"));
        guestIn ++;
        EventOnRealtime.sumGuest = Integer.toString(guestIn);
        rte.setGuestIn(Integer.toString(guestIn));
        rte.saveInBackground (new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if(e == null) {
                    Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });

    }
}