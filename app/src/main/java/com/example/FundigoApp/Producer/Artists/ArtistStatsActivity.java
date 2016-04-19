package com.example.FundigoApp.Producer.Artists;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.Events.EventPageActivity;
import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethod.EventDataMethods;
import com.example.FundigoApp.StaticMethod.GeneralStaticMethods;
import com.example.FundigoApp.StaticMethod.EventDataMethods.GetEventsDataCallback;
import com.example.FundigoApp.Tickets.EventsSeats;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArtistStatsActivity extends Activity implements GetEventsDataCallback {
    TextView artistTV;
    TextView sumIncomeTV;
    TextView numOfPastEventsTV;
    TextView numOfTicketsSoldTV;
    TextView soldTicketsPriceAvgTv;
    TextView sumIncomeUpcomingTV;
    TextView numOfUpcomingEventsTV;
    TextView numOfTicketsUpcomingTV;
    TextView upcomingTicketsPriceAvgTv;
    List<EventInfo> eventsList = new ArrayList<EventInfo> ();

    int sumIncomeSold = 0;
    int numTicketsSold = 0;
    int numOfPastEvents = 0;

    int sumIncomeUpcoming = 0;
    int numTicketsUpcoming = 0;
    int numOfUpcomingEvents = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.artist_stats);

        artistTV = (TextView) findViewById (R.id.artistTitle);
        sumIncomeTV = (TextView) findViewById (R.id.incomeSoFar);
        numOfTicketsSoldTV = (TextView) findViewById (R.id.numberTicketsSold);
        numOfPastEventsTV = (TextView) findViewById (R.id.numberOfPastEvents);
        soldTicketsPriceAvgTv = (TextView) findViewById (R.id.soldTicketPriceAvg);
        sumIncomeUpcomingTV = (TextView) findViewById (R.id.incomeUpcoming);
        numOfUpcomingEventsTV = (TextView) findViewById (R.id.numberOfUpcomingEvents);
        numOfTicketsUpcomingTV = (TextView) findViewById (R.id.numberTicketsUpcoming);
        upcomingTicketsPriceAvgTv = (TextView) findViewById (R.id.upcomingTicketPriceAvg);

        if (GlobalVariables.ALL_EVENTS_DATA.size () == 0) {
            Intent intent = new Intent (this, EventPageActivity.class);
            EventDataMethods.downloadEventsData (this, GlobalVariables.PRODUCER_PARSE_OBJECT_ID, this, intent);
        } else {
            calculateStates ();
        }
    }

    private void calculateStates() {
        String artistName = getIntent ().getStringExtra ("artist_name");
        artistTV.setText (artistName);
        GeneralStaticMethods.filterEventsByArtist (artistName,
                                                          eventsList);
        updateEventsSeatsLists (eventsList);
        getCalculatedData (eventsList);
        sumIncomeTV.setText (sumIncomeSold + "₪");
        numOfTicketsSoldTV.setText (numTicketsSold + "");
        numOfPastEventsTV.setText (numOfPastEvents + "");
        double sumIncomeSoldDouble = (double) sumIncomeSold / (double) numTicketsSold;
        DecimalFormat df = new DecimalFormat ("#.##");
        String dx = df.format (sumIncomeSoldDouble);
        soldTicketsPriceAvgTv.setText (dx + "₪");

        sumIncomeUpcomingTV.setText (sumIncomeUpcoming + "₪");
        numOfTicketsUpcomingTV.setText (numTicketsUpcoming + "");
        numOfUpcomingEventsTV.setText (numOfUpcomingEvents + "");
        double sumIncomeUpcomingDouble = (double) sumIncomeUpcoming / (double) numTicketsUpcoming;
        DecimalFormat df2 = new DecimalFormat ("#.##");
        String dx2 = df2.format (sumIncomeUpcomingDouble);
        upcomingTicketsPriceAvgTv.setText (dx2 + "₪");

    }

    @Override
    public void eventDataCallback() {
        calculateStates ();
    }

    public void goToEvents(View v) {
        Intent intent = new Intent (this, ArtistEventsActivity.class);
        intent.putExtra ("artist_name", getIntent ().getStringExtra ("artist_name"));
        startActivity (intent);
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
                    numTicketsSold++;
                    sumIncomeSold += eventsSeat.getPrice ();
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
