package com.example.events;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.sinch.verification.CodeInterceptionException;
import com.sinch.verification.Config;
import com.sinch.verification.IncorrectCodeException;
import com.sinch.verification.InvalidInputException;
import com.sinch.verification.ServiceErrorException;
import com.sinch.verification.SinchVerification;
import com.sinch.verification.Verification;
import com.sinch.verification.VerificationListener;

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
    String producer_id;
    String customer_id;
    private ImageView iv_share;
    private ImageView iv_chat;
    Button ticketsStatus;
    static final int REQUEST_CODE_MY_PICK = 1;
    Intent intent;
    Button editEvent;
    private String phone;
    private String vCode;


    private String date;
    private String eventName;
    private String eventPlace;
    private Uri uri;
    private String driving;
    private String walking;
    private boolean walkNdrive = false;
    private int walkValue = -1;
    Bitmap bitmap;
    private Config  config;
    private VerificationListener listener0;
    private Verification verification;
    private String tempPphone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);
        intent = getIntent ();
        config = SinchVerification.config().applicationKey ("b9ee3da5-0dc9-40aa-90aa-3d30320746f3").context (getApplicationContext ()).build();
        listener0 = new MyVerificationListener ();
        if (Constants.IS_PRODUCER) {
            ticketsStatus = (Button) findViewById (R.id.button);
            ticketsStatus.setText ("Tickets Status");
            
            editEvent = (Button) findViewById (R.id.priceEventPage);
            editEvent.setText ("Edit Event");
        }


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
        if (Constants.IS_PRODUCER) {
            TextView event_price = (TextView) findViewById (R.id.priceEventPage);
            event_price.setText (eventPrice);
        }

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
        checkIfChangeColorToSaveButtton ();
        String even_addr = eventPlace;
        even_addr = even_addr.replace (",", "");
        even_addr = even_addr.replace(" ", "+");
        if (MainActivity.cityFoundGPS) {
            new GetEventDis2 (EventPage.this).execute(
                    "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + getLocation2().getLatitude() + "," + getLocation2().getLongitude() + "&destinations=" + even_addr + "+Israel&mode=driving&language=en-EN&key=AIzaSyAuwajpG7_lKGFWModvUIoMqn3vvr9CMyc");
            new GetEventDis2 (EventPage.this).execute(
                    "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + getLocation2().getLatitude() + "," + getLocation2().getLongitude() + "&destinations=" + even_addr + "+Israel&mode=walking&language=en-EN&key=AIzaSyAuwajpG7_lKGFWModvUIoMqn3vvr9CMyc");
        }

    }



    public void openTicketsPage(View view) {



            if (!Constants.IS_PRODUCER) {
                Intent intentHere2 = getIntent();

                if(!intentHere2.getStringExtra("eventPrice").equals("FREE")) {
                    phone = readFromFile1();

                    if (phone.isEmpty()) {




                        final Dialog dialog1 = new Dialog(EventPage.this);
                        dialog1.setContentView(R.layout.custume_popup);
                        dialog1.setTitle("Phone Number Required");
                        final TextView myTextView1= (TextView) dialog1.findViewById(R.id.potextview);
                        myTextView1.setText("We Will Send You SMS With Verify Code");
                        Button button1 = (Button) dialog1.findViewById(R.id.button4);
                        button1.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {

                                EditText edit = (EditText) dialog1.findViewById(R.id.enter_phone);
                                String text = edit.getText().toString();


                                verification = SinchVerification.createSmsVerification(config, text, listener0);
                                verification.initiate();

                                dialog1.dismiss();


                            }
                        });







                        final Dialog dialog = new Dialog(EventPage.this);
                        dialog.setContentView(R.layout.custume_popup);
                        dialog.setTitle("Verification");


                        final TextView myTextView= (TextView) dialog.findViewById(R.id.potextview);
                        myTextView.setText("Enter Verify Code Sent By SMS...");;

                        Button button = (Button) dialog.findViewById(R.id.button4);
                        button.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                EditText edit = (EditText) dialog.findViewById(R.id.enter_phone);
                                String text = edit.getText().toString();
                                String code = edit.getText().toString();
                                verification.verify(text);
                                tempPphone = text;

                                dialog.dismiss();


                            }
                        });


                        dialog.show();
                        dialog1.show();

                    } else {
                        Bundle b = new Bundle();
                        Intent intentBrowser = new Intent(EventPage.this, WebBrowser.class);
                        Intent intentHere = getIntent();
                        intentBrowser.putExtra("eventName", intentHere.getStringExtra("eventName"));
                        intentBrowser.putExtra("eventPrice", intentHere.getStringExtra("eventPrice"));
                        intentBrowser.putExtra("phone", phone);
                        Log.d("m123", intentHere.getStringExtra("eventPrice"));
                        intentBrowser.putExtras(b);


                        Intent intentSeat = new Intent(EventPage.this, SelectSeat.class);
                        intentSeat.putExtras(b);
                        intentSeat.putExtra("eventPrice", intentHere.getStringExtra("eventPrice"));
                        intentSeat.putExtra("eventName", intentHere.getStringExtra("eventName"));
                        intentSeat.putExtra("phone", phone);
                        intentSeat.getStringExtra("eventPrice");

                        String str = intentHere.getStringExtra("eventPrice");
                        int id = intentHere.getExtras().getInt("index");
                        if (id % 2 != 0 || str.contains("-")) {
                            startActivity(intentSeat);
                        } else {
                            startActivity(intentBrowser);
                        }


                /*
                Intent intent = new Intent(EventPage.this, EventStatus.class);
                intent.putExtra("name", getIntent().getStringExtra("eventName"));
                intent.putExtra("phone", phone);
                startActivity(intent);
                */
                    }
                }else{
                    Toast.makeText(this,"Event Is Free",Toast.LENGTH_LONG).show();
                }
        }
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
        intent.putExtra ("index", this.intent.getIntExtra ("index", 0));
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
                if (!Constants.IS_PRODUCER) {
                    builder.setPositiveButton ("Send message to producer", listener);
                } else {
                    builder.setPositiveButton ("See Customers' Massages", listener);
                }
                builder.setNegativeButton ("Real Time Chat", listener);
                builder.setNeutralButton ("Cancel...", listener);
                AlertDialog dialog = builder.create ();
                dialog.show ();
                TextView messageText = (TextView) dialog.findViewById (android.R.id.message);
                messageText.setGravity (Gravity.CENTER);
                break;
        }
    }

    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener () {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Intent intentToSend;
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    if (!Constants.IS_PRODUCER) {
                        intentToSend = new Intent (EventPage.this, ChatActivity.class);
                        intentToSend.putExtra ("producer_id", producer_id);
                        intentToSend.putExtra ("customer_id", customer_id);
                        intentToSend.putExtra ("index", intent.getIntExtra ("index", 0));
                        startActivity (intentToSend);
                    } else {
                        loadMessagesPage ();
                    }
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    intentToSend = new Intent (EventPage.this, RealTimeChatActivity.class);
                    intentToSend.putExtra ("customer_id", customer_id);
                    intentToSend.putExtra ("producer_id", producer_id);
                    intentToSend.putExtra ("eventName", eventName);
                    startActivity (intentToSend);
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
        if (!Constants.IS_PRODUCER) {
            int index = intent.getIntExtra ("index", 0);
            if (MainActivity.all_events_data.get (index).getIsSaved ())
                save.setImageResource (R.mipmap.whsavedd);
            else {
                save.setImageResource (R.mipmap.wh);
            }
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
            AsyncTask.execute (new Runnable () {
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
        if (MainActivity.savedAcctivityRunnig) {
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
            try {
                URL url = new URL (params[0]);
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
                }
            } catch (MalformedURLException e) {
                e.printStackTrace ();
            } catch (IOException e) {
                e.printStackTrace ();
            }
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
            try {
                JSONObject obj = new JSONObject (jsonStr);
                duritation = obj.getJSONArray ("rows").getJSONObject (0).getJSONArray ("elements").getJSONObject (0).getJSONObject ("duration").get ("text").toString ();
                if (walkNdrive) {
                    walkValue = (int) obj.getJSONArray ("rows").getJSONObject (0).getJSONArray ("elements").getJSONObject (0).getJSONObject ("duration").get ("value");
                }
            } catch (JSONException e) {
                e.printStackTrace ();
            }
        }

        public void editEvent(View view) {
            if (Constants.IS_PRODUCER) {
                Intent intent = new Intent (EventPage.this, CreateEventActivity.class);
                intent.putExtra ("name", getIntent ().getStringExtra ("eventName"));
                intent.putExtra ("create", "false");
                startActivity (intent);
            }
        }
    }
    private String readFromFile1() {
        String phone_number = "";
        try {
            InputStream inputStream = openFileInput ("verify.txt");
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader (inputStream);
                BufferedReader bufferedReader = new BufferedReader (inputStreamReader);
                String receiveString = "";
                while ((receiveString = bufferedReader.readLine ()) != null) {
                    phone_number = receiveString;
                }
                inputStream.close ();
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return phone_number;
    }
    public void smsVerify(String phone_number) {
        Config config = SinchVerification.config().applicationKey ("b9ee3da5-0dc9-40aa-90aa-3d30320746f3").context (getApplicationContext ()).build ();
        VerificationListener listener1 = new MyVerificationListener ();
        Verification verification = SinchVerification.createSmsVerification (config, phone_number, listener1);
        verification.initiate ();

    }

    class MyVerificationListener implements VerificationListener {
        @Override
        public void onInitiated() {

        }

        @Override
        public void onInitiationFailed(Exception e) {
            if (e instanceof InvalidInputException) {
                // Incorrect number provided
            } else if (e instanceof ServiceErrorException) {
                // Sinch service error
            } else {
                // Other system error, such as UnknownHostException in case of network error
            }
        }

        @Override
        public void onVerified() {
            String filename = "verify.txt";
            String string = phone=tempPphone;
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(string.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Bundle b = new Bundle();
            Intent intentBrowser = new Intent(EventPage.this, WebBrowser.class);
            Intent intentHere = getIntent();
            intentBrowser.putExtra("eventName", intentHere.getStringExtra("eventName"));
            intentBrowser.putExtra("eventPrice", intentHere.getStringExtra("eventPrice"));
            intentBrowser.putExtra("phone", phone);
            Log.d("m123", intentHere.getStringExtra("eventPrice"));
            intentBrowser.putExtras(b);


            Intent intentSeat = new Intent(EventPage.this, SelectSeat.class);
            intentSeat.putExtras(b);
            intentSeat.putExtra("eventPrice", intentHere.getStringExtra("eventPrice"));
            intentSeat.putExtra("eventName", intentHere.getStringExtra("eventName"));
            intentSeat.putExtra("phone", phone);
            intentSeat.getStringExtra("eventPrice");

            String str = intentHere.getStringExtra("eventPrice");
            int id = intentHere.getExtras().getInt("index");
            if (id % 2 != 0 || str.contains("-")) {
                startActivity(intentSeat);
            } else {
                startActivity(intentBrowser);
            }
        }

        @Override
        public void onVerificationFailed(Exception e) {
            if (e instanceof InvalidInputException) {
                // Incorrect number or code provided
                Toast.makeText (getApplicationContext (), "invalid phone number try again.", Toast.LENGTH_SHORT).show ();
            } else if (e instanceof CodeInterceptionException) {
                // Intercepting the verification code automatically failed, input the code manually with verify()
            } else if (e instanceof IncorrectCodeException) {
            } else if (e instanceof ServiceErrorException) {
            } else {
            }
        }
    }
}
