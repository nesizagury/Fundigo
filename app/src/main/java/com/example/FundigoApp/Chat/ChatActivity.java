package com.example.FundigoApp.Chat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.FundigoApp.*;
import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.Verifications.Numbers;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends Activity {

    private EditText etMessage;
    private ListView lvChat;
    private ArrayList<MessageChat> mMessageChats;
    private com.example.FundigoApp.Chat.MessageAdapter mAdapter;
    private boolean mFirstLoad;
    private Handler handler = new Handler ();
    boolean isSaved = false;
    String body;
    String producer_id;
    String customer_id;
    ImageView profileImage;
    Button profileName;
    Button profileFaceBook;
    String faceBookId;
    String eventName;
    EventInfo eventInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.requestWindowFeature (Window.FEATURE_NO_TITLE);
        setContentView (R.layout.activity_main_chat);

        profileImage = (ImageView) findViewById (R.id.profileImage_chat);
        profileName = (Button) findViewById (R.id.ProfileName_chat);
        profileFaceBook = (Button) findViewById (R.id.ProfileFacebook_chat);
        Intent intent = getIntent ();
        producer_id = intent.getStringExtra ("producer_id");
        customer_id = intent.getStringExtra ("customer_id");
        int eventIndex = intent.getIntExtra ("index", 0);
        eventInfo = com.example.FundigoApp.MainActivity.all_events_data.get (eventIndex);
        eventName = eventInfo.getName ();
        if (Constants.IS_PRODUCER) {
            profileName.setText (customer_id);
            getUserDetailsFromParse ();
        } else {
            profileName.setText (eventName + " (Chat with Producer)");
            setEventInfo (eventInfo.getImageId ());
        }
        etMessage = (EditText) findViewById (R.id.etMessageChat);
        lvChat = (ListView) findViewById (R.id.messageListviewChat);
        mMessageChats = new ArrayList<MessageChat> ();
        // Automatically scroll to the bottom when a data set change notification is received and only if the last item is already visible on screen. Don't scroll to the bottom otherwise.
        lvChat.setTranscriptMode (1);
        mFirstLoad = true;
        mAdapter = new com.example.FundigoApp.Chat.MessageAdapter (this, mMessageChats);

        lvChat.setAdapter (mAdapter);
        handler.postDelayed (runnable, 0);
    }

    public void sendMessage(View view) {
        body = etMessage.getText ().toString ();
        Message message = new Message ();
        message.setBody (body);
        if (com.example.FundigoApp.MainActivity.isCustomer) {
            message.setUserId (customer_id);
        } else {
            message.setUserId (producer_id);
        }
        message.setCustomer (customer_id);
        message.setProducer (producer_id);
        message.setEventObjectId (eventInfo.getParseObjectId ());
        try {
            message.save ();
        } catch (ParseException e) {
            e.printStackTrace ();
        }
        etMessage.setText ("");
        receiveNoBackGround (producer_id, customer_id);

        if (com.example.FundigoApp.MainActivity.isCustomer && !isSaved) {
            deleteMessageRoomItem ();
        }
    }

    private void receiveMessage(String producer, final String customer) {
        ParseQuery<Message> query = ParseQuery.getQuery (Message.class);
        query.whereEqualTo ("producer", producer);
        query.whereEqualTo ("customer", customer);
        query.whereEqualTo ("eventObjectId", eventInfo.getParseObjectId ());
        query.orderByAscending ("createdAt");
        query.findInBackground (new FindCallback<Message> () {
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                    if (messages.size () > mMessageChats.size ()) {
                        mMessageChats.clear ();
                        for (int i = 0; i < messages.size (); i++) {
                            if (messages.get (i).getCustomer ().equals (customer)) {
                                Message msg = messages.get (i);
                                String id = msg.getUserId ();
                                boolean isMe = false;
                                if (!Constants.IS_PRODUCER) {
                                    if (id.equals (customer_id)) {
                                        isMe = true;
                                    }
                                } else {
                                    if (id.equals (producer_id)) {
                                        isMe = true;
                                        id = "Prudocer # " + id;
                                    }
                                }
                                mMessageChats.add (new MessageChat (
                                                                           MessageChat.MSG_TYPE_TEXT,
                                                                           MessageChat.MSG_STATE_SUCCESS,
                                                                           id,
                                                                           "avatar",
                                                                           "Jerry",
                                                                           "avatar",
                                                                           msg.getBody (),
                                                                           isMe,
                                                                           true,
                                                                           msg.getCreatedAt ()));
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
        query.whereEqualTo ("customer", customer);
        query.whereEqualTo ("eventObjectId", eventInfo.getParseObjectId ());
        query.orderByAscending ("createdAt");
        List<Message> messages = null;
        try {
            messages = query.find ();
            if (messages.size () > mMessageChats.size ()) {
                mMessageChats.clear ();
                for (int i = 0; i < messages.size (); i++) {
                    if (messages.get (i).getCustomer ().equals (customer)) {
                        Message msg = messages.get (i);
                        String id = msg.getUserId ();
                        boolean isMe = false;
                        if (!Constants.IS_PRODUCER) {
                            if (id.equals (customer_id)) {
                                isMe = true;
                            } else {
                                id = "Prodouer # " + id;
                            }
                        } else {
                            if (id.equals (producer_id)) {
                                isMe = true;
                            } else {
                                id = "Customer  " + id;
                            }
                        }
                        mMessageChats.add (new MessageChat (
                                                                   MessageChat.MSG_TYPE_TEXT,
                                                                   MessageChat.MSG_STATE_SUCCESS,
                                                                   id,
                                                                   "avatar",
                                                                   "Jerry",
                                                                   "avatar",
                                                                   msg.getBody (),
                                                                   isMe,
                                                                   true,
                                                                   msg.getCreatedAt ()));
                    }
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

    private void getUserDetailsFromParse() {
        ParseQuery<Numbers> query = ParseQuery.getQuery (Numbers.class);
        query.whereEqualTo ("number", customer_id);
        List<Numbers> numbers = null;
        try {
            numbers = query.find ();
            if (numbers.size () > 0) {
                Numbers number = numbers.get (0);
                faceBookId = number.getFbId ();
                if (faceBookId == null || faceBookId.isEmpty ()) {
                    profileFaceBook.setText ("");
                    profileFaceBook.setClickable (false);
                }
                String picUrl = number.getFbUrl ();
                if (picUrl != null && !picUrl.isEmpty ()) {
                    Picasso.with (this).load (picUrl).into (profileImage);
                } else {
                    ParseFile imageFile;
                    byte[] data = null;
                    Bitmap bmp;
                    imageFile = (ParseFile) number.getImageFile ();
                    if (imageFile != null) {
                        try {
                            data = imageFile.getData ();
                        } catch (ParseException e1) {
                            e1.printStackTrace ();
                        }
                        bmp = BitmapFactory.decodeByteArray (data, 0, data.length);
                        profileImage.setImageBitmap (bmp);
                    }
                }
            } else {
                profileFaceBook.setText ("");
                profileFaceBook.setClickable (false);
            }
        } catch (ParseException e) {
            e.printStackTrace ();
        }
    }

    public void oOpenFacebookIntent(View view) {
        startActivity (getOpenFacebookIntent ());
    }

    public Intent getOpenFacebookIntent() {
        String facebookUrl = "https://www.facebook.com/" + faceBookId;
        try {
            getPackageManager ().getPackageInfo ("com.facebook.katana", 0);
            return new Intent (Intent.ACTION_VIEW, Uri.parse ("fb://facewebmodal/f?href=" + facebookUrl));
        } catch (Exception e) {
            return new Intent (Intent.ACTION_VIEW, Uri.parse ("https://www.facebook.com/app_scoped_user_id/" + faceBookId));
        }
    }

    private void setEventInfo(Bitmap bitmap) {
        profileFaceBook.setVisibility (View.GONE);
        float hight = TypedValue.applyDimension (TypedValue.COMPLEX_UNIT_DIP, 55, getResources ().getDisplayMetrics ());
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams (
                                                      0,
                                                      Math.round (hight));
        params.weight = 90.0f;
        profileImage.setLayoutParams (params);
        profileImage.setImageBitmap (bitmap);
    }
}