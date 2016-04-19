package com.example.FundigoApp.Producer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.FundigoApp.R;
import com.parse.ParseObject;
import com.parse.ParsePush;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProducerSendPuchActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editText;
    Button send;
    String eventObjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_producer_push);
        eventObjectId = getIntent ().getExtras ().get ("id").toString ();
        editText = (EditText) findViewById (R.id.editTextPush);
        send = (Button) findViewById (R.id.sendPush);
        send.setOnClickListener (this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId ()) {
            case R.id.sendPush:
                if (editText.getText ().length () != 0) {
                    Toast.makeText (this, editText.getText (), Toast.LENGTH_SHORT).show ();
                    SimpleDateFormat sdf = new SimpleDateFormat ("dd/MM/yyyy_HH:mm:ss");
                    String currentDateandTime = sdf.format (new Date ());
                    ParsePush.subscribeInBackground ("a" + eventObjectId);
                    ParsePush push = new ParsePush ();
                    push.setChannel ("a" + eventObjectId);
                    ParseObject query = new ParseObject ("Push");
                    push.setMessage (editText.getText () + "(" + currentDateandTime + ")");
                    try {
                        push.send ();
                        query.put ("pushMessage", editText.getText ().toString ());
                        query.put ("Date", currentDateandTime);
                        query.put ("EvendId", eventObjectId);
                        query.save ();
                        ParsePush.unsubscribeInBackground ("a" + eventObjectId);
                    } catch (com.parse.ParseException e) {
                        e.getStackTrace ();
                    }
                } else {
                    Toast.makeText (this, "Enter text", Toast.LENGTH_SHORT).show ();
                }
                break;
        }
    }
}
