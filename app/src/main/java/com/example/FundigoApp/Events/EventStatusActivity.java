package com.example.FundigoApp.Events;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethod.EventDataMethods;
import com.example.FundigoApp.Tickets.EventsSeats;
import com.example.FundigoApp.Tickets.TicketAdapter;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventStatusActivity extends Activity implements AdapterView.OnItemClickListener {

    TextView eventNameTV;

    EventInfo eventInfo;

    TextView sumIncomeTV;
    TextView numOfTicketsSoldTV;
    TextView soldTicketsPriceAvgTv;
    TextView sumIncomeUpcomingTV;
    TextView numOfTicketsUpcomingTV;
    TextView upcomingTicketsPriceAvgTv;

    TextView numberGuestsEnterTV;
    int numberFoGust;
    TextView enter_gusst_value;


    ListView lv_tickets;
    TextView tv_price;
    TextView tv_ticket;
    ImageView imageView;
    TicketAdapter adapter;

    int sumIncomeSold = 0;
    int numTicketsSold = 0;

    int sumIncomeUpcoming = 0;
    int numTicketsUpcoming = 0;

    List<EventsSeats> list = new ArrayList<> ();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_event_status);
        numberGuestsEnterTV = (TextView) findViewById (R.id.numberGuestsEnterTV);
        enter_gusst_value = (TextView) findViewById (R.id.enter_gusst_value);
        eventNameTV = (TextView) findViewById (R.id.eventNameTV);
        eventNameTV.setText ("" + getIntent ().getStringExtra ("name"));

        String eventObjId = getIntent ().getStringExtra ("eventObjectId");
        eventInfo = EventDataMethods.getEventFromObjID (eventObjId,
                                                               GlobalVariables.ALL_EVENTS_DATA);
        adapter = new TicketAdapter (this, list);

        lv_tickets = (ListView) findViewById (R.id.lv_tickets);
        lv_tickets.setAdapter (adapter);
        lv_tickets.setOnItemClickListener (this);
        getListOfEventsTickets (eventInfo);
        getCalculatedData (eventInfo);

        sumIncomeTV = (TextView) findViewById (R.id.incomeSoFar);
        numOfTicketsSoldTV = (TextView) findViewById (R.id.numberTicketsSold);
        soldTicketsPriceAvgTv = (TextView) findViewById (R.id.soldTicketPriceAvg);
        sumIncomeUpcomingTV = (TextView) findViewById (R.id.incomeUpcoming);
        numOfTicketsUpcomingTV = (TextView) findViewById (R.id.numberTicketsUpcoming);
        upcomingTicketsPriceAvgTv = (TextView) findViewById (R.id.upcomingTicketPriceAvg);

        tv_price = (TextView) findViewById (R.id.ticketItem_tv_price);
        tv_ticket = (TextView) findViewById (R.id.ticketItem_tv_ticket);
        imageView = (ImageView) findViewById (R.id.iv_arena);
        if (!eventInfo.isStadium) {
            imageView.setVisibility (View.GONE);
        }

        sumIncomeTV.setText (sumIncomeSold + "₪");
        numOfTicketsSoldTV.setText (numTicketsSold + "");
        double sumIncomeSoldDouble = (double) sumIncomeSold / (double) numTicketsSold;
        DecimalFormat df = new DecimalFormat ("#.##");
        String dx = df.format (sumIncomeSoldDouble);
        soldTicketsPriceAvgTv.setText (dx + "₪");

        sumIncomeUpcomingTV.setText (sumIncomeUpcoming + "₪");
        numOfTicketsUpcomingTV.setText (numTicketsUpcoming + "");
        double sumIncomeUpcomingDouble = (double) sumIncomeUpcoming / (double) numTicketsUpcoming;
        DecimalFormat df2 = new DecimalFormat ("#.##");
        String dx2 = df2.format (sumIncomeUpcomingDouble);
        upcomingTicketsPriceAvgTv.setText (dx2 + "₪");
    }

    public void getListOfEventsTickets(EventInfo eventInfo) {
        List<EventsSeats> list;
        try {
            ParseQuery<EventsSeats> query = ParseQuery.getQuery ("EventsSeats");
            query.whereEqualTo ("eventObjectId", eventInfo.getParseObjectId ());
            if(!eventInfo.isStadium){
                query.whereEqualTo ("sold", true);
            }
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

    void getCalculatedData(EventInfo eventInfo) {
        List<EventsSeats> soldList = new ArrayList<> ();
        List<EventsSeats> availableList = new ArrayList<> ();
        list.clear ();
        int thisEventSoldTicketsNum = 0;
        List<EventsSeats> eventsSeatsList = eventInfo.getEventsSeatsList ();
        for (EventsSeats eventsSeat : eventsSeatsList) {
            if (eventsSeat.getBoolean ("CustomerEnter")) {
                numberFoGust++;
            }
            if (!eventsSeat.getIsSold () && eventInfo.isStadium () && eventInfo.isFutureEvent ()) {
                sumIncomeUpcoming += eventsSeat.getPrice ();
                numTicketsUpcoming++;
            } else if (eventsSeat.getIsSold ()) {
                thisEventSoldTicketsNum++;
                numTicketsSold++;
                sumIncomeSold += eventsSeat.getPrice ();
            }
            if (eventsSeat.getIsSold ()) {
                soldList.add (eventsSeat);
            } else {
                availableList.add (eventsSeat);
            }
        }
        if (eventInfo.isFutureEvent () && !eventInfo.isStadium () && !eventInfo.getPrice ().equals ("FREE")) {
            int thisEventNumTicketsUpcoming = eventInfo.getNumOfTickets () - thisEventSoldTicketsNum;
            numTicketsUpcoming += thisEventNumTicketsUpcoming;
            sumIncomeUpcoming += thisEventNumTicketsUpcoming * Integer.parseInt (eventInfo.getPrice ());
        }
        enter_gusst_value.setText (numberFoGust + "/" + (numTicketsSold));
        list.addAll (soldList);
        list.addAll (availableList);
        adapter.notifyDataSetChanged ();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent (this, EventDetailsActivity.class);
        intent.putExtra (GlobalVariables.OBJECTID, list.get (position).getObjectId ());
        startActivity (intent);
    }
}
