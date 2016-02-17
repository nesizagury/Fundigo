package com.example.events;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EventPage extends Activity implements View.OnClickListener {
    ImageView save;
    Button producerPush;
    String producer_id;
    String customer_id;
    private ImageView iv_share;
    private ImageView iv_chat;
    static final int REQUEST_CODE_MY_PICK = 1;
    Intent intent;

    private String date;
    private String eventName;
    private String eventPlace;
    private Uri uri;
    private String driving;
    private String walking;
    private boolean walkNdrive = false;
    private int walkValue;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);

        intent = getIntent ();
        if (getIntent ().getByteArrayExtra ("eventImage") != null) {
            byte[] byteArray = getIntent ().getByteArrayExtra ("eventImage");
            bitmap = BitmapFactory.decodeByteArray (byteArray, 0, byteArray.length);
            ImageView event_image = (ImageView) findViewById (R.id.eventPage_image);
            event_image.setImageBitmap (bitmap);
        }
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
        iv_chat = (ImageView) findViewById (R.id.imageEvenetPageView5);
        iv_chat.setOnClickListener (this);

        ImageView imageEvenetPageView4 = (ImageView) findViewById (R.id.imageEvenetPageView4);
        imageEvenetPageView4.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent (EventPage.this, EventService.class);
                intent2.putExtra ("toilet", intent.getStringExtra ("toilet"));
                intent2.putExtra ("parking", intent.getStringExtra ("parking"));
                intent2.putExtra ("capacity", intent.getStringExtra ("capacity"));
                intent2.putExtra ("atm", intent.getStringExtra ("atm"));
                intent2.putExtra ("driving", driving);
                intent2.putExtra ("walking", walking);
                intent2.putExtra ("walkValue", walkValue);
                startActivity (intent2);
            }
        });
        save = (ImageView) findViewById (R.id.imageEvenetPageView3);
        producerPush=(Button)findViewById(R.id.pushButton);
       // producerPush.setVisibility(View.INVISIBLE);
        producerPush.setVisibility(View.VISIBLE);
        producerPush.setOnClickListener(this);
        if(MainActivity.producerId!=null)
        {
            Toast.makeText(this,"enter",Toast.LENGTH_SHORT).show();
            boolean flag =true;
            for(int i=0 ; i < MainActivity.all_events_data.size() && flag ; i++)
            {
                if(MainActivity.all_events_data.get(i).producerId.equals(MainActivity.producerId))
                {
                    if(MainActivity.all_events_data.get(i).getName().equals(eventName))flag=false;
                }
            }

            if(!flag)
            {
                producerPush.setVisibility(View.VISIBLE);
                producerPush.setOnClickListener(this);
            }
        }
        checkIfChangeColorToSaveButtton ();
        String even_addr = eventPlace;
        even_addr = even_addr.replace (",", "");
        even_addr = even_addr.replace (" ", "+");
        new GetEventDis2 (EventPage.this).execute (

                                                          "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + getLocation2 ().getLatitude () + "," + getLocation2 ().getLongitude () + "&destinations=" + even_addr + "+Israel&mode=driving&language=en-EN&key=AIzaSyAuwajpG7_lKGFWModvUIoMqn3vvr9CMyc");
        new GetEventDis2 (EventPage.this).execute (
                                                          "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + getLocation2 ().getLatitude () + "," + getLocation2 ().getLongitude () + "&destinations=" + even_addr + "+Israel&mode=walking&language=en-EN&key=AIzaSyAuwajpG7_lKGFWModvUIoMqn3vvr9CMyc");
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

    private void loadMessagesPage() {
        List<Room> rList = new ArrayList<Room> ();
        List<MessageRoomBean> mrbList = new ArrayList<MessageRoomBean> ();
        ParseQuery<Room> query = ParseQuery.getQuery (Room.class);
        query.whereEqualTo ("producer_id", producer_id);
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
                handleSaveEventClicked (this.intent.getIntExtra ("index", 0));
                break;
            case R.id.imageEvenetPageView5:
                AlertDialog.Builder builder = new AlertDialog.Builder (this);
                builder.setTitle ("You can get more info\nabout the event!");
                builder.setMessage ("How do you want to do it?");
                builder.setPositiveButton ("Send message to producer", listener);
                builder.setNegativeButton ("Real Time Chat", listener);
                builder.setNeutralButton ("Cancel...", listener);
                AlertDialog dialog = builder.create ();
                dialog.show ();
                TextView messageText = (TextView) dialog.findViewById (android.R.id.message);
                messageText.setGravity (Gravity.CENTER);
                break;
            case R.id.pushButton:
                Intent pushIntent=new Intent(EventPage.this,Push.class);
                pushIntent.putExtra("id",eventName);
                startActivity(pushIntent);
                break;
        }
    }

    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener () {
        @Override

        public void onClick(DialogInterface dialog, int which) {
            Intent intent;
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    if (MainActivity.isCustomer) {
                        intent = new Intent (EventPage.this, ChatActivity.class);
                        intent.putExtra ("producer_id", producer_id);
                        intent.putExtra ("customer_id", customer_id);
                        startActivity (intent);
                    } else if (!MainActivity.isGuest) {
                        loadMessagesPage ();
                    }
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    intent = new Intent (EventPage.this, RealTimeChatActivity.class);
                    intent.putExtra ("customer_id", customer_id);
                    intent.putExtra ("producer_id", producer_id);
                    intent.putExtra ("eventName", eventName);
                    startActivity (intent);
                    break;
                case DialogInterface.BUTTON_NEUTRAL:
                    dialog.dismiss ();
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && requestCode == REQUEST_CODE_MY_PICK) {
            String appName = data.getComponent ().flattenToShortString ();
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
        int index = intent.getIntExtra ("index", 0);
        if (MainActivity.all_events_data.get (index).getIsSaved ())
            save.setImageResource (R.mipmap.whsavedd);
        else {
            save.setImageResource (R.mipmap.wh);
        }
    }

    public void handleSaveEventClicked(int index) {
        EventInfo event = MainActivity.all_events_data.get (index);
        final String eventName = event.getName ();
        final Context context = this;
        if (MainActivity.all_events_data.get (index).getIsSaved ()) {
            MainActivity.all_events_data.get (index).setIsSaved (false);
            save.setImageResource (R.mipmap.wh);
            Toast.makeText (this, "You unSaved this event", Toast.LENGTH_SHORT).show ();
            AsyncTask.execute(new Runnable () {
                @Override
                public void run() {
                    try {
                        getApplicationContext ().deleteFile ("temp");
                        InputStream inputStream = getApplicationContext ().openFileInput ("saves");
                        OutputStream outputStreamTemp = getApplicationContext ().openFileOutput ("temp", Context.MODE_PRIVATE);
                        BufferedReader bufferedReader = new BufferedReader (new InputStreamReader (inputStream));
                        BufferedWriter bufferedWriter = new BufferedWriter (new OutputStreamWriter (outputStreamTemp));
                        String lineToRemove = eventName;
                        String currentLine;
                        while ((currentLine = bufferedReader.readLine ()) != null) {
                            // trim newline when comparing with lineToRemove
                            String trimmedLine = currentLine.trim ();
                            if (trimmedLine.equals (lineToRemove)) continue;
                            else {
                                bufferedWriter.write (currentLine);
                                bufferedWriter.write (System.getProperty ("line.separator"));
                            }
                        }
                        bufferedReader.close ();
                        bufferedWriter.close ();
                        getApplicationContext ().deleteFile ("saves");
                        inputStream = getApplicationContext ().openFileInput ("temp");
                        outputStreamTemp = getApplicationContext ().openFileOutput ("saves", Context.MODE_PRIVATE);
                        bufferedReader = new BufferedReader (new InputStreamReader (inputStream));
                        bufferedWriter = new BufferedWriter (new OutputStreamWriter (outputStreamTemp));
                        while ((currentLine = bufferedReader.readLine ()) != null) {
                            bufferedWriter.write (currentLine);
                            bufferedWriter.write (System.getProperty ("line.separator"));
                        }
                        bufferedReader.close ();
                        bufferedWriter.close ();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace ();
                    } catch (IOException e) {
                        e.printStackTrace ();
                    }
                }
            });
        } else {
            MainActivity.all_events_data.get (index).setIsSaved (true);
            save.setImageResource (R.mipmap.whsavedd);
            Toast.makeText (this, "You Saved this event", Toast.LENGTH_SHORT).show ();
            AsyncTask.execute (new Runnable () {
                @Override
                public void run() {
                    try {
                        OutputStream outputStream = getApplicationContext ().openFileOutput ("saves", Context.MODE_APPEND + Context.MODE_PRIVATE);
                        outputStream.write (eventName.getBytes ());
                        outputStream.write (System.getProperty ("line.separator").getBytes ());
                        outputStream.close ();
                    } catch (IOException e) {
                        e.printStackTrace ();
                    }
                }
            });
        }
        MainActivity.eventsListAdapter.notifyDataSetChanged ();
        if(MainActivity.savedAcctivityRunnig) {
            SavedEventActivity.getSavedEventsFromJavaList ();
        }
    }

    public Location getLocation2() {
        LocationManager locationManager = (LocationManager) this.getSystemService (Context.LOCATION_SERVICE);
        if (locationManager != null) {
            Location lastKnownLocationGPS = locationManager.getLastKnownLocation (LocationManager.GPS_PROVIDER);
            if (lastKnownLocationGPS != null) {
                return lastKnownLocationGPS;
            } else {
                if (ActivityCompat.checkSelfPermission (this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission (this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return null;
                }
                Location loc = locationManager.getLastKnownLocation (LocationManager.PASSIVE_PROVIDER);
                return loc;
            }
        } else {
            return null;
        }
    }

    private class GetEventDis2 extends AsyncTask<String, Integer, String> {

        String jsonStr;
        String duritation;
        boolean toLongToWalk = false;

        public GetEventDis2(EventPage eventPage) {
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d ("mmm", "begen_doInBackground");
            try {
                URL url = new URL (params[0]);
                Log.d ("mmm", "url=" + url.toString ());
                HttpURLConnection con = (HttpURLConnection) url.openConnection ();
                con.setRequestMethod ("GET");
                con.connect ();
                if (con.getResponseCode () == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader (new InputStreamReader (con.getInputStream ()));
                    StringBuilder sr = new StringBuilder ();
                    String line = "";
                    while ((line = br.readLine ()) != null) {
                        sr.append (line);
                    }
                    jsonStr = sr.toString ();
                    parseJSON (jsonStr);
                } else {
                    Log.d ("mmm", "HttpURLConnection.NOT_OK");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace ();
            } catch (IOException e) {
                e.printStackTrace ();
            }
            Log.d ("mmm", "end_doInBackground");
            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String re) {
            if (!walkNdrive) {
                driving = duritation;
                walkNdrive = true;
            } else {
                if (!toLongToWalk) {
                    walking = duritation;
                    walkNdrive = false;
                    toLongToWalk = false;
                }
            }
        }

        public void parseJSON(String jsonStr) {
            Log.d ("mmm", "begen_parseJSON");
            try {
                JSONObject obj = new JSONObject (jsonStr);
                duritation = obj.getJSONArray ("rows").getJSONObject (0).getJSONArray ("elements").getJSONObject (0).getJSONObject ("duration").get ("text").toString ();
                if (walkNdrive) {
                    walkValue = (int) obj.getJSONArray ("rows").getJSONObject (0).getJSONArray ("elements").getJSONObject (0).getJSONObject ("duration").get ("value");
                    Log.d ("mmm", "walkValue= " + walkValue);
                }
            } catch (JSONException e) {
                e.printStackTrace ();
            }

            Log.d ("mmm", "duration= " + duritation);
            Log.d ("mmm", "end_parseJSON");
        }
    }
}
