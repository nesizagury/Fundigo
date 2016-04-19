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
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethod.EventDataMethods;
import com.example.FundigoApp.StaticMethod.FileAndImageMethods;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class RealTimeChatActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private EditText editTextMessage;
    private ListView listViewChat;
    private ArrayList<MessageChat> chatMessagesList;
    private ArrayList<MsgRealTime> messagesRealTimeList;
    private MessageAdapter mAdapter;
    private boolean messageFirstLoad;
    private Handler handler = new Handler ();
    private String eventObjectId;
    String current_user_id;
    private Button buttonSend;
    private static String faceBookUserId;
    Button eventName;
    ImageView eventImage;
    EventInfo eventInfo;
    ImageLoader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_real_time_cahts);
        eventImage = (ImageView) findViewById (R.id.profileImage_rt_chat);
        eventName = (Button) findViewById (R.id.ProfileName_rt_chat);
        buttonSend = (Button) findViewById (R.id.btSend_rt_Chat);
        loader = FileAndImageMethods.getImageLoader (this);
        Intent intent = getIntent ();
        eventObjectId = intent.getStringExtra ("eventObjectId");
        eventInfo = EventDataMethods.getEventFromObjID (eventObjectId, GlobalVariables.ALL_EVENTS_DATA);
        loader.displayImage (eventInfo.getPicUrl (), eventImage);
        eventName.setText (eventInfo.getName () + "(" + getResources ().getString (R.string.real_time_chat) + ")");
        if (GlobalVariables.IS_CUSTOMER_REGISTERED_USER) {
            current_user_id = GlobalVariables.CUSTOMER_PHONE_NUM;
        } else if (GlobalVariables.IS_PRODUCER) {
            current_user_id = GlobalVariables.PRODUCER_PARSE_OBJECT_ID;
        }
        editTextMessage = (EditText) findViewById (R.id.etMessage_rt_Chat);
        listViewChat = (ListView) findViewById (R.id.messageListview_rt_Chat);
        chatMessagesList = new ArrayList<MessageChat> ();
        messagesRealTimeList = new ArrayList<MsgRealTime> ();
        // Automatically scroll to the bottom when a data set change notification is received and only if the last item is already visible on screen. Don't scroll to the bottom otherwise.
        listViewChat.setTranscriptMode (1);
        messageFirstLoad = true;
        mAdapter = new MessageAdapter (this, chatMessagesList, true);

        listViewChat.setAdapter (mAdapter);
        setupMessagePosting ();
        handler.postDelayed (runnable, 0);
    }

    private void setupMessagePosting() {
        buttonSend.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                String body = editTextMessage.getText ().toString ();
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences (RealTimeChatActivity.this);
                String name = sp.getString (GlobalVariables.FB_NAME, null);
                String pic_url = sp.getString (GlobalVariables.FB_PIC_URL, null);
                String fb_id = sp.getString (GlobalVariables.FB_ID, null);
                MsgRealTime message = new MsgRealTime ();
                message.setUserId (current_user_id);
                if (GlobalVariables.IS_PRODUCER) {
                    message.setIsProducer (true);
                } else if (GlobalVariables.IS_CUSTOMER_REGISTERED_USER) {
                    message.setIsProducer (false);
                }
                message.setBody (body);
                message.setEventObjectId (eventObjectId);
                if (name != null && pic_url != null && fb_id != null) {
                    message.setSenderName (name);
                    message.setPicUrl (pic_url);
                    message.setFbId (fb_id);
                }
                try {
                    message.save ();
                } catch (ParseException e) {
                    e.printStackTrace ();
                }
                editTextMessage.setText ("");
                getAllMessagesInMainThread ();
            }
        });
        listViewChat.setOnItemClickListener (this);
    }

    private Runnable runnable = new Runnable () {
        @Override
        public void run() {
            getAllMessagesInBackground ();
            handler.postDelayed (this, 500);
        }
    };

    private void getAllMessagesInMainThread() {
        ParseQuery<MsgRealTime> query = ParseQuery.getQuery (MsgRealTime.class);
        query.whereEqualTo ("eventObjectId", eventObjectId);
        query.orderByAscending ("createdAt");
        List<MsgRealTime> messages = null;
        try {
            messages = query.find ();
            getMessagesData (messages);
        } catch (ParseException e) {
            e.printStackTrace ();
        }
    }

    private void getAllMessagesInBackground() {
        ParseQuery<MsgRealTime> query = ParseQuery.getQuery (MsgRealTime.class);
        query.whereEqualTo ("eventObjectId", eventObjectId);
        query.orderByAscending ("createdAt");
        query.findInBackground (new FindCallback<MsgRealTime> () {
            public void done(List<MsgRealTime> messages, ParseException e) {
                if (e == null) {
                    getMessagesData (messages);
                } else {
                    e.printStackTrace ();
                }
            }
        });
    }

    private void getMessagesData(List<MsgRealTime> messages) {
        chatMessagesList.clear ();
        messagesRealTimeList.clear ();
        messagesRealTimeList.addAll (messages);
        for (int i = 0; i < messages.size (); i++) {
            MsgRealTime msg = messages.get (i);
            String id = msg.getUserId ();
            StringBuilder idStringBuilder = new StringBuilder ();
            boolean isMe = false;
            if (!GlobalVariables.IS_PRODUCER) {
                if (id.equals (GlobalVariables.CUSTOMER_PHONE_NUM)) {
                    isMe = true;
                } else if (id.equals (eventInfo.getProducerId ())) {
                    idStringBuilder.append ("Producer # " + id);
                } else {
                    idStringBuilder.append ("Customer # " + id);
                }
            } else {
                if (id.equals (GlobalVariables.PRODUCER_PARSE_OBJECT_ID)) {
                    isMe = true;
                } else {
                    idStringBuilder.append ("Customer # " + id);
                }
            }
            chatMessagesList.add (new MessageChat (
                                                          MessageChat.MSG_TYPE_TEXT,
                                                          MessageChat.MSG_STATE_SUCCESS,
                                                          idStringBuilder.toString (),
                                                          msg.getBody (),
                                                          isMe,
                                                          true,
                                                          msg.getCreatedAt ()));
        }
        mAdapter.notifyDataSetChanged (); // update adapter
        // Scroll to the bottom of the eventList on initial load
        if (messageFirstLoad) {
            listViewChat.setSelection (mAdapter.getCount () - 1);
            messageFirstLoad = false;
        }
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
        faceBookUserId = messagesRealTimeList.get (position).getFbId ();
        AlertDialog.Builder builder = new AlertDialog.Builder (this);
        builder.setTitle (R.string.visit_user_facebook);
        builder.setIcon (R.mipmap.ic_mor_information);
        builder.setPositiveButton (R.string.go, listener);
        builder.setNegativeButton (R.string.cancel, listener);

        AlertDialog dialog = builder.create ();
        dialog.show ();
    }

    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener () {
        @Override

        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    startActivity (getOpenFacebookIntent (faceBookUserId));
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
