package com.example.FundigoApp.Events;

import android.app.ProgressDialog;
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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.google.gson.Gson;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import fr.ganfra.materialspinner.MaterialSpinner;

public class EditEventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private static final String TAG = "EditEventActivity";
    EditText et_name;
    EditText et_artist;
    EditText et_place;
    EditText et_description;
    EditText et_address;
    EditText et_hall_cap_edit;
    EditText et_parking_edit;
    EditText et_tags_edit;
    Button btn_val_add;
    Button btn_save;
    Button btn_chng_pic;
    String[] ATMS;
    String[] TOILETS;
    ImageView img;
    ImageView iv_val_add_edit;
    private MaterialSpinner filterSpinner;
    private MaterialSpinner atmSpinner;
    private MaterialSpinner toiletSpinner;
    private MaterialSpinner handicapToiletSpinner;
    private String eventObjectId;
    Event event;
    private Bitmap bmp;
    private String atmStatus;
    private String numOfToilets;
    private String numOfHandicapToilets;
    private String eventParkingService;
    private String eventCapacityService;
    private Gson gson;
    private Result result;
    private String address;
    private boolean address_ok;
    private double lat;
    private double lng;
    private String city;
    private String valid_address;
    private boolean pictureSelected;
    private static final int SELECT_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        Intent callingIntent = getIntent();
        eventObjectId = callingIntent.getStringExtra(GlobalVariables.OBJECTID);
        componentInit();
        event = getEvent(eventObjectId);
        //  setAll();
        // Log.e(TAG, "name "+ event.getName());
    }

    public Event getEvent(String eventObjectId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.getInBackground(eventObjectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    event = (Event) object;
                    ParseFile file = (ParseFile) object.get("ImageFile");
                    file.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            if (e == null) {

                                bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                img.setImageBitmap(bmp);

                            } else {
                                // something went wrong
                            }
                        }
                    });
                    setAll();
                } else {
                    Log.e(TAG, "Problem " + e.toString());
                }
            }
        });
        return event;
    }


    private void componentInit() {

        et_name = (EditText) findViewById(R.id.et_name_edit);
        et_artist = (EditText) findViewById(R.id.et_artist_edit);
        et_place = (EditText) findViewById(R.id.et_place_edit);
        et_description = (EditText) findViewById(R.id.et_description_edit);
        et_address = (EditText) findViewById(R.id.et_address_edit);
        et_tags_edit = (EditText) findViewById(R.id.et_tags_edit);
        btn_val_add = (Button) findViewById(R.id.btn_validate_address_edit);
        btn_save = (Button) findViewById(R.id.btn_save_edit_event);
        btn_chng_pic = (Button) findViewById(R.id.btn_change_pic);
        btn_save.setOnClickListener(this);
        btn_chng_pic.setOnClickListener(this);
        btn_val_add.setOnClickListener(this);
        img = (ImageView) findViewById(R.id.iv_event_pic);
        iv_val_add_edit = (ImageView) findViewById(R.id.iv_val_add_edit);
        et_hall_cap_edit = (EditText) findViewById(R.id.et_hall_cap_edit);
        et_parking_edit = (EditText) findViewById(R.id.et_parking_edit);
// ===============================ATM Spinner  stuff==================================
        ATMS = getResources().getStringArray(R.array.atms);
        ArrayAdapter<String> atmSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ATMS);
        atmSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        atmSpinner = (MaterialSpinner) findViewById(R.id.atmSpinner_edit);
        atmSpinner.setAdapter(atmSpinnerAdapter);
        atmSpinner.setOnItemSelectedListener(this);
//==============================================================================
// ===============================Toilet Spinner  stuff==================================
        TOILETS = getResources().getStringArray(R.array.toilets);
        ArrayAdapter<String> toiletSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, TOILETS);
        toiletSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toiletSpinner = (MaterialSpinner) findViewById(R.id.toiletSpinner_edit);
        toiletSpinner.setAdapter(toiletSpinnerAdapter);
        toiletSpinner.setOnItemSelectedListener(this);
//==============================================================================
// ===============================handicapToilet Spinner  stuff==================================
        handicapToiletSpinner = (MaterialSpinner) findViewById(R.id.handicapToiletSpinner_edit);
        handicapToiletSpinner.setAdapter(toiletSpinnerAdapter);
        handicapToiletSpinner.setOnItemSelectedListener(this);
