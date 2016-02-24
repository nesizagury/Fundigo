package com.example.FundigoApp.Producer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
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


    }

    public void Add(View view)
    {

        RealTimeEvent rte = new RealTimeEvent ();
        ParseACL parseAcl = new ParseACL ();
        parseAcl.setPublicReadAccess(true);
        parseAcl.setPublicWriteAccess(true);
        rte.setACL(parseAcl);
        rte.setQRCode(qrCodeET.getText().toString());
        rte.setProducer(GlobalVariables.PRODUCER_PARSE_OBJECT_ID);
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