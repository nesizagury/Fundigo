package com.example.FundigoApp.Events;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.google.gson.Gson;
import com.parse.GetCallback;
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
import java.util.Calendar;
import java.util.Date;

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
    EditText et_toilet;
    Button btn_validate_address;
    ImageView iv_val_add;
    Button btn_next;
    Button btn_next1;
    Button btn_next2;
    Button btn_pic;
    ImageView pic;
    LinearLayout create_event2;
    LinearLayout create_event3;
    LinearLayout ll_name;
    LinearLayout ll_date;
    LinearLayout ll_artist;
    LinearLayout ll_description;
    CheckBox atmBox;
    private static final int SELECT_PICTURE = 1;
    String picturePath;
    private boolean pictureSelected;
    private boolean address_ok = false;
    Gson gson;
    Result result;
    String address;
    String income;
    String sold;
    private String valid_address;
    private double lat;
    private double lng;
    private String city;
    private Button btn_date;
    private TextView tv_date_new;
    private String date;
    private String time;
    int year;
    int monthOfYear;
    int dayOfMonth;
    int hourOfDay;
    int minute;
    private boolean timeOk = false;
    private Date realDate;
    private boolean priceOk = false;
    private boolean freeEvent = false;
    private boolean quantityOk = false;
    private CheckBox freeBox;
    private TextView tv_quantity;
    private TimePickerDialog timePickerDialog;
    private DatePickerDialog datePickerDialog;
    String[] ITEMS;
    private MaterialSpinner spinner;
    private String filter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        componentInit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                if (timeOk) {
                    showSecondStage();
                } else {
                    Toast.makeText(CreateEventActivity.this, "Please enter valid date", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_next1:
                Log.e(TAG, "event is free " + freeEvent);
                if (freeEvent) {
                    if (address_ok) {
                        showThirdStage();
                    } else {
                        Toast.makeText(CreateEventActivity.this, "Please enter valid address", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (!validatePrice() || !validateQuantity()) {
                        Toast.makeText(CreateEventActivity.this, "Please enter valid price or quantity", Toast.LENGTH_SHORT).show();
                    } else {
                        if (address_ok) {
                            showThirdStage();
                        } else {
                            Toast.makeText(CreateEventActivity.this, "Please enter valid address", Toast.LENGTH_SHORT).show();
                        }
                    }

                }


                break;
            case R.id.btn_validate_address:
                validateAddress();
                break;
            case R.id.btn_next2:
                if (filter != null) {
                    saveEvent();
                } else {
                    Toast.makeText(CreateEventActivity.this, "Please choose a filter", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_pic:
                uploadPic();
                break;
            case R.id.btn_date:
                int year = Calendar.getInstance().get(Calendar.YEAR);
                int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                int month = Calendar.getInstance().get(Calendar.MONTH);
                datePickerDialog = new DatePickerDialog(this, listener, year, month, day);
                datePickerDialog.show();
                break;
        }
    }

    public boolean validatePrice() {
        String str = et_price.getText().toString();
        if (str.equals("0")) {
            return false;
        }
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    public boolean validateQuantity() {
        String str = et_quantity.getText().toString();
        if (str.equals("0")) {
            return false;
        }
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int y, int m, int d) {
            timePickerDialog = new TimePickerDialog(CreateEventActivity.this, timeListener, 12, 12, true);
            timePickerDialog.show();
            year = y;
            monthOfYear = m;
            dayOfMonth = d;
            date = dayOfMonth + "." + (monthOfYear + 1) + "." + year;
            tv_date_new.setText(date);
            tv_date_new.setVisibility(View.VISIBLE);

        }
    };

    TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String min;
            if (minute < 10) {
                min = "0" + minute;
            } else {
                min = "" + minute;
            }
            time = hourOfDay + ":" + min;
            Log.e(TAG, "time " + time);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            cal.set(Calendar.MONTH, monthOfYear);
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            cal.set(Calendar.MINUTE, minute);
            realDate = new Date(cal.getTimeInMillis());
            Log.e(TAG, "CAL " + cal.getTimeInMillis() + " now " + System.currentTimeMillis());

            if (cal.getTimeInMillis() <= System.currentTimeMillis()) {
                Toast.makeText(CreateEventActivity.this, "Are you living in the past?", Toast.LENGTH_SHORT).show();
                timeOk = false;
            } else {
                timeOk = true;
            }
        }
    };


    private void uploadPic() {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            ParcelFileDescriptor parcelFileDescriptor =
                    null;
            try {
                parcelFileDescriptor = getContentResolver().openFileDescriptor(selectedImage, "r");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            try {
                parcelFileDescriptor.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Matrix matrix = new Matrix();
            int angleToRotate = getOrientation(selectedImage);
            matrix.postRotate(angleToRotate);
            Bitmap rotatedBitmap = Bitmap.createBitmap(image,
                    0,
                    0,
                    image.getWidth(),
                    image.getHeight(),
                    matrix,
                    true);
            pic.setImageBitmap(rotatedBitmap);
            pic.setVisibility(View.VISIBLE);
            pictureSelected = true;
        }
    }

    public int getOrientation(Uri selectedImage) {
        int orientation = 0;
        final String[] projection = new String[]{MediaStore.Images.Media.ORIENTATION};
        final Cursor cursor = this.getContentResolver().query(selectedImage, projection, null, null, null);
        if (cursor != null) {
            final int orientationColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION);
            if (cursor.moveToFirst()) {
                orientation = cursor.isNull(orientationColumnIndex) ? 0 : cursor.getInt(orientationColumnIndex);
            }
            cursor.close();
        }
        return orientation;
    }

    private void showSecondStage() {
        if (et_name.length() != 0 && date.length() != 0 && et_artist.length() != 0 && et_description.length() != 0) {
            tv_create.setVisibility(View.GONE);
            ll_name.setVisibility(View.GONE);
            ll_date.setVisibility(View.GONE);
            ll_artist.setVisibility(View.GONE);
            ll_description.setVisibility(View.GONE);
            btn_next.setVisibility(View.GONE);
            create_event2.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(CreateEventActivity.this, "Please fill empty forms", Toast.LENGTH_SHORT).show();
        }
    }

    private void validateAddress() {
        address = et_address.getText().toString();
        iv_val_add.setVisibility(View.INVISIBLE);
        new ValidateAddress().execute(GlobalVariables.GEO_API_ADDRESS);
    }

    private void showThirdStage() {
        if (freeEvent && address_ok) {
            create_event2.setVisibility(View.GONE);
            create_event3.setVisibility(View.VISIBLE);
        } else if (et_quantity.length() != 0 && et_price.length() != 0 && address_ok && et_place.length() != 0) {
            create_event2.setVisibility(View.GONE);
            create_event3.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(CreateEventActivity.this, "Please fill empty forms", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveEvent() {
        final Event event = new Event();
        if (et_tags.getText().length() != 0) {
            if (getIntent().getStringExtra("create").equals("false")) {
                deleteRow();
                event.setSold(sold);
                event.setIncome(income);
            } else {
                event.setSold("0");
                event.setIncome("0");
            }
            event.setName(et_name.getText().toString());
            event.setDescription(et_description.getText().toString());

            if (freeEvent) {
                event.setPrice("FREE");
                event.setNumOfTicketsLeft("9999");
            } else {
                event.setNumOfTicketsLeft(et_quantity.getText().toString());
                event.setPrice(et_price.getText().toString());
            }
            event.setAddress(valid_address);
            event.setCity(city);
            event.setX(lat);
            event.setY(lng);
            //===========================Setting tags the right way==============
            StringBuilder stringBuilder = new StringBuilder();
            if (et_tags.length() == 0) {
                event.setTags("#" + filter);
            } else {
                stringBuilder.append("#" + filter);
                String str = et_tags.getText().toString();
                str = str.replaceAll(",", " ");
                str = str.replaceAll("#", "");
                String[] arr = str.split(" ");

                for (String ss : arr) {
                    if (!ss.equals(" ") && !ss.equals("")) {
                        stringBuilder.append(" #" + ss);
                    }
                    Log.e(TAG, "!" + ss+"!");
                }
                String finalString = stringBuilder.toString();
               // finalString.replaceAll("# ","");
                Log.e(TAG, "finalString " + finalString);
                event.setTags(finalString);

            }
            //===================================================================
            event.setFilterName(filter);
            event.setProducerId(GlobalVariables.PRODUCER_PARSE_OBJECT_ID);
            event.setDate(date);
            event.setPlace(et_place.getText().toString());
            event.setArtist(et_artist.getText().toString());
            event.setEventToiletService(et_toilet.getText().toString());
            event.setEventParkingService(et_parking.getText().toString());
            event.setEventCapacityService(et_capacity.getText().toString());
            event.setRealDate(realDate);

            if (atmBox.isChecked())
                event.setEventATMService("Yes");
            else
                event.setEventATMService("No");

            if (pictureSelected || tv_create.getText().toString().equals("Edit Event")) {
                pic.buildDrawingCache();
                Bitmap bitmap = pic.getDrawingCache();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] image = stream.toByteArray();
                ParseFile file = new ParseFile("picturePath", image);
                try {
                    file.save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                event.put("ImageFile", file);
            }


            try {
                event.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(), "Event has created successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else
            Toast.makeText(getApplicationContext(), "Please fill the  empty fields", Toast.LENGTH_SHORT).show();

    }

    public void deleteRow() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.whereEqualTo("objectId", getIntent().getStringExtra("eventObjectId"));
        query.orderByDescending("createdAt");
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    try {

                        object.delete();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    object.saveInBackground();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (timePickerDialog.isShowing()) {
            timePickerDialog.dismiss();
        }
        if (datePickerDialog.isShowing()) {
            datePickerDialog.dismiss();
        }

        if (et_name.getVisibility() == View.VISIBLE) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            CreateEventActivity.this.finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }


    private void componentInit() {
        tv_create = (TextView) findViewById(R.id.tv_create);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_artist = (TextView) findViewById(R.id.tv_address);
        tv_description = (TextView) findViewById(R.id.tv_description);
        tv_quantity = (TextView) findViewById(R.id.tv_quantity);
        tv_price = (TextView) findViewById(R.id.tv_price);
        et_name = (EditText) findViewById(R.id.et_name);
        et_artist = (EditText) findViewById(R.id.et_artist);
        et_description = (EditText) findViewById(R.id.et_description);
        et_price = (EditText) findViewById(R.id.et_price);
        et_quantity = (EditText) findViewById(R.id.et_quantity);
        et_address = (EditText) findViewById(R.id.et_address);
        et_place = (EditText) findViewById(R.id.et_place);
        et_capacity = (EditText) findViewById(R.id.et_capacity);
        et_parking = (EditText) findViewById(R.id.et_parking);
        et_tags = (EditText) findViewById(R.id.et_tags);
        et_toilet = (EditText) findViewById(R.id.et_toilet);
        btn_validate_address = (Button) findViewById(R.id.btn_validate_address);
        iv_val_add = (ImageView) findViewById(R.id.iv_val_add);
        atmBox = (CheckBox) findViewById(R.id.checkBoxAtm);
        freeBox = (CheckBox) findViewById(R.id.checkBoxFree);
        tv_date_new = (TextView) findViewById(R.id.tv_date_new);
        btn_date = (Button) findViewById(R.id.btn_date);
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_next1 = (Button) findViewById(R.id.btn_next1);
        btn_next2 = (Button) findViewById(R.id.btn_next2);
        btn_pic = (Button) findViewById(R.id.btn_pic);
        pic = (ImageView) findViewById(R.id.pic);
        btn_next.setOnClickListener(this);
        btn_next1.setOnClickListener(this);
        btn_next2.setOnClickListener(this);
        btn_pic.setOnClickListener(this);
        btn_validate_address.setOnClickListener(this);
        btn_date.setOnClickListener(this);
        freeBox.setOnCheckedChangeListener(this);
        create_event2 = (LinearLayout) findViewById(R.id.create_event2);
        create_event3 = (LinearLayout) findViewById(R.id.create_event3);
        ll_name = (LinearLayout) findViewById(R.id.ll_name);
        ll_date = (LinearLayout) findViewById(R.id.ll_date);
        ll_artist = (LinearLayout) findViewById(R.id.ll_artist);
        ll_description = (LinearLayout) findViewById(R.id.ll_description);
//===============================Spinner stuff==================================
        ITEMS = getResources().getStringArray(R.array.filters);
        Log.e(TAG, "ITEMS ARE " + ITEMS[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (MaterialSpinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
//==============================================================================
        if (!getIntent().getStringExtra("create").equals("true")) {
            tv_create.setText("Edit Event");
            et_name.setText("" + getIntent().getStringExtra("name"));

            for (int i = 0; i < GlobalVariables.ALL_EVENTS_DATA.size(); i++) {
                EventInfo event = GlobalVariables.ALL_EVENTS_DATA.get(i);
                if (event.getParseObjectId().equals(getIntent().getStringExtra("eventObjectId"))) {
                    income = event.getIncome();
                    sold = event.getSold();
                    et_artist.setText(event.getArtist());
                    et_description.setText(event.getInfo());
                    et_price.setText(event.getPrice());
                    et_quantity.setText(event.getTicketsLeft());
                    et_address.setText(event.getPlace());
                    pic.setImageBitmap(event.getImageBitmap());
                    et_tags.setText(event.getTags());
                }
            }
        }
    }

    /**
     * FREE CHECKBOX LISTENER:
     *
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.checkBoxFree:
                if (isChecked) {
                    freeEvent = true;
                    et_quantity.setVisibility(View.GONE);
                    tv_quantity.setVisibility(View.GONE);
                    tv_price.setVisibility(View.GONE);
                    et_price.setVisibility(View.GONE);
                } else {
                    freeEvent = false;
                    et_quantity.setVisibility(View.VISIBLE);
                    tv_quantity.setVisibility(View.VISIBLE);
                    tv_price.setVisibility(View.VISIBLE);
                    et_price.setVisibility(View.VISIBLE);
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
        et_tags.setHint("");
        switch (position) {
            case 0:
                filter = ITEMS[0];
                et_tags.setHint("Your first tag is #" + filter + " add more");
                break;
            case 1:
                filter = ITEMS[1];
                et_tags.setHint("Your first tag is #" + filter + " add more");
                break;
            case 2:
                filter = ITEMS[2];
                et_tags.setHint("Your first tag is #" + filter + " add more");
                break;
            case 3:
                filter = ITEMS[3];
                et_tags.setHint("Your first tag is #" + filter + " add more");
                break;
            case 4:
                filter = ITEMS[3];
                et_tags.setHint("Your first tag is #" + filter + " add more");
                break;
            case 5:
                filter = ITEMS[3];
                et_tags.setHint("Your first tag is #" + filter + " add more");
                break;
            case 6:
                filter = ITEMS[3];
                et_tags.setHint("Your first tag is #" + filter + " add more");
                break;
            case 7:
                filter = ITEMS[3];
                et_tags.setHint("Your first tag is #" + filter + " add more");
                break;
            case 8:
                filter = ITEMS[3];
                et_tags.setHint("Your first tag is #" + filter + " add more");
                break;

        }
        Log.e(TAG, "filter" + filter);
    }


    /**
     * Nothing selected in the spinner
     *
     * @param parent
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        filter = null;
        et_tags.setHint("");
    }


    class ValidateAddress extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(CreateEventActivity.this);
            dialog.setMessage("Validating...");
            dialog.show();
        }

        // ----------------------------------------------------
        @Override
        protected String doInBackground(String... params) {
            dialog.dismiss();
            String queryString = null;
            try {
                queryString = "" +
                        "&address=" + URLEncoder.encode(address, "utf-8") +
                        "&key=" + GlobalVariables.GEO_API_KEY;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            return HttpHandler.get(params[0], queryString);
        }

        // ----------------------------------------------------
        @Override
        protected void onPostExecute(String s) {

            if (s == null) {
                Toast.makeText(CreateEventActivity.this, "Something went wrong, plese try again", Toast.LENGTH_SHORT).show();
                iv_val_add.setImageResource(R.drawable.x);

            } else {
                gson = new Gson();
                result = gson.fromJson(s, Result.class);
                if (result.getStatus().equals("OK")) {
                    address_ok = true;
                    iv_val_add.setImageResource(R.drawable.v);
                    String long_name = result.getResults().get(0).getAddress_components().get(1).getLong_name();
                    String street = long_name.replaceAll("Street", "");
                    String number = result.getResults().get(0).getAddress_components().get(0).getShort_name();
                    lat = result.getResults().get(0).getGeometry().getLocation().getLat();
                    lng = result.getResults().get(0).getGeometry().getLocation().getLng();
                    city = result.getResults().get(0).getAddress_components().get(2).getShort_name();
                    valid_address = street + number + ", " + city;

                } else if (result.getStatus().equals("ZERO_RESULTS")) {
                    address_ok = false;
                    iv_val_add.setImageResource(R.drawable.x);
                    Toast.makeText(CreateEventActivity.this, "Problem is " + result.getStatus(), Toast.LENGTH_SHORT).show();
                }

            }

        }
    }
}

