package com.example.FundigoApp.Customer.CustomerMenu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethods;
import com.example.FundigoApp.Tickets.CustomerTicketsListAdapter;
import com.example.FundigoApp.Tickets.EventsSeats;
import com.example.FundigoApp.Tickets.EventsSeatsInfo;
import com.example.FundigoApp.Tickets.GetTicketQRCodeActivity;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyEventsTicketsActivity extends AppCompatActivity {
    static List<EventInfo> my_tickets_events_list = new ArrayList<EventInfo> ();
    static ArrayList<EventsSeatsInfo> my_tickets_list = new ArrayList<EventsSeatsInfo> ();

    ListView listT;
    TextView noTickets;
    ListAdapter _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_events_tickets);
        noTickets = (TextView) findViewById (R.id.noTickets);
        listT = (ListView) findViewById (R.id.listOfEventsTickets);

        _adapter = new CustomerTicketsListAdapter (this, R.layout.content_events_tickets, my_tickets_list);
        listT.setAdapter (_adapter);

        getListOfEventsTickets ();
    }

    public void getListOfEventsTickets() {
        my_tickets_events_list.clear ();
        my_tickets_list.clear ();
        String _userPhoneNumber = GlobalVariables.CUSTOMER_PHONE_NUM;
        List<EventsSeats> list;
        try {

            ParseQuery<EventsSeats> query = ParseQuery.getQuery ("EventsSeats");
            query.whereEqualTo ("CustomerPhone", _userPhoneNumber).whereEqualTo("sold", true).orderByDescending ("updatedAt");
            list = query.find ();
            Log.e("list size",list.size() + "");
            if (list.size () != 0) {
                for (EventsSeats eventsSeats : list) {
                    if (eventsSeats.getQR_CodeFile () == null) {
                        DownlandTask downlandTask = new DownlandTask ();
                        downlandTask.execute (GetTicketQRCodeActivity.googleUrl + eventsSeats.getObjectId (), eventsSeats.getObjectId ());
                        Thread.sleep (2000);
                        getListOfEventsTickets ();
                        return;
                    }
                    Bitmap qrCode;
                    byte[] data = null;
                    ParseFile imageFile = (ParseFile) eventsSeats.get ("QR_Code");
                    if (imageFile != null) {
                        try {
                            data = imageFile.getData ();
                        } catch (ParseException e1) {
                            e1.printStackTrace ();
                        }
                        qrCode = BitmapFactory.decodeByteArray (data, 0, data.length);
                    } else {
                        qrCode = null;
                    }
                    if (eventsSeats.getSoldTicketsPointer () == null) {
                        ParseQuery<ParseObject> querySoldTicket = ParseQuery.getQuery ("SoldTickets");
                        querySoldTicket.whereEqualTo ("orderid", eventsSeats.getObjectId ());
                        ParseObject parseObject = querySoldTicket.getFirst ();
                        eventsSeats.setPurchaseDate (parseObject.getCreatedAt ());
                        eventsSeats.setSoldTicketsPointer (ParseObject.createWithoutData ("SoldTickets", parseObject.getObjectId ()));
                        eventsSeats.save ();
                    }
                    EventInfo eventInfo = StaticMethods.getEventFromObjID (eventsSeats.getString ("eventObjectId"), GlobalVariables.ALL_EVENTS_DATA);
                    Date current_date = new Date ();
                    Date event_date = eventInfo.getDate ();
                    eventInfo.setIsFutureEvent(event_date.after(current_date));
                    my_tickets_events_list.add (eventInfo);
                    my_tickets_list.add (new EventsSeatsInfo (eventsSeats.getSeatNumber (),
                                                                     qrCode,
                                                                     eventsSeats.getPurchaseDate (),
                                                                     eventsSeats.getIntPrice (),
                                                                     eventInfo));
                }
                listT.deferNotifyDataSetChanged ();
            } else {
                noTickets.setText ("No Tickets To Display");
                noTickets.setVisibility (View.VISIBLE);
            }
        } catch (ParseException e) {
            e.printStackTrace ();
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    public void onClickButton(View v) {
        final Intent intent = new Intent (this, CustomerTicketsMoreDetailesActivity.class);
        try {
            View parentRow = (View) v.getParent ();
            ListView _listView = (ListView) parentRow.getParent ();
            int _position = _listView.getPositionForView (parentRow);
            intent.putExtra ("index", _position);
            startActivity (intent);
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    public class DownlandTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute ();
        }


        @Override
        protected String doInBackground(String... params) {
            String path = params[0];
            String seatObjectId = params[1];
            int fileLength = 0;
            try {
                URL url = new URL (path);
                URLConnection urlConnection = url.openConnection ();
                urlConnection.connect ();
                fileLength = urlConnection.getContentLength ();
                File newFolder = new File ("sdcard/fundigo_qr_code");
                if (!newFolder.exists ()) {
                    newFolder.mkdir ();
                }
                File input_file = new File (newFolder, seatObjectId + ".jpg");
                InputStream inputStream = new BufferedInputStream (url.openStream (), 8192);
                byte[] data = new byte[1024];
                int total = 0;
                int count = 0;
                OutputStream outputStream = new FileOutputStream (input_file);
                while ((count = inputStream.read (data)) != -1) {
                    total += count;
                    outputStream.write (data, 0, count);
                    int progress = (int) (total * 100 / fileLength);
                    publishProgress (progress);
                }
                inputStream.close ();
                outputStream.close ();
                ParseFile file = new ParseFile (input_file);
                try {
                    file.save ();
                    ParseQuery<EventsSeats> query = ParseQuery.getQuery ("EventsSeats");
                    query.whereEqualTo ("objectId", seatObjectId);
                    EventsSeats eventsSeats = query.getFirst ();
                    eventsSeats.put ("QR_Code", file);
                    eventsSeats.save ();
                } catch (ParseException e1) {
                    e1.printStackTrace ();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace ();
            } catch (IOException e) {
                e.printStackTrace ();
            }
            return "Downlaod Complete...";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }
}

