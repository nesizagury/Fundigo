package com.example.FundigoApp.Tickets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.FundigoApp.R;

import java.util.List;

public class CustomerTicketsListAdapter extends ArrayAdapter<EventsSeatsInfo> {

    public CustomerTicketsListAdapter(Context context, int resource, List objects) {
        super (context, resource, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        try {
            convertView = LayoutInflater.from (getContext ()).inflate (R.layout.activity_customer_tickets_list_item, parent, false);
            EventsSeatsInfo eventsSeatsInfo = (EventsSeatsInfo) getItem (position);

            if (eventsSeatsInfo != null) {
                TextView eventName = (TextView) convertView.findViewById (R.id.eventName);
                TextView ticketNameBody = (TextView) convertView.findViewById (R.id.ticketName);
                TextView ticketNameTitle = (TextView) convertView.findViewById (R.id.seatNameTitle);
                TextView eventDate = (TextView) convertView.findViewById (R.id.eventDate);
                TextView price = (TextView) convertView.findViewById (R.id.price);
                Button listViewButton = (Button) convertView.findViewById (R.id.moreDetailesButton);
                Button eventEndedButton = (Button) convertView.findViewById (R.id.eventEnded);
                TextView purchaseDate = (TextView) convertView.findViewById (R.id.purchaseDate);


                String priceString = String.valueOf (eventsSeatsInfo.getPrice ());
                eventName.setText (eventsSeatsInfo.getEventInfo ().getName ());
                eventDate.setText (eventsSeatsInfo.getEventInfo ().getDateAsString ());
                purchaseDate.setText(eventsSeatsInfo.getPurchaseDate().toString().substring(0,20));
                price.setText (priceString);
                listViewButton.setTag (position);
                String seatName = eventsSeatsInfo.getTicketName ();

                if (seatName == null || seatName.isEmpty ()) {
                    ticketNameBody.setVisibility (View.INVISIBLE);
                    ticketNameTitle.setVisibility (View.INVISIBLE);
                } else {
                    ticketNameBody.setText (eventsSeatsInfo.getTicketName ());
                }
                if(!eventsSeatsInfo.getEventInfo ().isFutureEvent ()){
                    eventEndedButton.setVisibility (View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return convertView;
    }
}
