package com.example.events;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class Mipo extends AppCompatActivity implements View.OnClickListener
{
    ImageButton massage;
    ImageView notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mipo);

        massage=(ImageButton)findViewById(R.id.message_Mipo);
        notification=(ImageView)findViewById(R.id.notification_Mipo);

        massage.setOnClickListener(this);
        notification.setOnClickListener(this);
    }


    @Override
    public void onClick(View v)
    {
        int id=v.getId();
        Intent intent;
        if(id==massage.getId())
        {
            intent=new Intent(Mipo.this,MessageProducer.class);
            startActivity(intent);
        }
        else if(id==notification.getId())
        {
            intent=new Intent(Mipo.this,MyNotification.class);
            startActivity(intent);
        }
    }
}
