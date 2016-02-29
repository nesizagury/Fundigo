package com.example.FundigoApp.Events;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.FundigoApp.DeepLinkActivity;
import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethods;

import java.util.ArrayList;
import java.util.List;

public class EventsListAdapter extends BaseAdapter {

    List<EventInfo> eventList = new ArrayList<EventInfo> ();
    Context context;
    private ImageView iv_share;
    Uri uri;
    boolean isSavedActivity;
    public int index;

    public EventsListAdapter(Context c, List<EventInfo> eventList, boolean isSavedActivity) {
        this.context = c;
        this.eventList = eventList;
        this.isSavedActivity = isSavedActivity;
    }

    @Override
    public int getCount() {
        return eventList.size ();
    }

    @Override
    public Object getItem(int i) {
        return eventList.get (i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = view;
        final EventListHolder eventListHolder;

        if (row == null) {
            LayoutInflater inflator = (LayoutInflater) context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
            row = inflator.inflate (R.layout.list_view, viewGroup, false);
            eventListHolder = new EventListHolder (row);
            row.setTag (eventListHolder);
        } else {
            eventListHolder = (EventListHolder) row.getTag ();
        }
        final EventInfo event = eventList.get (i);

        if (isSavedActivity && !event.getIsSaved ()) {
            row.setVisibility (View.INVISIBLE);
        }
        index = i;
        eventListHolder.image.setImageBitmap (event.imageId);
        eventListHolder.date.setText (event.getDate ());
        eventListHolder.name.setText (event.getName ());
        eventListHolder.tags.setText (event.getTags ());
        eventListHolder.price.setText (event.getPrice ());
        eventListHolder.place.setText (event.getPlace ());
        checkIfChangeColorToSaveButtton (event, eventListHolder.saveEvent);
        eventListHolder.saveEvent.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                StaticMethods.handleSaveEventClicked (event,
                                                             eventListHolder.saveEvent,
                                                             context,
                                                             R.mipmap.whhsaved,
                                                             R.mipmap.whh);
            }
        });

        iv_share = (ImageView) row.findViewById (R.id.imageView2);
        iv_share.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                switch (v.getId ()) {
                    case R.id.imageView2:
                        Intent intent = new Intent(context,DeepLinkActivity.class);
                        intent.putExtra("name",eventListHolder.name.getText().toString());
                        intent.putExtra("date",eventListHolder.date.getText().toString());
                        intent.putExtra("place", eventListHolder.place.getText().toString());
                        intent.putExtra("objectId", event.getParseObjectId ());
                        intent.putExtra("fbUrl",event.getFbUrl());//Assaf : url link to facebook
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        break;
                }
            }
        });
        return row;
    }

    private void checkIfChangeColorToSaveButtton(EventInfo event, ImageView saveEvent) {
        if (event.getIsSaved ()) {
            saveEvent.setImageResource (R.mipmap.whhsaved);
        } else {
            saveEvent.setImageResource (R.mipmap.whh);
        }
    }
}
