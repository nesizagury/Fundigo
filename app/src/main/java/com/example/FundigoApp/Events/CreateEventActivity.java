package com.example.FundigoApp.Events;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.example.FundigoApp.Tickets.TicketsPriceActivity;
import com.google.gson.Gson;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

public class CreateEventActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "CreateEventActivity";

    TextView tv_create;
    TextView tv_price;
    TextView tv_name;
    TextView tv_artist;
    TextView tv_description;
    EditText et_name;
    EditText et_artist;
    EditText et_description;
    EditText et_price;
    EditText et_quantity;
    EditText et_address;
    EditText et_place;
    EditText et_capacity;
    EditText et_parking;
    EditText et_tags;
    Button btn_validate_address;
    ImageView iv_val_add;
    Button btn_next;
    Button btn_next1;
    Button btn_next2;
    Button btn_pic;
    ImageView pic;
    Button btn_price_details;
    ScrollView create_event2;
    ScrollView create_event3;
    LinearLayout ll_name;
    LinearLayout ll_date;
    LinearLayout ll_artist;
    LinearLayout ll_description;
    private static final int SELECT_PICTURE = 1;
    private boolean pictureSelected = false;
    private boolean address_ok = false;
    Gson gson;
    Result result;
    String address;
    private String valid_address;
    private double lat;
    private double lng;
    private String city;
    private Button btn_date;
    private TextView tv_date_new;
    private String date;
    int year;
    int monthOfYear;
    int dayOfMonth;
    private boolean timeOk = false;
    private Date realDate;
    private boolean freeEvent = false;
    private CheckBox freeBox;
    private TextView tv_quantity;
    private TimePickerDialog timePickerDialog;
    private DatePickerDialog datePickerDialog;
    String[] FILTERS;
    String[] ATMS;
    String[] TOILETS;
    private MaterialSpinner filterSpinner;
    private String filter;
    private MaterialSpinner atmSpinner;
    private String atmStatus = "";
    private MaterialSpinner toiletSpinner;
    private MaterialSpinner handicapToiletSpinner;
    private String numOfToilets = "";
    private String numOfHandicapToilets = "";
    private String eventObjectId;
    int blueIncome;
    int pinkIncome;
    int greenIncome;
    int orangeIncome;
    int yellowIncome;
    int totalIncome;
    private boolean seats = false;
    private LinearLayout linearLayout;
    private CheckBox checkBoxPrice;
    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_create_event);
        sp = PreferenceManager.getDefaultSharedPreferences (this);
        componentInit ();
    }

    @Override
    protected void onResume() {
        super.onResume ();
        seats = sp.getBoolean (GlobalVariables.SEATS, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId ()) {
            case R.id.btn_next:
                if (timeOk) {
                    showSecondStage ();
                } else {
                    Toast.makeText (CreateEventActivity.this, "Please enter valid date", Toast.LENGTH_SHORT).show ();
                }
                break;
            case R.id.btn_next1:
                Log.e (TAG, "event is free " + freeEvent);
                if (freeEvent) {
                    if (address_ok) {
                        showThirdStage ();
                    } else {
                        Toast.makeText (CreateEventActivity.this, R.string.please_enter_valid_address, Toast.LENGTH_SHORT).show ();
                    }
                }
                Log.e (TAG, "seats " + seats);
                if (seats) {
                    Log.e (TAG, "seats1 " + seats);
                    if (address_ok) {
                        showThirdStage ();
                    } else {
                        Toast.makeText (CreateEventActivity.this, R.string.please_enter_valid_address, Toast.LENGTH_SHORT).show ();
                    }
                } else {
                    Log.e (TAG, "seats3 " + seats);
                    if (!validatePrice () || !validateQuantity ()) {
                        Toast.makeText (CreateEventActivity.this, "Please enter valid price or quantity", Toast.LENGTH_SHORT).show ();
                    } else {
                        if (address_ok) {
                            showThirdStage ();
                        } else {
                            Toast.makeText (CreateEventActivity.this, "Please enter valid address", Toast.LENGTH_SHORT).show ();
                        }
                    }

                }


                break;
            case R.id.btn_validate_address:
                validateAddress ();
                break;
            case R.id.btn_next2:
                if (filter != null) {
                    seats = sp.getBoolean (GlobalVariables.SEATS, false);
                    saveEvent ();
                } else {
                    Toast.makeText (CreateEventActivity.this, "Please choose a filter", Toast.LENGTH_SHORT).show ();
                }
                break;
            case R.id.btn_pic:
                uploadPic ();
                break;
            case R.id.btn_date:
                int year = Calendar.getInstance ().get (Calendar.YEAR);
                int day = Calendar.getInstance ().get (Calendar.DAY_OF_MONTH);
                int month = Calendar.getInstance ().get (Calendar.MONTH);
                datePickerDialog = new DatePickerDialog (this, listener, year, month, day);
                datePickerDialog.show ();
                break;
            case R.id.btn_price_details:
                Intent intent = new Intent (this, TicketsPriceActivity.class);
                startActivity (intent);
                break;
        }
    }

    public boolean validatePrice() {
        String str = et_price.getText ().toString ();
        if (str.equals ("0")) {
            return false;
        }
        try {
            Integer.parseInt (str);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    public boolean validateQuantity() {
        String str = et_quantity.getText ().toString ();
        if (str.equals ("0")) {
            return false;
        }
        try {
            Integer.parseInt (str);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener () {
        @Override
        public void onDateSet(DatePicker view, int y, int m, int d) {
            timePickerDialog = new TimePickerDialog (CreateEventActivity.this, timeListener, 12, 12, true);
            timePickerDialog.show ();
            year = y;
            monthOfYear = m;
            dayOfMonth = d;
            date = dayOfMonth + "." + (monthOfYear + 1) + "." + year;
            tv_date_new.setText (date);
            tv_date_new.setVisibility (View.VISIBLE);

        }
    };

    TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener () {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String min;
            if (minute < 10) {
                min = "0" + minute;
            } else {
                min = "" + minute;
            }
            Calendar cal = Calendar.getInstance ();
            cal.set (Calendar.DAY_OF_MONTH, dayOfMonth);
            cal.set (Calendar.MONTH, monthOfYear);
            cal.set (Calendar.YEAR, year);
            cal.set (Calendar.HOUR_OF_DAY, hourOfDay);
            cal.set (Calendar.MINUTE, minute);
            realDate = new Date (cal.getTimeInMillis ());

            if (cal.getTimeInMillis () <= System.currentTimeMillis ()) {
                Toast.makeText (CreateEventActivity.this, "Are you living in the past?", Toast.LENGTH_SHORT).show ();
                timeOk = false;
            } else {
                timeOk = true;
            }
        }
    };


    private void uploadPic() {
        Intent i = new Intent (
                                      Intent.ACTION_PICK,
                                      MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult (i, SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData ();
            ParcelFileDescriptor parcelFileDescriptor =
                    null;
            try {
                parcelFileDescriptor = getContentResolver ().openFileDescriptor (selectedImage, "r");
            } catch (FileNotFoundException e) {
                e.printStackTrace ();
            }
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor ();
            Bitmap image = BitmapFactory.decodeFileDescriptor (fileDescriptor);
            try {
                parcelFileDescriptor.close ();
            } catch (IOException e) {
                e.printStackTrace ();
            }
            Matrix matrix = new Matrix ();
            int angleToRotate = getOrientation (selectedImage);
            matrix.postRotate (angleToRotate);
            Bitmap rotatedBitmap = Bitmap.createBitmap (image,
                                                               0,
                                                               0,
                                                               image.getWidth (),
                                                               image.getHeight (),
                                                               matrix,
                                                               true);
            pic.setImageBitmap (rotatedBitmap);
            pic.setVisibility (View.VISIBLE);
            pictureSelected = true;
        }
    }

    public int getOrientation(Uri selectedImage) {
        int orientation = 0;
        final String[] projection = new String[]{MediaStore.Images.Media.ORIENTATION};
        final Cursor cursor = this.getContentResolver ().query (selectedImage, projection, null, null, null);
        if (cursor != null) {
            final int orientationColumnIndex = cursor.getColumnIndex (MediaStore.Images.Media.ORIENTATION);
            if (cursor.moveToFirst ()) {
                orientation = cursor.isNull (orientationColumnIndex) ? 0 : cursor.getInt (orientationColumnIndex);
            }
            cursor.close ();
        }
        return orientation;
    }

    private void showSecondStage() {
        if (et_name.length () != 0 && date.length () != 0 && et_description.length () != 0) {
            tv_create.setVisibility (View.GONE);
            ll_name.setVisibility (View.GONE);
            ll_date.setVisibility (View.GONE);
            ll_artist.setVisibility (View.GONE);
            ll_description.setVisibility (View.GONE);
            btn_next.setVisibility (View.GONE);
            create_event2.setVisibility (View.VISIBLE);
        } else {
            Toast.makeText (CreateEventActivity.this, R.string.please_fill_empty_forms, Toast.LENGTH_SHORT).show ();
        }
    }

    private void validateAddress() {
        address = et_address.getText ().toString ();
        iv_val_add.setVisibility (View.INVISIBLE);
        new ValidateAddress ().execute (GlobalVariables.GEO_API_ADDRESS);
    }

    private void showThirdStage() {
        if (freeEvent && address_ok) {
            create_event2.setVisibility (View.GONE);
            create_event3.setVisibility (View.VISIBLE);
        } else if (seats) {
            if (address_ok && et_place.length () != 0) {
                create_event2.setVisibility (View.GONE);
                create_event3.setVisibility (View.VISIBLE);
            } else {
                Toast.makeText (CreateEventActivity.this, R.string.please_fill_empty_forms, Toast.LENGTH_SHORT).show ();
            }
        } else {
            if (et_quantity.length () != 0 && et_price.length () != 0 && address_ok && et_place.length () != 0) {
                create_event2.setVisibility (View.GONE);
                create_event3.setVisibility (View.VISIBLE);
            } else {
                Toast.makeText (CreateEventActivity.this, R.string.please_fill_empty_forms, Toast.LENGTH_SHORT).show ();
            }
        }
    }

    public void saveEvent() {
        final Event event = new Event ();
        event.setName (et_name.getText ().toString ());
        event.setDescription (et_description.getText ().toString ());

        if (freeEvent) {
            event.setPrice ("FREE");
            event.setNumOfTickets (99999);
        } else if (!seats) {
            event.setNumOfTickets (Integer.parseInt (et_quantity.getText ().toString ()));
            event.setPrice (et_price.getText ().toString ());
        } else if (seats) {
            List<Integer> sum = new ArrayList<> ();
            sum.add (sp.getInt (GlobalVariables.ORANGE, 0));
            sum.add (sp.getInt (GlobalVariables.PINK, 0));
            sum.add (sp.getInt (GlobalVariables.BLUE, 0));
            sum.add (sp.getInt (GlobalVariables.YELLOW, 0));
            sum.add (sp.getInt (GlobalVariables.GREEN, 0));
            int max = Collections.max (sum);
            int min = Collections.min (sum);
            event.setPrice ("" + min + "-" + max + "");
            event.setNumOfTickets (101);
        }
        event.setAddress (valid_address);
        event.setCity (city);
        event.setX (lat);
        event.setY (lng);
        //===========================Setting tags the right way==============
        StringBuilder stringBuilder = new StringBuilder ();
        if (et_tags.length () == 0) {
            event.setTags ("#" + filter);
        } else {
            stringBuilder.append ("#" + filter);
            String str = et_tags.getText ().toString ();
            str = str.replaceAll (",", " ");
            str = str.replaceAll ("#", "");
            String[] arr = str.split (" ");

            for (String ss : arr) {
                if (!ss.equals (" ") && !ss.equals ("")) {
                    stringBuilder.append (" #" + ss);
                }
            }
            String finalString = stringBuilder.toString ();
            // finalString.replaceAll("# ","");
            event.setTags (finalString);

        }
        //===================================================================
        if (seats) {
            blueIncome = 4 * sp.getInt (GlobalVariables.BLUE, 0);
            orangeIncome = 17 * sp.getInt (GlobalVariables.ORANGE, 0);
            pinkIncome = 17 * sp.getInt (GlobalVariables.PINK, 0);
            pinkIncome = pinkIncome + 16 * sp.getInt (GlobalVariables.PINK, 0);
            yellowIncome = 17 * sp.getInt (GlobalVariables.YELLOW, 0);
            yellowIncome = yellowIncome + 16 * sp.getInt (GlobalVariables.YELLOW, 0);
            greenIncome = 7 * sp.getInt (GlobalVariables.GREEN, 0);
            greenIncome = greenIncome + 7 * sp.getInt (GlobalVariables.GREEN, 0);
            totalIncome = pinkIncome + yellowIncome + greenIncome + blueIncome + orangeIncome;

        } else {
            if (!freeEvent) {
                totalIncome = Integer.parseInt (et_price.getText ().toString ()) * Integer.parseInt (et_quantity.getText ().toString ());
            } else {
                totalIncome = 0;
            }
        }
        event.setFilterName (filter);
        event.setProducerId (GlobalVariables.PRODUCER_PARSE_OBJECT_ID);
        event.setRealDate (realDate);
        event.setPlace (et_place.getText ().toString ());
        event.setArtist (et_artist.getText ().toString ());
        event.setEventToiletService (numOfToilets + ", Handicapped " + numOfHandicapToilets);
        String eventParkingService;
        if (et_parking.getText ().toString ().equals ("")) {
            eventParkingService = "";
        } else {
            eventParkingService = "Up To " + et_parking.getText ().toString ();
        }
        event.setEventParkingService (eventParkingService);
        String eventCapacityService;
        if (et_capacity.getText ().toString ().equals ("")) {
            eventCapacityService = "";
        } else {
            eventCapacityService = "Up To " + et_capacity.getText ().toString ();
        }
        event.setEventCapacityService (eventCapacityService);
        event.setEventATMService (atmStatus);
        if (pictureSelected) {
            pic.buildDrawingCache ();
            Bitmap bitmap = pic.getDrawingCache ();
            ByteArrayOutputStream stream = new ByteArrayOutputStream ();
            bitmap.compress (Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] image = stream.toByteArray ();
            ParseFile file = new ParseFile ("picturePath", image);
            try {
                file.save ();
            } catch (ParseException e) {
                e.printStackTrace ();
            }
            event.put ("ImageFile", file);
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource (this.getResources (),
                                                                 R.drawable.event);
            ByteArrayOutputStream stream = new ByteArrayOutputStream ();
            bitmap.compress (Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] image = stream.toByteArray ();
            ParseFile file = new ParseFile ("picturePath", image);
            try {
                file.save ();
            } catch (ParseException e) {
                e.printStackTrace ();
            }
            event.put ("ImageFile", file);
        }

        try {
            if (seats) {
                event.setIsStadium (true);
            } else {
                event.setIsStadium (false);
            }
            event.save ();
            totalIncome = 0;
            if (!freeEvent) {
                Log.e (TAG, "freeEvent " + freeEvent);
                eventObjectId = event.getObjectId ();
                Log.e (TAG, "objectId " + eventObjectId);
                if (seats) {
                    saveTicketsPrice (eventObjectId);
                    //the producer did not chose colored seats
                } else {
                    totalIncome = Integer.parseInt (et_price.getText ().toString ()) * Integer.parseInt (et_quantity.getText ().toString ());
                }
                Snackbar snackbar = Snackbar
                                            .make (linearLayout, "Expected income:" + totalIncome, Snackbar.LENGTH_LONG)
                                            .setAction ("UNDO", new View.OnClickListener () {
                                                @Override
                                                public void onClick(View view) {
                                                    deleteEvent (eventObjectId);

                                                }
                                            });
                snackbar.setActionTextColor (Color.YELLOW);
                View snackbarView = snackbar.getView ();
                snackbarView.setBackgroundColor (Color.DKGRAY);
                TextView textView = (TextView) snackbarView.findViewById (android.support.design.R.id.snackbar_text);
                textView.setTextColor (Color.WHITE);
                snackbar.show ();
                new Handler ().postDelayed (new Runnable () {
                    @Override
                    public void run() {
                        finish ();
                    }
                }, 3000);
            }
            GlobalVariables.refreshArtistsList = true;
            Toast.makeText (this, R.string.event_has_created_successfully, Toast.LENGTH_LONG);//TODO
            finish ();
        } catch (ParseException e) {
            e.printStackTrace ();
        }
    }

    public void deleteEvent(final String objectId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery ("Event");
        query.whereEqualTo ("objectId", objectId);
        query.orderByDescending ("createdAt");
        query.getFirstInBackground (new GetCallback<ParseObject> () {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    try {
                        object.delete ();
                        Log.e (TAG, "Event deleted");
                    } catch (ParseException e1) {
                        e1.printStackTrace ();
                        Log.e (TAG, "Event not deleted " + e1.toString ());
                    }
                    object.saveInBackground ();
                }
            }
        });
        if (seats) {
            ParseQuery<ParseObject> querySeats = ParseQuery.getQuery ("EventsSeats");
            querySeats.whereEqualTo ("eventObjectId", objectId);
            querySeats.findInBackground (new FindCallback<ParseObject> () {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (objects.size () != 0) {
                        ParseObject.deleteAllInBackground (objects);

                    }
                }
            });
            //query and delete again because there is one last ticket left
            ParseQuery<ParseObject> querySeats1 = ParseQuery.getQuery ("EventsSeats");
            querySeats1.whereEqualTo ("eventObjectId", objectId);
            querySeats1.getFirstInBackground (new GetCallback<ParseObject> () {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        try {

                            object.delete ();
                        } catch (ParseException e1) {
                            e1.printStackTrace ();
                        }
                    } else {
                        Log.e (TAG, "" + e.toString ());
                    }
                }
            });
            //===================================================================
        }
        finish ();
    }

    @Override
    public void onBackPressed() {
        if (timePickerDialog != null && timePickerDialog.isShowing ()) {
            timePickerDialog.dismiss ();
        }
        if (datePickerDialog != null && datePickerDialog.isShowing ()) {
            datePickerDialog.dismiss ();
        }

        if (et_name.getVisibility () == View.VISIBLE) {
            AlertDialog.Builder builder = new AlertDialog.Builder (this);
            builder.setMessage (R.string.are_you_sure_you_want_to_exit)
                    .setCancelable (false)
                    .setPositiveButton ("Yes", new DialogInterface.OnClickListener () {
                        public void onClick(DialogInterface dialog, int id) {
                            CreateEventActivity.this.finish ();
                        }
                    })
                    .setNegativeButton ("No", new DialogInterface.OnClickListener () {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel ();
                        }
                    });
            AlertDialog alert = builder.create ();
            alert.show ();
        }
    }

    private void componentInit() {
        linearLayout = (LinearLayout) findViewById (R.id.create_profile_layout);
        tv_create = (TextView) findViewById (R.id.tv_create);
        tv_name = (TextView) findViewById (R.id.tv_name);
        tv_artist = (TextView) findViewById (R.id.tv_address);
        tv_description = (TextView) findViewById (R.id.tv_description);
        tv_quantity = (TextView) findViewById (R.id.tv_quantity);
        tv_price = (TextView) findViewById (R.id.tv_price);
        et_name = (EditText) findViewById (R.id.et_name);
        et_artist = (EditText) findViewById (R.id.et_artist);
        et_description = (EditText) findViewById (R.id.et_description);
        et_price = (EditText) findViewById (R.id.et_price);
        et_quantity = (EditText) findViewById (R.id.et_quantity);
        et_address = (EditText) findViewById (R.id.et_address);
        et_place = (EditText) findViewById (R.id.et_place);
        et_capacity = (EditText) findViewById (R.id.et_capacity);
        et_parking = (EditText) findViewById (R.id.et_parking);
        et_tags = (EditText) findViewById (R.id.et_tags);
        btn_validate_address = (Button) findViewById (R.id.btn_validate_address);
        iv_val_add = (ImageView) findViewById (R.id.iv_val_add);
        freeBox = (CheckBox) findViewById (R.id.checkBoxFree);
        checkBoxPrice = (CheckBox) findViewById (R.id.checkBoxPrice);
        tv_date_new = (TextView) findViewById (R.id.tv_date_new);
        btn_date = (Button) findViewById (R.id.btn_date);
        btn_next = (Button) findViewById (R.id.btn_next);
        btn_next1 = (Button) findViewById (R.id.btn_next1);
        btn_next2 = (Button) findViewById (R.id.btn_next2);
        btn_pic = (Button) findViewById (R.id.btn_pic);
        pic = (ImageView) findViewById (R.id.pic);
        btn_next.setOnClickListener (this);
        btn_next1.setOnClickListener (this);
        btn_next2.setOnClickListener (this);
        btn_pic.setOnClickListener (this);
        btn_validate_address.setOnClickListener (this);
        btn_date.setOnClickListener (this);
        freeBox.setOnCheckedChangeListener (this);
        checkBoxPrice.setOnCheckedChangeListener (this);
        create_event2 = (ScrollView) findViewById (R.id.create_event2);
        create_event3 = (ScrollView) findViewById (R.id.create_event3);
        ll_name = (LinearLayout) findViewById (R.id.ll_name);
        ll_date = (LinearLayout) findViewById (R.id.ll_date);
        ll_artist = (LinearLayout) findViewById (R.id.ll_artist);
        ll_description = (LinearLayout) findViewById (R.id.ll_description);
        btn_price_details = (Button) findViewById (R.id.btn_price_details);
        btn_price_details.setOnClickListener (this);

//===============================Filter Spinner stuff==================================
        FILTERS = getResources ().getStringArray (R.array.filters);
        ArrayAdapter<String> filterSpinnerAdapter = new ArrayAdapter<> (this, android.R.layout.simple_spinner_item, FILTERS);
        filterSpinnerAdapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
        filterSpinner = (MaterialSpinner) findViewById (R.id.filterSpinner);
        filterSpinner = (MaterialSpinner) findViewById (R.id.filterSpinner);
        filterSpinner.setAdapter (filterSpinnerAdapter);
        filterSpinner.setOnItemSelectedListener (this);
//=============================================================================

// ===============================ATM Spinner  stuff==================================
        ATMS = getResources ().getStringArray (R.array.atms);
        ArrayAdapter<String> atmSpinnerAdapter = new ArrayAdapter<> (this, android.R.layout.simple_spinner_item, ATMS);
        atmSpinnerAdapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
        atmSpinner = (MaterialSpinner) findViewById (R.id.atmSpinner);
        atmSpinner.setAdapter (atmSpinnerAdapter);
        atmSpinner.setOnItemSelectedListener (this);
//==============================================================================
// ===============================Toilet Spinner  stuff==================================
        TOILETS = getResources ().getStringArray (R.array.toilets);
        ArrayAdapter<String> toiletSpinnerAdapter = new ArrayAdapter<> (this, android.R.layout.simple_spinner_item, TOILETS);
        toiletSpinnerAdapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
        toiletSpinner = (MaterialSpinner) findViewById (R.id.toiletSpinner);
        toiletSpinner.setAdapter (toiletSpinnerAdapter);
        toiletSpinner.setOnItemSelectedListener (this);
//==============================================================================
// ===============================handicapToilet Spinner  stuff==================================
        handicapToiletSpinner = (MaterialSpinner) findViewById (R.id.handicapToiletSpinner);
        handicapToiletSpinner.setAdapter (toiletSpinnerAdapter);
        handicapToiletSpinner.setOnItemSelectedListener (this);
//==============================================================================
    }

    /**
     * FREE CHECKBOX LISTENER:
     *
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit ();
        switch (buttonView.getId ()) {
            case R.id.checkBoxFree:
                if (isChecked) {
                    freeEvent = true;
                    et_quantity.setVisibility (View.GONE);
                    tv_quantity.setVisibility (View.GONE);
                    tv_price.setVisibility (View.GONE);
                    et_price.setVisibility (View.GONE);
                    btn_price_details.setVisibility (View.GONE);
                    checkBoxPrice.setVisibility (View.GONE);
                } else {
                    freeEvent = false;
                    et_quantity.setVisibility (View.VISIBLE);
                    tv_quantity.setVisibility (View.VISIBLE);
                    tv_price.setVisibility (View.VISIBLE);
                    et_price.setVisibility (View.VISIBLE);
                    if (!checkBoxPrice.isChecked ()) {
                        btn_price_details.setVisibility (View.VISIBLE);
                    }
                    checkBoxPrice.setVisibility (View.GONE);
                }
                break;
            case R.id.checkBoxPrice:
                if (isChecked) {
                    et_quantity.setVisibility (View.GONE);
                    tv_quantity.setVisibility (View.GONE);
                    tv_price.setVisibility (View.GONE);
                    et_price.setVisibility (View.GONE);
                    btn_price_details.setVisibility (View.VISIBLE);
                    editor.putBoolean (GlobalVariables.SEATS, true);
                    editor.apply ();
                } else {
                    et_quantity.setVisibility (View.VISIBLE);
                    tv_quantity.setVisibility (View.VISIBLE);
                    tv_price.setVisibility (View.VISIBLE);
                    et_price.setVisibility (View.VISIBLE);
                    btn_price_details.setVisibility (View.GONE);
                    editor.putBoolean (GlobalVariables.SEATS, false);
                    editor.apply ();
                }

                break;
        }
    }

    /**
     * Spinner items selected
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId ()) {
            case R.id.filterSpinner:
                et_tags.setHint ("");
                switch (position) {
                    case 0:
                        filter = FILTERS[0];
                        et_tags.setHint ("Your first tag is #" + filter + " add more");
                        break;
                    case 1:
                        filter = FILTERS[1];
                        et_tags.setHint ("Your first tag is #" + filter + " add more");
                        break;
                    case 2:
                        filter = FILTERS[2];
                        et_tags.setHint ("Your first tag is #" + filter + " add more");
                        break;
                    case 3:
                        filter = FILTERS[3];
                        et_tags.setHint ("Your first tag is #" + filter + " add more");
                        break;
                    case 4:
                        filter = FILTERS[3];
                        et_tags.setHint ("Your first tag is #" + filter + " add more");
                        break;
                    case 5:
                        filter = FILTERS[3];
                        et_tags.setHint ("Your first tag is #" + filter + " add more");
                        break;
                    case 6:
                        filter = FILTERS[3];
                        et_tags.setHint ("Your first tag is #" + filter + " add more");
                        break;
                    case 7:
                        filter = FILTERS[3];
                        et_tags.setHint ("Your first tag is #" + filter + " add more");
                        break;
                    case 8:
                        filter = FILTERS[3];
                        et_tags.setHint ("Your first tag is #" + filter + " add more");
                        break;

                }
                break;

            case R.id.atmSpinner:
                switch (position) {
                    case 0:
                        atmStatus = ATMS[0];
                        break;
                    case 1:
                        atmStatus = ATMS[1];
                        break;
                    case 2:
                        atmStatus = ATMS[2];
                        break;
                }
                break;
            case R.id.toiletSpinner:
                switch (position) {
                    case 0:
                        numOfToilets = TOILETS[0];
                        break;
                    case 1:
                        numOfToilets = TOILETS[1];
                        break;
                    case 2:
                        numOfToilets = TOILETS[2];
                        break;
                    case 3:
                        numOfToilets = TOILETS[3];
                        break;
                    case 4:
                        numOfToilets = TOILETS[4];
                        break;
                    case 5:
                        numOfToilets = TOILETS[5];
                        break;
                    case 6:
                        numOfToilets = TOILETS[6];
                        break;
                    case 7:
                        numOfToilets = TOILETS[7];
                        break;
                    case 8:
                        numOfToilets = TOILETS[8];
                        break;
                    case 9:
                        numOfToilets = TOILETS[9];
                        break;
                    case 10:
                        numOfToilets = TOILETS[10];
                        break;

                }
                break;
            case R.id.handicapToiletSpinner:
                //               numOfHandicapToilets = TOILETS[position];
                // java.lang.ArrayIndexOutOfBoundsException: length=11; index=-1
                switch (position) {
                    case 0:
                        numOfHandicapToilets = TOILETS[0];
                        break;
                    case 1:
                        numOfHandicapToilets = TOILETS[1];
                        break;
                    case 2:
                        numOfHandicapToilets = TOILETS[2];
                        break;
                    case 3:
                        numOfHandicapToilets = TOILETS[3];
                        break;
                    case 4:
                        numOfHandicapToilets = TOILETS[4];
                        break;
                    case 5:
                        numOfHandicapToilets = TOILETS[5];
                        break;
                    case 6:
                        numOfHandicapToilets = TOILETS[6];
                        break;
                    case 7:
                        numOfHandicapToilets = TOILETS[7];
                        break;
                    case 8:
                        numOfHandicapToilets = TOILETS[8];
                        break;
                    case 9:
                        numOfHandicapToilets = TOILETS[9];
                        break;
                    case 10:
                        numOfHandicapToilets = TOILETS[10];
                        break;

                }
                break;
        }


    }


    /**
     * Nothing selected in the Spinners
     *
     * @param parent
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        switch (parent.getId ()) {
            case R.id.filterSpinner:
                filter = null;
                et_tags.setHint ("");
                break;
            case R.id.atmSpinner:
                atmStatus = "";
                break;
            case R.id.toiletSpinner:
                numOfToilets = "";
                break;
            case R.id.handicapToiletSpinner:
                numOfToilets = "";
                break;
        }

    }


    class ValidateAddress extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog (CreateEventActivity.this);
            dialog.setMessage ("" + R.string.validating);
            dialog.show ();
        }

        // ----------------------------------------------------
        @Override
        protected String doInBackground(String... params) {
            dialog.dismiss ();
            String queryString = null;
            try {
                queryString = "" +
                                      "&address=" + URLEncoder.encode (address, "utf-8") +
                                      "&key=" + GlobalVariables.GEO_API_KEY;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace ();
            }


            return HttpHandler.get (params[0], queryString);
        }

        // ----------------------------------------------------
        @Override
        protected void onPostExecute(String s) {

            if (s == null) {
                Toast.makeText (CreateEventActivity.this, R.string.something_went_wrong_plese_try_again, Toast.LENGTH_SHORT).show ();
                iv_val_add.setImageResource (R.drawable.x);
                iv_val_add.setVisibility (View.VISIBLE);

            } else {
                gson = new Gson ();
                result = gson.fromJson (s, Result.class);
                if (result.getStatus ().equals ("OK")) {
                    address_ok = true;
                    iv_val_add.setImageResource (R.drawable.v);
                    iv_val_add.setVisibility (View.VISIBLE);
                    String long_name = result.getResults ().get (0).getAddress_components ().get (1).getLong_name ();
                    String street = long_name.replaceAll ("Street", "");
                    String number = result.getResults ().get (0).getAddress_components ().get (0).getShort_name ();
                    lat = result.getResults ().get (0).getGeometry ().getLocation ().getLat ();
                    lng = result.getResults ().get (0).getGeometry ().getLocation ().getLng ();
                    city = result.getResults ().get (0).getAddress_components ().get (2).getShort_name ();
                    valid_address = street + number + ", " + city;

                } else if (result.getStatus ().equals ("ZERO_RESULTS")) {
                    address_ok = false;
                    iv_val_add.setImageResource (R.drawable.x);
                    iv_val_add.setVisibility (View.VISIBLE);
                    Toast.makeText (CreateEventActivity.this, R.string.problem_is + result.getStatus (), Toast.LENGTH_SHORT).show ();
                }
            }
        }
    }

    private void saveTicketsPrice(String eventObjectId) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences (this);
        SharedPreferences.Editor editor = sp.edit ();
        HashMap<String, String> map = new HashMap<> ();
        map.put ("start", "1");
        map.put ("end", "4");
        map.put ("price", "" + sp.getInt (GlobalVariables.BLUE, 0));
        map.put ("eventObjectId", eventObjectId);
        map.put ("seatNumber", "Floor ");
        ParseCloud.callFunctionInBackground ("saveTicketsPrice", map, new FunctionCallback<Object> () {
            @Override
            public void done(Object response, ParseException exc) {
                Log.e ("cloud code example", "response: " + response);
            }
        });

        map.put ("start", "11");
        map.put ("end", "27");
        map.put ("price", "" + sp.getInt (GlobalVariables.ORANGE, 0));
        map.put ("eventObjectId", eventObjectId);
        map.put ("seatNumber", "Orange");
        ParseCloud.callFunctionInBackground ("saveTicketsPrice", map, new FunctionCallback<Object> () {
            @Override
            public void done(Object response, ParseException exc) {
                Log.e ("cloud code example", "response: " + response);
            }
        });

        map.put ("start", "101");
        map.put ("end", "117");
        map.put ("price", "" + sp.getInt (GlobalVariables.PINK, 0));
        map.put ("eventObjectId", eventObjectId);
        map.put ("seatNumber", "Pink");
        ParseCloud.callFunctionInBackground ("saveTicketsPrice", map, new FunctionCallback<Object> () {
            @Override
            public void done(Object response, ParseException exc) {
                Log.e ("cloud code example", "response: " + response);
            }
        });

        map.put ("start", "121");
        map.put ("end", "136");
        map.put ("price", "" + sp.getInt (GlobalVariables.PINK, 0));
        map.put ("eventObjectId", eventObjectId);
        map.put ("seatNumber", "Pink");
        ParseCloud.callFunctionInBackground ("saveTicketsPrice", map, new FunctionCallback<Object> () {
            @Override
            public void done(Object response, ParseException exc) {
                Log.e ("cloud code example", "response: " + response);
            }
        });

        map.put ("start", "201");
        map.put ("end", "217");
        map.put ("price", "" + sp.getInt (GlobalVariables.YELLOW, 0));
        map.put ("eventObjectId", eventObjectId);
        map.put ("seatNumber", "Yellow");
        ParseCloud.callFunctionInBackground ("saveTicketsPrice", map, new FunctionCallback<Object> () {
            @Override
            public void done(Object response, ParseException exc) {
                Log.e ("cloud code example", "response: " + response);
            }
        });


        map.put ("start", "221");
        map.put ("end", "236");
        map.put ("price", "" + sp.getInt (GlobalVariables.YELLOW, 0));
        map.put ("eventObjectId", eventObjectId);
        map.put ("seatNumber", "Yellow");
        try {
            Integer result = ParseCloud.callFunction ("saveTicketsPrice", map);
        } catch (ParseException e) {
            e.printStackTrace ();
        }

        map.put ("start", "207");
        map.put ("end", "213");
        map.put ("price", "" + sp.getInt (GlobalVariables.GREEN, 0));
        map.put ("eventObjectId", eventObjectId);
        map.put ("seatNumber", "green");
        ParseCloud.callFunctionInBackground ("saveTicketsPrice", map, new FunctionCallback<Object> () {
            @Override
            public void done(Object response, ParseException exc) {
                Log.e ("cloud code example", "response: " + response);
            }
        });

        map.put ("start", "225");
        map.put ("end", "231");
        map.put ("price", "" + sp.getInt (GlobalVariables.GREEN, 0));
        map.put ("eventObjectId", eventObjectId);
        map.put ("seatNumber", "green");
        ParseCloud.callFunctionInBackground ("saveTicketsPrice", map, new FunctionCallback<Object> () {
            @Override
            public void done(Object response, ParseException exc) {
                Log.e ("cloud code example", "response: " + response);
            }
        });
        editor.putBoolean (GlobalVariables.SEATS, false);
        editor.apply ();
    }
}

