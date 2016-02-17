package com.example.FundigoApp.Chat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.FundigoApp.Constants;
import com.example.FundigoApp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class RealTimeChatActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private EditText etMessage;
    private ListView lvChat;
    private ArrayList<MsgRealTime> mMessages;
    private RTCAdapter mAdapter;
    private boolean mFirstLoad;
    private Handler handler = new Handler ();
    private String eventObjectId;
    String producer_id;
    String customer_id;
    private Button btnSend;
    private static String fbId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_real_time_chat);
        Intent intent = getIntent ();

        producer_id = intent.getStringExtra ("producer_id");
        customer_id = intent.getStringExtra ("customer_id");
        eventObjectId = intent.getStringExtra ("eventObjectId");

        etMessage = (EditText) findViewById (R.id.et_Message);
        lvChat = (ListView) findViewById (R.id.lv_Chat);
        btnSend = (Button) findViewById (R.id.btn_Send);
        setupMessagePosting ();
        handler.postDelayed (runnable, 500);
    }

    private Runnable runnable = new Runnable () {
        @Override
        public void run() {
            refreshMessages ();
            handler.postDelayed (this, 500);
        }
    };

    private void refreshMessages() {
        receiveMessage ();
    }

    private void setupMessagePosting() {
        mMessages = new ArrayList<MsgRealTime> ();
        lvChat.setTranscriptMode (1);
        mFirstLoad = true;
        mAdapter = new RTCAdapter (this, customer_id, producer_id, mMessages);
        lvChat.setAdapter (mAdapter);
        btnSend.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                String body = etMessage.getText ().toString ();
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences (RealTimeChatActivity.this);
                String name = sp.getString (Constants.FB_NAME, null);
                String pic_url = sp.getString (Constants.FB_PIC_URL, null);
                String fb_id = sp.getString (Constants.FB_ID, null);
                MsgRealTime message = new MsgRealTime ();
                if (Constants.IS_PRODUCER) {
                    message.setUserId (producer_id);
                    message.setCustomer (producer_id);
                    message.setIsProducer (true);
                } else {
                    message.setUserId (customer_id);
                    message.setCustomer (customer_id);
                    message.setIsProducer (false);
                }
                message.setBody (body);
                message.setEventObjectId (eventObjectId);
                message.setProducer (producer_id);
                if (name != null && pic_url != null && fb_id != null) {
                    message.setSenderName (name);
                    message.setPicUrl (pic_url);
                    message.setFbId (fb_id);
                }

                message.saveInBackground (new SaveCallback () {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            receiveMessage ();
                        } else {
                            e.printStackTrace ();
                        }
                    }
                });
                etMessage.setText ("");
            }
        });
        lvChat.setOnItemClickListener (this);
    }

    private void receiveMessage() {
        ParseQuery<MsgRealTime> query = ParseQuery.getQuery (MsgRealTime.class);
        query.setLimit (50);
        query.whereEqualTo ("eventObjectId", eventObjectId);
        query.orderByAscending ("createdAt");
        query.findInBackground (new FindCallback<MsgRealTime> () {
            public void done(List<MsgRealTime> messages, ParseException e) {
                if (e == null) {
                    mMessages.clear ();
                    mMessages.addAll (messages);
                    mAdapter.notifyDataSetChanged ();
                    if (mFirstLoad) {
                        lvChat.setSelection (mAdapter.getCount () - 1);
                        mFirstLoad = false;
                    }
                } else {
                    Log.d ("message", "Error: " + e.getMessage ());
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause ();
        handler.removeCallbacks (runnable);
    }

    @Override
    public void onResume() {
        super.onResume ();
        handler.postDelayed (runnable, 500);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        fbId = mMessages.get (position).getFbId ();
        AlertDialog.Builder builder = new AlertDialog.Builder (this);
        builder.setTitle ("Visit user facebook page");
        builder.setIcon (R.mipmap.ic_mor_information);
        builder.setPositiveButton ("Go!", listener);
        builder.setNegativeButton ("Cancel...", listener);

        AlertDialog dialog = builder.create ();
        dialog.show ();
    }

    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener () {
        @Override

        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    startActivity (getOpenFacebookIntent (fbId));
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss ();
                    break;
            }
        }
    };


    public Intent getOpenFacebookIntent(String userId) {
        String facebookUrl = "https://www.facebook.com/" + userId;
        try {
            getPackageManager ().getPackageInfo ("com.facebook.katana", 0);
            return new Intent (Intent.ACTION_VIEW, Uri.parse ("fb://facewebmodal/f?href=" + facebookUrl));
        } catch (Exception e) {
            return new Intent (Intent.ACTION_VIEW, Uri.parse ("https://www.facebook.com/app_scoped_user_id/" + userId));
        }
    }
}
