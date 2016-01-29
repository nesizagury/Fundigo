package com.example.events;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.sinch.verification.CodeInterceptionException;
import com.sinch.verification.Config;
import com.sinch.verification.IncorrectCodeException;
import com.sinch.verification.InvalidInputException;
import com.sinch.verification.ServiceErrorException;
import com.sinch.verification.SinchVerification;
import com.sinch.verification.Verification;
import com.sinch.verification.VerificationListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class LoginActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;
    String picturePath;
    Spinner s;
    private String array_spinner[];
    String username;
    EditText phoneET;
    String phone_number;
    String area;

    TextView phoneTV;

    TextView usernameTV;
    EditText usernameTE;

    TextView imageTV;
    Button upload_button;

    Button signup;

    ImageView imageV;
    TextView optionalTV;

    String mPhoneNumber;

    TextView expTV;

    boolean image_selected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_login);


        array_spinner = new String[6];
        array_spinner[0] = "050";
        array_spinner[1] = "052";
        array_spinner[2] = "053";
        array_spinner[3] = "054";
        array_spinner[4] = "055";
        array_spinner[5] = "058";
        s = (Spinner) findViewById (R.id.spinner);
        ArrayAdapter adapter = new ArrayAdapter (this,
                                                        android.R.layout.simple_spinner_item, array_spinner);
        s.setAdapter (adapter);

        usernameTE = (EditText) findViewById (R.id.usernameTE);
        phoneET = (EditText) findViewById (R.id.phoneET);
        usernameTV = (TextView) findViewById (R.id.usernameTV);
        usernameTE = (EditText) findViewById (R.id.usernameTE);
        phoneET = (EditText) findViewById (R.id.phoneET);
        phoneTV = (TextView) findViewById (R.id.phoneTV);

        phoneET.setOnEditorActionListener (new TextView.OnEditorActionListener () {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode () == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    area = s.getSelectedItem().toString ();
                    username = usernameTE.getText ().toString();
                    phone_number = getNumber (phoneET.getText ().toString (), area);

                    Toast.makeText (getApplicationContext (), phone_number, Toast.LENGTH_SHORT).show();

                    ParsePush push = new ParsePush();
                    push.unsubscribeInBackground("All_Users");
                    push.setChannel("All_Users");
                    push.setMessage("Hey Come To See Events Near You");

                    push.subscribeInBackground("All_Users");
                    push.sendInBackground();


                    smsVerify (phone_number);


                }
                return false;
            }
        });

        usernameTE.setOnEditorActionListener (new TextView.OnEditorActionListener () {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode () == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {

                    usernameTE.setVisibility (View.INVISIBLE);
                    usernameTV.setVisibility (View.INVISIBLE);
                    optionalTV = (TextView) findViewById (R.id.optionalTV);
                    imageV = (ImageView) findViewById (R.id.imageV);
                    imageV.setVisibility (View.VISIBLE);
                    upload_button = (Button) findViewById (R.id.upload_button);
                    upload_button.setVisibility (View.VISIBLE);
                    signup = (Button) findViewById (R.id.button2);
                    signup.setVisibility (View.VISIBLE);
                    optionalTV.setVisibility (View.VISIBLE);
                }
                return false;
            }
        });

    }

    public void Signup(View view) {

        username = usernameTE.getText ().toString ();
        Numbers number = new Numbers ();
        number.setName (username);

        if (image_selected) {
            Bitmap bitmap = BitmapFactory.decodeFile (picturePath);
            ByteArrayOutputStream stream = new ByteArrayOutputStream ();
            bitmap.compress (CompressFormat.JPEG, 100, stream);
            byte[] image = stream.toByteArray ();
            ParseFile file = new ParseFile ("picturePath", image);
            try {
                file.save ();
            } catch (ParseException e) {
                e.printStackTrace ();
            }
            number.put ("ImageFile", file);
        }
        number.setNumber (phone_number);
        try {
            number.save ();
            Toast.makeText (getApplicationContext (), "Successfully Signed up", Toast.LENGTH_SHORT).show ();
            Bundle b = new Bundle ();
            Intent ticketsPageIntent = new Intent (LoginActivity.this, TicketsPage.class);
            Intent intentHere = getIntent ();
            ticketsPageIntent.putExtra("eventName", intentHere.getStringExtra("eventName"));
            ticketsPageIntent.putExtras(b);
            saveToFile(phone_number);



            startActivity (ticketsPageIntent);
            finish ();
        } catch (ParseException e) {
            Toast.makeText (getApplicationContext (), " Error ): ", Toast.LENGTH_SHORT).show ();

            e.printStackTrace ();
        }
    }

    public String getNumber(String number, String area) {
        switch (area) {
            case "050":
                number = "97250" + number;
                break;
            case "052":
                number = "97252" + number;
                break;
            case "053":
                number = "97253" + number;
                break;
            case "054":
                number = "97254" + number;
                break;
            case "055":
                number = "97255" + number;
                break;
            case "058":
                number = "97258" + number;
                break;
        }
        return number;
    }


    public void imageUpload(View view) {
        Intent i = new Intent (
                                      Intent.ACTION_PICK,
                                      MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult (i, SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData ();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver ().query (selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst ();
            int columnIndex = cursor.getColumnIndex (filePathColumn[0]);
            picturePath = cursor.getString (columnIndex);
            cursor.close ();
            imageV.setImageBitmap (BitmapFactory.decodeFile (picturePath));
            image_selected = true;

        }
    }

    public String getPath(Uri uri) {
        // just some safety built in
        if (uri == null) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery (uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString (column_index);
    }

    public void smsVerify(String phone_number) {
        Config config = SinchVerification.config ().applicationKey ("030961d4-2f78-4ca4-8f46-bd846b374308").context (getApplicationContext ()).build ();
        VerificationListener listener = new MyVerificationListener ();
        Verification verification = SinchVerification.createSmsVerification (config, phone_number, listener);
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

            // usernameTE.setText(checkUser());

            usernameTV.setVisibility (View.VISIBLE);
            usernameTE.setVisibility (View.VISIBLE);
            phoneET.setVisibility (View.INVISIBLE);
            phoneTV.setVisibility (View.INVISIBLE);
            expTV = (TextView) findViewById (R.id.explanationTV);
            expTV.setVisibility (View.INVISIBLE);
            s.setVisibility (View.INVISIBLE);

        }

        @Override
        public void onVerificationFailed(Exception e) {
            Toast.makeText (getApplicationContext (), "a", Toast.LENGTH_SHORT).show ();

            if (e instanceof InvalidInputException) {
                // Incorrect number or code provided
                Toast.makeText (getApplicationContext (), "invalid phone number try again.", Toast.LENGTH_SHORT).show ();
            } else if (e instanceof CodeInterceptionException) {
                // Intercepting the verification code automatically failed, input the code manually with verify()
                Toast.makeText (getApplicationContext (), "b", Toast.LENGTH_SHORT).show ();

            } else if (e instanceof IncorrectCodeException) {
                Toast.makeText (getApplicationContext (), "c", Toast.LENGTH_SHORT).show ();
            } else if (e instanceof ServiceErrorException) {
                Toast.makeText (getApplicationContext (), "d", Toast.LENGTH_SHORT).show ();
            } else {
                Toast.makeText (getApplicationContext (), "e", Toast.LENGTH_SHORT).show ();
            }
        }

    }



    void saveToFile(String phone_number) {

        OutputStreamWriter outputStreamWriter = null;

        try {
            outputStreamWriter = new OutputStreamWriter (this.openFileOutput ("verify.txt", Context.MODE_PRIVATE));
        } catch (FileNotFoundException e) {
            e.printStackTrace ();
        }

        PrintWriter writer = new PrintWriter (outputStreamWriter);

        writer.println (phone_number);

        try {
            outputStreamWriter.close ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }


    private String checkUser() {


        String user_number = phone_number;
        String username = "";


        ParseQuery<ParseObject> query = ParseQuery.getQuery ("Numbers");
        query.whereEqualTo("number", user_number);
        query.getFirstInBackground (new GetCallback<ParseObject> () {
            public void done(ParseObject object, ParseException e) {

                if (e == null) {
                    object.deleteInBackground ();

                }

            }
        });

        return  username;

    }


}