package com.example.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebBackForwardList;
import android.widget.TextView;
import android.widget.Toast;

import com.devmarvel.creditcardentry.library.CardValidCallback;
import com.devmarvel.creditcardentry.library.CreditCard;
import com.devmarvel.creditcardentry.library.CreditCardForm;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by nesi on 31/12/2015.
 */
public class VerifyCard extends AppCompatActivity {

    TextView event_name;
    TextView price;
    String eventName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_payment);

        Intent intent = getIntent ();
        eventName = intent.getStringExtra ("eventName");
        final String eventPrice = intent.getStringExtra ("eventPrice");

        event_name = (TextView) findViewById (R.id.event_name_tv);
        price = (TextView) findViewById (R.id.price_tv);

        event_name.setText (eventName);
        price.setText (eventPrice);
        final CreditCardForm noZipForm = (CreditCardForm) findViewById (R.id.form_no_zip);
        noZipForm.setOnCardValidCallback (cardValidCallback);
    }

    CardValidCallback cardValidCallback = new CardValidCallback () {
        @Override
        public void cardValid(CreditCard card) {
            Toast.makeText (VerifyCard.this, "Card valid and complete", Toast.LENGTH_SHORT).show ();
            ParseQuery<Event> query = new ParseQuery<Event> ("Event");
            List<Event> list = null;
            try {
                list = query.find ();
                for (Event event : list) {
                    if (eventName.equals (event.getName ())) {
                        int tickets = Integer.parseInt (event.getNumOfTicketsLeft ());
                        int t = tickets - 1;
                        String left = Integer.toString (t);
                        Toast.makeText (VerifyCard.this, "Enjoy Yout Ticket!", Toast.LENGTH_LONG).show ();
                        event.put ("NumOfTicketsLeft", left);
                        try {
                            event.save ();
                        } catch (Exception e1) {
                            e1.printStackTrace ();
                        }
                        break;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace ();
            }
            finish ();
        }
    };


    public void pay(View view){

        Intent intent = new Intent(VerifyCard.this, WebRequest.class);
        startActivity(intent);

    }

}
