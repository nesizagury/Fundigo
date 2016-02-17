package com.example.events;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class MyNotification extends AppCompatActivity implements AdapterView.OnItemClickListener,View.OnClickListener
{

    ListView notificationList;
    ImageButton massage,mipo;
    List<ParseObject> myList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_notification);
        notificationList=(ListView)findViewById(R.id.listViewNotification);
        massage=(ImageButton)findViewById(R.id.Message_itemPush);
        mipo=(ImageButton)findViewById(R.id.Mipo_Push);

        myList=getNotification();
        PushAdapter adapter=new PushAdapter(this,myList);

        notificationList.setAdapter(adapter);
        notificationList.setSelector(new ColorDrawable(Color.TRANSPARENT));
        notificationList.setOnItemClickListener(this);

        massage.setOnClickListener(this);
        mipo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == massage.getId())
        {
            Intent MessageIntent = new Intent(MyNotification.this, MessageProducer.class);
            startActivity(MessageIntent);
        }
        else if(v.getId()==mipo.getId())
        {
            Intent mipoIntent=new Intent(MyNotification.this,Mipo.class);
            startActivity(mipoIntent);
        }
    }

    public List getNotification()
    {
        List<ParseObject> listObject = new ArrayList<>() ;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Push");
        try
        {
            listObject = query.find();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        List<ParseObject> temp=new ArrayList<>();
        for(int i=listObject.size()-1;i>=0;i--)
        {
            temp.add(listObject.get(i));
        }
        return temp;
    }

    @Override
    public void onItemClick(AdapterView<?> av, View view, int i, long l) {
        Bundle b = new Bundle ();
        Intent intent = new Intent (MyNotification.this, PushPage.class);
        intent.putExtra ("Message", myList.get(i).getString("pushMessage").toString());
        intent.putExtra("Date", myList.get(i).getString("Date").toString());
        intent.putExtra("eventName", myList.get(i).getString("EvendId").toString());
        startActivity(intent);
    }



}
