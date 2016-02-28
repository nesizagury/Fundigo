package com.example.FundigoApp.Events;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.example.FundigoApp.Tickets.EventsSeats;
import com.example.FundigoApp.Tickets.Ticket;
import com.example.FundigoApp.Tickets.TicketAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class EventStatus extends Activity {

    private static final String TAG = "EventStatus";
    TextView eventNameTV;
    TextView soldTV;
    TextView leftTV;
    TextView incomeTV;
    TextView futureTV;
    ListView lv_tickets;
    TextView tv_price;
    TextView tv_ticket;
    String eventObjectId;
    ImageView imageView;
    private TicketAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_status);

        eventNameTV = (TextView) findViewById(R.id.eventNameTV);
        eventNameTV.setText("" + getIntent().getStringExtra("name"));
        soldTV = (TextView) findViewById(R.id.soldTV);
        leftTV = (TextView) findViewById(R.id.leftTV);
        incomeTV = (TextView) findViewById(R.id.incomeTV);
        futureTV = (TextView) findViewById(R.id.futureTV);
        lv_tickets = (ListView) findViewById(R.id.lv_tickets);
        tv_price = (TextView) findViewById(R.id.ticketItem_tv_price);
        tv_ticket = (TextView) findViewById(R.id.ticketItem_tv_ticket);
        imageView = (ImageView) findViewById(R.id.iv_arena);

        for (int i = 0; i < GlobalVariables.ALL_EVENTS_DATA.size(); i++) {
            EventInfo event = GlobalVariables.ALL_EVENTS_DATA.get(i);

            if (event.getParseObjectId().equals(getIntent().getStringExtra("eventObjectId"))) {
                eventObjectId = getIntent().getStringExtra("eventObjectId");
                soldTV.setText("Tickets Sold: " + event.getSold());
                leftTV.setText("Tickets Left: " + event.getTicketsLeft());
                incomeTV.setText("Sum Income: " + event.getIncome());
                // StringBuilder sb = new StringBuilder (event.getPrice ());
                // sb.deleteCharAt (sb.length () - 1);
                int ticketsLeft = Integer.parseInt(event.getTicketsLeft());

                if (event.getPrice().equals("FREE")) {
                    futureTV.setText("The event is free");
                } else {
//                    int price = Integer.parseInt(event.getPrice());
                    //                   futureTV.setText("Future Income: " + (price * ticketsLeft));
                }

                break;
            }
        }


        adapter = new TicketAdapter(this, getTickets(eventObjectId));
        imageView.setVisibility(View.VISIBLE);
        lv_tickets.setAdapter(adapter);


    }

    private List<Ticket> getTickets(String eventObjectId) {
        final List<Ticket> list = new ArrayList<>();
        list.clear();
        ParseQuery<EventsSeats> query = ParseQuery.getQuery("EventsSeats");
        query.whereEqualTo("eventObjectId", eventObjectId);
        query.findInBackground(new FindCallback<EventsSeats>() {
            @Override
            public void done(List<EventsSeats> objects, ParseException e) {
                if (e == null) {
                    Ticket ticket = new Ticket();
                    for (int i = 0; i < objects.size(); i++) {
                        Log.e(TAG, objects.get(i).getSeatNumber());
                        ticket.setPrice(objects.get(i).getPrice());
                        ticket.setEventObjectId(objects.get(i).getEventObjectId());
                        ticket.setSeatNumber(objects.get(i).getSeatNumber());
                        list.add(ticket);
                    }

                    Log.e(TAG, "" + list.size());

                } else {
                    Log.e(TAG, "problem " + e.toString());
                }
            }
        });
        return list;
    }


}
