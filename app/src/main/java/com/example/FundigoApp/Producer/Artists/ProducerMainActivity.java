package com.example.FundigoApp.Producer.Artists;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.FundigoApp.Events.EventPage;
import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethods;
import com.example.FundigoApp.StaticMethods.GetEventsDataCallback;

import java.util.ArrayList;
import java.util.List;

public class ProducerMainActivity extends Fragment implements GetEventsDataCallback {

    static List<Artist> artist_list = new ArrayList<Artist> ();
    ListView artistListView;
    ArtistAdapter artistAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate (R.layout.activity_main_producer, container, false);
        artistListView = (ListView) rootView.findViewById (R.id.artist_list_view);
        artistAdapter = new ArtistAdapter (getActivity ().getApplicationContext (), artist_list);
        artistListView.setAdapter (artistAdapter);
        artistListView.setSelector (new ColorDrawable (Color.TRANSPARENT));

        if (GlobalVariables.ALL_EVENTS_DATA.size () == 0) {
            Intent intent = new Intent (this.getActivity (), EventPage.class);
            StaticMethods.uploadEventsData (this, GlobalVariables.PRODUCER_PARSE_OBJECT_ID, this.getContext (), intent);
        }
        artistListView.setOnItemClickListener (new AdapterView.OnItemClickListener () {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent (getActivity (), ArtistStatsActivity.class);
                intent.putExtra ("artist_name", artist_list.get (position).getName ());
                startActivity (intent);
            }
        });
        return rootView;
    }

    @Override
    public void eventDataCallback() {
        StaticMethods.uploadArtistData (artist_list);
        artistAdapter.notifyDataSetChanged ();
    }
}