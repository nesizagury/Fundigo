package com.example.FundigoApp.Customer.Social;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.Events.EventPageActivity;
import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethod.EventDataMethods;

public class DetailedNotificationActivity extends AppCompatActivity {

    Intent intent;
    TextView message, date, eventObjectId;
    Button goToEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_customer_push_page);
        intent = getIntent ();
        message = (TextView) findViewById (R.id.eventInfoEventPage);
        date = (TextView) findViewById (R.id.PushPage_date);
        eventObjectId = (TextView) findViewById (R.id.PushPage_eventName);
        goToEvent = (Button) findViewById (R.id.Button_pushPage);

        message.setText (intent.getExtras ().getString ("Message"));
        final EventInfo eventInfo = EventDataMethods.getEventFromObjID (intent.getExtras ().getString ("EvendId"),
                                                                               GlobalVariables.ALL_EVENTS_DATA);
        eventObjectId.setText (eventInfo.getName ());
        date.setText (eventInfo.getDateAsString ());
        final Intent intent = new Intent (this, EventPageActivity.class);
        goToEvent.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle ();
                EventDataMethods.onEventItemClick (eventInfo.getIndexInFullList (),
                                                          GlobalVariables.ALL_EVENTS_DATA,
                                                          intent);
                intent.putExtras (b);
                startActivity (intent);
            }
        });
    }
}
