package com.example.FundigoApp.Tickets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.devmarvel.creditcardentry.library.CardValidCallback;
import com.devmarvel.creditcardentry.library.CreditCard;
import com.devmarvel.creditcardentry.library.CreditCardForm;
import com.example.FundigoApp.Events.Event;
import com.example.FundigoApp.R;
import com.parse.ParseQuery;

import java.util.List;

public class TicketsPage extends AppCompatActivity {
    TextView event_name;
    TextView price;
    String eventName;
    String eventObjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_payment);

        Intent intent = getIntent ();
        eventName = intent.getStringExtra ("eventName");
        eventObjectId = intent.getStringExtra ("eventObjectId");
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
            makePurchase ();
            Toast.makeText (TicketsPage.this, R.string.card_valid_and_complete, Toast.LENGTH_SHORT).show ();
        }
    };

    private void makePurchase() {
        ParseQuery<Event> query = new ParseQuery<> ("Event");
        query.whereEqualTo ("eventObjectId", eventObjectId);
        try {
            List<Event> list = query.find ();
            Event event = list.get (0);
            String income = event.getIncome ();
            String price = event.getPrice ().replace ("$", "");
            if (income != null && !income.isEmpty () &&
                        price != null && !price.isEmpty ()) {
                int incomeInt = Integer.parseInt (income);
                int priceInt = Integer.parseInt (price);
                String newIncome = Integer.toString (incomeInt + priceInt);
                event.setIncome (newIncome);
            }
            int tickets = Integer.parseInt (event.getNumOfTicketsLeft ());
            int t = tickets - 1;
            String left = Integer.toString (t);
            Toast.makeText (TicketsPage.this, R.string.enjoy_yout_ticket, Toast.LENGTH_LONG).show ();
            event.put("NumOfTicketsLeft", left);
            event.save ();
        } catch (Exception e1) {
            e1.printStackTrace ();
        }
    }
}
