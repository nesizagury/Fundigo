package com.example.FundigoApp.Events;

import android.app.Activity;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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

public class CreateEventActivity extends Activity {

    private static final String TAG = "CreateEventActivity";
    String picturePath;
    private static final int SELECT_PICTURE = 1;
    ImageView imageV;
    TextView imageTV;
    EditText nameET;
    TextView nameTV;
    TextView descriptionTV;
    Button browse_button;
    EditText descriptionET;

    TextView priceTV;
    TextView addressTV;
    TextView quantityTV;
    TextView xTV;
    TextView yTV;
    EditText priceET;
    EditText addressET;
    EditText quantityET;
    EditText xET;
    EditText yET;

    Button done_button;
    TextView tagsTV;
    EditText tagsET;
    boolean pictureSelected = false;
    TextView dateTV;
    EditText dateET;
    TextView placeTV;
    EditText placeET;
    TextView artistTV;
    EditText artistET;
    TextView headTV;
    String income;
    String sold;

    TextView capacityTV;
    TextView toiletTV;
    TextView parkingTV;
    EditText capacityET;
    EditText toiletET;
    CheckBox atmBox;
    EditText parkingET;

    Button btn_val;
    ImageView iv_val;

    Gson gson;
    Result result;
    String address;

    private String valid_address;
    private double lat;
    private double lng;
    private String city;
    private ProgressDialog dialog;
    private boolean address_ok = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        componentInit();

