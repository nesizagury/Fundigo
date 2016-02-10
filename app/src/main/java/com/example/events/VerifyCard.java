package com.example.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.devmarvel.creditcardentry.library.CardValidCallback;
import com.devmarvel.creditcardentry.library.CreditCard;
import com.devmarvel.creditcardentry.library.CreditCardForm;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by nesi on 31/12/2015.
 */


public class VerifyCard extends AppCompatActivity {

    TextView event_name;
    TextView price;
    String eventName;
    private String eventDate;// assaf added
    private String customerNumber;
    private int numberOfTickets;
    //final String[] validCard = new String[]{"false"}; // Assaf:final Array since for Anynymos/inner method be be bale to update it

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Intent intent = getIntent();
        eventName = intent.getStringExtra("eventName");
        final String eventPrice = intent.getStringExtra("eventPrice");

        event_name = (TextView) findViewById(R.id.event_name_tv);
        price = (TextView) findViewById(R.id.price_tv);

        event_name.setText(eventName);
        price.setText(eventPrice);

        eventDate = intent.getStringExtra("eventDate");// Assaf added for save the date in Ticket Table

        final CreditCardForm noZipForm = (CreditCardForm) findViewById(R.id.form_no_zip);
        noZipForm.setOnCardValidCallback(cardValidCallback);

       // if (validCard)       //Assaf-  call this method only after card validated
           this.ticketsDataSave();
    }

    CardValidCallback cardValidCallback = new CardValidCallback() {
        @Override
        public void cardValid(CreditCard card) {
            Toast.makeText(VerifyCard.this, "Card valid and complete", Toast.LENGTH_SHORT).show();
            ParseQuery<Event> query = new ParseQuery<Event>("Event");
            List<Event> list = null;
            try {
                list = query.find();
                for (Event eventParse : list) {
                    if (eventName.equals(eventParse.getName())) {
                        int tickets = Integer.parseInt(eventParse.getNumOfTicketsLeft());
                        int t = tickets - 1;
                        String left = Integer.toString(t);
                        Toast.makeText(VerifyCard.this, "Enjoy Your Ticket!", Toast.LENGTH_LONG).show();
                        eventParse.put("NumOfTicketsLeft", left);

                        try {
                            eventParse.save();
                            //validCard[0] = "true"; // Assaf - a flag to insure card validaion and call "TicketsDatasaved"
                                                      // method after card validated
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        break;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            finish();
        }
    };

    public void ticketsDataSave() { // Assaf - save tickets and Event to Parse
        List<ParseObject> list;
        addTickets();         // add Ticket Counter to each time ticket purchased.not support more the one Ticket to the same event

        String _userPhoneNumber = this.readFromFile();
        if (!_userPhoneNumber.isEmpty()) {
            try {
                ParseObject _ticketsObj = new ParseObject("Tickets");
                _ticketsObj.put("Number", _userPhoneNumber);
                _ticketsObj.put("Event", eventName);
                _ticketsObj.put("EventDate",eventDate);
                _ticketsObj.put("Tickets", getAmountOfTickets());
                ParseACL parseAcl = new ParseACL();
                parseAcl.setPublicReadAccess(true);
                parseAcl.setPublicWriteAccess(true);
                _ticketsObj.setACL(parseAcl);
                _ticketsObj.saveInBackground();
               } catch (Exception e) {
                Log.e("Exception catch", e.toString());
            }
        }
    }

    private String readFromFile() {
        String phone_number = "";
        try {
            InputStream inputStream = openFileInput ("verify.txt");
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader (inputStream);
                BufferedReader bufferedReader = new BufferedReader (inputStreamReader);
                String receiveString = "";
                while ((receiveString = bufferedReader.readLine ()) != null) {
                    phone_number = receiveString;
                }
                inputStream.close ();
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }

        return phone_number;
       //return "1-555-521-5554";
    }

    public String getAmountOfTickets()
    {
        return String.valueOf(numberOfTickets);
    }

    public void addTickets()
    {
        this.numberOfTickets+=1;
    }
}
