package com.example.FundigoApp.Verifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.example.FundigoApp.Customer.CustomerDetails;
import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethods;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
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
    Numbers previousDataFound = null;
    ImageLoader loader;

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
        Numbers number;
        if (previousDataFound != null) {
            number = previousDataFound;
        } else {
            number = new Numbers ();
        }
        number.setName (username);
        if (image_selected) {
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
            number.setACL (parseAcl);
            number.put ("ImageFile", file);
        }
        number.setNumber (area + phoneET.getText ().toString ());
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences (SmsSignUpActivity.this);
        String fbId = sp.getString (GlobalVariables.FB_ID, null);
        if (fbId != null) {
            number.setFbId (fbId);
        }
        String fbUrl = sp.getString (GlobalVariables.FB_PIC_URL, null);
        if (fbUrl != null) {
            number.setFbUrl (fbUrl);
        }
        try {
            number.save ();
            Toast.makeText (getApplicationContext (), "Successfully Signed up", Toast.LENGTH_SHORT).show ();
            saveToFile (area + phoneET.getText ().toString ());
            GlobalVariables.CUSTOMER_PHONE_NUM = area + phoneET.getText ().toString ();
            GlobalVariables.IS_CUSTOMER_REGISTERED_USER = true;
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
        OutputStreamWriter outputStreamWriter = null;
        try {
            outputStreamWriter = new OutputStreamWriter (this.openFileOutput ("verify.txt", Context.MODE_MULTI_PROCESS));
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

    private void getUserPreviousDetails(String user_number) {
        ParseQuery<Numbers> query = ParseQuery.getQuery (Numbers.class);
        query.whereEqualTo ("number", user_number);
        query.findInBackground (new FindCallback<Numbers> () {
            public void done(List<Numbers> numbers, ParseException e) {
                if (e == null) {
                    if (numbers.size () > 0) {
                        CustomerDetails customerDetails = StaticMethods.getUserDetails (numbers);
                        if (usernameTE.getText ().toString ().isEmpty ()) {
                            usernameTE.setText (customerDetails.getCustomerName () + "");
                            usernameTE.setSelection (usernameTE.getText ().length ());
                        }
                        if (!image_selected) {
                            String customerImage = customerDetails.getCustomerImage ();
                            if (customerImage != null) {
                                image_selected = true;
                                loader = StaticMethods.getImageLoader(SmsSignUpActivity.this);
                                loader.displayImage(customerImage,customerImageView);
                            }
                        }
                    }
                } else {
                    e.printStackTrace ();
                }
            }
        });
    }
}