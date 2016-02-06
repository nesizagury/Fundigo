package com.example.events;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by מנהל on 01/02/2016.
 */
public class ArtistStats extends Activity {

    TextView artistTV;
    TextView sumIncomeTV;
    TextView sumTicketsTV;
    TextView pastEventsTV;
    TextView upcomingEventsTV;
    TextView allTicketsTV;
    TextView allTicketValueTV;
    TextView ticketAvgTV;
    TextView upcomingTicketAvgTV;
    List <EventInfo> list = new ArrayList<EventInfo>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_stats);

        artistTV = (TextView) findViewById(R.id.eventTV);
        sumIncomeTV = (TextView) findViewById(R.id.sumIncomeTv);
        sumTicketsTV = (TextView) findViewById(R.id.soldTV);
        pastEventsTV = (TextView) findViewById(R.id.pastEventsTV);
        upcomingEventsTV = (TextView) findViewById(R.id.upcomingEventsTV);
        allTicketsTV = (TextView) findViewById(R.id.alTicketsTV);
        allTicketValueTV = (TextView) findViewById(R.id.allTicketsValueTV);
        ticketAvgTV = (TextView) findViewById(R.id.ticketAvgTV);
        upcomingTicketAvgTV = (TextView) findViewById(R.id.upcomingTicketAvgTV);

        int sumIncome = 0;
        int sumTickets = 0;
        int pastEvents = 0;
        int upcomingEvents = 0;
        int allTickets = 0;
        int ticketsValue = 0;
        int ticketAvg = 0;
        int upcomingTicketAvg = 0;
        Date eventDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        Date todayDate = Calendar.getInstance().getTime();
        artistTV.setText(getIntent().getStringExtra("name"));



        for (int i = 0; i < ArtistsPage.filtered_events_data.size(); i++) {

            EventInfo event = ArtistsPage.filtered_events_data.get(i);
            eventDate = null;
            try {
                eventDate = dateFormat.parse(event.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            StringBuilder sb = new StringBuilder(event.getPrice());
            sb.deleteCharAt(sb.length()-1);
            int ticketsLeft = Integer.parseInt(event.getTicketsLeft());
            int price = Integer.parseInt(sb.toString());

            if(eventDate.before(todayDate)) {
                pastEvents++;
                ticketAvg += price;
            }
            else
            {
                upcomingEvents++;
                allTickets += Integer.parseInt(event.getTicketsLeft());
               ticketsValue += price * ticketsLeft;
                upcomingTicketAvg += price;
            }

            sumIncome += Integer.parseInt(event.getIncome());
            sumTickets += Integer.parseInt(event.getSold());


        }


        sumIncomeTV.setText(sumIncome + "");
        sumTicketsTV.setText(sumTickets + "");
        pastEventsTV.setText(pastEvents + "");
        upcomingEventsTV.setText(upcomingEvents + "");
        allTicketsTV.setText(allTickets + "");
        allTicketValueTV.setText(ticketsValue + "");
        if(pastEvents > 0)
        ticketAvgTV.setText((ticketAvg / pastEvents ) + "");
        if(upcomingEvents > 0)
        upcomingTicketAvgTV.setText((upcomingTicketAvg / upcomingEvents) + "");


    }



    public void goToEvents(View v){

        finish();



    }

}
