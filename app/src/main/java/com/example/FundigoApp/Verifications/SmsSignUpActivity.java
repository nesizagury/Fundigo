package com.example.FundigoApp.Verifications;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Build;
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
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

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
    private Locale locale = null;
    boolean imageSelected = false;
    boolean image_was_before = false;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow ().getDecorView ().setLayoutDirection (View.LAYOUT_DIRECTION_LTR);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        forceRTLIfSupported ();
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_sms_varification);
        Locale.getDefault ().getDisplayLanguage ();
        array_spinner = new String[6];
        array_spinner[0] = "050";
        array_spinner[1] = "052";
        array_spinner[2] = "053";
        array_spinner[3] = "054";
        array_spinner[4] = "055";
        array_spinner[5] = "058";
        s = (Spinner) findViewById (R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String> (this,
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
                    getUserPreviousDetails (area + phoneET.getText ().toString ());
                    smsVerify (phone_number_to_verify);
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
        MipoProfile mipoProfileParseObject;
        if (previousDataFound != null) {
            mipoProfileParseObject = previousDataFound;
            GlobalVariables.CUSTOMER_PHONE_NUM = previousDataFound.getNumber ();
            ParseUser.logOut ();
            try {
                ParseUser.logIn (GlobalVariables.CUSTOMER_PHONE_NUM, GlobalVariables.CUSTOMER_PHONE_NUM);
            } catch (ParseException e) {
                e.printStackTrace ();
            }
            if (mipoProfileParseObject.getChanels () != null) {
                GlobalVariables.userChanels.addAll (mipoProfileParseObject.getChanels ());
            }
            if (!GlobalVariables.userChanels.isEmpty ()) {
                ParseInstallation installation = ParseInstallation.getCurrentInstallation ();
                installation.addAll ("Channels", (Collection<?>) GlobalVariables.userChanels);
                installation.saveInBackground ();
                for (int i = 0; i < GlobalVariables.userChanels.size (); i++) {
                    ParsePush.subscribeInBackground ("a" + GlobalVariables.userChanels.get (i));
                }
            }
        } else {
            mipoProfileParseObject = new MipoProfile ();
        }
        mipoProfileParseObject.setName (username);
        if (imageSelected) {
            customerImageView.buildDrawingCache ();
            Bitmap bitmap = customerImageView.getDrawingCache ();
            ByteArrayOutputStream stream = new ByteArrayOutputStream ();
            bitmap.compress (CompressFormat.JPEG, 100, stream);
            byte[] image = stream.toByteArray ();
            ParseFile file = new ParseFile ("picturePath", image);
            try {
                file.save ();
            } catch (ParseException e) {
                e.printStackTrace ();
            }
            ParseACL parseAcl = new ParseACL ();
            parseAcl.setPublicReadAccess (true);
            parseAcl.setPublicWriteAccess (true);
            mipoProfileParseObject.setACL (parseAcl);
            mipoProfileParseObject.put ("pic", file);
        } else if(!image_was_before) {
            Bitmap bmp = BitmapFactory.decodeResource (this.getResources (),
                                                       R.drawable.no_image_icon_md);
            customerImageView.setImageBitmap (bmp);
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
            ParseACL parseAcl = new ParseACL ();
            parseAcl.setPublicReadAccess (true);
            parseAcl.setPublicWriteAccess (true);
            mipoProfileParseObject.setACL (parseAcl);
            mipoProfileParseObject.put ("pic", file);
        }
        mipoProfileParseObject.setNumber (area + phoneET.getText ().toString ());
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences (SmsSignUpActivity.this);
        String fbId = sp.getString (GlobalVariables.FB_ID, null);
        if (fbId != null) {
            mipoProfileParseObject.setFbId (fbId);
        }
        String fbUrl = sp.getString (GlobalVariables.FB_PIC_URL, null);
        if (fbUrl != null) {
            mipoProfileParseObject.setFbUrl (fbUrl);
        }
        if (GlobalVariables.MY_LOCATION != null) {
            ParseGeoPoint parseGeoPoint = new ParseGeoPoint (GlobalVariables.MY_LOCATION.getLatitude (),
                                                                    GlobalVariables.MY_LOCATION.getLongitude ());
            mipoProfileParseObject.setLocation (parseGeoPoint);
        } else {
            ParseGeoPoint parseGeoPoint = new ParseGeoPoint (31.8971205,
                                                                    34.8136008);
            mipoProfileParseObject.setLocation (parseGeoPoint);
        }
        try {
            mipoProfileParseObject.save ();
            Toast.makeText (getApplicationContext (), R.string.successfully_signed_up, Toast.LENGTH_SHORT).show ();
            saveToFile (area + phoneET.getText ().toString ());
            GlobalVariables.CUSTOMER_PHONE_NUM = area + phoneET.getText ().toString ();
            GlobalVariables.IS_CUSTOMER_REGISTERED_USER = true;
            GlobalVariables.IS_CUSTOMER_GUEST = false;
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
        startActivityForResult (i, GlobalVariables.SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GlobalVariables.SELECT_PICTURE && resultCode == RESULT_OK && null != data) {
            Bitmap image = StaticMethods.getImageFromDevice (data, this);
            customerImageView.setImageBitmap (image);
            image_selected = true;
            imageSelected = true;
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
                Toast.makeText (getApplicationContext (), R.string.invalid_phone_number_try_again, Toast.LENGTH_SHORT).show ();
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
        File myExternalFile = new File (Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_DOWNLOADS), "verify.txt");
        try {
            FileOutputStream fos = new FileOutputStream (myExternalFile);
            fos.write (phone_number.getBytes ());
            fos.close ();
            Log.e ("number", phone_number);
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }

    private void getUserPreviousDetails(String user_number) {
        ParseQuery<MipoProfile> query = ParseQuery.getQuery ("Profile");
        query.whereEqualTo ("number", user_number);
        query.findInBackground (new FindCallback<MipoProfile> () {
            public void done(List<MipoProfile> numbers, ParseException e) {
                if (e == null) {
                    if (numbers.size () > 0) {
                        previousDataFound = numbers.get (0);
                        CustomerDetails customerDetails = StaticMethods.getUserDetailsWithBitmap (numbers);
                        if (usernameTE.getText ().toString ().isEmpty ()) {
                            usernameTE.setText (customerDetails.getCustomerName () + "");
                            usernameTE.setSelection (usernameTE.getText ().length ());
                        }
                        if (!image_selected) {
                            Bitmap customerImage = customerDetails.getBitmap ();
                            if (customerImage != null) {
                                customerImageView.setImageBitmap (customerImage);
                                image_was_before = true;
                            }
                        }
                    }
                } else {
                    e.printStackTrace ();
                }
            }
        });
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged (newConfig);
        if (locale != null) {
            newConfig.locale = locale;
            Locale.setDefault (locale);
            getBaseContext ().getResources ().updateConfiguration (newConfig, getBaseContext ().getResources ().getDisplayMetrics ());
        }
    }
}