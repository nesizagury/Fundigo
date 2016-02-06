package com.example.events;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventsListAdapter extends BaseAdapter {

    List<EventInfo> eventList = new ArrayList<EventInfo> ();
    Context context;
    private ImageView iv_share;
    private final static String TAG = "EventsListAdapter";
    static final int REQUEST_CODE_MY_PICK = 1;
    Uri uri;


    public EventsListAdapter(Context c, List<EventInfo> eventList) {

        this.context = c;
        this.eventList = eventList;

    }

    public EventsListAdapter(Context c, ArrayList<Event> arrayList) {

        this.context = c;
        List<EventInfo> ans = new ArrayList<EventInfo> ();

        boolean flag = true;
        for (int i = 0; i < arrayList.size (); i++) {
            for (int j = 0; j < ans.size () && flag; j++) {
                if (ans.get (j).getName ().equals (arrayList.get (i).getName ()) && flag) {
                    ans.get (j).setPlace (ans.get (j).getPlace () + " " + arrayList.get (i).getdis () + " km away");
                    eventList.add (ans.get (j));
                    flag = false;
                }
            }
            flag = true;
        }
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
                if (event.getPress ()) {
                    event.setPress (false);
                    eventListHolder.saveEvent.setImageResource (R.mipmap.whh);
                    Toast.makeText (context, "You unSaved this event", Toast.LENGTH_SHORT).show ();
                    try {
                        File inputFile = new File ("saves");
                        File tempFile = new File ("myTempFile");
                        BufferedReader reader = new BufferedReader (new FileReader (inputFile));
                        BufferedWriter writer = new BufferedWriter (new FileWriter (tempFile));
                        String lineToRemove = event.name;
                        String currentLine;
                        while ((currentLine = reader.readLine ()) != null) {
                            // trim newline when comparing with lineToRemove
                            String trimmedLine = currentLine.trim ();
                            if (trimmedLine.equals (lineToRemove)) continue;
                            writer.write (currentLine);
                        }
                        writer.close ();
                        reader.close ();
                        tempFile.renameTo (inputFile);
                    } catch (FileNotFoundException e) {
                    } catch (IOException e) {
                    }
                } else {
                    event.setPress (true);
                    eventListHolder.saveEvent.setImageResource (R.mipmap.whhsaved);
                    Toast.makeText (context, "You Saved this event", Toast.LENGTH_SHORT).show ();
                    String filename = "saves";
                    FileOutputStream outputStream;
                    try {
                        outputStream = context.openFileOutput (filename, Context.MODE_PRIVATE);
                        outputStream.write (event.name.getBytes ());
                        outputStream.close ();
                    } catch (Exception e) {
                        e.printStackTrace ();
                    }
                }
            }
        });

        iv_share = (ImageView) row.findViewById (R.id.imageView2);
        iv_share.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                switch (v.getId ()) {
                    case R.id.imageView2:
                        Log.e (TAG, "iv_share in adapter");
                        try {

                            Bitmap largeIcon = BitmapFactory.decodeResource (context.getResources (), R.mipmap.pic0);
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream ();
                            largeIcon.compress (Bitmap.CompressFormat.JPEG, 40, bytes);
                            File f = new File (Environment.getExternalStorageDirectory () + File.separator + "test.jpg");
                            f.createNewFile ();
                            FileOutputStream fo = new FileOutputStream (f);
                            fo.write (bytes.toByteArray ());
                            fo.close ();
                        } catch (IOException e) {
                            e.printStackTrace ();
                        }
                        Intent intent = new Intent (Intent.ACTION_SEND);
                        intent.setType ("image/jpeg");
                        intent.putExtra (Intent.EXTRA_TEXT, "I`m going to " + eventListHolder.name.getText ().toString () +
                                                                    "\n" + "C u there at " + eventListHolder.date.getText ().toString () + " !" +
                                                                    "\n" + "At " + eventListHolder.place.getText ().toString () +
                                                                    "\n" + "http://eventpageURL.com/here");
                        String imagePath = Environment.getExternalStorageDirectory () + File.separator + "test.jpg";
                        File imageFileToShare = new File (imagePath);
                        uri = Uri.fromFile (imageFileToShare);
                        intent.putExtra (Intent.EXTRA_STREAM, uri);

                        Intent intentPick = new Intent ();
                        intentPick.setAction (Intent.ACTION_PICK_ACTIVITY);
                        intentPick.putExtra (Intent.EXTRA_TITLE, "Launch using");
                        intentPick.putExtra (Intent.EXTRA_INTENT, intent);
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences (context);
                        SharedPreferences.Editor editor = sp.edit ();
                        editor.putString ("name", eventListHolder.name.getText ().toString ());
                        editor.putString ("date", eventListHolder.date.getText ().toString ());
                        editor.putString ("place", eventListHolder.place.getText ().toString ());
                        editor.apply ();
                        Log.e (TAG, "" + eventListHolder.name.getText ().toString () + " " + eventListHolder.date.getText ().toString () + " " + eventListHolder.place.getText ().toString ());
                        ((Activity) context).startActivityForResult (intentPick, REQUEST_CODE_MY_PICK);
                        break;
                }
            }

        });
        return row;
    }

    public EventsListAdapter(Context c, String name, int from, ArrayList<EventInfo> arrayList) {
        this.context = c;
        if (from == 1) {
            for (int i = 0; i < MainActivity.events_data.size (); i++) {
                if (MainActivity.events_data.get (i).getPlace ().contains (name) && !eventList.contains (MainActivity.events_data.get (i))) {
                    eventList.add (MainActivity.events_data.get (i));
                }
            }
        } else {
            for (int i = 0; i < arrayList.size (); i++) {
                if (arrayList.get (i).getPlace ().contains (name) && !eventList.contains (arrayList.get (i))) {
                    eventList.add (arrayList.get (i));
                }
            }
        }
    }

    public EventsListAdapter(Context c, String name, ArrayList<EventInfo> arrayList) {
        this.context = c;
        if (name.equals ("filter")) {
            eventList = arrayList;
        } else {
            for (int i = 0; i < arrayList.size (); i++) {
                if (arrayList.get (i).getName ().equals (name) && !eventList.contains (arrayList.get (i)))
                    eventList.add (arrayList.get (i));
            }
        }
    }

    private void checkIfChangeColorToSaveButtton(EventInfo event, ImageView saveEvent) {
        if (event.getPress ()) {
            saveEvent.setImageResource (R.mipmap.whhsaved);
        } else {
            saveEvent.setImageResource (R.mipmap.whh);
        }
    }
}
