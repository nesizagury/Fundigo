package com.example.FundigoApp.Events;

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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.FundigoApp.Chat.ChatActivity;
import com.example.FundigoApp.Chat.MessagesRoomActivity;
import com.example.FundigoApp.Chat.RealTimeChatActivity;
import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.Producer.EventOnRealtimeActivity;
import com.example.FundigoApp.Producer.ProducerSendPuchActivity;
import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethods;
import com.example.FundigoApp.Tickets.SelectSeatActivity;
import com.example.FundigoApp.Tickets.WebBrowserActivity;
import com.example.FundigoApp.Verifications.SmsSignUpActivity;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;

public class EventPageActivity extends Activity implements View.OnClickListener {
    ImageView saveButton;
    private ImageView iv_share;
    private ImageView iv_chat;
    Button ticketsStatus;
    Intent intent;
    Button editEvent;
    Button producerPush;

    private String date;
    private String eventName;
    private String eventPlace;
    private Uri uri;
    private String driving;
    private String walking;
    private boolean walkNdrive = false;
    private int walkValue = -1;
    Bitmap bitmap;
    EventInfo event;
    Button realTimeButton;
    String x;
    private GoogleApiClient mClient;
    private Uri mUrl;
    private String mTitle;
    private String mDescription;
    String i = "";
    private ImageView ivQrScan;
    private String faceBookUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_event_page);
        ivQrScan = (ImageView) findViewById (R.id.iv_qrscan);
        producerPush = (Button) findViewById (R.id.pushButton);
        if (GlobalVariables.IS_PRODUCER) {
            ticketsStatus = (Button) findViewById (R.id.button);
            ticketsStatus.setText ("Tickets Status");
            editEvent = (Button) findViewById (R.id.priceEventPage);
            editEvent.setText ("Edit Event");
            realTimeButton = (Button) findViewById (R.id.realTime);
            realTimeButton.setVisibility (View.VISIBLE);
            ivQrScan.setOnClickListener (this);
            producerPush.setVisibility (View.VISIBLE);
            producerPush.setOnClickListener (this);
        } else {
            ivQrScan.setVisibility (View.GONE);
            producerPush.setVisibility (View.GONE);
        }

        mClient = new GoogleApiClient.Builder (this).addApi (AppIndex.API).build ();
        mUrl = Uri.parse ("http://examplepetstore.com/dogs/standard-poodle");
        mTitle = "Standard Poodle";
        mDescription = "The Standard Poodle stands at least 18 inches at the withers";

        intent = getIntent ();
        faceBookUrl = intent.getStringExtra ("fbUrl");//get link from the Intent
        GlobalVariables.deepLinkEventObjID = "";
        GlobalVariables.deepLink_params = "";
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
        event = GlobalVariables.ALL_EVENTS_DATA.get
                                                        (intent.getIntExtra ("index", 0));
        i = getIntent ().getStringExtra ("i");
        TextView event_name = (TextView) findViewById (R.id.eventPage_name);
        event_name.setText (eventName);
        String eventTags = intent.getStringExtra ("eventTags");
        TextView event_tags = (TextView) findViewById (R.id.eventPage_tags);
        event_tags.setText (eventTags);
        String eventPrice = intent.getStringExtra ("eventPrice");
        if (GlobalVariables.IS_PRODUCER) {
            TextView event_price = (TextView) findViewById (R.id.priceEventPage);
            event_price.setText (eventPrice);
        }
        ;
        TextView event_price = (TextView) findViewById (R.id.priceEventPage);
        event_price.setText (eventPrice);

        String eventInfo = intent.getStringExtra ("eventInfo");
        TextView event_info = (TextView) findViewById (R.id.eventInfoEventPage);
        event_info.setText (eventInfo);
        eventPlace = intent.getStringExtra ("eventPlace");
        TextView event_place = (TextView) findViewById (R.id.eventPage_location);
        event_place.setText (eventPlace);
        Bundle b = getIntent ().getExtras ();
        iv_share = (ImageView) findViewById (R.id.imageEvenetPageView2);
        iv_share.setOnClickListener (this);
        iv_chat = (ImageView) findViewById (R.id.imageEvenetPageView5);
        iv_chat.setOnClickListener (this);

        ImageView imageEvenetPageView4 = (ImageView) findViewById (R.id.imageEvenetPageView4);
        imageEvenetPageView4.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent (EventPageActivity.this, EventServiceActivity.class);
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
        saveButton = (ImageView) findViewById (R.id.imageEvenetPageView3);
        checkIfChangeColorToSaveButtton ();
        String even_addr = eventPlace;
        even_addr = even_addr.replace (",", "");
        even_addr = even_addr.replace (" ", "+");
        if (GlobalVariables.MY_LOCATION != null && StaticMethods.isLocationEnabled (this)) {
            new GetEventDis2 (EventPageActivity.this).execute (
                                                                      "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" +
                                                                              getLocation2 ().getLatitude () +
                                                                              "," +
                                                                              getLocation2 ().getLongitude () +
                                                                              "&destinations=" +
                                                                              even_addr +
                                                                              "+Israel&mode=driving&language=en-EN&key=AIzaSyAuwajpG7_lKGFWModvUIoMqn3vvr9CMyc");
            new GetEventDis2 (EventPageActivity.this).execute (
                                                                      "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" +
                                                                              getLocation2 ().getLatitude () +
                                                                              "," +
                                                                              getLocation2 ().getLongitude () +
                                                                              "&destinations=" +
                                                                              even_addr +
                                                                              "+Israel&mode=walking&language=en-EN&key=AIzaSyAuwajpG7_lKGFWModvUIoMqn3vvr9CMyc");
        }
    }

    public void openTicketsPage(View view) {
        if (!GlobalVariables.IS_PRODUCER) {
            if (event.getPrice ().equals ("FREE")) {
                Toast.makeText (this, "Event Is Free", Toast.LENGTH_LONG).show ();
            } else {
                int id = event.getIndexInFullList ();
                String eventPrice = event.getPrice ();
                if (id % 2 != 0 || eventPrice.contains ("-")) {
                    Bundle b = new Bundle ();
                    Intent intentSeat = new Intent (EventPageActivity.this, SelectSeatActivity.class);
                    intentSeat.putExtras (b);
                    intentSeat.putExtra ("eventPrice", event.getPrice ());
                    intentSeat.putExtra ("eventName", event.getName ());
                    intentSeat.putExtra ("phone", GlobalVariables.CUSTOMER_PHONE_NUM);
                    intentSeat.getStringExtra ("eventPrice");
                    intentSeat.putExtra ("eventObjectId", event.getParseObjectId ());
                    startActivity (intentSeat);
                } else {
                    Intent intentPelePay = new Intent (EventPageActivity.this, WebBrowserActivity.class);
                    intentPelePay.putExtra ("eventObjectId", event.getParseObjectId ());
                    intentPelePay.putExtra ("isChoose", "no");
                    intentPelePay.putExtra ("eventPrice", event.getPrice ());
                    startActivity (intentPelePay);
                }
            }
        } else {
            Intent intent = new Intent (EventPageActivity.this, EventStatusActivity.class);
            intent.putExtra ("name", getIntent ().getStringExtra ("eventName"));
            intent.putExtra ("eventObjectId", event.getParseObjectId ());
            startActivity (intent);
        }
    }

    private void loadMessagesPageProducer() {
        Intent intent = new Intent (this, MessagesRoomActivity.class);
        intent.putExtra ("index", event.getIndexInFullList ());
        startActivity (intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId ()) {
            case R.id.imageEvenetPageView2:
                AlertDialog.Builder builder = new AlertDialog.Builder (this);
                builder.setMessage ("Share:")
                        .setCancelable (false)
                        .setPositiveButton ("Share App Page", new DialogInterface.OnClickListener () {
                            public void onClick(DialogInterface dialog, int id) {
                                shareDeepLink ();
                            }
                        })
                        .setNegativeButton ("Share Web Page", new DialogInterface.OnClickListener () {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent webIntent;
                                if (faceBookUrl != "" && faceBookUrl != null) {
                                    try {
                                        getPackageManager ().getPackageInfo ("com.facebook.katana", 0);
                                        webIntent = new Intent (Intent.ACTION_VIEW, Uri.parse ("fb://facewebmodal/f?href=" + faceBookUrl));
                                        startActivity (webIntent);
                                    } catch (Exception e) {
                                        Log.e (e.toString (), "Open link to FaceBook App is fail, sending to Browser");
                                        try {
                                            webIntent = new Intent (Intent.ACTION_VIEW, Uri.parse (faceBookUrl));
                                            startActivity (webIntent);
                                        } catch (Exception e1) {
                                            Log.e (e1.toString (), "Open link to FaceBook Browser is fail");
                                        }
                                    }
                                } else
                                    Toast.makeText (EventPageActivity.this, "No FaceBook Page to Present", Toast.LENGTH_SHORT).show ();
                            }
                        })
                        .setCancelable (true);
                AlertDialog alert = builder.create ();
                alert.show ();
                break;
            case R.id.imageEvenetPageView3:
                handleSaveEventClicked (this.intent.getIntExtra ("index", 0));
                break;
            case R.id.imageEvenetPageView5:
                AlertDialog.Builder builder2 = new AlertDialog.Builder (this);
                builder2.setTitle ("You can get more info\nabout the event!");
                builder2.setMessage ("How do you want to do it?");
                if (!GlobalVariables.IS_PRODUCER) {
                    builder2.setPositiveButton ("Send message to producer", listener);
                } else {
                    builder2.setPositiveButton ("See Customers' Massages", listener);
                }
                builder2.setNegativeButton ("Real Time Chat", listener);
                builder2.setNeutralButton ("Cancel...", listener);
                AlertDialog dialog = builder2.create ();
                dialog.show ();
                TextView messageText = (TextView) dialog.findViewById (android.R.id.message);
                messageText.setGravity (Gravity.CENTER);
                break;
            case R.id.pushButton:
                Intent pushIntent = new Intent (EventPageActivity.this, ProducerSendPuchActivity.class);
                pushIntent.putExtra ("id", event.getParseObjectId ());
                startActivity (pushIntent);
                break;
            case R.id.iv_qrscan:
                IntentIntegrator integrator = new IntentIntegrator (this);
                integrator.initiateScan ();
                break;
        }
    }

    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener () {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Intent intentToSend;
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    if (GlobalVariables.IS_CUSTOMER_REGISTERED_USER &&
                                !GlobalVariables.CUSTOMER_PHONE_NUM.equals ("GUEST")) {
                        intentToSend = new Intent (EventPageActivity.this, ChatActivity.class);
                        intentToSend.putExtra ("index", intent.getIntExtra ("index", 0));
                        intentToSend.putExtra ("customer_phone", GlobalVariables.CUSTOMER_PHONE_NUM);
                        startActivity (intentToSend);
                    } else if (GlobalVariables.IS_PRODUCER) {
                        loadMessagesPageProducer ();
                    } else if (GlobalVariables.CUSTOMER_PHONE_NUM.equals ("GUEST")) {
                        dialogForGuestToRegister (); // in case of Guest
                    }
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    if (GlobalVariables.CUSTOMER_PHONE_NUM != null &&
                                GlobalVariables.CUSTOMER_PHONE_NUM.equals ("GUEST")) {
                        dialogForGuestToRegister (); //in case of Guest
                    } else {
                        intentToSend = new Intent (EventPageActivity.this, RealTimeChatActivity.class);
                        intentToSend.putExtra ("eventName", eventName);
                        intentToSend.putExtra ("eventObjectId", event.getParseObjectId ());
                        startActivity (intentToSend);
                    }
                    break;
                case DialogInterface.BUTTON_NEUTRAL:
                    dialog.dismiss ();
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scan = null;
        if (data != null) {
            scan = IntentIntegrator.parseActivityResult (requestCode,
                                                                resultCode,
                                                                data);
        }
        if (scan != null) {
            String result = scan.getContents ();
            String objectId = result.substring (13, 23);
            Toast.makeText (EventPageActivity.this, "" + scan.getFormatName () + " " + scan.getContents () + " ObjectId is " + objectId, Toast.LENGTH_LONG).show ();

        } else {
            Toast.makeText (EventPageActivity.this, "Scan didn`t finish", Toast.LENGTH_SHORT).show ();
        }
        if (data != null && requestCode == GlobalVariables.REQUEST_CODE_MY_PICK) {
            StaticMethods.onActivityResult (requestCode,
                                                   data,
                                                   this);
        }
    }

    public void checkIfChangeColorToSaveButtton() {
        if (!GlobalVariables.IS_PRODUCER) {
            int index = intent.getIntExtra ("index", 0);
            if (GlobalVariables.ALL_EVENTS_DATA.get (index).getIsSaved ())
                saveButton.setImageResource (R.mipmap.whsavedd);
            else {
                saveButton.setImageResource (R.mipmap.wh);
            }
        }
    }

    public void handleSaveEventClicked(int index) {
        EventInfo event = GlobalVariables.ALL_EVENTS_DATA.get (index);
        StaticMethods.handleSaveEventClicked (event,
                                                     saveButton,
                                                     this.getApplicationContext (),
                                                     R.mipmap.whsavedd,
                                                     R.mipmap.wh);
    }

    public boolean dialogForGuestToRegister() {
        //Assaf:show dialog in case  Guest want to Chat
        final AlertDialog.Builder builder = new AlertDialog.Builder (this);
        builder.setMessage ("In order to Chat or Send Message you have to pass Registration First")
                .setCancelable (true)
                .setNeutralButton ("Register by SMS", new DialogInterface.OnClickListener () {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent smsRegister = new Intent (EventPageActivity.this, SmsSignUpActivity.class);
                        startActivity (smsRegister);
                    }
                });

        builder.setPositiveButton ("Cancel", new DialogInterface.OnClickListener () {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel ();
            }
        });
        AlertDialog smsAlert = builder.create ();
        smsAlert.show ();
        return true;
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

        public GetEventDis2(EventPageActivity eventPageActivity) {
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

    }

    public Action getAction() {
        Thing object = new Thing.Builder ()
                               .setName (mTitle)
                               .setDescription (mDescription)
                               .setUrl (mUrl)
                               .build ();

        return new Action.Builder (Action.TYPE_VIEW)
                       .setObject (object)
                       .setActionStatus (Action.STATUS_TYPE_COMPLETED)
                       .build ();
    }

    @Override
    public void onStop() {
        AppIndex.AppIndexApi.end (mClient, getAction ());
        mClient.disconnect ();
        super.onStop ();
    }

    public void shareDeepLink() {
        BranchUniversalObject branchUniversalObject = new BranchUniversalObject ()
                                                              .setCanonicalIdentifier ("item/1234")
                                                              .setTitle ("My Content Title")
                                                              .setContentDescription ("My Content Description")
                                                              .setContentIndexingMode (BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                                                              .addContentMetadata ("i", i);

        io.branch.referral.util.LinkProperties linkProperties = new LinkProperties ()
                                                                        .setChannel ("My Application")
                                                                        .setFeature ("sharing");

        ShareSheetStyle shareSheetStyle = new ShareSheetStyle (EventPageActivity.this, "Check this out!", "This stuff is awesome: ")
                                                  .setCopyUrlStyle (getResources ().getDrawable (android.R.drawable.ic_menu_send), "Copy", "Added to clipboard")
                                                  .setMoreOptionStyle (getResources ().getDrawable (android.R.drawable.ic_menu_search), "Show more")
                                                  .addPreferredSharingOption (SharingHelper.SHARE_WITH.FACEBOOK)
                                                  .addPreferredSharingOption (SharingHelper.SHARE_WITH.EMAIL)
                                                  .addPreferredSharingOption (SharingHelper.SHARE_WITH.WHATS_APP);

        branchUniversalObject.showShareSheet (this,
                                                     linkProperties,
                                                     shareSheetStyle,
                                                     new Branch.BranchLinkShareListener () {
                                                         @Override
                                                         public void onShareLinkDialogLaunched() {
                                                         }

                                                         @Override
                                                         public void onShareLinkDialogDismissed() {
                                                         }

                                                         @Override
                                                         public void onLinkShareResponse(String sharedLink, String sharedChannel, BranchError error) {
                                                         }

                                                         @Override
                                                         public void onChannelSelected(String channelName) {
                                                         }
                                                     });
        branchUniversalObject.generateShortUrl (getApplicationContext (), linkProperties, new Branch.BranchLinkCreateListener () {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                if (error == null) {
                    Toast.makeText (getApplicationContext (), url, Toast.LENGTH_LONG).show ();
                } else
                    Toast.makeText (getApplicationContext (), error.getMessage () + "", Toast.LENGTH_SHORT).show ();

            }
        });

    }

    public void eventOnRealtime(View view) {
        Intent intent = new Intent (EventPageActivity.this, EventOnRealtimeActivity.class);
        intent.putExtra ("eventName", eventName);
        intent.putExtra ("eventDate", date);
        intent.putExtra ("artist", getIntent ().getStringExtra ("artist"));
        startActivity (intent);

    }

    public void editEvent(View view) {
        if (GlobalVariables.IS_PRODUCER) {
            Intent intent = new Intent (EventPageActivity.this, CreateEventActivity.class);
            intent.putExtra ("name", getIntent ().getStringExtra ("eventName"));
            intent.putExtra ("create", "false");
            startActivity (intent);
        }
    }
}