        descriptionET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    HideFirstStage(null);
                }
                return false;
            }
        });


        placeET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    showThirdStage(null);
                }
                return false;
            }
        });

    }


    public void imageUpload(View view) {
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
            imageV.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            pictureSelected = true;
        }
    }


    public void saveEvent(View view) {

        if (address_ok) {

            Event event = new Event();
            if (tagsET.getText().length() != 0) {
                if (getIntent().getStringExtra("create").equals("false")) {
                    deleteRow();
                    event.setSold(sold);
                    event.setIncome(income);
                } else {
                    event.setSold("0");
                    event.setIncome("0");
                }


                event.setName(nameET.getText().toString());
                event.setDescription(descriptionET.getText().toString());
                event.setPrice(priceET.getText().toString());
                event.setNumOfTicketsLeft(quantityET.getText().toString());
                event.setAddress(valid_address);
                event.setCity(city);
                event.setX(lat);
                event.setY(lng);
                event.setTags(tagsET.getText().toString());
                event.setProducerId(MainActivity.producerId);
                event.setDate(dateET.getText().toString());
                event.setPlace(placeET.getText().toString());
                event.setArtist(artistET.getText().toString());
                event.setEventToiletService(toiletET.getText().toString());
                event.setEventParkingService(parkingET.getText().toString());
                event.setEventCapacityService(capacityET.getText().toString());
                if (atmBox.isChecked())
                    event.setEventATMService("Yes");
                else
                    event.setEventATMService("No");


                if (pictureSelected || headTV.getText().toString().equals("Edit Event")) {
                    imageV.buildDrawingCache();
                    Bitmap bitmap = imageV.getDrawingCache();
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
        } else {
            Toast.makeText(CreateEventActivity.this, "Please enter valid address", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {

        if (nameET.getVisibility() == View.VISIBLE) {
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

        // at stage two
        if (priceET.getVisibility() == View.VISIBLE) {
            showFirstStage(null);
            hideSecondStage(null);
        }

        // at stage three
        if (tagsET.getVisibility() == View.VISIBLE) {
            hideThirdStage(null);
            showSecondStage(null);
        }


    }

    public void showFirstStage(View view) {

        nameTV.setVisibility(View.VISIBLE);
        descriptionTV.setVisibility(View.VISIBLE);
        nameET.setVisibility(View.VISIBLE);
        descriptionET.setVisibility(View.VISIBLE);
        dateTV.setVisibility(View.VISIBLE);
        dateET.setVisibility(View.VISIBLE);
        artistTV.setVisibility(View.VISIBLE);
        artistET.setVisibility(View.VISIBLE);

    }

    public void HideFirstStage(View view) {

        if (nameET.getText().length() != 0 && descriptionET.getText().length() != 0
                && dateET.getText().length() != 0) {
            nameTV.setVisibility(View.INVISIBLE);
            descriptionTV.setVisibility(View.INVISIBLE);
            nameET.setVisibility(View.INVISIBLE);
            descriptionET.setVisibility(View.INVISIBLE);
            dateTV.setVisibility(View.INVISIBLE);
            dateET.setVisibility(View.INVISIBLE);
            artistTV.setVisibility(View.INVISIBLE);
            artistET.setVisibility(View.INVISIBLE);
            showSecondStage(null);

        }

    }

    public void showSecondStage(View view) {


        priceTV.setVisibility(View.VISIBLE);
        priceET.setVisibility(View.VISIBLE);
        quantityTV.setVisibility(View.VISIBLE);
        quantityET.setVisibility(View.VISIBLE);
        addressTV.setVisibility(View.VISIBLE);
        addressET.setVisibility(View.VISIBLE);
        placeTV.setVisibility(View.VISIBLE);
        placeET.setVisibility(View.VISIBLE);
//        xTV.setVisibility (View.VISIBLE);
//        xET.setVisibility (View.VISIBLE);
//        yTV.setVisibility (View.VISIBLE);
//        yET.setVisibility (View.VISIBLE);
        btn_val.setVisibility(View.VISIBLE);
        iv_val.setVisibility(View.VISIBLE);

    }

    public void hideSecondStage(View view) {


        priceTV.setVisibility(View.INVISIBLE);
        priceET.setVisibility(View.INVISIBLE);
        quantityTV.setVisibility(View.INVISIBLE);
        quantityET.setVisibility(View.INVISIBLE);
        placeET.setVisibility(View.INVISIBLE);
        placeTV.setVisibility(View.INVISIBLE);
        addressTV.setVisibility(View.INVISIBLE);
        addressET.setVisibility(View.INVISIBLE);
//        xTV.setVisibility (View.INVISIBLE);
        xET.setVisibility(View.INVISIBLE);
//        yTV.setVisibility (View.INVISIBLE);
        yET.setVisibility(View.INVISIBLE);
        btn_val.setVisibility(View.INVISIBLE);
        iv_val.setVisibility(View.INVISIBLE);

    }

    public void showThirdStage(View view) {

        if (priceET.getText().length() != 0 && quantityET.getText().length() != 0 &&
                addressET.getText().length() != 0 &&
                placeET.getText().length() != 0) {
            imageTV.setVisibility(View.VISIBLE);
            imageV.setVisibility(View.VISIBLE);
            browse_button.setVisibility(View.VISIBLE);
            tagsTV.setVisibility(View.VISIBLE);
            tagsET.setVisibility(View.VISIBLE);
            done_button.setVisibility(View.VISIBLE);
            atmBox.setVisibility(View.VISIBLE);
            toiletTV.setVisibility(View.VISIBLE);
            toiletET.setVisibility(View.VISIBLE);
            parkingTV.setVisibility(View.VISIBLE);
            parkingET.setVisibility(View.VISIBLE);
            capacityET.setVisibility(View.VISIBLE);
            capacityTV.setVisibility(View.VISIBLE);

            hideSecondStage(null);
        } else
            Toast.makeText(getApplicationContext(), "Please fill the  empty fields", Toast.LENGTH_SHORT).show();


    }

    public void hideThirdStage(View view) {

        imageTV.setVisibility(View.INVISIBLE);
        imageV.setVisibility(View.INVISIBLE);
        browse_button.setVisibility(View.INVISIBLE);
        tagsTV.setVisibility(View.INVISIBLE);
        tagsET.setVisibility(View.INVISIBLE);
        done_button.setVisibility(View.INVISIBLE);
        atmBox.setVisibility(View.INVISIBLE);
        toiletTV.setVisibility(View.INVISIBLE);
        toiletET.setVisibility(View.INVISIBLE);
        parkingTV.setVisibility(View.INVISIBLE);
        parkingET.setVisibility(View.INVISIBLE);
        capacityET.setVisibility(View.INVISIBLE);
        capacityTV.setVisibility(View.INVISIBLE);

    }

    private void componentInit() {

        nameTV = (TextView) findViewById(R.id.nameTV);
        descriptionTV = (TextView) findViewById(R.id.descriptionTV);
        nameET = (EditText) findViewById(R.id.nameET);
        descriptionET = (EditText) findViewById(R.id.descriptionET);
        priceTV = (TextView) findViewById(R.id.priceTV);
        priceET = (EditText) findViewById(R.id.priceET);
        artistTV = (TextView) findViewById(R.id.artistTV);
        artistET = (EditText) findViewById(R.id.artistET);
        quantityTV = (TextView) findViewById(R.id.quantityTV);
        quantityET = (EditText) findViewById(R.id.quantityET);
        addressTV = (TextView) findViewById(R.id.addressTV);
        addressET = (EditText) findViewById(R.id.addressET);
        placeTV = (TextView) findViewById(R.id.placeTV);
        placeET = (EditText) findViewById(R.id.placeET);
        xTV = (TextView) findViewById(R.id.xTV);
        xET = (EditText) findViewById(R.id.xET);
        yTV = (TextView) findViewById(R.id.yTV);
        yET = (EditText) findViewById(R.id.yET);
        dateTV = (TextView) findViewById(R.id.dateTV);
        dateET = (EditText) findViewById(R.id.dateET);
        imageTV = (TextView) findViewById(R.id.imageTV);
        imageV = (ImageView) findViewById(R.id.create_imageV);
        browse_button = (Button) findViewById(R.id.browse_button);
        capacityET = (EditText) findViewById(R.id.capacityET);
        toiletET = (EditText) findViewById(R.id.toiletET);
        parkingET = (EditText) findViewById(R.id.parkingET);
        parkingTV = (TextView) findViewById(R.id.parkingTV);
        atmBox = (CheckBox) findViewById(R.id.checkBox);
        capacityTV = (TextView) findViewById(R.id.capacityTV);
        toiletTV = (TextView) findViewById(R.id.toiletTV);

        btn_val = (Button) findViewById(R.id.btn_validate);
        iv_val = (ImageView) findViewById(R.id.iv_val);


        tagsTV = (TextView) findViewById(R.id.tagsTV);
        tagsET = (EditText) findViewById(R.id.tagsET);
        done_button = (Button) findViewById(R.id.done_button);

        if (!getIntent().getStringExtra("create").equals("true")) {
            headTV = (TextView) findViewById(R.id.headTV);
            headTV.setText("Edit Event");
            nameET.setText("" + getIntent().getStringExtra("name"));

            for (int i = 0; i < ArtistsPage.all_events.size(); i++) {
                EventInfo event = ArtistsPage.all_events.get(i);
                if (event.getParseObjectId().equals(getIntent().getStringExtra("eventObjectId"))) {
                    income = event.getIncome();
                    sold = event.getSold();
                    dateET.setText(event.getDate());
                    artistET.setText(event.getArtist());
                    descriptionET.setText(event.getInfo());
                    priceET.setText(event.getPrice());
                    quantityET.setText(event.getTicketsLeft());
                    addressET.setText(event.getPlace());
                    imageV.setImageBitmap(event.getImageId());
                    tagsET.setText(event.getTags());
                }

            }
        }

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

    public void validateAddress(View view) {
        address = addressET.getText().toString();
        if (address.length() != 0) {
            new ValidateAddress().execute(Constants.GEO_API_ADDRESS);
        } else {
            Toast.makeText(CreateEventActivity.this, "Please enter address", Toast.LENGTH_SHORT).show();
        }
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
                Toast.makeText(CreateEventActivity.this, "Something went wrong, plese try again", Toast.LENGTH_SHORT).show();
                iv_val.setImageResource(R.drawable.x);

            } else {
                Log.e(TAG, "Result is " + s);
                gson = new Gson();
                result = gson.fromJson(s, Result.class);
                Log.e(TAG, "status " + result.getStatus());
                if(result.getStatus().equals("OK")){
                    address_ok = true;
                    iv_val.setImageResource(R.drawable.v);
                    String street = result.getResults().get(0).getAddress_components().get(1).getShort_name();
                    String number = result.getResults().get(0).getAddress_components().get(0).getShort_name();
                    valid_address = street + "." + " , " + number;
                    Log.e(TAG, "valid address - " + valid_address);
                    lat = result.getResults().get(0).getGeometry().getLocation().getLat();
                    lng = result.getResults().get(0).getGeometry().getLocation().getLng();
                    city = result.getResults().get(0).getAddress_components().get(2).getShort_name();
                    Log.e(TAG, "city - " + city);

                }else if (result.getStatus().equals("ZERO_RESULTS")) {
                    address_ok = false;
                    iv_val.setImageResource(R.drawable.x);
                    Toast.makeText(CreateEventActivity.this, "Problem is " + result.getStatus(), Toast.LENGTH_SHORT).show();
                     }

                }

            }


        }
    }
