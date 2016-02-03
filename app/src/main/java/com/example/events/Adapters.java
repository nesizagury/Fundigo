package com.example.events;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Adapters extends BaseAdapter {


    List<EventInfo> list = new ArrayList<EventInfo> ();
    Context context;
    private ImageView iv_share;
    private final static String TAG = "Adapters";
    static final int REQUEST_CODE_MY_PICK = 1;
    Uri uri;


    public Adapters(Context c) {

        this.context = c;

        Resources res = context.getResources ();
        String[] eventDate_list;
        String[] eventName_list;
        String[] eventTag_list;
        String[] eventPrice_list;
        String[] eventInfo_list;
        String[] eventPlace_list;
        String[] eventFilter_list;


        eventName_list = res.getStringArray (R.array.eventNames);
        eventDate_list = res.getStringArray (R.array.eventDates);
        eventTag_list = res.getStringArray (R.array.eventTags);
        eventPrice_list = res.getStringArray (R.array.eventPrice);
        eventInfo_list = res.getStringArray (R.array.eventInfo);
        eventPlace_list = res.getStringArray (R.array.eventPlace);
        eventFilter_list=res.getStringArray(R.array.filterTags);

        String arrToilet[] = res.getStringArray (R.array.eventToiletService);
        String arrParking[] = res.getStringArray (R.array.eventParkingService);
        String arrCapacity[] = res.getStringArray (R.array.eventCapacityService);
        String arrATM[] = res.getStringArray (R.array.eventATMService);

        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 14; i++)
                list.add (new EventInfo (
                                                R.mipmap.pic0 + i,
                                                eventDate_list[i],
                                                eventName_list[i],
                                                eventTag_list[i],
                                                eventPrice_list[i],
                                                eventInfo_list[i],
                                                eventPlace_list[i],
                                                arrToilet[i],
                                                arrParking[i],
                                                arrCapacity[i],
                                                arrATM[i],
                                                eventFilter_list[i]));
        }
    }

    public Adapters(Context c, ArrayList<Event> arrayList) {

        this.context = c;

        Resources res = context.getResources ();
        String[] eventDate_list;
        String[] eventName_list;
        String[] eventTag_list;
        String[] eventPrice_list;
        String[] eventInfo_list;
        String[] eventPlace_list;
        String[] eventFilter_list;

        eventName_list = res.getStringArray (R.array.eventNames);
        eventDate_list = res.getStringArray (R.array.eventDates);
        eventTag_list = res.getStringArray (R.array.eventTags);
        eventPrice_list = res.getStringArray (R.array.eventPrice);
        eventInfo_list = res.getStringArray (R.array.eventInfo);
        eventPlace_list = res.getStringArray (R.array.eventPlace);
        eventFilter_list=res.getStringArray(R.array.filterTags);

        String arrToilet[] = res.getStringArray (R.array.eventToiletService);
        String arrParking[] = res.getStringArray (R.array.eventParkingService);
        String arrCapacity[] = res.getStringArray (R.array.eventCapacityService);
        String arrATM[] = res.getStringArray (R.array.eventATMService);

        List<EventInfo> ans = new ArrayList<EventInfo> ();

        for (int j = 0; j < 1; j++) {
            for (int i = 0; i < 15; i++)
                ans.add (new EventInfo (
                                               R.mipmap.pic0 + i,
                                               eventDate_list[i],
                                               eventName_list[i],
                                               eventTag_list[i],
                                               eventPrice_list[i],
                                               eventInfo_list[i],
                                               eventPlace_list[i],
                                               arrToilet[i],
                                               arrParking[i],
                                               arrCapacity[i],
                                               arrATM[i],
                                               eventFilter_list[i]));
        }
        boolean flag = true;
        for (int i = 0; i < arrayList.size (); i++) {
            for (int j = 0; j < ans.size () && flag; j++) {
                if (ans.get (j).getName ().equals (arrayList.get (i).getName ()) && flag) {
                    ans.get (j).setPlace (ans.get (j).getPlace () + " " + arrayList.get (i).getdis () + " km away");
                    list.add (ans.get (j));
                    flag = false;
                }
            }
            flag = true;
        }
    }


    @Override
    public int getCount() {
        return list.size ();
    }

    @Override
    public Object getItem(int i) {
        return list.get (i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = view;
        final Holder holder;


        if (row == null) {
            LayoutInflater inflator = (LayoutInflater) context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
            row = inflator.inflate (R.layout.list_view, viewGroup, false);
            holder = new Holder (row);
            row.setTag (holder);

        } else {
            holder = (Holder) row.getTag ();
        }

        EventInfo event = list.get (i);
        holder.image.setImageResource (event.imageId);

        holder.date.setText (event.getDate ());
        holder.name.setText (event.getName ());
        holder.tags.setText (event.getTags ());
        holder.price.setText (event.getPrice ());
        holder.place.setText (event.getPlace ());

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
                            // TODO Auto-generated catch block
                            e.printStackTrace ();
                        }
                        Intent intent = new Intent (Intent.ACTION_SEND);
                        intent.setType ("image/jpeg");
                        intent.putExtra (Intent.EXTRA_TEXT, "I`m going to " + holder.name.getText ().toString () +
                                                                    "\n" + "C u there at " + holder.date.getText ().toString () + " !" +
                                                                    "\n" + "At " + holder.place.getText ().toString () +
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
                        editor.putString ("name", holder.name.getText ().toString ());
                        editor.putString ("date", holder.date.getText ().toString ());
                        editor.putString ("place", holder.place.getText ().toString ());
                        editor.apply ();
                        Log.e (TAG, "" + holder.name.getText ().toString () + " " + holder.date.getText ().toString () + " " + holder.place.getText ().toString ());
                        ((Activity) context).startActivityForResult (intentPick, REQUEST_CODE_MY_PICK);
                        break;
                }
            }


        });


        return row;


    }

    public Adapters(Context c, String name, int from, ArrayList<EventInfo> arrayList) {
        this.context = c;
        if (from == 1) {
            for (int i = 0; i < MainActivity.events_data.size (); i++) {
                if (MainActivity.events_data.get (i).getPlace ().contains (name) && !list.contains (MainActivity.events_data.get (i))) {
                    list.add (MainActivity.events_data.get (i));
                }
            }
        } else {
            for (int i = 0; i < arrayList.size (); i++) {
                if (arrayList.get (i).getPlace ().contains (name) && !list.contains (arrayList.get (i))) {
                    list.add (arrayList.get (i));
                }
            }
        }
    }

    public Adapters(Context c, String name, ArrayList<EventInfo> arrayList) {
        this.context = c;
        if (name.equals ("filter")) {
            list = arrayList;
        } else {
            for (int i = 0; i < arrayList.size (); i++) {
                if (arrayList.get (i).getName ().equals (name) && !list.contains (arrayList.get (i)))
                    list.add (arrayList.get (i));
            }
        }
    }
}
