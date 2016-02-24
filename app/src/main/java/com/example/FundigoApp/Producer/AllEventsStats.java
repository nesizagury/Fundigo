package com.example.FundigoApp.Producer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.Events.EventPage;
import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.Producer.Artists.Artist;
import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethods;
import com.example.FundigoApp.StaticMethods.GetEventsDataCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AllEventsStats extends Fragment implements GetEventsDataCallback {
    public static List<Artist> artist_list = new ArrayList<Artist> ();

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
        View rootView = inflater.inflate (R.layout.events_stats, container, false);

        sumTickets = (TextView) rootView.findViewById (R.id.soldTV);
        soldAvg = (TextView) rootView.findViewById (R.id.soldAvgTV);
        soFarSum = (TextView) rootView.findViewById (R.id.soFarSumTV);
        ticketFeeAvg = (TextView) rootView.findViewById (R.id.ticketFeeAvgTV);
        ticketsForSale = (TextView) rootView.findViewById (R.id.ticketsForSaleTV);
        forSaleValue = (TextView) rootView.findViewById (R.id.forSaleValueTV);
        eventSum = (TextView) rootView.findViewById (R.id.eventSumTV);
        sumArtist = (TextView) rootView.findViewById (R.id.sumArtistTV);

        if (GlobalVariables.ALL_EVENTS_DATA.size () == 0) {
            Intent intent = new Intent (this.getActivity (), EventPage.class);
            StaticMethods.uploadEventsData (this, GlobalVariables.PRODUCER_PARSE_OBJECT_ID, this.getContext (), intent);
        } else {
            if (artist_list.size () == 0) {
                StaticMethods.uploadArtistData (artist_list);
            }
            calculateStats ();
        }

        return rootView;
    }

    void calculateStats() {
        int ticketsSold = 0;
        int tickets = 0;
        int soFarIntSum = 0;
        int ticketsForsale = 0;
        int forSaleIntValue = 0;

        Date eventDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat ("dd.MM.yy");
        Date todayDate = Calendar.getInstance ().getTime ();
        for (int i = 0; i < GlobalVariables.ALL_EVENTS_DATA.size (); i++) {
            EventInfo event = GlobalVariables.ALL_EVENTS_DATA.get (i);
            if (!event.getSold ().equals (""))
                ticketsSold += Integer.parseInt (event.getSold ());

            tickets += Integer.parseInt (event.getTicketsLeft ());

            if (!event.getIncome ().equals (""))
                soFarIntSum += Integer.parseInt (event.getIncome ());

            eventDate = null;
            try {
                eventDate = dateFormat.parse (event.getDate ());
            } catch (ParseException e) {
                e.printStackTrace ();
            }

            if (eventDate.after (todayDate) && !event.getPrice ().contains ("-")&& !event.getPrice().equals("FREE")) {
                StringBuilder sb = new StringBuilder (event.getPrice ());
              //  sb.deleteCharAt (sb.length () - 1);
                int ticketsLeft = Integer.parseInt (event.getTicketsLeft ());
                int price = Integer.parseInt (sb.toString ());
                ticketsForsale += Integer.parseInt (event.getTicketsLeft ());
                forSaleIntValue += (price * ticketsLeft);
            }
        }

        sumTickets.setText ("Tickets Sold: " + ticketsSold);
        if (tickets != 0 && ticketsSold != 0)
            soldAvg.setText ("Sales Avg: " + (ticketsSold / tickets) * 100);
        soFarSum.setText ("So Far Income: " + soFarIntSum);
        ticketsForSale.setText ("Tickets For Sale: " + ticketsForsale);
        forSaleValue.setText ("All Tickets Value: " + forSaleIntValue);
        eventSum.setText ("Num Of Events: " + GlobalVariables.ALL_EVENTS_DATA.size ());
        sumArtist.setText ("Num Of Artists: " + (artist_list.size () - 1));
        ticketFeeAvg.setText ("Tickets fee average is: 15%");
    }

    @Override
    public void eventDataCallback() {
        StaticMethods.uploadArtistData (artist_list);
        calculateStats ();
    }
}