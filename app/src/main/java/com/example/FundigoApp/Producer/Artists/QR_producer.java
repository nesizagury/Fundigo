package com.example.FundigoApp.Producer.Artists;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethods;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;

public class QR_producer extends AppCompatActivity implements View.OnClickListener{

    private ListView qr_detiels_listView;
    private Button qr_scan_button;
    private Button customer_enter_butt;
    private String seatObjId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_producer);
        qr_scan_button=(Button)findViewById(R.id.qr_scan_button);
        qr_detiels_listView=(ListView)findViewById(R.id.qr_scan_list_view);
        customer_enter_butt=(Button) findViewById(R.id.customer_enter_butt);
        customer_enter_butt.setOnClickListener(this);
        customer_enter_butt.setVisibility(View.GONE);

        qr_scan_button.setOnClickListener(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {
            final String contents = result.getContents();
            if (contents != null) {


                ParseQuery<ParseObject> query = ParseQuery.getQuery("EventsSeats");
                query.include("soldTicketsPointer");
                query.getInBackground(contents, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, com.parse.ParseException e) {
                        if (e == null) {
                            EventInfo eventInfo=StaticMethods.getEventFromObjID(object.getString("eventObjectId"), GlobalVariables.ALL_EVENTS_DATA);
                            if(eventInfo!=null ) {
                                seatObjId = contents;
                                qr_detiels_listView.setAdapter(new QRDetailAdapter(getApplicationContext(), object, eventInfo));
                            }else {
                                Toast.makeText(getApplicationContext(),"No Such Ticket For This Producer",Toast.LENGTH_LONG).show();
                            }
                        } else {
                           Log.d("m1234"," something went wrong");
                        }
                    }
                });



                Log.d("m1234","R.string.result_succeeded "+contents);
            } else {
                Log.d("m1234","R.string.result_failed");
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.qr_scan_button:
                IntentIntegrator integrator = new IntentIntegrator(this);
                integrator.initiateScan();
                Log.d("m1234", "on create");
                break;
            case R.id.customer_enter_butt:
                ParseQuery<ParseObject> query = ParseQuery.getQuery("EventsSeats");
                query.getInBackground(seatObjId, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, com.parse.ParseException e) {
                        if (e == null) {
                            object.put("CustomerEnter",true);
                            object.saveInBackground();
                            customer_enter_butt.setVisibility(View.GONE);
                        } else {
                            Log.d("m1234"," something went wrong");
                        }
                    }
                });

        }
    }



    class QR_Row {
        String title;
        String description;


        QR_Row(String title, String description) {
            this.title = title;
            this.description = description;

        }
    }

    class QRDetailAdapter extends BaseAdapter {
        String title;
        ParseObject parseObject;
        ParseObject soldTicets;
        Context context;
        EventInfo eventInfo;

        ArrayList<QR_Row> list;

        QRDetailAdapter(final Context c, ParseObject parseObject, EventInfo eventInfo) {
            list = new ArrayList<QR_Row> ();
            this.parseObject=parseObject;
            this.context=c;
            this.eventInfo=eventInfo;

            list.add(new QR_Row("Event",eventInfo.getName()));

            if(parseObject.getBoolean("CustomerEnter")){
                list.add (new QR_Row (c.getString(R.string.customer_enter),c.getString(R.string.yes)));
            }else{
                list.add (new QR_Row (c.getString(R.string.customer_enter),c.getString(R.string.no)));
            }

            if(parseObject.getBoolean("sold")){
                list.add (new QR_Row (c.getString(R.string.sold),c.getString(R.string.yes)));
            }else{
                list.add (new QR_Row (c.getString(R.string.sold),c.getString(R.string.no)));
            }

            list.add(new QR_Row(c.getString(R.string.customer_phone), parseObject.getString("CustomerPhone")));
            list.add (new QR_Row (c.getString(R.string.seat_number), parseObject.getString("seatNumber")));
            list.add (new QR_Row (c.getString(R.string.price), parseObject.getString("price")));
            list.add (new QR_Row (c.getString(R.string.purchase_date), parseObject.getDate("purchase_date").toString()));


            list.add (new QR_Row (c.getString(R.string.confirmation_code), parseObject.getParseObject("soldTicketsPointer")
                    .getString("ConfirmationCode")));
            list.add (new QR_Row (c.getString(R.string.buyer_first_name), parseObject.getParseObject("soldTicketsPointer")
                    .getString("Firstname")));
            list.add (new QR_Row (c.getString(R.string.buyer_last_name), parseObject.getParseObject("soldTicketsPointer")
                    .getString("lastname")));
            list.add (new QR_Row (c.getString(R.string.order_id), parseObject.getParseObject("soldTicketsPointer")
                    .getString("orderid")));
            list.add (new QR_Row (c.getString(R.string.amount), parseObject.getParseObject("soldTicketsPointer")
                    .getNumber("amount").toString()));
            list.add (new QR_Row (c.getString(R.string.email), parseObject.getParseObject("soldTicketsPointer")
                    .getString("email")));
            list.add (new QR_Row (c.getString(R.string.buyer_phone), parseObject.getParseObject("soldTicketsPointer")
                    .getString("Phone")));
            list.add (new QR_Row (c.getString(R.string.response_code), parseObject.getParseObject("soldTicketsPointer")
                    .getString("Response")));


            ArrayList<QR_Row> tempList = new ArrayList<QR_Row> ();
            for (int i = 0; i < list.size (); i++) {
                if (list.get (i).description != null && !list.get (i).description.isEmpty ()) {
                    tempList.add (list.get (i));
                }
            }
            if(!parseObject.getBoolean("CustomerEnter")) {
                customer_enter_butt.setVisibility(View.VISIBLE);
            }else{
                customer_enter_butt.setVisibility(View.GONE);
            }
            list.clear ();
            list.addAll (tempList);
        }

        @Override
        public int getCount() {
            return list.size ();
        }

        @Override
        public Object getItem(int position) {
            return list.get (position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater layoutInfla = (LayoutInflater) context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInfla.inflate (R.layout.qr_scan_row, parent, false);
            TextView title = (TextView) row.findViewById (R.id.textView_categor_qr);
            TextView description = (TextView) row.findViewById (R.id.textView_descri_qr);

            QR_Row temp = list.get (position);
            title.setText (temp.title);
            description.setText (temp.description);

            if(parseObject.getBoolean("CustomerEnter")){
                description.setTextColor(Color.RED);
                title.setTextColor(Color.RED);
            }

            return row;
        }
    }
}









