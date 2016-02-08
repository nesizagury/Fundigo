package com.example.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.devmarvel.creditcardentry.library.CardValidCallback;
import com.devmarvel.creditcardentry.library.CreditCard;
import com.devmarvel.creditcardentry.library.CreditCardForm;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by nesi on 31/12/2015.
 */
public class TicketsPage extends AppCompatActivity {

    List<Event> eventsList = new ArrayList<Event> ();
    ListView list_view;
    TextView event_name;
    TextView price;
    private CreditCardForm form;
    String eventName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_payment);

        Intent intent = getIntent();
        eventName = intent.getStringExtra("eventName");
        final String eventPrice = intent.getStringExtra ("eventPrice");

        event_name = (TextView) findViewById(R.id.event_name_tv);
        price = (TextView) findViewById(R.id.price_tv);

        event_name.setText(eventName);
        price.setText(eventPrice);
        final CreditCardForm noZipForm = (CreditCardForm) findViewById(R.id.form_no_zip);
        noZipForm.setOnCardValidCallback(cardValidCallback);



    }


    CardValidCallback cardValidCallback = new CardValidCallback() {
        @Override
        public void cardValid(CreditCard card) {

            makePurchase();

            Toast.makeText(TicketsPage.this, "Card valid and complete", Toast.LENGTH_SHORT).show();


        }
    };

    private void makePurchase() {

        ParseQuery<Event> query = new ParseQuery<> ("Event");
        try {
            List<Event> list = query.find();
            for (Event event : list) {
                if (eventName.equals(event.getName())) {

                    int accountBalance = Integer.parseInt(event.getAccountBalance());
                    int a = accountBalance + Integer.parseInt(event.getPrice());
                    String balance = Integer.toString(a);
                    event.put ("AccountBalance", balance);

                    int tickets = Integer.parseInt(event.getNumOfTicketsLeft());
                    int t = tickets - 1;
                    String left = Integer.toString(t);
                    Toast.makeText(TicketsPage.this, "Enjoy Yout Ticket!", Toast.LENGTH_LONG).show();
                    event.put("NumOfTicketsLeft", left);

                    try {
                        event.save();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }


        } catch (ParseException e) {
            e.printStackTrace ();
        }
    }

}
