package com.example.events;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class PushPage extends AppCompatActivity implements View.OnClickListener {

    Intent intent;
    TextView message, date, eventName;
    Button goToEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_page);
        intent = getIntent();
        message = (TextView) findViewById(R.id.eventInfoEventPage);
        date = (TextView) findViewById(R.id.PushPage_date);
        eventName = (TextView) findViewById(R.id.PushPage_eventName);
        goToEvent = (Button) findViewById(R.id.Button_pushPage);

        message.setText(intent.getExtras().getString("Message"));
        date.setText(intent.getExtras().getString("Date"));
        eventName.setText(intent.getExtras().getString("eventName"));
        goToEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = true;
                int index = 0;
                if (v.getId()==goToEvent.getId()){
                    for (int i = 0; i < MainActivity.all_events_data.size() && flag; i++)
                    {
                        if (MainActivity.all_events_data.get(i).getName().equals(eventName)) {
                            flag = false;
                            index = i;
                        }
                    }
                    overToEvent(index);
                    Toast.makeText(PushPage.this,"hello="+index,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        Toast.makeText(this, "Onclick="+MainActivity.all_events_data.size(), Toast.LENGTH_SHORT).show();
        boolean flag = true;
        int index = 0;
        if (v.getId()==goToEvent.getId()){
                Toast.makeText(this, "before for", Toast.LENGTH_SHORT).show();
                for (int i = 0; i < MainActivity.all_events_data.size() && flag; i++)
                {
                    if (MainActivity.all_events_data.get(i).getName().equals(eventName)) {
                        Toast.makeText(this, eventName+"="+MainActivity.all_events_data.get(i).getName(), Toast.LENGTH_SHORT).show();
                        flag = false;
                        index = i;
                    }
                }
                Toast.makeText(this, "after for", Toast.LENGTH_SHORT).show();
                overToEvent(index);
        }
    }

    private void overToEvent(int i) {
        Toast.makeText(this,"overToEvent",Toast.LENGTH_SHORT).show();
        //Bundle b = new Bundle();
        Intent intent = new Intent(PushPage.this, EventPage.class);
        intent.putExtra("eventImage", MainActivity.all_events_data.get(i).getImageId());
        intent.putExtra("eventDate", MainActivity.all_events_data.get(i).getDate());
        intent.putExtra("eventName", MainActivity.all_events_data.get(i).getName());
        intent.putExtra("eventTags", MainActivity.all_events_data.get(i).getTags());
        intent.putExtra("eventPrice", MainActivity.all_events_data.get(i).getPrice());
        intent.putExtra("eventInfo", MainActivity.all_events_data.get(i).getInfo());
        intent.putExtra("eventPlace", MainActivity.all_events_data.get(i).getPlace());
        //b.putInt("userIndex", i);
        //intent.putExtras(b);
        startActivity(intent);
    }
}
