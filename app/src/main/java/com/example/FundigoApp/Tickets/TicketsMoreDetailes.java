package com.example.FundigoApp.Tickets;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.FundigoApp.Customer.CustomerMenu.EventsTickets;
import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.Events.EventPage;
import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethods;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by assafbe on 25/02/2016.
 */
public class TicketsMoreDetailes extends AppCompatActivity {

    private Intent intent;
    private TextView purchaseTv;
    private TextView eventLinkTv;
    private ImageView qrImg;
    private Intent linkToEventIntent;
    private EventInfo eventInfo;
    List<EventInfo> listOfSingleEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets_more_detailes);
        intent = getIntent();
        purchaseTv = (TextView) findViewById(R.id.dateRow);
        qrImg = (ImageView) findViewById(R.id.qrTicketImage);
        eventLinkTv = (TextView) findViewById(R.id.linkEventRow);
        getIntentData();
    }

    public void getIntentData() {
        try {

            EventsTickets _evenTickets = new EventsTickets();
            //Get data from Intent sent by EventTickets
            String purchaseDate = intent.getStringExtra("purchaseDate");
            Bitmap _qrCodeImage = intent.getParcelableExtra("qrCode");
             //eventInfo = b.getParcelable("singleEvent");
            //eventInfo =intent.getParcelableExtra("singleEvent");
            eventInfo = _evenTickets._singleEvenInfo;
            purchaseTv.setText(purchaseDate);
            qrImg.setImageBitmap(_qrCodeImage);

        } catch (Exception e) {
            Log.e(e.getMessage(), "intentData Exception");
        }
        //Intent for Presnet the Event when click the link
        linkToEventIntent = new Intent (this,EventPage.class);
        listOfSingleEvent = new ArrayList<>();
        listOfSingleEvent.add(eventInfo);
        eventLinkTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    //linkToEventIntent.putExtra("event",(Parcelable)eventInfo);
                    Bundle b = new Bundle();
                    StaticMethods.onEventItemClick(0, listOfSingleEvent, linkToEventIntent);
                    linkToEventIntent.putExtras(b);
                    startActivity(linkToEventIntent);
                } catch (Exception e) {
                   Log.e(e.getMessage(),"Exception OnClick and Intentbuild");
                }
            }
        });
        }
}
