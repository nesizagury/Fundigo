package com.example.FundigoApp.Tickets;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.FundigoApp.R;

import java.util.List;

/**
 * Created by assafbe on 24/02/2016.
 */
public class CustomAdapterTicketsListView extends ArrayAdapter<EventsSeatsInfo> {

    EventsSeatsInfo eventsInfo;


    public CustomAdapterTicketsListView(Context context, int resource, List objects) {
        super(context,resource, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        try {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.content_events_tickets, parent, false);
            eventsInfo = getItem(position);

            if (eventsInfo!=null) {
                TextView eventName = (TextView) convertView.findViewById(R.id.eventName);
                TextView ticketName = (TextView) convertView.findViewById(R.id.ticketName);
                TextView eventDate = (TextView) convertView.findViewById(R.id.eventDate);
                TextView price = (TextView) convertView.findViewById(R.id.price);
                Button listViewButton = (Button) convertView.findViewById(R.id.moreDetailesButton);


                String priceString = String.valueOf(eventsInfo.getPrice());
                eventName.setText(eventsInfo.getEventName());
                eventDate.setText(eventsInfo.getEventDate());
                price.setText(priceString);
                listViewButton.setTag(position);

                String seatName = eventsInfo.getTicketName(); // for a case that No Seat Same , just regular Ticket
                if (seatName == null|| seatName.isEmpty())
                    ticketName.setText(" ");
                else
                    ticketName.setText(eventsInfo.getTicketName());
            }

        } catch (Exception e) {
            Log.e(e.toString(), "getView Failure");
        }
        return convertView;
    }
}
