package com.example.FundigoApp.Customer.RealTime;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.FundigoApp.Customer.SavedEvents.SavedEventActivity;
import com.example.FundigoApp.DeepLinkActivity;
import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethods;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class EventsGridAdapter extends BaseAdapter {

    List<EventInfo> eventList = new ArrayList<EventInfo> ();
    Context context;
    private ImageView iv_share;
    ImageLoader loader;

    public EventsGridAdapter(Context c, List<EventInfo> eventList) {
        this.context = c;
        this.eventList = eventList;
        loader = StaticMethods.getImageLoader(c);
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
        final EventGridHolder eventGridHolder;

        if (row == null) {
            LayoutInflater inflator = (LayoutInflater) context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
            row = inflator.inflate (R.layout.grid_view, viewGroup, false);
            eventGridHolder = new EventGridHolder (row);
            row.setTag (eventGridHolder);
        } else {
            eventGridHolder = (EventGridHolder) row.getTag ();
        }
        final EventInfo event = eventList.get (i);

        if(event.getPicUrl() != null)
            loader.displayImage(event.getPicUrl(),eventGridHolder.image);
        else
        eventGridHolder.image.setImageResource(R.drawable.pic0);

        eventGridHolder.name.setText (event.getName ());
        eventGridHolder.tags.setText (event.getTags ());
        eventGridHolder.price.setText (StaticMethods.getDisplayedEventPrice (event.getPrice ()));
        eventGridHolder.place.setText (event.getDist () + " km away");
        checkIfChangeColorToSaveButtton (event, eventGridHolder.saveEvent);
        eventGridHolder.saveEvent.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                final String eventObjId = event.getParseObjectId ();
                if (event.getIsSaved ()) {
                    event.setIsSaved (false);
                    eventGridHolder.saveEvent.setImageResource (R.mipmap.whh);
                    Toast.makeText (context, "You unSaved this event", Toast.LENGTH_SHORT).show ();
                    AsyncTask.execute (new Runnable () {
                        @Override
                        public void run() {
                            try {
                                InputStream inputStream = context.getApplicationContext ().openFileInput ("saves");
                                context.getApplicationContext ().deleteFile ("temp");
                                OutputStream outputStreamTemp = context.getApplicationContext ().openFileOutput ("temp", Context.MODE_PRIVATE);
                                BufferedReader bufferedReader = new BufferedReader (new InputStreamReader (inputStream));
                                BufferedWriter bufferedWriter = new BufferedWriter (new OutputStreamWriter (outputStreamTemp));
                                String lineToRemove = eventObjId;
                                String currentLine;
                                while ((currentLine = bufferedReader.readLine ()) != null) {
                                    // trim newline when comparing with lineToRemove
                                    String trimmedLine = currentLine.trim ();
                                    if (trimmedLine.equals (lineToRemove)) continue;
                                    else {
                                        bufferedWriter.write (currentLine);
                                        bufferedWriter.write (System.getProperty ("line.separator"));
                                    }
                                }
                                bufferedReader.close ();
                                bufferedWriter.close ();
                                inputStream = context.getApplicationContext ().openFileInput ("temp");
                                context.getApplicationContext ().deleteFile ("saves");
                                outputStreamTemp = context.getApplicationContext ().openFileOutput ("saves", Context.MODE_PRIVATE);
                                bufferedReader = new BufferedReader (new InputStreamReader (inputStream));
                                bufferedWriter = new BufferedWriter (new OutputStreamWriter (outputStreamTemp));
                                while ((currentLine = bufferedReader.readLine ()) != null) {
                                    bufferedWriter.write (currentLine);
                                    bufferedWriter.write (System.getProperty ("line.separator"));
                                }
                                bufferedReader.close ();
                                bufferedWriter.close ();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace ();
                            } catch (IOException e) {
                                e.printStackTrace ();
                            }
                        }
                    });
                } else {
                    event.setIsSaved (true);
                    eventGridHolder.saveEvent.setImageResource (R.mipmap.whhsaved);
                    Toast.makeText (context, "You Saved this event", Toast.LENGTH_SHORT).show ();
                    AsyncTask.execute (new Runnable () {
                        @Override
                        public void run() {
                            try {
                                OutputStream outputStream = context.getApplicationContext ().openFileOutput ("saves", Context.MODE_APPEND + Context.MODE_PRIVATE);
                                outputStream.write (eventObjId.getBytes ());
                                outputStream.write (System.getProperty ("line.separator").getBytes ());
                                outputStream.close ();
                            } catch (IOException e) {
                                e.printStackTrace ();
                            }
                        }
                    });
                }
                if (GlobalVariables.SAVED_ACTIVITY_RUNNING) {
                    SavedEventActivity.getSavedEventsFromJavaList ();
                }
            }
        });

        iv_share = (ImageView) row.findViewById (R.id.imageView2_grid);
        iv_share.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                switch (v.getId ()) {
                    case R.id.imageView2_grid:
                        Intent intent = new Intent (context, DeepLinkActivity.class);
                        intent.putExtra ("name", eventGridHolder.name.getText ().toString ());
                        intent.putExtra ("date", eventGridHolder.date.getText ().toString ());
                        intent.putExtra ("place", eventGridHolder.place.getText ().toString ());
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
        if (event.getIsSaved ()) {
            saveEvent.setImageResource (R.mipmap.whhsaved);
        } else {
            saveEvent.setImageResource (R.mipmap.whh);
        }
    }
}
