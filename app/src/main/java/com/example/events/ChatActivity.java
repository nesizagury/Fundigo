package com.example.events;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends Activity {

    private EditText etMessage;
    private ListView lvChat;
    private ArrayList<Message> mMessages;
    private ChatListAdapter mAdapter;
    private boolean mFirstLoad;
    private Handler handler = new Handler ();
    boolean isSaved = false;
    String body;
    String producer_id;
    String customer_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.requestWindowFeature (Window.FEATURE_NO_TITLE);
        setContentView (R.layout.activity_chat);

        Intent intent = getIntent ();
        Bundle b = getIntent ().getExtras ();
        producer_id = intent.getStringExtra ("producer_id");
        customer_id = intent.getStringExtra ("customer_id");
        etMessage = (EditText) findViewById (R.id.etMessage);
        lvChat = (ListView) findViewById (R.id.lvChat);
        mMessages = new ArrayList<Message> ();
        // Automatically scroll to the bottom when a data set change notification is received and only if the last item is already visible on screen. Don't scroll to the bottom otherwise.
        lvChat.setTranscriptMode (1);
        mFirstLoad = true;

        if (MainActivity.isCustomer) {
            mAdapter = new ChatListAdapter (ChatActivity.this, mMessages, customer_id);
        } else {
            mAdapter = new ChatListAdapter (ChatActivity.this, mMessages, producer_id);
        }
        lvChat.setAdapter (mAdapter);
        handler.postDelayed (runnable, 0);
    }

    public void sendMessage(View view) {
        body = etMessage.getText ().toString ();
        Message message = new Message ();
        message.setBody (body);
        if (MainActivity.isCustomer)
            message.setUserId (customer_id);
        else
            message.setUserId (producer_id);
        message.setCustomer (customer_id);
        message.setProducer (producer_id);
        try {
            message.save ();
        } catch (ParseException e) {
            e.printStackTrace ();
        }
        etMessage.setText ("");
        receiveNoBackGround (producer_id, customer_id);

        if (MainActivity.isCustomer && !isSaved) {
            deleteMessageRoomItem ();
        }
    }

    private void receiveMessage(String producer, final String customer) {
        ParseQuery<Message> query = ParseQuery.getQuery (Message.class);
        query.whereEqualTo ("producer", producer);
        query.orderByAscending ("createdAt");
        query.findInBackground (new FindCallback<Message> () {
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                    if (messages.size () > mMessages.size ()) {
                        mMessages.clear ();
                        for (int i = 0; i < messages.size (); i++) {
                            if (messages.get (i).getCustomer ().equals (customer)) {
                                mMessages.add (messages.get (i));
                            }
                        }
                        mAdapter.notifyDataSetChanged (); // update adapter
                        // Scroll to the bottom of the eventList on initial load
                        if (mFirstLoad) {
                            lvChat.setSelection (mAdapter.getCount () - 1);
                            mFirstLoad = false;
                        }
                    }
                } else {
                    e.printStackTrace ();
                }
            }
        });
    }

    private void receiveNoBackGround(String producer, final String customer) {
        ParseQuery<Message> query = ParseQuery.getQuery (Message.class);
        query.whereEqualTo ("producer", producer);

        query.orderByAscending ("createdAt");
        List<Message> messages = null;
        try {
            messages = query.find ();
            if (messages.size () > mMessages.size ()) {
                mMessages.clear ();
                for (int i = 0; i < messages.size (); i++) {
                    if (messages.get (i).getCustomer ().equals (customer))
                        mMessages.add (messages.get (i));

                }
                mAdapter.notifyDataSetChanged (); // update adapter
                // Scroll to the bottom of the eventList on initial load
                if (mFirstLoad) {
                    lvChat.setSelection (mAdapter.getCount () - 1);
                    mFirstLoad = false;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace ();
        }
    }

    private Runnable runnable = new Runnable () {
        @Override
        public void run() {
            refreshMessages ();
            handler.postDelayed (this, 300);
        }
    };

    private void refreshMessages() {
        receiveMessage (producer_id, customer_id);
    }

    public void deleteMessageRoomItem() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery ("Room");
        query.whereEqualTo ("ConversationId", customer_id + " - " + producer_id);
        query.orderByDescending ("createdAt");
        query.getFirstInBackground (new GetCallback<ParseObject> () {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    try {
                        object.delete ();
                    } catch (ParseException e1) {
                        e1.printStackTrace ();
                    }
                    object.saveInBackground ();
                }
                saveToMessagesRoom ();
            }
        });
    }

    private void saveToMessagesRoom() {
        Room room = new Room ();
        ParseACL parseAcl = new ParseACL ();
        parseAcl.setPublicReadAccess (true);
        parseAcl.setPublicWriteAccess (true);
        room.setACL (parseAcl);
        room.setCustomer_id (customer_id);
        room.setProducer_id (producer_id);
        room.setConversationId (customer_id + " - " + producer_id);
        room.saveInBackground (new SaveCallback () {
            @Override
            public void done(ParseException e) {
                isSaved = true;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause ();
        handler.removeCallbacks (runnable);
    }

    @Override
    public void onRestart() {
        super.onRestart ();
        handler.postDelayed (runnable, 300);
    }

}