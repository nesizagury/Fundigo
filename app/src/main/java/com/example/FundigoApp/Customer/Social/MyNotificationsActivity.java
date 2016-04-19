package com.example.FundigoApp.Customer.Social;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethod.EventDataMethods;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class MyNotificationsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    ListView notificationList;
    ImageButton massage, mipo;
    List<ParseObject> pushObjectsList = new ArrayList<ParseObject> ();
    List<EventInfo> notificationsEventList = new ArrayList<EventInfo> ();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_my_notification);
        notificationList = (ListView) findViewById (R.id.listViewNotification);
        massage = (ImageButton) findViewById (R.id.Message_itemPush);
        mipo = (ImageButton) findViewById (R.id.Mipo_Push);

        getNotification ();
        NotificationAdapter adapter = new NotificationAdapter (this, notificationsEventList, pushObjectsList);

        notificationList.setAdapter (adapter);
        notificationList.setSelector (new ColorDrawable (Color.TRANSPARENT));
        notificationList.setOnItemClickListener (this);

        massage.setOnClickListener (this);
        mipo.setOnClickListener (this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId () == massage.getId ()) {
            Intent MessageIntent = new Intent (MyNotificationsActivity.this, CustomerMessageConversationsListActivity.class);
            startActivity (MessageIntent);
        } else if (v.getId () == mipo.getId ()) {
            Intent mipoIntent = new Intent (MyNotificationsActivity.this, MipoActivity.class);
            startActivity (mipoIntent);
        }
    }

    public void getNotification() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery ("Push");
        try {
            pushObjectsList = query.find ();
        } catch (ParseException e) {
            e.printStackTrace ();
        }
        List<ParseObject> tempPushObjectsList = new ArrayList<ParseObject> ();
        for (int i = 0; i < pushObjectsList.size (); i++) {
            ParseObject parseObject = pushObjectsList.get (i);
            EventInfo eventInfo = EventDataMethods.getEventFromObjID (parseObject.getString ("EvendId"),
                                                                             GlobalVariables.ALL_EVENTS_DATA);
            if(eventInfo == null){
                continue;
            } else {
                notificationsEventList.add (eventInfo);
                tempPushObjectsList.add(pushObjectsList.get (i));
            }
        }
        pushObjectsList.clear ();
        pushObjectsList.addAll (tempPushObjectsList);
    }

    @Override
    public void onItemClick(AdapterView<?> av, View view, int i, long l) {
        Intent intent = new Intent (MyNotificationsActivity.this, DetailedNotificationActivity.class);
        intent.putExtra ("Message", pushObjectsList.get (i).getString ("pushMessage").toString ());
        intent.putExtra ("EvendId", notificationsEventList.get (i).getParseObjectId ());
        startActivity (intent);
    }
}
