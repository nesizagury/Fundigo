package com.example.FundigoApp.Events;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.FundigoApp.DeepLinkActivity;
import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.Producer.ProducerSendPuchActivity;
import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethod.EventDataMethods;
import com.example.FundigoApp.StaticMethod.FileAndImageMethods;
import com.example.FundigoApp.StaticMethod.GeneralStaticMethods;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class EventsListAdapter extends BaseAdapter {

    List<EventInfo> eventList = new ArrayList<EventInfo> ();
    Context context;
    private ImageView iv_share;
    boolean isSavedActivity;
    public int index;
    ImageLoader loader;

    public EventsListAdapter(Context c, List<EventInfo> eventList, boolean isSavedActivity) {
        this.context = c;
        this.eventList = eventList;
        this.isSavedActivity = isSavedActivity;
        loader = FileAndImageMethods.getImageLoader (c);
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
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

        index = i;
        loader.displayImage (eventList.get (i).getPicUrl (), eventListHolder.image);
        eventListHolder.date.setText (event.getDateAsString ());
        eventListHolder.name.setText (event.getName ());
        eventListHolder.tags.setText (event.getTags ());
        eventListHolder.price.setText (EventDataMethods.getDisplayedEventPrice (event.getPrice ()));
        eventListHolder.place.setText (event.getPlace ());
        checkIfChangeColorToSaveButtton (event, eventListHolder.saveEvent);
        eventListHolder.saveEvent.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if (GlobalVariables.IS_PRODUCER) {
                    Intent pushIntent = new Intent (context, ProducerSendPuchActivity.class);
                    pushIntent.putExtra ("id", event.getParseObjectId ());
                    context.startActivity (pushIntent);
                } else {
                    GeneralStaticMethods.handleSaveEventClicked (event,
                                                                        eventListHolder.saveEvent,
                                                                        context,
                                                                        R.mipmap.whhsaved,
                                                                        R.mipmap.whh);
                }
            }
        });

        iv_share = (ImageView) row.findViewById (R.id.imageView2);
        iv_share.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                switch (v.getId ()) {
                    case R.id.imageView2:
                        Intent intent = new Intent (context, DeepLinkActivity.class);
                        intent.putExtra ("name", eventListHolder.name.getText ().toString ());
                        intent.putExtra ("date", eventListHolder.date.getText ().toString ());
                        intent.putExtra ("place", eventListHolder.place.getText ().toString ());
                        intent.putExtra ("objectId", event.getParseObjectId ());
                        intent.putExtra ("fbUrl", event.getFbUrl ());
                        intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity (intent);
                        break;
                }
            }
        });
        return row;
    }

    private void checkIfChangeColorToSaveButtton(EventInfo event, ImageView saveEvent) {
        if (GlobalVariables.IS_PRODUCER) {
            saveEvent.setImageResource (R.drawable.ic_send_push);
        } else {
            if (event.getIsSaved ()) {
                saveEvent.setImageResource (R.mipmap.whhsaved);
            } else {
                saveEvent.setImageResource (R.mipmap.whh);
            }
        }
    }
}
