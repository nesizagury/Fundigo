package com.example.events;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by MAMO on 15/01/2016.
 */
public class EventService extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event_service);
        ListView event_service_listView;
        DisplayMetrics ma =new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(ma);



        int width = ma.widthPixels;
        int hieght= ma.heightPixels;

        getWindow().setLayout((int) (width * .7), (int) (hieght * .325));

        final Intent myintent = getIntent ();


        String toilet=myintent.getStringExtra("toilet");
        String parking=myintent.getStringExtra("parking");
        String capacity=myintent.getStringExtra("capacity");
        String atm=myintent.getStringExtra("atm");
        String driving=myintent.getStringExtra("driving");
        String walking=myintent.getStringExtra("walking");
        int walkValue= myintent.getIntExtra("walkValue",0);
        Log.d("mmm", "driving="+driving);

        event_service_listView=(ListView) findViewById(R.id.event_service_listView);
        event_service_listView.setAdapter(new CostumeAdapter(this,driving,walking,toilet,parking,capacity,atm,walkValue));

    }
}



class SignelRow{
    String title;
    String description;
    int image;

    SignelRow(String title,String description,int image){
        this.title=title;
        this.description=description;
        this.image=image;
    }

}

class CostumeAdapter extends BaseAdapter{

    Context c;
    String driving;
    String walking;
    String toilet;
    String parking;
    String capacety;
    String atm;
    Context context;
    int walkValue;
    ArrayList<SignelRow> list;
    CostumeAdapter(Context c, String driving, String walking, String toilet, String parking, String capacety, String atm,int walkValue){
        list=new ArrayList<SignelRow>();

        String[] titles=c.getResources().getStringArray(R.array.eventServiceTitles);

        int [] images= new int[]{R.drawable.driving_ldpi,R.drawable.walking_ldpi,R.drawable.toilet_ldpi, R.drawable.parking_ldpi, R.drawable.crowd_ldpi, R.drawable.atm_icon_ldpi};
        this.driving=driving;
        this.walking=walking;
        this.toilet=toilet;
        this.parking=parking;
        this.capacety=capacety;
        this.atm=atm;
        this.context=c;
        this.walkValue=walkValue;


        list.add(new SignelRow(titles[0],driving,images[0]));
        list.add(new SignelRow(titles[1],walking,images[1]));
        list.add(new SignelRow(titles[2],toilet,images[2]));
        list.add(new SignelRow(titles[3],parking,images[3]));
        list.add(new SignelRow(titles[4],capacety,images[4]));
        list.add(new SignelRow(titles[5],atm,images[5]));

        if(walkValue/3600>1){
            list.remove(1);
        }
        for(int i=0;i<list.size();i++){
            if(list.get(i).description==null || list.get(i).description=="" || list.get(i).description==" "){
                list.remove(i);
            }
        }

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInfla= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row=layoutInfla.inflate(R.layout.service_row, parent, false);
        TextView title= (TextView) row.findViewById(R.id.textView5);
        TextView description= (TextView) row.findViewById(R.id.textView6);
        ImageView image= (ImageView) row.findViewById(R.id.imageView4);

        SignelRow temp=list.get(position);
        title.setText(temp.title);
        description.setText(temp.description);
        image.setImageResource(temp.image);

        return row;
    }
}
