package com.example.FundigoApp.Producer.Artists;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.Events.EventPage;
import com.example.FundigoApp.Events.EventsListAdapter;
import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethods;

import java.util.ArrayList;
import java.util.List;

public class ArtistEventsActivity extends Activity implements AdapterView.OnItemClickListener {
    private static List<EventInfo> eventsList = new ArrayList<EventInfo> ();
    ListView eventsListView;
    private static EventsListAdapter eventsListAdapter;
    TextView artistTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.artist_events);
        artistTV = (TextView) findViewById (R.id.artistNameEventsPage);
        String artistName = getIntent ().getStringExtra ("artist_name");
        artistTV.setText (artistName);

        eventsListView = (ListView) findViewById (R.id.artistEventList);
        eventsListAdapter = new EventsListAdapter (this,
                                                          eventsList,
                                                          false);
        eventsListView.setAdapter (eventsListAdapter);
        eventsListView.setSelector (new ColorDrawable (Color.TRANSPARENT));
        eventsListView.setOnItemClickListener (this);
        StaticMethods.filterEventsByArtist (artistName,
                                                   eventsList);
        eventsListAdapter.notifyDataSetChanged ();
    }

    @Override
    public void onItemClick(AdapterView<?> av, View view, int i, long l) {
        Bundle b = new Bundle ();
        Intent intent = new Intent (this, EventPage.class);
        StaticMethods.onEventItemClick (i, eventsList, intent);
        intent.putExtras (b);
        startActivity (intent);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        StaticMethods.onActivityResult (requestCode,
                                               data,
                                               this);
    }
}
