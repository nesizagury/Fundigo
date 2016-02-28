package com.example.FundigoApp.Tickets;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.FundigoApp.R;

import java.util.List;

/**
 * Created by Sprintzin on 28/02/2016.
 */
public class TicketAdapter extends ArrayAdapter<Ticket> {
    private static final String TAG = "TicketAdapter";
    Context context;
    List<Ticket> tickets;


    public TicketAdapter(Context context, List<Ticket> tickets) {
        super(context, 0, tickets);
        this.context = context;
        this.tickets = tickets;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Ticket ticket = getItem(position);
        Log.e(TAG, "lalala" + ticket.getSeatNumber());
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.ticket_item, null);

            final ViewHolder holder = new ViewHolder();
            holder.tv_price = (TextView) convertView.findViewById(R.id.ticketItem_tv_price);
            holder.tv_ticket = (TextView) convertView.findViewById(R.id.ticketItem_tv_ticket);

            convertView.setTag(holder);
        }

        final ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.tv_price.setText(ticket.getPrice() + "$");
        holder.tv_ticket.setText(ticket.getSeatNumber());
        return convertView;
    }

    final class ViewHolder {
        public TextView tv_price;
        public TextView tv_ticket;
    }
}
