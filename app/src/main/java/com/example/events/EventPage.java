package com.example.events;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EventPage extends Activity implements View.OnClickListener {
    ImageView save;
    String producer_id;
    String customer_id;
    private ImageView iv_share;
    private final static String TAG = "EventPage";
    private int image_id;
    static final int REQUEST_CODE_MY_PICK = 1;
    Intent intent;

    private String date;
    private String eventName;
    private String eventPlace;
    private Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_event_page);

        intent = getIntent ();
        image_id = intent.getIntExtra ("eventImage", R.mipmap.pic0);
        ImageView event_image = (ImageView) findViewById (R.id.eventPage_image);
        event_image.setImageResource (image_id);
        date = intent.getStringExtra ("eventDate");
        TextView event_date = (TextView) findViewById (R.id.eventPage_date);
        event_date.setText (date);
        eventName = intent.getStringExtra ("eventName");
        TextView event_name = (TextView) findViewById (R.id.eventPage_name);
        event_name.setText (eventName);
        String eventTags = intent.getStringExtra ("eventTags");
        TextView event_tags = (TextView) findViewById (R.id.eventPage_tags);
        event_tags.setText (eventTags);
        String eventPrice = intent.getStringExtra ("eventPrice");
        TextView event_price = (TextView) findViewById (R.id.priceEventPage);
        event_price.setText (eventPrice);
        String eventInfo = intent.getStringExtra ("eventInfo");
        TextView event_info = (TextView) findViewById (R.id.eventInfoEventPage);
        event_info.setText (eventInfo);
        eventPlace = intent.getStringExtra ("eventPlace");
        TextView event_place = (TextView) findViewById (R.id.eventPage_location);
        event_place.setText (eventPlace);
        Bundle b = getIntent ().getExtras ();
        producer_id = b.getString ("producer_id");
        customer_id = b.getString ("customer_id");
        iv_share = (ImageView) findViewById (R.id.imageEvenetPageView2);
        iv_share.setOnClickListener (this);

        ImageView imageEvenetPageView4 = (ImageView) findViewById (R.id.imageEvenetPageView4);
        imageEvenetPageView4.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent (EventPage.this, EventService.class);
                intent2.putExtra ("toilet", intent.getStringExtra ("toilet"));
                intent2.putExtra ("parking", intent.getStringExtra ("parking"));
                intent2.putExtra ("capacity", intent.getStringExtra ("capacity"));
                intent2.putExtra ("atm", intent.getStringExtra ("atm"));
                startActivity (intent2);
            }
        });
        save = (ImageView) findViewById (R.id.imageEvenetPageView3);
        checkIfChangeColorToSaveButtton ();
    }

    public void openTicketsPage(View view) {
        Bundle b = new Bundle ();
        Intent ticketsPageIntent = new Intent (EventPage.this, TicketsPage.class);
        Intent intentHere = getIntent ();
        ticketsPageIntent.putExtra ("eventName", intentHere.getStringExtra ("eventName"));
        ticketsPageIntent.putExtra ("eventPrice", intentHere.getStringExtra ("eventPrice"));
        ticketsPageIntent.putExtras (b);
        startActivity (ticketsPageIntent);
    }

    public void openChat(View view) {
        if (MainActivity.isCustomer) {
            Intent intent = new Intent (EventPage.this, ChatActivity.class);
            intent.putExtra ("producer_id", producer_id);
            intent.putExtra ("customer_id", customer_id);
            startActivity (intent);
        } else if (!MainActivity.isGuest)
            loadMessagesPage ();
    }

    private void loadMessagesPage() {
        List<Room> rList = new ArrayList<Room> ();
        List<MessageRoomBean> mrbList = new ArrayList<MessageRoomBean> ();
        ParseQuery<Room> query = ParseQuery.getQuery (Room.class);
        query.whereEqualTo ("producer_id", customer_id);
        query.orderByDescending ("createdAt");
        try {
            rList = query.find ();
        } catch (ParseException e) {
            e.printStackTrace ();
        }
        for (int i = 0; i < rList.size (); i++) {
            mrbList.add (new MessageRoomBean (0, null, "", rList.get (i).getCustomer_id (), producer_id));
        }
        Intent intent = new Intent (this, MessagesRoom.class);
        intent.putExtra ("array", (Serializable) mrbList);
        intent.putExtra ("producer_id", producer_id);
        startActivity (intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId ()) {
            case R.id.imageEvenetPageView2:
                Log.e (TAG, "" + image_id);
                try {
                    Bitmap largeIcon = BitmapFactory.decodeResource (getResources (), R.mipmap.pic0);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream ();
                    largeIcon.compress (Bitmap.CompressFormat.JPEG, 40, bytes);
                    File f = new File (Environment.getExternalStorageDirectory () + File.separator + "test.jpg");
                    f.createNewFile ();
                    FileOutputStream fo = new FileOutputStream (f);
                    fo.write (bytes.toByteArray ());
                    fo.close ();
                } catch (IOException e) {
                    e.printStackTrace ();
                }
                Intent intent = new Intent (Intent.ACTION_SEND);
                intent.setType ("image/jpeg");
                intent.putExtra (Intent.EXTRA_TEXT, "I`m going to " + eventName +
                                                            "\n" + "C u there at " + date + " !" +
                                                            "\n" + "At " + eventPlace +
                                                            "\n" + "http://eventpageURL.com/here");
                String imagePath = Environment.getExternalStorageDirectory () + File.separator + "test.jpg";
                File imageFileToShare = new File (imagePath);
                uri = Uri.fromFile (imageFileToShare);
                intent.putExtra (Intent.EXTRA_STREAM, uri);

                Intent intentPick = new Intent ();
                intentPick.setAction (Intent.ACTION_PICK_ACTIVITY);
                intentPick.putExtra (Intent.EXTRA_TITLE, "Launch using");
                intentPick.putExtra (Intent.EXTRA_INTENT, intent);
                startActivityForResult (intentPick, REQUEST_CODE_MY_PICK);
                break;
            case R.id.imageEvenetPageView3:
                handleSaveEventClicked (this.intent.getIntExtra ("press", 0),
                                               eventName,
                                               save);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && requestCode == REQUEST_CODE_MY_PICK) {
            String appName = data.getComponent ().flattenToShortString ();
            Log.e (TAG, "" + appName);
            if (appName.equals ("com.facebook.katana/com.facebook.composer.shareintent.ImplicitShareIntentHandlerDefaultAlias")) {
                ShareDialog shareDialog;
                shareDialog = new ShareDialog (this);

                ShareLinkContent linkContent = new ShareLinkContent.Builder ()
                                                       .setContentTitle ("I`m going to " + eventName)
                                                       .setImageUrl (Uri.parse ("https://lh3.googleusercontent.com/-V5wz7jKaQW8/VpvKq0rwEOI/AAAAAAAAB6Y/cZoicmGpQpc/s279-Ic42/pic0.jpg"))
                                                       .setContentDescription (
                                                                                      "C u there at " + date + " !" + "\n" + "At " + eventPlace)
                                                       .setContentUrl (Uri.parse ("http://eventpageURL.com/here"))
                                                       .build ();
                shareDialog.show (linkContent);
            } else {
                startActivity (data);
            }
        }
    }

    public void checkIfChangeColorToSaveButtton() {
        int index = intent.getIntExtra ("press", 0);
        if (MainActivity.events_data.get (index).getPress ()) save.setBackgroundColor (Color.RED);
        else save.setBackgroundColor (Color.GRAY);
    }

    public void handleSaveEventClicked(int index, String eventName, ImageView saveButton) {
        if (MainActivity.events_data.get (index).getPress ()) {
            MainActivity.events_data.get (index).setPress (false);
            save.setBackgroundColor (Color.GRAY);
            try {
                File inputFile = new File ("saves");
                File tempFile = new File ("myTempFile");
                BufferedReader reader = new BufferedReader (new FileReader (inputFile));
                BufferedWriter writer = new BufferedWriter (new FileWriter (tempFile));
                String lineToRemove = eventName;
                String currentLine;
                while ((currentLine = reader.readLine ()) != null) {
                    // trim newline when comparing with lineToRemove
                    String trimmedLine = currentLine.trim ();
                    if (trimmedLine.equals (lineToRemove)) continue;
                    writer.write (currentLine);
                }
                writer.close ();
                reader.close ();
                tempFile.renameTo (inputFile);
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            }
        } else {
            MainActivity.events_data.get (index).setPress (true);
            saveButton.setBackgroundColor (Color.RED);
            String filename = "saves";
            FileOutputStream outputStream;
            try {
                outputStream = openFileOutput (filename, Context.MODE_PRIVATE);
                outputStream.write (eventName.getBytes ());
                outputStream.close ();
            } catch (Exception e) {
                e.printStackTrace ();
            }
        }
    }
}
