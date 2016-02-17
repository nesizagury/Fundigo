package com.example.FundigoApp.Events;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.FundigoApp.Producer.Artists.ArtistsPage;
import com.example.FundigoApp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EventsStats extends Fragment {

    TextView sumTickets;
    TextView soldAvg;
    TextView soFarSum;
    TextView ticketFeeAvg;
    TextView ticketsForSale;
    TextView forSaleValue;
    TextView eventSum;
    TextView sumArtist;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.events_stats,container, false);

        sumTickets = (TextView)rootView.findViewById(R.id.soldTV);
        soldAvg = (TextView)rootView.findViewById(R.id.soldAvgTV);
        soFarSum = (TextView)rootView.findViewById(R.id.soFarSumTV);
        ticketFeeAvg = (TextView)rootView.findViewById(R.id.ticketFeeAvgTV);
        ticketsForSale = (TextView)rootView.findViewById(R.id.ticketsForSaleTV);
        forSaleValue = (TextView)rootView.findViewById(R.id.forSaleValueTV);
        eventSum = (TextView)rootView.findViewById(R.id.eventSumTV);
        sumArtist = (TextView)rootView.findViewById(R.id.sumArtistTV);
        
        int ticketsSold = 0;
        int tickets = 0;
        int soFarIntSum = 0;
        int ticketsForsale = 0;
        int forSaleIntValue = 0;

        Date eventDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        Date todayDate = Calendar.getInstance().getTime();

        for (int i = 0; i < ArtistsPage.all_events.size(); i++) {

            EventInfo event = ArtistsPage.all_events.get(i);
            if(!event.getSold().equals(""))
                ticketsSold += Integer.parseInt(event.getSold());

            tickets += Integer.parseInt(event.getTicketsLeft());

            if(!event.getIncome().equals(""))
            soFarIntSum += Integer.parseInt(event.getIncome());

            eventDate = null;
            try {
                eventDate = dateFormat.parse(event.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(eventDate.after(todayDate) && !event.getPrice().contains("-"))
            {
                StringBuilder sb = new StringBuilder(event.getPrice());
                sb.deleteCharAt(sb.length()-1);
                int ticketsLeft = Integer.parseInt(event.getTicketsLeft());
                int price = Integer.parseInt(sb.toString());
                ticketsForsale += Integer.parseInt(event.getTicketsLeft());
                forSaleIntValue += (price * ticketsLeft);
            }


        }

        sumTickets.setText("Tickets Sold: " + ticketsSold);
        if(tickets != 0 && ticketsSold != 0)
        soldAvg.setText("Sales Avg: " + (ticketsSold / tickets) * 100);
        soFarSum.setText("So Far Income: " + soFarIntSum);
        ticketsForSale.setText("Tickets For Sale: " + ticketsForsale);
        forSaleValue.setText("All Tickets Value: " + forSaleIntValue);
        eventSum.setText("Num Of Events: " + ArtistsPage.all_events.size());
        sumArtist.setText("Num Of Artists: " + (ArtistsPage.artist_list.size() - 1));


        return rootView;

    }
}