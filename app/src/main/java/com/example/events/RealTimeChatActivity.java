package com.example.events;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class RealTimeChatActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private EditText etMessage;
    private ListView lvChat;
    private ArrayList<Message> mMessages;
    private RTCAdapter mAdapter;
    private boolean mFirstLoad;
    private Handler handler = new Handler();
    private String eventName;
    boolean isSaved = false;
    String body;
    String producer_id;
    String customer_id;
    private final static String TAG = "ChatActivity";
    private boolean rtc = false;
    private Button btnSend;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_chat);
        Intent intent = getIntent();

        producer_id = intent.getStringExtra("producer_id");
        customer_id = intent.getStringExtra("customer_id");
        eventName = intent.getStringExtra("eventName");

        Log.e(TAG, "producer_id "+"customer_id "+ customer_id+ "eventName "+eventName );

        etMessage = (EditText) findViewById(R.id.et_Message);
        lvChat = (ListView) findViewById(R.id.lv_Chat);
        btnSend = (Button) findViewById(R.id.btn_Send);
        setupMessagePosting();
        Log.e(TAG, "MainActivity.isCustomer " + MainActivity.isCustomer);
        handler.postDelayed(runnable, 500);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            refreshMessages();
            handler.postDelayed(this, 500);
        }
    };

    private void refreshMessages() {
        receiveMessage();
    }


    private void setupMessagePosting() {
        mMessages = new ArrayList<>();
        lvChat.setTranscriptMode(1);
        mFirstLoad = true;
        mAdapter = new RTCAdapter(this, customer_id, producer_id, mMessages);
        lvChat.setAdapter(mAdapter);
        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String body = etMessage.getText().toString();
                Message message = new Message();
                if (Constants.IS_PRODUCER) {
                    message.setUserId(producer_id);
                    message.setCustomer(producer_id);
                } else {
                    message.setUserId(customer_id);
                    message.setCustomer(customer_id);
                }
                message.setBody(body);
                message.setEventName(eventName);
                message.setProducer(producer_id);

                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        receiveMessage();
                    }
                });
                etMessage.setText("");
            }
        });
        lvChat.setOnItemClickListener(this);
    }

    private void receiveMessage() {
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.setLimit(50);
        query.whereEqualTo("eventName", eventName);
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<Message>() {
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                    mMessages.clear();
                    mMessages.addAll(messages);
                    mAdapter.notifyDataSetChanged();
                    if (mFirstLoad) {
                        lvChat.setSelection(mAdapter.getCount() - 1);
                        mFirstLoad = false;
                    }
                } else {
                    Log.d("message", "Error: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onResume() {
        super.onResume ();
        handler.postDelayed(runnable, 500);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        user = mMessages.get(position).getUserId();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Visit user facebook page");
       // builder.setMessage("How do you want to do it?");
        builder.setIcon(R.mipmap.ic_mor_information);
        builder.setPositiveButton("Go!", listener);
        builder.setNegativeButton("Cancel...", listener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        @Override

        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    Toast.makeText(RealTimeChatActivity.this, "This user was chosen "+ user, Toast.LENGTH_SHORT).show();

                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();

                    break;

            }
        }
    };
}



