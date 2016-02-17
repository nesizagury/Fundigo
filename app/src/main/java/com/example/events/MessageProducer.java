package com.example.events;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class MessageProducer extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener
{
    ImageButton mipo;
    ImageView notification;
    ListView listView;
    String customer_id;
    List<MessageRoomBean> list=new ArrayList<>();
    ArrayList<Bitmap> pic=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_producer);
        listView=(ListView)findViewById(R.id.listView_massge_producer);

        mipo=(ImageButton)findViewById(R.id.mipo_MassageProducer);
        notification=(ImageView)findViewById(R.id.notification_MassageProducer);
        if(MainActivity.isCustomer)
        {
            customer_id=MainActivity.customer_id;
            list=getMassage();
            MessageRoomAdapter arr=new MessageRoomAdapter(this,list,pic);
            listView.setAdapter(arr);
            listView.setOnItemClickListener(this);
        }
        mipo.setOnClickListener(this);
        notification.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId()== R.id.notification_MassageProducer) {
            Intent MessageIntent = new Intent(MessageProducer.this, MyNotification.class);
            startActivity(MessageIntent);
        }
         else if(v.getId()==R.id.mipo_MassageProducer)
        {
                Intent mipoIntent=new Intent(MessageProducer.this,Mipo.class);
                startActivity(mipoIntent);
        }
    }

    private List<MessageRoomBean> getMassage()
    {
        List<Room> listObject = new ArrayList<>();
        //ParseQuery<ParseObject> query = ParseQuery.getQuery("Room");
        ParseQuery<Room> query = ParseQuery.getQuery (Room.class);
        query.whereEqualTo ("customer_id", customer_id);
        query.orderByDescending ("createdAt");
        try
        {
            listObject = query.find();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        List<MessageRoomBean> arr=new ArrayList<>();
        ParseQuery<Event> querySecond = ParseQuery.getQuery ("Event");
        ParseQuery<Message> queryThree = ParseQuery.getQuery (Message.class);
        byte[] picture=null;
        List<Event> listForFindPicture= new ArrayList<>();
        List<String> lastMessgeToShow=new ArrayList<>();
        List<Message> lastMessage=new ArrayList<>();
        for(int i=0;i<listObject.size();i++)
        {
            //need to add whereEqualTo(" Name of event").
            querySecond.whereEqualTo("producerId", listObject.get(i).getProducer_id());
            queryThree.whereEqualTo ("producer",listObject.get(i).getProducer_id() );
            queryThree.orderByDescending ("createdAt");

            try{
                listForFindPicture=querySecond.find();
                lastMessage=queryThree.find();
                ParseFile imageFile = (ParseFile)listForFindPicture.get(0).get("ImageFile");
                picture=imageFile.getData();
            }catch (ParseException e){}
            Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
            pic.add(bitmap);
            lastMessgeToShow.add(lastMessage.get(0).getBody());
        }

        if(customer_id!=null)
        {
            for (int i = 0; i < listObject.size(); i++)
            {
                arr.add(new MessageRoomBean(0, listObject.get(i).getName(), lastMessgeToShow.get(i) , listObject.get(i).getCustomer_id(), listObject.get(i).getProducer_id()));
                Log.d("MY APP:",lastMessgeToShow.get(i)+"////");

            }
        }
        return arr;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent (this, ChatActivity.class);
        MessageItemHolder holder = (MessageItemHolder) view.getTag ();
        MessageRoomBean mrb = (MessageRoomBean) holder.customer.getTag ();
        intent.putExtra ("producer_id", list.get(i).getProducer_id());
        intent.putExtra ("customer_id", mrb.customer_id);
        startActivity (intent);
    }

}
