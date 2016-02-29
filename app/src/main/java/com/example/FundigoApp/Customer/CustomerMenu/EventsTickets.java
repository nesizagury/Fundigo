package com.example.FundigoApp.Customer.CustomerMenu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethods;
import com.example.FundigoApp.Tickets.CustomAdapterTicketsListView;
import com.example.FundigoApp.Tickets.EventsSeats;
import com.example.FundigoApp.Tickets.EventsSeatsInfo;
import com.example.FundigoApp.Tickets.TicketsMoreDetailes;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventsTickets extends AppCompatActivity {
    private final ArrayList<EventInfo> my_tickets_events_list = new ArrayList<> ();
    private final ArrayList<EventsSeatsInfo> arrayOfEventsInfo = new ArrayList<>();
    View customView;
    ListView listT;
    Button listViewButton;
    TextView noTickets;
    public static EventInfo _singleEvenInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_tickets);
        noTickets = (TextView)findViewById(R.id.noTickets);
       try {

            this.getListOfEventsTickets();
        }
        catch (Exception e)
        {
            Log.e (e.toString() , "Call to getListEveneTickets");
        }

    }

        public void displayTicektsData() {
            try {

                listT = (ListView) findViewById(R.id.listOfEventsTickets);
                ListAdapter _adapter = new CustomAdapterTicketsListView(this, R.layout.content_events_tickets, arrayOfEventsInfo);
                listT.setAdapter(_adapter);

            } catch (Exception e) {
                Log.e(e.toString(), "DisplayTicket Data Exception");
            }

        }

            public void onClickButton(View v) {
                final Intent intent = new Intent(this, TicketsMoreDetailes.class);
                listViewButton = (Button) findViewById(R.id.moreDetailesButton);

                try {
                    View parentRow = (View) v.getParent();
                    ListView _listView = (ListView) parentRow.getParent();
                    int _position = _listView.getPositionForView(parentRow);
                     //Bundle b = new Bundle();
                     //b.putParcelable("singleEvent", my_tickets_events_list.get(_position));
                    //intent.putExtra("singleEvent", (Parcelable) my_tickets_events_list.get(_position));
                    _singleEvenInfo = my_tickets_events_list.get(_position);
                    intent.putExtra("purchaseDate", arrayOfEventsInfo.get(_position).getPurchaseDate().toString());
                    intent.putExtra("qrCode", arrayOfEventsInfo.get(_position).getQR());
                    //intent.putExtras(b);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }



    public void getListOfEventsTickets() { // upload tickets detailes sorted by purchase date


            String _userPhoneNumber = GlobalVariables.CUSTOMER_PHONE_NUM;
            ParseQuery<EventsSeats> query = ParseQuery.getQuery("EventsSeats");
            try {
            query.whereEqualTo("buyer_phone", _userPhoneNumber).addDescendingOrder("purchase_date");
            query.findInBackground(new FindCallback<EventsSeats>() {
                public void done(List<EventsSeats> List, ParseException e) {
                    if (e == null) {
                       ParseFile imageFile;
                        byte[] data = null;
                        String tempEventDate;
                        String tempEventName;
                        Bitmap qrCode;
                        Date _date = new Date();

                        for (EventsSeats obj : List) {
                            tempEventDate = StaticMethods.getEventFromObjID(obj.getString("eventObjectId"),
                                    GlobalVariables.ALL_EVENTS_DATA).getDate();
                            try {
                                //Format Events date String to Date object
                                DateFormat format = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
                                 _date = format.parse(tempEventDate);
                            } catch (Exception e2) {
                                Log.e(e2.getMessage(), "Date Format Exception");
                            }
                            // Compare if Event date passed or not BETWEEN LOCAL TIME AND EVENT TIME ON THE SAME TIME ZONE
                            if (_date.after(getCurrentDate()))
                            {
                                imageFile = (ParseFile) obj.get("QR_Code");
                                if (imageFile != null) {
                                    try {
                                        data = imageFile.getData();
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                    qrCode = BitmapFactory.decodeByteArray(data, 0, data.length);
                                } else {
                                    qrCode = null;
                                }
                                //full data Events for present the EventInfo later
                                my_tickets_events_list.add(StaticMethods.getEventFromObjID(obj.getString("eventObjectId"),
                                        GlobalVariables.ALL_EVENTS_DATA));
                                tempEventName = StaticMethods.getEventFromObjID(obj.getString("eventObjectId"),
                                        GlobalVariables.ALL_EVENTS_DATA).getName();
                                //  Tickets and event Data
                                arrayOfEventsInfo.add(new EventsSeatsInfo(tempEventName, obj.getSeatNumber(), obj.getPurchaseDate(),
                                        obj.getIntPrice(), tempEventDate, qrCode));
                            }
                        }
                        Log.d("OK", "Passed ok");
                        if (List.size() != 0 && arrayOfEventsInfo.size()!=0)
                            try {
                                 displayTicektsData();
                            } catch (Exception e1) {
                                Log.e(e.toString(), "DisplayTickets Find in Background");
                            }
                        else {//if no tickets or tickets events are old
                            noTickets.setText("No Tickets To Display");
                            noTickets.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.d("Failed", e.getMessage());
                    }
                }
            });

        }
        catch (Exception e)
        {
            Log.e (e.toString(),"Find in BackgroundcallBack");
        }
    }

      public Date getCurrentDate()// current date
      {
           Date currentTime = new Date(System.currentTimeMillis());
          return currentTime;
      }


//    @Override
//    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
//        StaticMethods.onActivityResult(requestCode,
//                data,
//                this);
//    }
}

