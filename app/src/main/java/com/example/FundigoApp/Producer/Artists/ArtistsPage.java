package com.example.FundigoApp.Producer.Artists;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.FundigoApp.Constants;
import com.example.FundigoApp.Events.Event;
import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.Events.EventPage;
import com.example.FundigoApp.Events.EventsListAdapter;
import com.example.FundigoApp.MainActivity;
import com.example.FundigoApp.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ArtistsPage extends Fragment implements AdapterView.OnItemClickListener {

    EventsListAdapter eventsListAdapter;
    public static List<Artist> artist_list = new ArrayList<Artist> ();
    public static List<EventInfo> filtered_events_data = new ArrayList<EventInfo> ();
    public static List<EventInfo> all_events = new ArrayList<EventInfo> ();
    private static final String TAG = "ArtistsPage";


    ListView lv;
    ListView lv2;
    ArtistAdapter artistAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate (R.layout.activity_main_producer, container, false);
        lv = (ListView) rootView.findViewById (R.id.artist_list_view);
        lv2 = (ListView) rootView.findViewById (R.id.listView2);
        uploadArtistData (MainActivity.producerId);
        artistAdapter = new ArtistAdapter (getActivity ().getApplicationContext (), artist_list);
        lv2.setVisibility (View.INVISIBLE);
        lv.setAdapter (artistAdapter);
        lv.setSelector (new ColorDrawable (Color.TRANSPARENT));
        lv2.setOnItemClickListener (this);
        eventsListAdapter = new EventsListAdapter (getActivity ().getApplicationContext (), filtered_events_data, false);

        all_events = uploadUserData ("");

        lv.setOnItemClickListener (new AdapterView.OnItemClickListener () {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position != artist_list.size () - 1) {
                    uploadUserData (artist_list.get (position).getName ());
                    Intent intent = new Intent (getActivity (), ArtistStats.class);
                    intent.putExtra ("name", artist_list.get (position).getName ());
                    startActivity (intent);
                } else {
                    uploadUserData ("");
                    for (int i = 0; i < filtered_events_data.size (); i++) {
                        if (!filtered_events_data.get (i).getArtist ().equals ("")) {
                            filtered_events_data.remove (i);
                            i--;
                        }
                    }
                }

                lv.setVisibility (View.INVISIBLE);
                lv2.setVisibility (View.VISIBLE);
                lv2.setAdapter (eventsListAdapter);
                lv2.setSelector (new ColorDrawable (Color.TRANSPARENT));
            }
        });
        return rootView;
    }

    private List uploadUserData(String artist) {
        MainActivity.all_events_data.clear ();
        filtered_events_data.clear ();

        ParseQuery<Event> query = new ParseQuery ("Event");
        if (artist != null && artist != "") {
            query.whereEqualTo ("artist", artist);
        } else {
            query.whereEqualTo ("producerId", MainActivity.producerId);
        }
        query.orderByDescending ("createdAt");
        List<Event> events = null;
        try {
            events = query.find ();
            ParseFile imageFile;
            byte[] data;
            Bitmap bmp;

            for (int i = 0; i < events.size (); i++) {
                imageFile = (ParseFile) events.get (i).get ("ImageFile");
                if (imageFile != null) {
                    data = imageFile.getData ();
                    bmp = BitmapFactory.decodeByteArray (data, 0, data.length);
                } else {
                    bmp = null;
                }
                MainActivity.all_events_data.add (new EventInfo (
                                                                        bmp,
                                                                        events.get (i).getDate (),
                                                                        events.get (i).getName (),
                                                                        events.get (i).getTags (),
                                                                        events.get (i).getPrice (),
                                                                        events.get (i).getDescription (),
                                                                        events.get (i).getAddress (),
                                                                        events.get (i).getEventToiletService (),
                                                                        events.get (i).getEventParkingService (),
                                                                        events.get (i).getEventCapacityService (),
                                                                        events.get (i).getEventATMService (),
                                                                        events.get (i).getCity (),
                                                                        i,
                                                                        events.get (i).getFilterName ()));
                MainActivity.all_events_data.get (i).setProducerId (events.get (i).getProducerId ());
                MainActivity.all_events_data.get (i).setArtist (events.get (i).getArtist ());
                MainActivity.all_events_data.get (i).setIncome (events.get (i).getIncome ());
                MainActivity.all_events_data.get (i).setSold (events.get (i).getSold ());
                MainActivity.all_events_data.get (i).setTicketsLeft (events.get (i).getNumOfTicketsLeft ());
                MainActivity.all_events_data.get (i).setParseObjectId (events.get (i).getObjectId ());
                ;
            }
            filtered_events_data.addAll (MainActivity.all_events_data);
        } catch (ParseException e) {
            e.printStackTrace ();
        }
        return MainActivity.all_events_data;
    }

    public void uploadArtistData(String producerId) {
        artist_list.clear ();
        List<Event> events;
        ParseQuery<Event> query = new ParseQuery<Event> ("Event");
        List<String> temp = new ArrayList<String> ();
        query.whereEqualTo ("producerId", producerId);
        events = null;
        try {
            events = query.find ();
        } catch (ParseException e) {
            e.printStackTrace ();
        }
        int position = 0;
        for (int i = position; i < events.size (); i++) {
            if (!events.get (i).getArtist ().equals ("") && !temp.contains (events.get (i).getArtist ())) {
                temp.add (events.get (i).getArtist ());
                artist_list.add (new Artist (events.get (i).getArtist (), events.get (i).getSold ()));
            }
        }
        artist_list.add (new Artist ("All Events", ""));
    }

    @Override
    public void onResume() {
        super.onResume ();
        getView ().setFocusableInTouchMode (true);
        getView ().requestFocus ();
        getView ().setOnKeyListener (new View.OnKeyListener () {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction () == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    if (lv.getVisibility () == View.VISIBLE) {
                        return false;
                    }
                    lv2.setVisibility (View.INVISIBLE);
                    lv.setVisibility (View.VISIBLE);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> av, View view, int i, long l) {
        Bundle b = new Bundle ();
        Intent intent = new Intent (getActivity (), EventPage.class);
        if (filtered_events_data.get (i).getImageId () != null) {
            Bitmap bmp = filtered_events_data.get (i).getImageId ();
            ByteArrayOutputStream stream = new ByteArrayOutputStream ();
            bmp.compress (Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray ();
            intent.putExtra ("eventImage", byteArray);
        } else {
            intent.putExtra ("eventImage", "");
        }
        intent.putExtra ("eventDate", filtered_events_data.get (i).getDate ());
        intent.putExtra ("eventName", filtered_events_data.get (i).getName ());
        intent.putExtra ("eventTags", filtered_events_data.get (i).getTags ());
        intent.putExtra ("eventPrice", filtered_events_data.get (i).getPrice ());
        intent.putExtra ("eventInfo", filtered_events_data.get (i).getInfo ());
        intent.putExtra ("eventPlace", filtered_events_data.get (i).getPlace ());
        intent.putExtra ("toilet", filtered_events_data.get (i).getToilet ());
        intent.putExtra ("parking", filtered_events_data.get (i).getParking ());
        intent.putExtra ("capacity", filtered_events_data.get (i).getCapacity ());
        intent.putExtra ("atm", filtered_events_data.get (i).getAtm ());
        intent.putExtra ("index", filtered_events_data.get (i).getIndexInFullList ());

        if (MainActivity.producerId != null) {
            b.putString ("producer_id", MainActivity.producerId);
            if (MainActivity.producerId.equals(filtered_events_data.get (i).getProducerId())) {
                Log.e(TAG, "true " + MainActivity.producerId + " " + filtered_events_data.get(i).getProducerId());
                intent.putExtra(Constants.QR, true);
            } else {
                Log.e(TAG, "false "+ MainActivity.producerId +" "+ filtered_events_data.get (i).getProducerId());
                intent.putExtra(Constants.QR, false);
            }
        } else {
            b.putString ("producer_id", filtered_events_data.get (i).getProducerId ());
        }
        intent.putExtras (b);
        startActivity (intent);
    }
}