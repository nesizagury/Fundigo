package com.example.FundigoApp.Producer.Artists;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.FundigoApp.Events.EditEventActivity;
import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.Events.EventPageActivity;
import com.example.FundigoApp.Events.EventsListAdapter;
import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethod.EventDataMethods;
import com.example.FundigoApp.StaticMethod.GeneralStaticMethods;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ArtistEventsActivity extends Activity implements AdapterView.OnItemClickListener {
    private static List<EventInfo> eventsList = new ArrayList<EventInfo> ();
    ListView eventsListView;
    private static EventsListAdapter eventsListAdapter;
    TextView artistTV;
    String eventObjectId;

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
        GeneralStaticMethods.filterEventsByArtist (artistName,
                                                          eventsList);
        eventsListAdapter.notifyDataSetChanged ();
        registerForContextMenu (eventsListView);
    }

    @Override
    public void onItemClick(AdapterView<?> av, View view, int i, long l) {
        Bundle b = new Bundle ();
        Intent intent = new Intent (this, EventPageActivity.class);
        EventDataMethods.onEventItemClick (i, eventsList, intent);
        intent.putExtras (b);
        startActivity (intent);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        GeneralStaticMethods.onActivityResult (requestCode,
                                                      data,
                                                      this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu (menu, v, menuInfo);
        getMenuInflater ().inflate (R.menu.context_menu, menu);
    }

    @Override
    protected void onResume() {
        super.onResume ();
        eventsListAdapter.notifyDataSetChanged ();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo ();
        final int pos = info.position;

        switch (item.getItemId ()) {
            case R.id.delete_event:
                eventObjectId = eventsList.get (pos).getParseObjectId ();
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener () {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                deleteEvent (eventObjectId);
                                GlobalVariables.refreshArtistsList = true;
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss ();
                                break;
                            case DialogInterface.BUTTON_NEUTRAL:
                                dialog.dismiss ();
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder (this);
                builder.setTitle ("Are you sure?");
                builder.setIcon (R.drawable.warning);
                builder.setMessage ("It can not be undone!");
                builder.setPositiveButton ("Yes!", listener);
                builder.setNegativeButton ("No!", listener);
                builder.setNeutralButton ("Cancel...", listener);
                AlertDialog dialog = builder.create ();
                dialog.show ();

                return true;
            case R.id.edit_event:
                eventObjectId = eventsList.get (pos).getParseObjectId ();
                Intent intent = new Intent (this, EditEventActivity.class);
                intent.putExtra (GlobalVariables.OBJECTID, eventObjectId);
                startActivity (intent);
                return true;
            default:
                return super.onContextItemSelected (item);
        }
    }

    public void deleteEvent(final String objectId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery ("Event");
        query.whereEqualTo ("objectId", objectId);
        query.orderByDescending ("createdAt");
        try {
            ParseObject parseObject = query.getFirst ();
            parseObject.delete ();
            parseObject.save ();
            for (int i = 0; i < eventsList.size (); i++) {
                EventInfo eventInfo = eventsList.get (i);
                if (eventInfo.getParseObjectId ().equals (objectId)) {
                    eventsList.remove (i);
                    eventsListAdapter.notifyDataSetChanged ();
                    break;
                }
            }
        } catch (ParseException e1) {
            e1.printStackTrace ();
        }

        ParseQuery<ParseObject> querySeats = ParseQuery.getQuery ("EventsSeats");
        querySeats.whereEqualTo ("eventObjectId", objectId);
        querySeats.findInBackground (new FindCallback<ParseObject> () {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects.size () != 0) {
                    ParseObject.deleteAllInBackground (objects);
                }
            }
        });
        ParseQuery<ParseObject> queryMessages = new ParseQuery("Message");
        queryMessages.whereEqualTo("eventObjectId",objectId);
        queryMessages.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (objects.size() != 0) {
                    ParseObject.deleteAllInBackground(objects);
                }
            }
        });
        ParseQuery<ParseObject> queryRoom = new ParseQuery("Room");
        queryRoom.whereEqualTo("eventObjId", objectId);
        queryRoom.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects.size() != 0) {
                    ParseObject.deleteAllInBackground(objects);
                }
            }
        });
        ParseQuery<ParseObject> queryMsgRealTime = new ParseQuery("MsgRealTime");
        queryMsgRealTime.whereEqualTo("eventObjectId", objectId);
        queryMsgRealTime.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects.size() != 0) {
                    ParseObject.deleteAllInBackground(objects);
                }
            }
        });
        ParseQuery<ParseObject> queryPush = new ParseQuery("Push");
        queryPush.whereEqualTo("EventId", objectId);
        queryPush.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects.size() != 0) {
                    ParseObject.deleteAllInBackground(objects);
                }
            }
        });
        finish ();
    }
}
