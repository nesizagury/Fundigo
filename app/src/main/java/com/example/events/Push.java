package com.example.events;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParsePush;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Push extends AppCompatActivity implements View.OnClickListener {

    EditText editText;
    Button send;
    String eventName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);
        eventName=getIntent().getExtras().get("id").toString();
        editText = (EditText) findViewById(R.id.editTextPush);
        send = (Button) findViewById(R.id.sendPush);
        send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendPush:
                if (editText.getText().length() != 0)
                {
                    Toast.makeText(this, editText.getText(), Toast.LENGTH_SHORT).show();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss");
                    String currentDateandTime = sdf.format(new Date());
                    ParsePush push = new ParsePush();
                    ParseObject query = new ParseObject("Push");
                    push.setMessage(editText.getText()+"(" + currentDateandTime + ")");
                    try
                    {
                        push.send();
                        query.put("pushMessage", editText.getText().toString());
                        query.put("Date", currentDateandTime);
                        query.put("EvendId", eventName);
                        query.save();
                    }
                    catch (com.parse.ParseException e)
                    {
                        e.getStackTrace();
                    }

                } else {
                    Toast.makeText(this, "Enter text", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }
}
