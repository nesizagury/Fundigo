package com.example.FundigoApp.Customer.Social;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.FundigoApp.Chat.ChatActivity;
import com.example.FundigoApp.Chat.MessageRoomAdapter;
import com.example.FundigoApp.Chat.MessageRoomBean;
import com.example.FundigoApp.Chat.Room;
import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethods;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class CustomerMessageConversationsListActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    ImageButton mipo;
    ImageView notification;
    ListView listView;
    String customer_id;
    List<MessageRoomBean> list = new ArrayList<> ();
    ArrayList<Bitmap> pic = new ArrayList<> ();
    List<EventInfo> event_info_list = new ArrayList<EventInfo> ();
    private Handler handler = new Handler ();
    MessageRoomAdapter messageRoomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_message_producer);
        listView = (ListView) findViewById (R.id.listView_massge_producer);

        mipo = (ImageButton) findViewById (R.id.mipo_MassageProducer);
        notification = (ImageView) findViewById (R.id.notification_MassageProducer);
        if (GlobalVariables.IS_CUSTOMER_REGISTERED_USER) {
            customer_id = GlobalVariables.CUSTOMER_PHONE_NUM;
            messageRoomAdapter = new MessageRoomAdapter (this, list, pic);
            listView.setAdapter (messageRoomAdapter);
            listView.setOnItemClickListener (this);
            getMassage ();
            handler.postDelayed (runnable, 500);
        }
        mipo.setOnClickListener (this);
        notification.setOnClickListener (this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId () == R.id.notification_MassageProducer) {
            Intent MessageIntent = new Intent (CustomerMessageConversationsListActivity.this, MyNotifications.class);
            startActivity (MessageIntent);
        } else if (v.getId () == R.id.mipo_MassageProducer) {
            Intent mipoIntent = new Intent (CustomerMessageConversationsListActivity.this, Mipo.class);
            startActivity (mipoIntent);
        }
    }

    private void getMassage() {
        List<Room> listOfConversationWithProducer = new ArrayList<> ();
        ParseQuery<Room> query = ParseQuery.getQuery (Room.class);
        query.whereEqualTo ("customer_id", customer_id);
        query.orderByDescending ("createdAt");
        try {
            listOfConversationWithProducer = query.find ();
            updateLists (listOfConversationWithProducer);
        } catch (ParseException e) {
            e.printStackTrace ();
        }
    }

    private void getMassageInBackGround() {
        ParseQuery<Room> query = ParseQuery.getQuery (Room.class);
        query.whereEqualTo ("customer_id", customer_id);
        query.orderByDescending ("createdAt");
        query.findInBackground (new FindCallback<Room> () {
            public void done(List<Room> listOfConversationWithProducer, ParseException e) {
                if (e == null) {
                    updateLists (listOfConversationWithProducer);
                } else {
                    e.printStackTrace ();
                }
            }
        });
    }

    private void updateLists(List<Room> listOfConversationWithProducer) {
        List<MessageRoomBean> arr = new ArrayList<> ();
        ArrayList<Bitmap> pic_temp = new ArrayList<> ();
        List<EventInfo> event_info_list_temp = new ArrayList<EventInfo> ();
        for (int i = 0; i < listOfConversationWithProducer.size (); i++) {
            Room room = listOfConversationWithProducer.get (i);
            EventInfo eventInfo = StaticMethods.getEventFromObjID (room.getEventObjId (), GlobalVariables.ALL_EVENTS_DATA);
            pic_temp.add (eventInfo.getImageBitmap ());
            event_info_list_temp.add (eventInfo);
            arr.add (new MessageRoomBean (room.getLastMessage (),
                                                 eventInfo.getName (),
                                                 room.getProducer_id ()));
        }
        list.clear ();
        list.addAll (arr);
        pic.clear ();
        pic.addAll (pic_temp);
        event_info_list.clear ();
        event_info_list.addAll (event_info_list_temp);
        messageRoomAdapter.notifyDataSetChanged ();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent (this, ChatActivity.class);
        intent.putExtra ("index", event_info_list.get (i).getIndexInFullList ());
        intent.putExtra ("customer_phone", GlobalVariables.CUSTOMER_PHONE_NUM);
        startActivity (intent);
    }

    private Runnable runnable = new Runnable () {
        @Override
        public void run() {
            getMassageInBackGround ();
            handler.postDelayed (this, 500);
        }
    };

    @Override
    public void onPause() {
        super.onPause ();
        handler.removeCallbacks (runnable);
    }

    @Override
    public void onRestart() {
        super.onRestart ();
        handler.postDelayed (runnable, 500);
    }
}
