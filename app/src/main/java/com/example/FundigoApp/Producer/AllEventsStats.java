package com.example.FundigoApp.Producer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.Events.EventPageActivity;
import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.Producer.Artists.Artist;
import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethods;
import com.example.FundigoApp.StaticMethods.GetEventsDataCallback;
import com.example.FundigoApp.Tickets.EventsSeats;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AllEventsStats extends Fragment implements GetEventsDataCallback {
    public static List<Artist> artist_list = new ArrayList<Artist> ();

    TextView sumIncomeTV;
    TextView numOfPastEventsTV;
    TextView numOfTicketsSoldTV;
    TextView soldTicketsPriceAvgTv;
    TextView sumIncomeUpcomingTV;
    TextView numOfUpcomingEventsTV;
    TextView numOfTicketsUpcomingTV;
    TextView upcomingTicketsPriceAvgTv;

    int sumIncomeSold = 0;
    int numTicketsSold = 0;
    int numOfPastEvents = 0;

    int sumIncomeUpcoming = 0;
    int numTicketsUpcoming = 0;
    int numOfUpcomingEvents = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate (R.layout.events_stats, container, false);

        sumIncomeTV = (TextView) rootView.findViewById (R.id.incomeSoFar);
        numOfTicketsSoldTV = (TextView) rootView.findViewById (R.id.numberTicketsSold);
        numOfPastEventsTV = (TextView) rootView.findViewById (R.id.numberOfPastEvents);
        soldTicketsPriceAvgTv = (TextView) rootView.findViewById (R.id.soldTicketPriceAvg);
        sumIncomeUpcomingTV = (TextView) rootView.findViewById (R.id.incomeUpcoming);
        numOfUpcomingEventsTV = (TextView) rootView.findViewById (R.id.numberOfUpcomingEvents);
        numOfTicketsUpcomingTV = (TextView) rootView.findViewById (R.id.numberTicketsUpcoming);
        upcomingTicketsPriceAvgTv = (TextView) rootView.findViewById (R.id.upcomingTicketPriceAvg);

        if (GlobalVariables.ALL_EVENTS_DATA.size () == 0) {
            Intent intent = new Intent (this.getActivity (), EventPageActivity.class);
            StaticMethods.downloadEventsData (this, GlobalVariables.PRODUCER_PARSE_OBJECT_ID, this.getContext (), intent);
        } else {
            if (artist_list.size () == 0) {
                StaticMethods.uploadArtistData (artist_list);
            }
            calculateStats ();
        }
        return rootView;
    }

    void calculateStats() {
        for (Artist artist : artist_list) {
            List<EventInfo> eventsListFiltered = new ArrayList<EventInfo> ();
            StaticMethods.filterEventsByArtist (artist.getName (),
                                                       eventsListFiltered);
            updateEventsSeatsLists (eventsListFiltered);
            getCalculatedData (eventsListFiltered);
        }
        sumIncomeTV.setText (sumIncomeSold + "₪");
        numOfTicketsSoldTV.setText (numTicketsSold + "");
        numOfPastEventsTV.setText (numOfPastEvents + "");
        double sumIncomeSoldDouble = (double) sumIncomeSold / (double) numTicketsSold;
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        DecimalFormat df = (DecimalFormat)nf;
        df = new DecimalFormat ("#.##", df.getDecimalFormatSymbols ());
        String dx = df.format (sumIncomeSoldDouble);
        soldTicketsPriceAvgTv.setText (dx + "₪");

        sumIncomeUpcomingTV.setText (sumIncomeUpcoming + "₪");
        numOfTicketsUpcomingTV.setText (numTicketsUpcoming + "");
        numOfUpcomingEventsTV.setText (numOfUpcomingEvents + "");
        double sumIncomeUpcomingDouble = (double) sumIncomeUpcoming / (double) numTicketsUpcoming;
        String dx2 = df.format (sumIncomeUpcomingDouble);
        upcomingTicketsPriceAvgTv.setText (dx2 + "₪");
    }

    @Override
    public void eventDataCallback() {
        StaticMethods.uploadArtistData (artist_list);
        calculateStats ();
    }

    void updateEventsSeatsLists(List<EventInfo> eventsList) {
        for (EventInfo eventInfo : eventsList) {
            getListOfEventsTickets (eventInfo);
        }
    }

    public void getListOfEventsTickets(EventInfo eventInfo) {
        List<EventsSeats> list;
        try {
            ParseQuery<EventsSeats> query = ParseQuery.getQuery ("EventsSeats");
            query.whereEqualTo ("eventObjectId", eventInfo.getParseObjectId ());
            list = query.find ();
            eventInfo.setEventsSeatsList (list);
            Date currentDate = new Date ();
            Date eventDate = eventInfo.getDate ();
            eventInfo.setIsFutureEvent (eventDate.after (currentDate));
        } catch (ParseException e) {
            e.printStackTrace ();
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    void getCalculatedData(List<EventInfo> eventsList) {
        for (EventInfo eventInfo : eventsList) {
            int thisEventSoldTicketsNum = 0;
            if (eventInfo.isFutureEvent ()) {
                numOfUpcomingEvents++;
            } else {
                numOfPastEvents++;
            }
            List<EventsSeats> eventsSeatsList = eventInfo.getEventsSeatsList ();
            for (EventsSeats eventsSeat : eventsSeatsList) {
                if (!eventsSeat.getIsSold () && eventInfo.isStadium () && eventInfo.isFutureEvent ()) {
                    sumIncomeUpcoming += eventsSeat.getPrice ();
                    numTicketsUpcoming++;
                } else if (eventsSeat.getIsSold ()) {
                    thisEventSoldTicketsNum++;
                    sumIncomeSold += eventsSeat.getPrice ();
                    numTicketsSold++;
                }
            }
            if (eventInfo.isFutureEvent () && !eventInfo.isStadium () && !eventInfo.getPrice ().equals ("FREE")) {
                int thisEventNumTicketsUpcoming = eventInfo.getNumOfTickets () - thisEventSoldTicketsNum;
                numTicketsUpcoming += thisEventNumTicketsUpcoming;
                sumIncomeUpcoming += thisEventNumTicketsUpcoming * Integer.parseInt (eventInfo.getPrice ());
            }
        }
    }
}