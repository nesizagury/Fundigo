package com.example.FundigoApp.Verifications;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.FundigoApp.Customer.Social.Profile;
import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.MainActivity;
import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethod.FileAndImageMethods;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;

public class LoginActivity extends Activity {
    Button producer_loginButton;
    String producer_username;
    String producer_password;
    EditText producer_passwordET;
    EditText producer_usernameET;
    Button customer_loginButton;
    boolean emailVerified = false;
    boolean passwordVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.requestWindowFeature (Window.FEATURE_NO_TITLE);
        setContentView (R.layout.activity_login_page);
        producer_usernameET = (EditText) findViewById (R.id.username_et);
        producer_passwordET = (EditText) findViewById (R.id.password_et);
        producer_loginButton = (Button) findViewById (R.id.button_login);
        customer_loginButton = (Button) findViewById (R.id.button_customer);

        GlobalVariables.CUSTOMER_PHONE_NUM = FileAndImageMethods.getCustomerPhoneNumFromFile (this);
        if (GlobalVariables.CUSTOMER_PHONE_NUM == null || GlobalVariables.CUSTOMER_PHONE_NUM.equals ("")) {
            customer_loginButton.setText (R.string.guest_login);
        }
    }

    public void producerLogin(View v) {
        producer_username = producer_usernameET.getText ().toString ();
        producer_password = producer_passwordET.getText ().toString ();
        passwordVerified = false;
        emailVerified = false;
        List<ParseUser> parseUsers = new ArrayList<ParseUser> ();
        ParseQuery<ParseUser> query1 = ParseUser.getQuery ();
        try {
            parseUsers = query1.find ();
        } catch (ParseException e) {
            e.printStackTrace ();
        }
        boolean exists = false;
        for (ParseUser user : parseUsers) {
            if (user.getUsername ().equals (producer_username)) {
                exists = true;
                if (user.get ("emailVerified") != null &&
                            (boolean) user.get ("emailVerified")) {
                    emailVerified = true;
                }
                break;
            }
        }
        if (exists) {
            try {
                ParseUser.logIn (producer_username, producer_password);
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
                e1.printStackTrace ();
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
            GlobalVariables.IS_CUSTOMER_REGISTERED_USER = false;
            GlobalVariables.IS_CUSTOMER_GUEST = true;
            GlobalVariables.CUSTOMER_PHONE_NUM = "";
        } else {
            GlobalVariables.IS_CUSTOMER_GUEST = false;
            GlobalVariables.IS_CUSTOMER_REGISTERED_USER = true;
            ParseQuery<Profile> query = ParseQuery.getQuery ("Profile");
            query.whereEqualTo ("number", GlobalVariables.CUSTOMER_PHONE_NUM);
            query.findInBackground (new FindCallback<Profile> () {
                @Override
                public void done(List<Profile> objects, ParseException e) {
                    if (e == null) {
                        if(objects.get (0).getChanels () != null){
                            if(GlobalVariables.userChanels.size () == 0) {
                                GlobalVariables.userChanels.addAll (objects.get (0).getChanels ());
                            }
                            ParseInstallation installation = ParseInstallation.getCurrentInstallation ();
                            installation.addAll ("Channels", (Collection<?>) GlobalVariables.userChanels);
                            installation.saveInBackground ();
                            for (int i = 0; i < GlobalVariables.userChanels.size (); i++) {
                                ParsePush.subscribeInBackground ("a" + GlobalVariables.userChanels.get (i));
                            }
                        }
                    } else{
                        e.printStackTrace ();
                    }
                }

            });
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
                        GlobalVariables.deepLinkEventObjID = referringParams.getString ("objectId");
                    } catch (JSONException e) {
                        e.printStackTrace ();
                    }
                } else {
                    Log.e ("LoginActivity", error.getMessage ());
                }
            }
        }, this.getIntent ().getData (), this);
    }

    public void signUp(View view) {
        Intent intent = new Intent (LoginActivity.this, CreateNewProducerActivity.class);
        startActivity (intent);
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent (intent);
    }
}
