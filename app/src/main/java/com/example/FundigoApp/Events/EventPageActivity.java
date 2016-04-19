package com.example.FundigoApp.Events;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.FundigoApp.Chat.MessagesRoomProducerActivity;
import com.example.FundigoApp.Chat.RealTimeChatActivity;
import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.Producer.ProducerSendPuchActivity;
import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethod.EventDataMethods;
import com.example.FundigoApp.StaticMethod.FileAndImageMethods;
import com.example.FundigoApp.StaticMethod.GPSMethods;
import com.example.FundigoApp.StaticMethod.GeneralStaticMethods;
import com.example.FundigoApp.Tickets.EventsSeats;
import com.example.FundigoApp.Tickets.SelectSeatActivity;
import com.example.FundigoApp.Tickets.WebBrowserActivity;
import com.example.FundigoApp.Verifications.SmsSignUpActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    Button getTicketsButton;
    Intent intent;

    private String date;
    private String eventName;
    private String eventPlace;
    private String driving;
    private String walking;
    private boolean walkNdrive = false;
    private int walkValue = -1;
    EventInfo eventInfo;
    String i = "";


    private String faceBookUrl;
    ImageLoader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_event_page);
        getTicketsButton = (Button) findViewById (R.id.button);

        intent = getIntent ();
        eventInfo = GlobalVariables.ALL_EVENTS_DATA.get
                                                            (intent.getIntExtra ("index", 0));

        Date currentDate = new Date ();
        Date eventDate = eventInfo.getDate ();
        eventInfo.setIsFutureEvent (eventDate.after (currentDate));
        saveButton = (ImageView) findViewById (R.id.imageEvenetPageView3);
        if (eventInfo.getPrice ().equals ("FREE")) {
            getTicketsButton.setText ("FUNDIGO");
            getTicketsButton.setClickable (false);
        }
        if (!GlobalVariables.IS_PRODUCER && !eventInfo.isFutureEvent ()) {
            getTicketsButton.setText ("EVENT ENDED");
            getTicketsButton.setClickable (false);
        }
        if (GlobalVariables.IS_PRODUCER) {
            if (!eventInfo.getPrice ().equals ("FREE")) {
                getTicketsButton.setText (this.getString (R.string.tickets_status));
            }
            saveButton.setImageResource (R.drawable.ic_micro_send_push_frame);
        } else {
            if (eventInfo.isFutureEvent () && !eventInfo.getPrice ().equals ("FREE")) {
                checkIfTicketsLeft ();
            }
        }

        faceBookUrl = intent.getStringExtra ("fbUrl");//get link from the Intent
        GlobalVariables.deepLinkEventObjID = "";
        GlobalVariables.deepLink_params = "";

        ImageView event_image = (ImageView) findViewById (R.id.eventPage_image);
        loader = FileAndImageMethods.getImageLoader (this);
        loader.displayImage (eventInfo.getPicUrl(), event_image);
        date = eventInfo.getDateAsString ();
        TextView event_date = (TextView) findViewById (R.id.eventPage_date);
        event_date.setText (date);
        eventName = intent.getStringExtra ("eventName");
        i = getIntent ().getStringExtra ("i");
        TextView event_name = (TextView) findViewById (R.id.eventPage_name);
        event_name.setText (eventName);
        String eventTags = intent.getStringExtra ("eventTags");
        TextView event_tags = (TextView) findViewById (R.id.eventPage_tags);
        event_tags.setText (eventTags);
        String eventPrice = intent.getStringExtra ("eventPrice");
        TextView event_price = (TextView) findViewById (R.id.priceEventPage);
        if (GlobalVariables.IS_PRODUCER) {
            event_price.setText ("Edit Event");
        } else {
            event_price.setText (EventDataMethods.getDisplayedEventPrice (eventPrice));
        }
        String eventDescription = intent.getStringExtra ("eventInfo");
        TextView event_info = (TextView) findViewById (R.id.eventInfoEventPage);
        event_info.setText (eventDescription);
        eventPlace = intent.getStringExtra ("eventPlace");
        TextView event_place = (TextView) findViewById (R.id.eventPage_location);
        event_place.setText (eventPlace + ", " + eventInfo.getAddress ());
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
                intent2.putExtra ("artist", eventInfo.getArtist ());
                startActivity (intent2);
            }
        });
        checkIfChangeColorToSaveButtton ();
        String even_addr = eventInfo.getAddress ();
        even_addr = even_addr.replace (",", "");
        even_addr = even_addr.replace (" ", "+");
        if (GlobalVariables.MY_LOCATION != null && GPSMethods.isLocationEnabled (this)) {
            new GetEventDis2 (EventPageActivity.this).execute (
                                                                      "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" +
                                                                              getLocation2 ().getLatitude () +
                                                                              "," +
                                                                              getLocation2 ().getLongitude () +
                                                                              "&destinations=" +
                                                                              even_addr +
                                                                              "+Israel&mode=driving&language=" + Locale.getDefault ().getLanguage () + "&key=AIzaSyAuwajpG7_lKGFWModvUIoMqn3vvr9CMyc");
            new GetEventDis2 (EventPageActivity.this).execute (
                                                                      "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" +
                                                                              getLocation2 ().getLatitude () +
                                                                              "," +
                                                                              getLocation2 ().getLongitude () +
                                                                              "&destinations=" +
                                                                              even_addr +
                                                                              "+Israel&mode=walking&language=" + Locale.getDefault ().getLanguage () + "&key=AIzaSyAuwajpG7_lKGFWModvUIoMqn3vvr9CMyc");
        }
    }

    public void openTicketsPage(View view) {
        if (!GlobalVariables.IS_PRODUCER) {
            if (GlobalVariables.IS_CUSTOMER_GUEST) {
                dialogForGuestToRegister (); // in case of Guest
            } else {
                if (eventInfo.isStadium) {
                    Bundle b = new Bundle ();
                    Intent intentSeat = new Intent (EventPageActivity.this, SelectSeatActivity.class);
                    intentSeat.putExtras (b);
                    intentSeat.putExtra ("eventPrice", eventInfo.getPrice ());
                    intentSeat.putExtra ("eventName", eventInfo.getName ());
                    intentSeat.putExtra ("phone", GlobalVariables.CUSTOMER_PHONE_NUM);
                    intentSeat.getStringExtra ("eventPrice");
                    intentSeat.putExtra ("eventObjectId", eventInfo.getParseObjectId ());
                    startActivity (intentSeat);
                } else {
                    Handler handler = new Handler ();
                    handler.postDelayed (new Runnable () {
                        @Override
                        public void run() {
                            for (int i = 0; i < 3; i++) {
                                Toast.makeText (getApplicationContext (),
                                                       "You Have 20 Minutes to complete the purchase, Otherwise the ticket will be available to all again",
                                                       Toast.LENGTH_SHORT).show ();
                            }
                        }
                    }, 0);
                    Intent intentPelePay = new Intent (EventPageActivity.this, WebBrowserActivity.class);
                    intentPelePay.putExtra ("eventObjectId", eventInfo.getParseObjectId ());
                    intentPelePay.putExtra ("isChoose", "no");
                    intentPelePay.putExtra ("eventPrice", eventInfo.getPrice ());
                    startActivity (intentPelePay);
                }
            }
        } else {
            Intent intent = new Intent (EventPageActivity.this, EventStatusActivity.class);
            intent.putExtra ("name", getIntent ().getStringExtra ("eventName"));
            intent.putExtra ("eventObjectId", eventInfo.getParseObjectId ());
            startActivity (intent);
        }
    }

    private void loadMessagesPageProducer() {
        Intent intent = new Intent (this, MessagesRoomProducerActivity.class);
        intent.putExtra ("index", eventInfo.getIndexInFullList ());
        startActivity (intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId ()) {
            case R.id.imageEvenetPageView2:
                AlertDialog.Builder builder = new AlertDialog.Builder (this);
                builder.setMessage ("Share:")
                        .setCancelable (false)
                        .setPositiveButton (this.getString (R.string.share_app_page), new DialogInterface.OnClickListener () {
                            public void onClick(DialogInterface dialog, int id) {
                                shareDeepLink ();
                            }
                        })
                        .setNegativeButton (this.getString (R.string.share_web_page), new DialogInterface.OnClickListener () {
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
                if (GlobalVariables.IS_PRODUCER) {
                    Intent pushIntent = new Intent (EventPageActivity.this, ProducerSendPuchActivity.class);
                    pushIntent.putExtra ("id", eventInfo.getParseObjectId ());
                    startActivity (pushIntent);
                } else {
                    handleSaveEventClicked (this.intent.getIntExtra ("index", 0));
                }
                break;
            case R.id.imageEvenetPageView5:
                AlertDialog.Builder builder2 = new AlertDialog.Builder (this);
                builder2.setTitle (this.getString (R.string.you_can_get_more_info_about_the_event));
                builder2.setMessage (this.getString (R.string.how_do_you_want_to_do_it));
                if (!GlobalVariables.IS_PRODUCER) {
                    builder2.setPositiveButton (this.getString (R.string.Send_message_to_producer), listener);
                } else {
                    builder2.setPositiveButton (this.getString (R.string.see_customers_massages), listener);
                }
                builder2.setNegativeButton (this.getString (R.string.real_time_chat), listener);
                builder2.setNeutralButton (this.getString (R.string.cancel), listener);
                AlertDialog dialog = builder2.create ();
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
                    if (GlobalVariables.IS_CUSTOMER_REGISTERED_USER) {
                        intentToSend = new Intent (EventPageActivity.this, ChatActivity.class);
                        intentToSend.putExtra ("index", intent.getIntExtra ("index", 0));
                        intentToSend.putExtra ("customer_phone", GlobalVariables.CUSTOMER_PHONE_NUM);
                        startActivity (intentToSend);
                    } else if (GlobalVariables.IS_PRODUCER) {
                        loadMessagesPageProducer ();
                    } else if (GlobalVariables.IS_CUSTOMER_GUEST) {
                        dialogForGuestToRegister (); // in case of Guest
                    }
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    if (GlobalVariables.IS_CUSTOMER_GUEST) {
                        dialogForGuestToRegister (); //in case of Guest
                    } else {
                        intentToSend = new Intent (EventPageActivity.this, RealTimeChatActivity.class);
                        intentToSend.putExtra ("eventName", eventName);
                        intentToSend.putExtra ("eventObjectId", eventInfo.getParseObjectId ());
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
            Toast.makeText (EventPageActivity.this, R.string.scan_didnt_finish, Toast.LENGTH_SHORT).show ();
        }
        if (data != null && requestCode == GlobalVariables.REQUEST_CODE_MY_PICK) {
            GeneralStaticMethods.onActivityResult (requestCode,
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
        GeneralStaticMethods.handleSaveEventClicked (event,
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

    public void shareDeepLink() {
        BranchUniversalObject branchUniversalObject = new BranchUniversalObject ()
                                                              .setCanonicalIdentifier ("item/1234")
                                                              .setTitle ("My Content Title")
                                                              .setContentDescription (this.getString (R.string.my_content_description))
                                                              .setContentIndexingMode (BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                                                              .addContentMetadata ("objectId", eventInfo.getParseObjectId ());

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
                    //Toast.makeText (getApplicationContext (), url, Toast.LENGTH_LONG).show ();
                } else {
                    Toast.makeText (getApplicationContext (), error.getMessage () + "", Toast.LENGTH_SHORT).show ();
                }

            }
        });

    }

    public void editEvent(View view) {
        if (GlobalVariables.IS_PRODUCER) {
            Intent intent = new Intent (this, EditEventActivity.class);
            intent.putExtra (GlobalVariables.OBJECTID, eventInfo.getParseObjectId ());
            startActivity (intent);
        }
    }

    public void checkIfTicketsLeft() {
        ParseQuery<EventsSeats> query = ParseQuery.getQuery ("EventsSeats");
        // Retrieve the object by id
        query.whereEqualTo ("eventObjectId", eventInfo.getParseObjectId ()).whereEqualTo ("sold", true);
        query.findInBackground (new FindCallback<EventsSeats> () {
            public void done(List<EventsSeats> eventsSeatsList, ParseException e) {
                if (e == null) {
                    if (eventsSeatsList.size () >= eventInfo.getNumOfTickets ()) {
                        getTicketsButton.setText ("NO TICKET LEFT");
                        getTicketsButton.setClickable (false);
                    }
                } else {
                    e.printStackTrace ();
                }
            }
        });
    }
}
