package com.example.FundigoApp.Events;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.FundigoApp.MainActivity;
import com.example.FundigoApp.R;
import com.example.FundigoApp.Customer.SavedEvents.SavedEventActivity;

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

public class EventsListAdapter extends BaseAdapter {

    List<EventInfo> eventList = new ArrayList<EventInfo> ();
    Context context;
    private ImageView iv_share;
    static final int REQUEST_CODE_MY_PICK = 1;
    Uri uri;
    boolean isSavedActivity;

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

        if(isSavedActivity && !event.getIsSaved ()){
            row.setVisibility(View.INVISIBLE);
        }
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
                final String eventObjectId = event.getParseObjectId ();
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
                                String lineToRemove = eventObjectId;
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
                                outputStream.write (eventObjectId.getBytes ());
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
                        ((Activity) context).startActivityForResult (intentPick, REQUEST_CODE_MY_PICK);
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
}
