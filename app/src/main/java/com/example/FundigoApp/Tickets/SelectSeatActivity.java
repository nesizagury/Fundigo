package com.example.FundigoApp.Tickets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class SelectSeatActivity extends AppCompatActivity {
    private String eventObjectId;
    private ListView seatsList;
    private String customerPhone;
    ArrayList<EventsSeats> seatsArray = new ArrayList<> ();
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_select_seat);
        seatsList = (ListView) findViewById (R.id.listView2);

        context = this;

        Intent intentHere1 = getIntent ();
        eventObjectId = intentHere1.getStringExtra ("eventObjectId");
        customerPhone = intentHere1.getStringExtra ("phone");
        ParseQuery<EventsSeats> query = new ParseQuery ("EventsSeats");
        query.whereMatches ("eventObjectId", eventObjectId).whereNotEqualTo ("CustomerPhone", null);
        seatsArray.clear ();
        try {
            List<EventsSeats> tempSeatsList = query.find ();
            if (tempSeatsList.size () == 0) {
                //TODO delete this test loops
                for (int i = 1; i <= 4; i++) {
                    EventsSeats eventsSeats = new EventsSeats ();
                    eventsSeats.put ("price", 1);
                    eventsSeats.put ("eventObjectId", eventObjectId);
                    eventsSeats.put ("seatNumber", "Floor " + i);
                    eventsSeats.setIsSold (false);
                    eventsSeats.save ();
                }
                for (int i = 11; i <= 27; i++) {
                    EventsSeats eventsSeats = new EventsSeats ();
                    eventsSeats.put ("price", 175);
                    eventsSeats.put ("eventObjectId", eventObjectId);
                    eventsSeats.put ("seatNumber", "Orange " + i);
                    eventsSeats.setIsSold (false);
                    eventsSeats.save ();
                }
                for (int i = 101; i <= 117; i++) {
                    EventsSeats eventsSeats = new EventsSeats ();
                    eventsSeats.put ("price", 150);
                    eventsSeats.put ("eventObjectId", eventObjectId);
                    eventsSeats.put ("seatNumber", "Pink " + i);
                    eventsSeats.setIsSold (false);
                    eventsSeats.save ();
                }
                for (int i = 121; i <= 136; i++) {
                    EventsSeats eventsSeats = new EventsSeats ();
                    eventsSeats.put ("price", 150);
                    eventsSeats.put ("eventObjectId", eventObjectId);
                    eventsSeats.put ("seatNumber", "Pink " + i);
                    eventsSeats.setIsSold (false);
                    eventsSeats.save ();
                }
                for (int i = 201; i <= 217; i++) {
                    EventsSeats eventsSeats = new EventsSeats ();
                    eventsSeats.put ("price", 125);
                    eventsSeats.put ("eventObjectId", eventObjectId);
                    eventsSeats.put ("seatNumber", "Yellow " + i);
                    eventsSeats.setIsSold (false);
                    eventsSeats.save ();
                }
                for (int i = 221; i <= 236; i++) {
                    EventsSeats eventsSeats = new EventsSeats ();
                    eventsSeats.put ("price", 125);
                    eventsSeats.put ("eventObjectId", eventObjectId);
                    eventsSeats.put ("seatNumber", "Yellow " + i);
                    eventsSeats.setIsSold (false);
                    eventsSeats.save ();
                }
                for (int i = 207; i <= 213; i++) {
                    EventsSeats eventsSeats = new EventsSeats ();
                    eventsSeats.put ("price", 100);
                    eventsSeats.put ("eventObjectId", eventObjectId);
                    eventsSeats.put ("seatNumber", "Green " + i);
                    eventsSeats.setIsSold (false);
                    eventsSeats.save ();
                }
                for (int i = 225; i <= 231; i++) {
                    EventsSeats eventsSeats = new EventsSeats ();
                    eventsSeats.put ("price", 100);
                    eventsSeats.put ("eventObjectId", eventObjectId);
                    eventsSeats.put ("seatNumber", "Green " + i);
                    eventsSeats.setIsSold (false);
                    eventsSeats.save ();
                }
                tempSeatsList = query.find ();
            }
            seatsArray.addAll (tempSeatsList);
        } catch (ParseException e) {
            e.printStackTrace ();
        }
        seatsList.setAdapter (new SelectSeatAdapter (this, seatsArray));
    }

    class SelectSeatAdapter extends BaseAdapter {
        Context context;
        ArrayList<EventsSeats> seat;
        ArrayList<SeatRow> seatList;

        SelectSeatAdapter(Context c, ArrayList<EventsSeats> seatList) {
            this.seatList = new ArrayList<SeatRow> ();
            this.seat = seatList;

            this.context = c;
            int images = R.drawable.seat_ldpi;
            for (int i = 0; i < seatList.size (); i++) {
                this.seatList.add (new SeatRow (seatList.get (i).getString ("seatNumber"),
                                                       seatList.get (i).getPrice (),
                                                       images,
                                                       seatList.get (i).getObjectId ()));
            }
        }

        @Override
        public int getCount() {
            return seatList.size ();
        }

        @Override
        public Object getItem(int position) {

            return seatList.get (position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInfla = (LayoutInflater) context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
            final View row = layoutInfla.inflate (R.layout.seat_row, parent, false);
            final TextView title = (TextView) row.findViewById (R.id.textView7);
            TextView priceTicket = (TextView) row.findViewById (R.id.textView8);
            ImageView image = (ImageView) row.findViewById (R.id.imageView6);
            Button buyTicket = (Button) row.findViewById (R.id.button3);
            buyTicket.setText (R.string.buy_ticket);
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
            final SeatRow temp = seatList.get (position);
            title.setText (temp.title);
            priceTicket.setText (getApplicationContext ().getString (R.string.price) + " " + temp.ticketPrice + "₪");
            image.setImageResource (temp.image);
            buyTicket.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    Handler handler = new Handler ();
                    handler.postDelayed (new Runnable () {
                        @Override
                        public void run() {
                            for (int i = 0; i < 3; i++) {
                                Toast.makeText (getApplicationContext (),
                                                       "You Have 20 Minutes to complete the purchase, Otherwise the ticket will be available to all again",
                                                       Toast.LENGTH_SHORT).show ();
                            }
                        }
                    }, 0);
                    Intent intentQr = new Intent (SelectSeatActivity.this, WebBrowserActivity.class);
                    intentQr.putExtra ("seatNumber", temp.title);
                    intentQr.putExtra ("eventObjectId", eventObjectId);
                    intentQr.putExtra ("isChoose", "yes");
                    intentQr.putExtra ("seatParseObjId", temp.getParseObjId ());
                    intentQr.putExtra ("phone", customerPhone);
                    intentQr.putExtra ("eventPrice", Integer.toString (temp.ticketPrice));
                    seatsArray.get (position).setCustomerPhone (GlobalVariables.CUSTOMER_PHONE_NUM);
                    try {
                        seatsArray.get (position).save ();
                    } catch (ParseException e) {
                        e.printStackTrace ();
                    }
                    startActivity (intentQr);
                    finish ();
                }
            });

            return row;
        }

        class SeatRow {
            String title;
            int ticketPrice;
            int image;
            String seatParseObjId;

            SeatRow(String title, int ticketPrice, int image, String seatParseObjId) {
                this.title = title;
                this.ticketPrice = ticketPrice;
                this.image = image;
                this.seatParseObjId = seatParseObjId;
            }

            public String getParseObjId() {
                return seatParseObjId;
            }
        }
    }
}
