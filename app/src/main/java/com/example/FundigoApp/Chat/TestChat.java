package com.example.FundigoApp.Chat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ListView;

import com.example.FundigoApp.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TestChat extends ActionBarActivity {

    private MessageInputToolBox box;
    private ListView listView;
    private MessageAdapter adapter;

    @SuppressLint("UseSparseArrays")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        setContentView (R.layout.activity_main_chat);

        initMessageInputToolBox ();

        initListView ();
    }

    /**
     * init MessageInputToolBox
     */
    @SuppressLint("ShowToast")
    private void initMessageInputToolBox() {
        //box = (MessageInputToolBox) findViewById(R.id.messageInputToolBox);
        box.setOnOperationListener (new OnOperationListener () {

            @Override
            public void send(String content) {

                System.out.println ("===============" + content);

                MessageChat messageChat = new MessageChat (0, 1, "Tom", "avatar", "Jerry", "avatar", content, true, true, new Date ());


                adapter.getData ().add (messageChat);
                listView.setSelection (listView.getBottom ());

                //Just demo
                createReplayMsg (messageChat);
            }

            @Override
            public void selectedFace(String content) {

                System.out.println ("===============" + content);
                MessageChat messageChat = new MessageChat (MessageChat.MSG_TYPE_FACE, MessageChat.MSG_STATE_SUCCESS, "Tomcat", "avatar", "Jerry", "avatar", content, true, true, new Date ());
                adapter.getData ().add (messageChat);
                listView.setSelection (listView.getBottom ());

                //Just demo
                createReplayMsg (messageChat);
            }


            @Override
            public void selectedFuncation(int index) {

                System.out.println ("===============" + index);

                switch (index) {
                    case 0:
                        //do some thing
                        break;
                    case 1:
                        //do some thing
                        break;

                    default:
                        break;
                }

            }

        });

        ArrayList<String> faceNameList = new ArrayList<String> ();
        for (int x = 1; x <= 10; x++) {
            faceNameList.add ("big" + x);
        }
        for (int x = 1; x <= 10; x++) {
            faceNameList.add ("big" + x);
        }

        ArrayList<String> faceNameList1 = new ArrayList<String> ();
        for (int x = 1; x <= 7; x++) {
            faceNameList1.add ("cig" + x);
        }


        ArrayList<String> faceNameList2 = new ArrayList<String> ();
        for (int x = 1; x <= 24; x++) {
            faceNameList2.add ("dig" + x);
        }

        Map<Integer, ArrayList<String>> faceData = new HashMap<Integer, ArrayList<String>> ();
        faceData.put (R.drawable.em_cate_magic, faceNameList2);
        faceData.put (R.drawable.em_cate_rib, faceNameList1);
        faceData.put (R.drawable.em_cate_duck, faceNameList);
        box.setFaceData (faceData);


        List<Option> functionData = new ArrayList<Option> ();
        for (int x = 0; x < 5; x++) {
            Option takePhotoOption = new Option (this, "Take", R.drawable.take_photo);
            Option galleryOption = new Option (this, "Gallery", R.drawable.gallery);
            functionData.add (galleryOption);
            functionData.add (takePhotoOption);
        }
        box.setFunctionData (functionData);
    }


    private void initListView() {
        listView = (ListView) findViewById (R.id.messageListviewChat);

        //create Data
        MessageChat messageChat = new MessageChat (MessageChat.MSG_TYPE_TEXT, MessageChat.MSG_STATE_SUCCESS, "Tom", "avatar", "Jerry", "avatar", "Hi", false, true, new Date (System.currentTimeMillis () - (1000 * 60 * 60 * 24) * 8));
        MessageChat messageChat1 = new MessageChat (MessageChat.MSG_TYPE_TEXT, MessageChat.MSG_STATE_SUCCESS, "Tom", "avatar", "Jerry", "avatar", "Hello World", true, true, new Date (System.currentTimeMillis () - (1000 * 60 * 60 * 24) * 8));
        MessageChat messageChat2 = new MessageChat (MessageChat.MSG_TYPE_PHOTO, MessageChat.MSG_STATE_SUCCESS, "Tom", "avatar", "Jerry", "avatar", "device_2014_08_21_215311", false, true, new Date (System.currentTimeMillis () - (1000 * 60 * 60 * 24) * 7));
        MessageChat messageChat3 = new MessageChat (MessageChat.MSG_TYPE_TEXT, MessageChat.MSG_STATE_SUCCESS, "Tom", "avatar", "Jerry", "avatar", "Haha", true, true, new Date (System.currentTimeMillis () - (1000 * 60 * 60 * 24) * 7));
        MessageChat messageChat4 = new MessageChat (MessageChat.MSG_TYPE_FACE, MessageChat.MSG_STATE_SUCCESS, "Tom", "avatar", "Jerry", "avatar", "big3", false, true, new Date (System.currentTimeMillis () - (1000 * 60 * 60 * 24) * 7));
        MessageChat messageChat5 = new MessageChat (MessageChat.MSG_TYPE_FACE, MessageChat.MSG_STATE_SUCCESS, "Tom", "avatar", "Jerry", "avatar", "big2", true, true, new Date (System.currentTimeMillis () - (1000 * 60 * 60 * 24) * 6));
        MessageChat messageChat6 = new MessageChat (MessageChat.MSG_TYPE_TEXT, MessageChat.MSG_STATE_FAIL, "Tom", "avatar", "Jerry", "avatar", "test send fail", true, false, new Date (System.currentTimeMillis () - (1000 * 60 * 60 * 24) * 6));
        MessageChat messageChat7 = new MessageChat (MessageChat.MSG_TYPE_TEXT, MessageChat.MSG_STATE_SENDING, "Tom", "avatar", "Jerry", "avatar", "test sending", true, true, new Date (System.currentTimeMillis () - (1000 * 60 * 60 * 24) * 6));

        List<MessageChat> messageChats = new ArrayList<MessageChat> ();
        messageChats.add (messageChat);
        messageChats.add (messageChat1);
        messageChats.add (messageChat2);
        messageChats.add (messageChat3);
        messageChats.add (messageChat4);
        messageChats.add (messageChat5);
        messageChats.add (messageChat6);
        messageChats.add (messageChat7);

        adapter = new MessageAdapter (this, messageChats, false);
        listView.setAdapter (adapter);
        adapter.notifyDataSetChanged ();

        listView.setOnTouchListener (new OnTouchListener () {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                box.hide ();
                return false;
            }
        });

    }


    private void createReplayMsg(MessageChat messageChat) {

        final MessageChat reMessageChat = new MessageChat (messageChat.getType (), 1, "Tom", "avatar", "Jerry", "avatar",
                                                                  messageChat.getType () == 0 ? "Re:" + messageChat.getContent () : messageChat.getContent (),
                                                                  false, true, new Date ()
        );
        new Thread (new Runnable () {

            @Override
            public void run() {
                try {
                    Thread.sleep (1000 * (new Random ().nextInt (3) + 1));
                    runOnUiThread (new Runnable () {

                        @Override
                        public void run() {
                            adapter.getData ().add (reMessageChat);
                            listView.setSelection (listView.getBottom ());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace ();
                }
            }
        }).start ();
    }

}
