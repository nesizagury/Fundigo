package com.example.FundigoApp.Verifications;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.MainActivity;
import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethods;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;

public class LoginActivity extends Activity {
    final static String TRIAL_GUEST_PHONE = "GUEST";

    Button producer_loginButton;
    String producer_username;
    String producer_password;
    EditText producer_passwordET;
    EditText producer_usernameET;
    Button customer_loginButton;
    boolean emailVerified = false;
    public static String x = "";
    boolean passwordVerified;
    Spinner spinr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login_page);

        producer_usernameET = (EditText) findViewById (R.id.username_et);
        producer_passwordET = (EditText) findViewById (R.id.password_et);
        producer_loginButton = (Button) findViewById (R.id.button_login);
        customer_loginButton = (Button) findViewById (R.id.button_customer);
        spinr=(Spinner)findViewById(R.id.spinner);
        if(Locale.getDefault().getDisplayLanguage().equals("iw")){
        }
        GlobalVariables.CUSTOMER_PHONE_NUM = StaticMethods.getCustomerPhoneNumFromFile (this);
        if (GlobalVariables.CUSTOMER_PHONE_NUM == null || GlobalVariables.CUSTOMER_PHONE_NUM.equals ("")) {
            customer_loginButton.setText (R.string.guest_login);
            GlobalVariables.IS_CUSTOMER_GUEST=true;
        }
    }

    public void producerLogin(View v) {
        producer_username = producer_usernameET.getText ().toString ();
        producer_password = producer_passwordET.getText ().toString ();
        passwordVerified = false;
        emailVerified = false;
        List<ParseUser> list = new ArrayList<ParseUser> ();
        ParseQuery<ParseUser> query1 = ParseUser.getQuery ();
        try {
            list = query1.find ();
        } catch (ParseException e) {
            Toast.makeText (this, "Error " + e, Toast.LENGTH_SHORT).show ();
        }
        boolean exists = false;
        for (ParseUser user : list) {
            if (user.getUsername ().equals (producer_username)) {
                exists = true;
                if (user.get ("emailVerified") != null &&
                            (boolean)user.get ("emailVerified") == true) {
                    emailVerified = true;
                }
                break;
            }
        }
        if (exists) {
            try {
                ParseUser.logIn(producer_username, producer_password);
                if (!emailVerified) {
                    Toast.makeText (getApplicationContext (), R.string.verify_email, Toast.LENGTH_SHORT).show ();
                    ParseUser.logOut ();
                    return;
                }
                Toast.makeText (getApplicationContext (), R.string.successfully_logged_in_as_producer, Toast.LENGTH_SHORT).show ();
                GlobalVariables.IS_PRODUCER = true;
                GlobalVariables.IS_CUSTOMER_REGISTERED_USER = false;
                GlobalVariables.IS_CUSTOMER_GUEST = false;
                GlobalVariables.CUSTOMER_PHONE_NUM = null;
                GlobalVariables.PRODUCER_PARSE_OBJECT_ID = ParseUser.getCurrentUser ().getObjectId ();
                Intent intent = new Intent (this, MainActivity.class);
                GlobalVariables.ALL_EVENTS_DATA.clear ();
                startActivity (intent);
                finish ();
            } catch (ParseException e1) {
                Toast.makeText (getApplicationContext (), R.string.wrong_password_try_again, Toast.LENGTH_SHORT).show ();
            }
        } else {
            Toast.makeText (getApplicationContext (), R.string.this_user_does_not_exist_try_again, Toast.LENGTH_SHORT).show ();
        }
    }

    public void customerLogin(View v) {
        Toast.makeText (this, R.string.successfully_logged_in_as_customer, Toast.LENGTH_SHORT).show ();
        Intent intent = new Intent (this, MainActivity.class);
        if (GlobalVariables.CUSTOMER_PHONE_NUM == null || GlobalVariables.CUSTOMER_PHONE_NUM.equals ("")) {
            //at the moment we make all guest user with a trial phone num
            //for the presentation purpose
            //in the future guest will not be able to send msg
            //or save tickets etc.
            GlobalVariables.IS_CUSTOMER_REGISTERED_USER = false;//TODO
            GlobalVariables.IS_CUSTOMER_GUEST = true;//TODO
            GlobalVariables.CUSTOMER_PHONE_NUM = TRIAL_GUEST_PHONE;
        } else {
            GlobalVariables.IS_CUSTOMER_GUEST = false;
            GlobalVariables.IS_CUSTOMER_REGISTERED_USER = true;
        }
        GlobalVariables.IS_PRODUCER = false;
        GlobalVariables.PRODUCER_PARSE_OBJECT_ID = null;
        GlobalVariables.ALL_EVENTS_DATA.clear ();
        startActivity (intent);
        finish ();
    }

    @Override
    public void onStart() {
        super.onStart ();

        Branch branch = Branch.getInstance ();
        branch.initSession (new Branch.BranchReferralInitListener () {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    // params are the deep linked params associated with the link that the user clicked before showing up
                    try {
                        x = referringParams.getString ("objectId");
                        Toast.makeText (getApplicationContext (), "id = " + x, Toast.LENGTH_SHORT).show ();

                    } catch (JSONException e) {
                        e.printStackTrace ();
                    }

                } else
                    Toast.makeText (getApplicationContext (), error.getMessage (), Toast.LENGTH_SHORT).show ();

            }

        }, this.getIntent ().getData (), this);


    }

    public void signUp(View view) {
        Intent intent = new Intent (LoginActivity.this, SignUpActivity.class);
        startActivity (intent);
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent (intent);
    }
}
