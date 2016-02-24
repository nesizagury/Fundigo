package com.example.FundigoApp.Producer.Artists;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.Events.EventPage;
import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethods;
import com.example.FundigoApp.StaticMethods.GetEventsDataCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ArtistStatsActivity extends Activity implements GetEventsDataCallback {
    TextView artistTV;
    TextView sumIncomeTV;
    TextView sumTicketsTV;
    TextView pastEventsTV;
    TextView upcomingEventsTV;
    TextView allTicketsTV;
    TextView allTicketValueTV;
    TextView ticketAvgTV;
    TextView upcomingTicketAvgTV;
    List<EventInfo> eventsList = new ArrayList<EventInfo> ();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.artist_stats);

        artistTV = (TextView) findViewById (R.id.eventTV);
        sumIncomeTV = (TextView) findViewById (R.id.sumIncomeTv);
        sumTicketsTV = (TextView) findViewById (R.id.soldTV);
        pastEventsTV = (TextView) findViewById (R.id.pastEventsTV);
        upcomingEventsTV = (TextView) findViewById (R.id.upcomingEventsTV);
        allTicketsTV = (TextView) findViewById (R.id.alTicketsTV);
        allTicketValueTV = (TextView) findViewById (R.id.allTicketsValueTV);
        ticketAvgTV = (TextView) findViewById (R.id.ticketAvgTV);
        upcomingTicketAvgTV = (TextView) findViewById (R.id.upcomingTicketAvgTV);

        if (GlobalVariables.ALL_EVENTS_DATA.size () == 0) {
            Intent intent = new Intent (this, EventPage.class);
            StaticMethods.uploadEventsData (this, GlobalVariables.PRODUCER_PARSE_OBJECT_ID, this, intent);
        } else {
            calculateStates ();
        }
    }

    private void calculateStates() {
        int sumIncome = 0;
        int sumTickets = 0;
        int pastEvents = 0;
        int upcomingEvents = 0;
        int allTickets = 0;
        int ticketsValue = 0;
        int ticketAvg = 0;
        int upcomingTicketAvg = 0;
        Date eventDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat ("dd.MM.yy");
        Date todayDate = Calendar.getInstance ().getTime ();
        String artistName = getIntent ().getStringExtra ("artist_name");
        artistTV.setText (artistName);
        StaticMethods.filterEventsByArtist (artistName,
                                                   eventsList);
        for (int i = 0; i < eventsList.size (); i++) {
            EventInfo event = eventsList.get (i);
            eventDate = null;
            try {
                eventDate = dateFormat.parse (event.getDate ());
            } catch (ParseException e) {
                e.printStackTrace ();
            }
            StringBuilder sb = new StringBuilder (event.getPrice ());
            sb.deleteCharAt (sb.length () - 1);
            int ticketsLeft = Integer.parseInt (event.getTicketsLeft ());
            int price = Integer.parseInt (sb.toString ());

            if (eventDate.before (todayDate)) {
                pastEvents++;
                ticketAvg += price;
            } else {
                upcomingEvents++;
                allTickets += Integer.parseInt (event.getTicketsLeft ());
                ticketsValue += price * ticketsLeft;
                upcomingTicketAvg += price;
            }
            sumIncome += Integer.parseInt (event.getIncome ());
            sumTickets += Integer.parseInt (event.getSold ());
        }
        sumIncomeTV.setText (sumIncome + "");
        sumTicketsTV.setText (sumTickets + "");
        pastEventsTV.setText (pastEvents + "");
        upcomingEventsTV.setText (upcomingEvents + "");
        allTicketsTV.setText (allTickets + "");
        allTicketValueTV.setText (ticketsValue + "");
        if (pastEvents > 0) {
            ticketAvgTV.setText ((ticketAvg / pastEvents) + "");
        }
        if (upcomingEvents > 0) {
            upcomingTicketAvgTV.setText ((upcomingTicketAvg / upcomingEvents) + "");
        }
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
}
