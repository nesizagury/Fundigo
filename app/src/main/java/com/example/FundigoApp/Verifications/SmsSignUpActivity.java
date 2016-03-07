package com.example.FundigoApp.Verifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import com.example.FundigoApp.Customer.CustomerDetails;
import com.example.FundigoApp.Customer.Social.MipoProfile;
import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethods;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sinch.verification.CodeInterceptionException;
import com.sinch.verification.Config;
import com.sinch.verification.IncorrectCodeException;
import com.sinch.verification.InvalidInputException;
import com.sinch.verification.ServiceErrorException;
import com.sinch.verification.SinchVerification;
import com.sinch.verification.Verification;
import com.sinch.verification.VerificationListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

public class SmsSignUpActivity extends AppCompatActivity {
    Spinner s;
    private String array_spinner[];
    String username;
    EditText phoneET;
    String phone_number_to_verify;
    String area;
    TextView phoneTV;
    TextView usernameTV;
    EditText usernameTE;
    Button upload_button;
    Button signup;
    ImageView customerImageView;
    TextView optionalTV;
    TextView expTV;
    boolean image_selected = false;
    MipoProfile previousDataFound = null;
    ParseUser user;
    Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_sms_varification);

        array_spinner = new String[6];
        array_spinner[0] = "050";
        array_spinner[1] = "052";
        array_spinner[2] = "053";
        array_spinner[3] = "054";
        array_spinner[4] = "055";
        array_spinner[5] = "058";
        s = (Spinner) findViewById (R.id.spinner);
        ArrayAdapter adapter = new ArrayAdapter (this,
                                                        android.R.layout.simple_spinner_item,
                                                        array_spinner);
        s.setAdapter (adapter);

        usernameTV = (TextView) findViewById (R.id.usernameTV);
        usernameTE = (EditText) findViewById (R.id.usernameTE);
        phoneET = (EditText) findViewById (R.id.phoneET);
        phoneTV = (TextView) findViewById (R.id.phoneTV);
        customerImageView = (ImageView) findViewById (R.id.imageV);

        phoneET.setOnEditorActionListener (new TextView.OnEditorActionListener () {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null &&
                             (event.getKeyCode () == KeyEvent.KEYCODE_ENTER)) ||
                            (actionId == EditorInfo.IME_ACTION_DONE)) {
                    area = s.getSelectedItem ().toString ();
                    username = usernameTE.getText ().toString ();
                    phone_number_to_verify = getNumber (phoneET.getText ().toString (), area);
                    getUserPreviousDetails(area + phoneET.getText().toString());
                    usernameTV.setVisibility (View.VISIBLE);
                    usernameTE.setVisibility (View.VISIBLE);
                    phoneET.setVisibility (View.INVISIBLE);
                    phoneTV.setVisibility (View.INVISIBLE);
                    expTV = (TextView) findViewById (R.id.explanationTV);
                    expTV.setVisibility (View.INVISIBLE);
                    s.setVisibility(View.INVISIBLE);
                   // smsVerify(phone_number_to_verify);
                }
                return false;
            }
        });

        usernameTE.setOnEditorActionListener (new TextView.OnEditorActionListener () {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode () == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    usernameTE.setVisibility (View.INVISIBLE);
                    usernameTV.setVisibility (View.INVISIBLE);
                    customerImageView = (ImageView) findViewById (R.id.imageV);
                    customerImageView.setVisibility (View.VISIBLE);
                    upload_button = (Button) findViewById (R.id.upload_button);
                    upload_button.setVisibility (View.VISIBLE);
                    signup = (Button) findViewById (R.id.button2);
                    signup.setVisibility (View.VISIBLE);
                    optionalTV = (TextView) findViewById (R.id.optionalTV);
                    optionalTV.setVisibility (View.VISIBLE);
                }
                return false;
            }
        });
    }

    public void Signup(View view) {
        username = usernameTE.getText ().toString ();
        MipoProfile profile;
        if (previousDataFound != null) {
            profile = previousDataFound;
            ParseUser.logOut();
            try {
                ParseUser.logIn (area + phoneET.getText ().toString (), area + phoneET.getText ().toString ());
            } catch (ParseException e) {
                e.printStackTrace ();
            }
        } else {
            profile = new MipoProfile ();
            profile.setNumber (area + phoneET.getText ().toString ());
            profile.setUser (user);
        }
         profile.setName(username);
         profile.setLastSeen (new Date());
        if (image_selected) {
            customerImageView.buildDrawingCache ();
            ByteArrayOutputStream stream = new ByteArrayOutputStream ();
            bmp.compress (CompressFormat.JPEG, 100, stream);
            byte[] image = stream.toByteArray ();
            ParseFile file = new ParseFile ("picturePath", image);
            try {
                file.save ();
            } catch (ParseException e) {
                e.printStackTrace ();
            }

            profile.put("pic", file);

        }
        try {
            ParseACL parseAcl = new ParseACL ();
            parseAcl.setPublicReadAccess(true);
            parseAcl.setPublicWriteAccess(true);
            profile.setACL(parseAcl);
            ParseGeoPoint parseGeoPoint = new ParseGeoPoint (31.8971205,
                    34.8136008);
            profile.setLocation(parseGeoPoint);
            profile.save();
            GlobalVariables.CUSTOMER_PHONE_NUM = area + phoneET.getText ().toString ();;
            Toast.makeText (getApplicationContext (), "Successfully Signed Up", Toast.LENGTH_SHORT).show();
            saveToFile (area + phoneET.getText ().toString ());
            // MainPageActivity.downloadProfilesDataInBackGround ();
            finish ();
        } catch (ParseException e) {
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
        startActivityForResult (i, GlobalVariables.SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GlobalVariables.SELECT_PICTURE && resultCode == RESULT_OK && null != data) {
            Bitmap image = StaticMethods.getImageFromDevice(data, this);
            customerImageView.setImageBitmap (image);
            bmp = image;
            image_selected = true;
        }
    }

    public void smsVerify(String phone_number) {
        Config config = SinchVerification.config ().applicationKey ("b9ee3da5-0dc9-40aa-90aa-3d30320746f3").context (getApplicationContext ()).build ();
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
                e.printStackTrace ();
            } else if (e instanceof ServiceErrorException) {
                // Sinch service error
                e.printStackTrace ();
            } else {
                // Other system error, such as UnknownHostException in case of network error
                e.printStackTrace ();
            }
        }

        @Override
        public void onVerified() {
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
            if (e instanceof InvalidInputException) {
                // Incorrect number or code provided
                Toast.makeText (getApplicationContext (), "invalid phone number try again.", Toast.LENGTH_SHORT).show ();
                e.printStackTrace ();
            } else if (e instanceof CodeInterceptionException) {
                // Intercepting the verification code automatically failed, input the code manually with verify()
                e.printStackTrace ();
            } else if (e instanceof IncorrectCodeException) {
                e.printStackTrace ();
            } else if (e instanceof ServiceErrorException) {
                e.printStackTrace ();
            } else {
                e.printStackTrace ();
            }
        }
    }

    void saveToFile(String phone_number) {
        phone_number = phone_number + " isFundigo";
        File myExternalFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "verify.txt");        try{
        FileOutputStream fos = new FileOutputStream(myExternalFile);
        fos.write(phone_number.getBytes());
        fos.close();
            Log.e("number",phone_number);
    } catch (IOException e) {
        e.printStackTrace();
    }

    }

    private void getUserPreviousDetails(final String user_number) {
        ParseQuery<MipoProfile> query = ParseQuery.getQuery ("Profile");
        query.whereEqualTo ("number", user_number);
        query.findInBackground (new FindCallback<MipoProfile> () {
            public void done(List<MipoProfile> profiles, ParseException e) {
                if (e == null) {

                    if (profiles.size () > 0) {
                        previousDataFound = profiles.get (0);
                        if (usernameTE.getText ().toString ().isEmpty ()) {
                            usernameTE.setText (profiles.get (0).get ("name") + "");
                            usernameTE.setSelection (usernameTE.getText ().length ());
                        }

                        if (!image_selected) {
                            ParseFile imageFile = (ParseFile) profiles.get (0).get ("pic");
                            if (imageFile != null) {
                                imageFile.getDataInBackground (new GetDataCallback() {
                                    public void done(byte[] data, ParseException e) {
                                        if (e == null) {
                                            image_selected = true;
                                            bmp = BitmapFactory
                                                    .decodeByteArray(
                                                            data, 0,
                                                            data.length);
                                            customerImageView.setImageBitmap (bmp);
                                        } else {
                                            e.printStackTrace ();
                                        }
                                    }
                                });
                            }
                        }
                    }
                    else
                    {
                        ParseUser.logOut();
                        user = new ParseUser();
                        user.setUsername (user_number);
                        user.setPassword (user_number);
                        try {
                            user.signUp ();
                        } catch (ParseException e1) {
                            e1.printStackTrace ();
                        }
                    }
                } else {
                    e.printStackTrace ();
                }
            }
        });
    }
}