//==============================================================================


    }

    public void setAll() {
        et_name.setText(event.getName());
        et_address.setText(event.getAddress());
        et_description.setText(event.getDescription());
        et_place.setText(event.getPlace());
        et_artist.setText(event.getArtist());
        String cap = event.getEventCapacityService();
        String[] a = cap.split("\\s+");
        for (int i = 0; i < a.length; i++) {
            a[i] = a[i].replaceAll(",", "");
            Log.e(TAG, "a " + a[i]);
        }

        et_hall_cap_edit.setText(a[2]);
        String park = event.getEventParkingService();
        String[] b = park.split("\\s+");
        for (int i = 0; i < b.length; i++) {
            b[i] = b[i].replaceAll(",", "");
            Log.e(TAG, "b " + b[i]);
        }
        et_parking_edit.setText(b[2]);
        String toilet = event.getEventToiletService();
        String[] c = toilet.split("\\s+");
        for (int i = 0; i < c.length; i++) {
            c[i] = c[i].replaceAll(",", "");
            Log.e(TAG, "c " + c[i]);
        }

        String atm = event.getEventATMService();
        if (atm.equals("Yes")) {
            atmSpinner.setSelection(1);
          //  atmStatus = ATMS[0];
        } else if (atm.equals("No")) {
            atmSpinner.setSelection(2);
         //   atmStatus = ATMS[1];
        } else if (atm.equals("Unknown")) {
          //  atmStatus = ATMS[2];
            atmSpinner.setSelection(3);
        }
        if (c[0].equals("Unknown")) {

        }else if(c[0].equals("None")){
            toiletSpinner.setSelection(1);
        }else{
            toiletSpinner.setSelection(Integer.parseInt(c[0]) + 1);
        }
        if (c[2].equals("Unknown")) {

        }else if(c[0].equals("None")){
            handicapToiletSpinner.setSelection(1);
        }else{
            handicapToiletSpinner.setSelection(Integer.parseInt(c[2]) + 1);
        }
        et_tags_edit.setText(event.getTags());
        lat = event.getX();
        lng = event.getY();


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.atmSpinner_edit:
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
            case R.id.toiletSpinner_edit:
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
            case R.id.handicapToiletSpinner_edit:
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

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_validate_address_edit:
                validateAddress();
                break;
            case R.id.btn_save_edit_event:
                if (address_ok) {
                    saveTheEvent();
                }else{
                    Toast.makeText(EditEventActivity.this, "Please validate address", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_change_pic:
                uploadPic();
                break;
        }

    }

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
            img.setImageBitmap(rotatedBitmap);
            //img.setVisibility(View.VISIBLE);
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

    private void saveTheEvent() {
        event.put("Name", et_name.getText().toString());
        event.put("eventToiletService", numOfToilets + ", Handicapped " + numOfHandicapToilets);
        event.put("place", et_place.getText().toString());
        if (et_parking_edit.getText().toString().equals("")) {
            eventParkingService = "Unknown";
        } else {
            eventParkingService = "Up To " + et_parking_edit.getText().toString();
        }
        event.put("eventParkingService", eventParkingService);
        if (et_hall_cap_edit.getText().toString().equals("")) {
            eventCapacityService = "Unknown";
        } else {
            eventCapacityService = "Up To " + et_hall_cap_edit.getText().toString();
        }

        event.put("eventCapacityService", eventCapacityService);
        event.put("eventATMService", atmStatus);
        event.put("artist", et_artist.getText().toString());
        event.put("X", lat);
        event.put("Y", lng);
        event.put("address", valid_address);
        event.put("city", city);

        if (pictureSelected) {
            img.buildDrawingCache();
            Bitmap bitmap = img.getDrawingCache();
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

        event.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(EditEventActivity.this, "Event saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditEventActivity.this, "Not  saved " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void validateAddress() {
        address = et_address.getText().toString();
        iv_val_add_edit.setVisibility(View.INVISIBLE);
        new ValidateAddress().execute(GlobalVariables.GEO_API_ADDRESS);
    }

    class ValidateAddress extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(EditEventActivity.this);
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
                Toast.makeText(EditEventActivity.this, "Something went wrong, plese try again", Toast.LENGTH_SHORT).show();
                iv_val_add_edit.setImageResource(R.drawable.x);
                iv_val_add_edit.setVisibility(View.VISIBLE);

            } else {
                gson = new Gson();
                result = gson.fromJson(s, Result.class);
                if (result.getStatus().equals("OK")) {
                    address_ok = true;
                    iv_val_add_edit.setImageResource(R.drawable.v);
                    iv_val_add_edit.setVisibility(View.VISIBLE);
                    String long_name = result.getResults().get(0).getAddress_components().get(1).getLong_name();
                    String street = long_name.replaceAll("Street", "");
                    String number = result.getResults().get(0).getAddress_components().get(0).getShort_name();
                    lat = result.getResults().get(0).getGeometry().getLocation().getLat();
                    lng = result.getResults().get(0).getGeometry().getLocation().getLng();
                    city = result.getResults().get(0).getAddress_components().get(2).getShort_name();
                    valid_address = street + number + ", " + city;

                } else if (result.getStatus().equals("ZERO_RESULTS")) {
                    address_ok = false;
                    iv_val_add_edit.setImageResource(R.drawable.x);
                    iv_val_add_edit.setVisibility(View.VISIBLE);
                    Toast.makeText(EditEventActivity.this, "Problem is " + result.getStatus(), Toast.LENGTH_SHORT).show();
                }

            }

        }

    }
}
