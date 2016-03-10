package com.example.FundigoApp.Events;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.FundigoApp.R;

import java.util.ArrayList;

public class EventServiceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        
        setContentView (R.layout.activity_event_service);
        ListView event_service_listView;
        DisplayMetrics ma = new DisplayMetrics ();
        getWindowManager ().getDefaultDisplay ().getMetrics (ma);

        int width = ma.widthPixels;
        int hieght = ma.heightPixels;
        
        getWindow ().setLayout ((int) (width * .7), (int) (hieght * .325));
        
        final Intent myintent = getIntent ();

        String toilet = myintent.getStringExtra ("toilet");
        String parking = myintent.getStringExtra ("parking");
        String capacity = myintent.getStringExtra ("capacity");
        String atm = myintent.getStringExtra ("atm");
        String driving = myintent.getStringExtra ("driving");
        String artist = myintent.getStringExtra ("artist");
        int walkValue = myintent.getIntExtra ("walkValue", -1);
        String walking;

        if (walkValue == -1 || walkValue / 3600 > 1) {
            walking = null;
        } else {
            walking = myintent.getStringExtra ("walking");
        }

        event_service_listView = (ListView) findViewById (R.id.event_service_listView);
        event_service_listView.setAdapter (new CostumeAdapter (this,
                                                                      driving,
                                                                      walking,
                                                                      toilet,
                                                                      parking,
                                                                      capacity,
                                                                      atm,
                                                                      artist));
    }
}

class SignelRow {
    String title;
    String description;
    int image;
    
    SignelRow(String title, String description, int image) {
        this.title = title;
        this.description = description;
        this.image = image;
    }
}

class CostumeAdapter extends BaseAdapter {
    String driving;
    String walking;
    String toilet;
    String parking;
    String capacety;
    String atm;
    String artist;
    Context context;
    ArrayList<SignelRow> list;
    
    CostumeAdapter(Context c,
                   String driving,
                   String walking,
                   String toilet,
                   String parking,
                   String capacety,
                   String atm,
                   String artist) {

        list = new ArrayList<SignelRow> ();
        String[] titles = c.getResources ().getStringArray (R.array.eventServiceTitles);
        int[] images = new int[]{R.drawable.microphone,
                                        R.drawable.driving_ldpi,
                                        R.drawable.walking_ldpi,
                                        R.drawable.toilet_ldpi,
                                        R.drawable.parking_ldpi,
                                        R.drawable.crowd_ldpi,
                                        R.drawable.atm_icon_ldpi};
        this.driving = driving;
        this.walking = walking;
        this.toilet = toilet;
        this.parking = parking;
        this.capacety = capacety;
        this.atm = atm;
        this.context = c;
        this.artist = artist;

        list.add (new SignelRow (titles[0], artist, images[0]));
        list.add (new SignelRow (titles[1], driving, images[1]));
        list.add (new SignelRow (titles[2], walking, images[2]));
        list.add (new SignelRow (titles[3], toilet, images[3]));
        list.add (new SignelRow (titles[4], parking, images[4]));
        list.add (new SignelRow (titles[5], capacety, images[5]));
        list.add (new SignelRow (titles[6], atm, images[6]));

        ArrayList<SignelRow> tempList = new ArrayList<SignelRow> ();
        for (int i = 0; i < list.size (); i++) {
            if (list.get (i).description != null && !list.get (i).description.isEmpty ()) {
                tempList.add (list.get (i));
            }
        }

        list.clear ();
        list.addAll (tempList);
    }
    
    @Override
    public int getCount() {
        return list.size ();
    }
    
    @Override
    public Object getItem(int position) {
        return list.get (position);
    }
    
    @Override
    public long getItemId(int position) {
        return 0;
    }
    
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        
        LayoutInflater layoutInfla = (LayoutInflater) context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInfla.inflate (R.layout.service_row, parent, false);
        TextView title = (TextView) row.findViewById (R.id.textView5);
        TextView description = (TextView) row.findViewById (R.id.textView6);
        ImageView image = (ImageView) row.findViewById (R.id.imageView4);
        
        final SignelRow temp = list.get (position);
        title.setText (temp.title);
        description.setText (temp.description);
        image.setImageResource (temp.image);
        
        return row;
    }
}
