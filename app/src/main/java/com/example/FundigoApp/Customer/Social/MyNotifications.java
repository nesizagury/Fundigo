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
import com.example.FundigoApp.StaticMethods;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class MyNotifications extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    ListView notificationList;
    ImageButton massage, mipo;
    List<ParseObject> pustObjectsList = new ArrayList<ParseObject> ();
    List<EventInfo> notificationsEventList = new ArrayList<EventInfo> ();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_my_notification);
        notificationList = (ListView) findViewById (R.id.listViewNotification);
        massage = (ImageButton) findViewById (R.id.Message_itemPush);
        mipo = (ImageButton) findViewById (R.id.Mipo_Push);

        getNotification ();
        NotificationAdapter adapter = new NotificationAdapter (this, notificationsEventList, pustObjectsList);

        notificationList.setAdapter (adapter);
        notificationList.setSelector (new ColorDrawable (Color.TRANSPARENT));
        notificationList.setOnItemClickListener (this);

        massage.setOnClickListener (this);
        mipo.setOnClickListener (this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId () == massage.getId ()) {
            Intent MessageIntent = new Intent (MyNotifications.this, CustomerMessageConversationsListActivity.class);
            startActivity (MessageIntent);
        } else if (v.getId () == mipo.getId ()) {
            Intent mipoIntent = new Intent (MyNotifications.this, Mipo.class);
            startActivity (mipoIntent);
        }
    }

    public void getNotification() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery ("Push");
        try {
            pustObjectsList = query.find ();
        } catch (ParseException e) {
            e.printStackTrace ();
        }
        for (int i = pustObjectsList.size () - 1; i >= 0; i--) {
            ParseObject parseObject = pustObjectsList.get (i);
            EventInfo eventInfo = StaticMethods.getEventFromObjID (parseObject.getString ("EvendId"),
                                                                          GlobalVariables.ALL_EVENTS_DATA);
            notificationsEventList.add (eventInfo);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> av, View view, int i, long l) {
        Bundle b = new Bundle ();
        Intent intent = new Intent (MyNotifications.this, DetailedNotificationPage.class);
        intent.putExtra ("Message", pustObjectsList.get (i).getString ("pushMessage").toString ());
        intent.putExtra ("Date", notificationsEventList.get (i).getDate ());
        intent.putExtra ("EvendId", notificationsEventList.get (i).getParseObjectId ());

        startActivity (intent);
    }
}
