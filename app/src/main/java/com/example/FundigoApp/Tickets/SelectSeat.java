package com.example.FundigoApp.Tickets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.FundigoApp.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class SelectSeat extends AppCompatActivity {

    private String eventObjectId;
    private ListView mylist;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_select_seat);
        mylist = (ListView) findViewById (R.id.listView2);

        Intent intentHere1 = getIntent ();
        eventObjectId = intentHere1.getStringExtra ("eventObjectId");
        phone = intentHere1.getStringExtra ("phone");
        ParseQuery<ParseObject> query = ParseQuery.getQuery ("EventsSeats").whereMatches ("eventObjectId", eventObjectId).whereDoesNotExist ("QR_Code");

        ArrayList<ParseObject> seatsList = new ArrayList<> ();
        try {
            List<ParseObject> commentList = query.find ();
            if(commentList.size () == 0){
                for(int i = 1; i <= 4; i++){
                    EventsSeats eventsSeats = new EventsSeats ();
                    eventsSeats.put("price", 200);
                    eventsSeats.put("eventObjectId", eventObjectId);
                    eventsSeats.put("seatNumber", "Floor " + i);
                    eventsSeats.save ();
                }
                for(int i = 11; i <= 27; i++){
                    EventsSeats eventsSeats = new EventsSeats ();
                    eventsSeats.put("price", 175);
                    eventsSeats.put("eventObjectId", eventObjectId);
                    eventsSeats.put ("seatNumber", "Orange " + i);
                    eventsSeats.save ();
                }
                for(int i = 101; i <= 117; i++){
                    EventsSeats eventsSeats = new EventsSeats ();
                    eventsSeats.put("price", 150);
                    eventsSeats.put("eventObjectId", eventObjectId);
                    eventsSeats.put("seatNumber", "Pink " + i);
                    eventsSeats.save ();
                }
                for(int i = 121; i <= 136; i++){
                    EventsSeats eventsSeats = new EventsSeats ();
                    eventsSeats.put("price", 150);
                    eventsSeats.put("eventObjectId", eventObjectId);
                    eventsSeats.put("seatNumber", "Pink " + i);
                    eventsSeats.save ();
                }
                for(int i = 201; i <= 217; i++){
                    EventsSeats eventsSeats = new EventsSeats ();
                    eventsSeats.put("price", 125);
                    eventsSeats.put("eventObjectId", eventObjectId);
                    eventsSeats.put("seatNumber", "Yellow " + i);
                    eventsSeats.save ();
                }
                for(int i = 221; i <= 236; i++){
                    EventsSeats eventsSeats = new EventsSeats ();
                    eventsSeats.put("price", 125);
                    eventsSeats.put("eventObjectId", eventObjectId);
                    eventsSeats.put("seatNumber", "Yellow " + i);
                    eventsSeats.save ();
                }
                for(int i = 207; i <= 213; i++){
                    EventsSeats eventsSeats = new EventsSeats ();
                    eventsSeats.put("price", 100);
                    eventsSeats.put("eventObjectId", eventObjectId);
                    eventsSeats.put("seatNumber", "Green " + i);
                    eventsSeats.save ();
                }
                for(int i = 225; i <= 231; i++){
                    EventsSeats eventsSeats = new EventsSeats ();
                    eventsSeats.put("price", 100);
                    eventsSeats.put("eventObjectId", eventObjectId);
                    eventsSeats.put("seatNumber", "Green " + i);
                    eventsSeats.save ();
                }
                commentList = query.find ();
            }
            seatsList.addAll (commentList);
        } catch (ParseException e) {
            e.printStackTrace ();
        }

        mylist.setAdapter (new SelectSeatAdapter (this, seatsList));
    }

    class SelectSeatAdapter extends BaseAdapter {
        Context context;
        ArrayList<ParseObject> seat;
        ArrayList<SeatRow> list;

        SelectSeatAdapter(Context c, ArrayList<ParseObject> seat) {
            list = new ArrayList<SeatRow> ();
            this.seat = seat;

            this.context = c;
            int images = R.drawable.seat_ldpi;
            Log.d ("xxx", " SelectSeatAdapter(Context c,ArrayL...");
            for (int i = 0; i < seat.size (); i++) {
                String price;
                if (seat.get (i).get ("price") == null || seat.get (i).get ("price") == "") {
                    price = "Free";
                } else {
                    price = seat.get (i).get ("price").toString () + "$";
                }
                list.add (new SeatRow (seat.get (i).getString ("seatNumber"), price, images, seat.get (i).getObjectId ()));

            }


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
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater layoutInfla = (LayoutInflater) context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
            final View row = layoutInfla.inflate (R.layout.seat_row, parent, false);
            final TextView title = (TextView) row.findViewById (R.id.textView7);
            TextView description = (TextView) row.findViewById (R.id.textView8);
            ImageView image = (ImageView) row.findViewById (R.id.imageView6);
            Button buyTicket = (Button) row.findViewById (R.id.button3);
            buyTicket.setText ("Buy Ticket");
            buyTicket.setVisibility (View.GONE);

            row.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    if (row.findViewById (R.id.button3).getVisibility () == View.GONE) {
                        row.findViewById (R.id.button3).setVisibility (View.VISIBLE);
                    } else {
                        row.findViewById (R.id.button3).setVisibility (View.GONE);
                    }
                }

            });
            final SeatRow temp = list.get (position);
            title.setText (temp.title);
            description.setText ("Price: " + temp.description);
            image.setImageResource (temp.image);
            buyTicket.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    Intent intentQr = new Intent (SelectSeat.this, WebBrowser.class);
                    intentQr.putExtra ("seatNumber", temp.title);
                    intentQr.putExtra ("eventObjectId", eventObjectId);
                    intentQr.putExtra ("isChoose", "yes");
                    intentQr.putExtra ("seatKey", temp.getSeatKey ());
                    intentQr.putExtra ("phone", phone);
                    intentQr.putExtra ("eventPrice",temp.description);
                    startActivity (intentQr);

                }
            });

            return row;
        }

        class SeatRow {
            String title;
            String description;
            int image;
            String seatKey;

            SeatRow(String title, String description, int image, String seatKey) {
                this.title = title;
                this.description = description;
                this.image = image;
                this.seatKey = seatKey;
            }

            public String getSeatKey() {
                return seatKey;
            }

        }
    }
}
