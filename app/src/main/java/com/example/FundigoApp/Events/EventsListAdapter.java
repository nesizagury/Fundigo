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
import java.util.Calendar;
import java.util.List;

public class EventsListAdapter extends BaseAdapter {

    private static final String TAG = "EventsListAdapter";
    List<EventInfo> eventList = new ArrayList<EventInfo>();
    Context context;
    private ImageView iv_share;
    Uri uri;
    boolean isSavedActivity;
    public int index;
    private String date;


    public EventsListAdapter(Context c, List<EventInfo> eventList, boolean isSavedActivity) {
        this.context = c;
        this.eventList = eventList;
        this.isSavedActivity = isSavedActivity;
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Object getItem(int i) {
        return eventList.get(i);
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
            LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflator.inflate(R.layout.list_view, viewGroup, false);
            eventListHolder = new EventListHolder(row);
            row.setTag(eventListHolder);
        } else {
            eventListHolder = (EventListHolder) row.getTag();
        }
        final EventInfo event = eventList.get(i);
        date = event.getRealDate();
        long realDate = Long.parseLong(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(realDate);
        String dayOfWeek = null;
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                dayOfWeek = "SUN";
                break;
            case 2:
                dayOfWeek = "MON";
                break;
            case 3:
                dayOfWeek = "TUE";
                break;
            case 4:
                dayOfWeek = "WED";
                break;
            case 5:
                dayOfWeek = "THU";
                break;
            case 6:
                dayOfWeek = "FRI";
                break;
            case 7:
                dayOfWeek = "SAT";
                break;
        }
        String month = null;
        switch (calendar.get(Calendar.MONTH)) {
            case 0:
                month = "JAN";
                break;
            case 1:
                month = "FEB";
                break;
            case 2:
                month = "MAR";
                break;
            case 3:
                month = "APR";
                break;
            case 4:
                month = "MAY";
                break;
            case 5:
                month = "JUN";
                break;
            case 6:
                month = "JUL";
                break;
            case 7:
                month = "AUG";
                break;
            case 8:
                month = "SEP";
                break;
            case 9:
                month = "OCT";
                break;
            case 10:
                month = "NOV";
                break;
            case 11:
                month = "DEC";
                break;
        }
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String ampm = null;
        if (calendar.get(Calendar.AM_PM) == Calendar.AM)
            ampm = "AM";
        else if (calendar.get(Calendar.AM_PM) == Calendar.PM)
            ampm = "PM";

        String min;
        if (minute < 10) {
            min = "0" + minute;
        } else {
            min = "" + minute;
        }

        eventListHolder.date.setText(dayOfWeek + ", " + month + " " + day + ", " + hour + ":" + min + " " + ampm);

        if (isSavedActivity && !event.getIsSaved()) {
            row.setVisibility(View.INVISIBLE);
        }
        index = i;
        eventListHolder.image.setImageBitmap(event.imageId);
        //eventListHolder.date.setText (event.getDate ());
        String artist = event.getArtist();
        if (artist.length() != 0) {
            eventListHolder.artist.setText(event.getArtist());
            eventListHolder.artist.setVisibility(View.VISIBLE);
        } else {
            eventListHolder.artist.setVisibility(View.GONE);
        }
        eventListHolder.name.setText(event.getName());
        eventListHolder.tags.setText(event.getTags());
        eventListHolder.price.setText(event.getPrice());
        eventListHolder.place.setText(event.getPlace());
        checkIfChangeColorToSaveButtton(event, eventListHolder.saveEvent);
        eventListHolder.saveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticMethods.handleSaveEventClicked(event,
                        eventListHolder.saveEvent,
                        context,
                        R.mipmap.whhsaved,
                        R.mipmap.whh);
            }
        });

        iv_share = (ImageView) row.findViewById(R.id.imageView2);
        iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.imageView2:
                        Intent intent = new Intent(context, DeepLinkActivity.class);
                        intent.putExtra("name", eventListHolder.name.getText().toString());
                        intent.putExtra("date", eventListHolder.date.getText().toString());
                        intent.putExtra("place", eventListHolder.place.getText().toString());
                        intent.putExtra("objectId", event.getParseObjectId());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        break;
                }
            }
        });
        return row;
    }

    private void checkIfChangeColorToSaveButtton(EventInfo event, ImageView saveEvent) {
        if (event.getIsSaved()) {
            saveEvent.setImageResource(R.mipmap.whhsaved);
        } else {
            saveEvent.setImageResource(R.mipmap.whh);
        }
    }
}
