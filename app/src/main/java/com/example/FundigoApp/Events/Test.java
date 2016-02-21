package com.example.FundigoApp.Events;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.FundigoApp.Constants;
import com.example.FundigoApp.MainActivity;
import com.example.FundigoApp.Producer.Artists.ArtistsPage;
import com.example.FundigoApp.R;
import com.google.gson.Gson;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;

public class Test extends Activity implements View.OnClickListener {

    private static final String TAG = "Test";

    TextView tv_create;
    TextView tv_name;
    TextView tv_date;
    TextView tv_artist;
    TextView tv_description;
    EditText et_date;
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
    private ProgressDialog dialog;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event1);
        componentInit();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                showSecondStage();
                break;
            case R.id.btn_next1:
                if (address_ok) {
                    showThirdStage();
                } else {
                    Toast.makeText(Test.this, "Please enter valid address", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_validate_address:
                validateAddress();
                break;
            case R.id.btn_next2:
                saveEvent();
                break;
            case R.id.btn_pic:
                uploadPic();
                break;
            case R.id.btn_date:
                int year = Calendar.getInstance().get(Calendar.YEAR);
                int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                int month = Calendar.getInstance().get(Calendar.MONTH);
                DatePickerDialog dialog = new DatePickerDialog(this, listener, year, month, day);
                dialog.show();

                break;

        }

    }

    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            date = dayOfMonth +"."+ (monthOfYear+1)+"."+ (year-2000);
            tv_date_new.setText(date);
            tv_date_new.setVisibility(View.VISIBLE);
            Log.e(TAG, ""+ date);
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
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                cursor.close();
                pic.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                pic.setVisibility(View.VISIBLE);
                pictureSelected = true;


            }
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
               // btn_date.setVisibility(View.GONE);
              //  tv_date_new.setVisibility(View.GONE);

            } else {
                Toast.makeText(Test.this, "Please fill empty forms", Toast.LENGTH_SHORT).show();
            }
        }

        private void validateAddress() {
            address = et_address.getText().toString();
            new ValidateAddress().execute(Constants.GEO_API_ADDRESS);
        }

        private void showThirdStage() {
            if (et_price.length() != 0 && et_quantity.length() != 0 && address_ok && et_place.length() != 0) {
                create_event2.setVisibility(View.GONE);
                create_event3.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(Test.this, "Please fill empty forms", Toast.LENGTH_SHORT).show();
            }

        }

        public void saveEvent() {

            Event event = new Event();
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
                event.setPrice(et_price.getText().toString());
                event.setNumOfTicketsLeft(et_quantity.getText().toString());
                event.setAddress(valid_address);
                event.setCity(city);
                event.setX(lat);
                event.setY(lng);
                event.setTags(et_tags.getText().toString());
                event.setProducerId(MainActivity.producerId);
                event.setDate(date);
                event.setPlace(et_place.getText().toString());
                event.setArtist(et_artist.getText().toString());
                event.setEventToiletService(et_toilet.getText().toString());
                event.setEventParkingService(et_parking.getText().toString());
                event.setEventCapacityService(et_capacity.getText().toString());

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

            if (et_name.getVisibility() == View.VISIBLE) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Test.this.finish();
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
            // tv_date = (TextView) findViewById(R.id.tv_date);
            tv_artist = (TextView) findViewById(R.id.tv_address);
            tv_description = (TextView) findViewById(R.id.tv_description);
            et_name = (EditText) findViewById(R.id.et_name);
            // et_date = (EditText) findViewById(R.id.et_date);
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
            atmBox = (CheckBox) findViewById(R.id.checkBox);
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
            create_event2 = (LinearLayout) findViewById(R.id.create_event2);
            create_event3 = (LinearLayout) findViewById(R.id.create_event3);
            ll_name = (LinearLayout) findViewById(R.id.ll_name);
            ll_date = (LinearLayout) findViewById(R.id.ll_date);
            ll_artist = (LinearLayout) findViewById(R.id.ll_artist);
            ll_description = (LinearLayout) findViewById(R.id.ll_description);

            if (!getIntent().getStringExtra("create").equals("true")) {
                tv_create.setText("Edit Event");
                et_name.setText("" + getIntent().getStringExtra("name"));

                for (int i = 0; i < ArtistsPage.all_events.size(); i++) {
                    EventInfo event = ArtistsPage.all_events.get(i);
                    if (event.getParseObjectId().equals(getIntent().getStringExtra("eventObjectId"))) {
                        income = event.getIncome();
                        sold = event.getSold();
                        et_date.setText(event.getDate());
                        et_artist.setText(event.getArtist());
                        et_description.setText(event.getInfo());
                        et_price.setText(event.getPrice());
                        et_quantity.setText(event.getTicketsLeft());
                        et_address.setText(event.getPlace());
                        pic.setImageBitmap(event.getImageId());
                        et_tags.setText(event.getTags());
                    }

                }
            }
        }

        class ValidateAddress extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog;

            @Override
            protected void onPreExecute() {

                dialog = new ProgressDialog(Test.this);
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
                            "&key=" + Constants.GEO_API_KEY;
                    Log.e(TAG, " " + queryString);
                } catch (UnsupportedEncodingException e) {
                }


                return HttpHandler.get(params[0], queryString);
            }

            // ----------------------------------------------------
            @Override
            protected void onPostExecute(String s) {

                if (s == null) {
                    Log.e(TAG, "No results ");
                    Toast.makeText(Test.this, "Something went wrong, plese try again", Toast.LENGTH_SHORT).show();
                    iv_val_add.setImageResource(R.drawable.x);

                } else {
                    Log.e(TAG, "Result is " + s);
                    gson = new Gson();
                    result = gson.fromJson(s, Result.class);
                    Log.e(TAG, "status " + result.getStatus());
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
                        Log.e(TAG, "valid address - " + valid_address);
                        Log.e(TAG, "city - " + city);

                    } else if (result.getStatus().equals("ZERO_RESULTS")) {
                        address_ok = false;
                        iv_val_add.setImageResource(R.drawable.x);
                        Toast.makeText(Test.this, "Problem is " + result.getStatus(), Toast.LENGTH_SHORT).show();
                    }

                }

            }
        }
    }
