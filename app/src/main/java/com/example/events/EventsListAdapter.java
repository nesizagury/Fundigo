package com.example.events;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;

public class EventsListAdapter extends BaseAdapter {

    List<EventInfo> eventList = new ArrayList<EventInfo> ();
    Context context;
    private ImageView iv_share;
    static final int REQUEST_CODE_MY_PICK = 1;
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

        if(isSavedActivity && !event.getIsSaved ()){
            row.setVisibility(View.INVISIBLE);
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
                final String eventName = event.name;
                if (event.getIsSaved ()) {
                    event.setIsSaved (false);
                    eventListHolder.saveEvent.setImageResource (R.mipmap.whh);
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
                                String lineToRemove = eventName;
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
                    eventListHolder.saveEvent.setImageResource (R.mipmap.whhsaved);
                    Toast.makeText (context, "You Saved this event", Toast.LENGTH_SHORT).show ();
                    AsyncTask.execute (new Runnable () {
                        @Override
                        public void run() {
                            try {
                                OutputStream outputStream = context.getApplicationContext ().openFileOutput ("saves", Context.MODE_APPEND + Context.MODE_PRIVATE);
                                outputStream.write (eventName.getBytes ());
                                outputStream.write (System.getProperty ("line.separator").getBytes ());
                                outputStream.close ();
                            } catch (IOException e) {
                                e.printStackTrace ();
                            }
                        }
                    });
                }
                MainActivity.eventsListAdapter.notifyDataSetChanged ();
                if(MainActivity.savedAcctivityRunnig) {
                    SavedEventActivity.getSavedEventsFromJavaList ();
                }
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
                        intent.putExtra("objectId", event.getObjectId());
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
            saveEvent.setImageResource (R.mipmap.whhsaved);
        } else {
            saveEvent.setImageResource (R.mipmap.whh);
        }
    }

    public EventsListAdapter(Context c, String name, ArrayList<EventInfo> arrayList) {
        this.context = c;
        if (name.equals("filter")) {
            eventList = arrayList;
        } else {
            for (int i = 0; i < arrayList.size (); i++) {
                if (arrayList.get (i).getName ().equals (name) && !eventList.contains (arrayList.get (i)))
                    eventList.add (arrayList.get (i));
            }
        }
    }




}
