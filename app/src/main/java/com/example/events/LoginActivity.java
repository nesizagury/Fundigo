package com.example.events;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
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
    String isGuest = "";
    String customer_id;
    boolean emailVerified = false;
   static String x = "";
    boolean passwordVerified;
    boolean exists;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login2);

        producer_usernameET = (EditText) findViewById (R.id.username_et);
        producer_passwordET = (EditText) findViewById (R.id.password_et);
        producer_loginButton = (Button) findViewById (R.id.button_login);
        customer_loginButton = (Button) findViewById (R.id.button_customer);

        customer_id = readFromFile("verify");
        if (customer_id.equals ("") || customer_id == null) {
            customer_loginButton.setText("GUEST LOGIN");
            isGuest = "true";
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

        for (ParseUser user : list) {
            if (user.getUsername ().equals (producer_username)) {

                if(user.get("emailVerified") == true)
                    emailVerified = true;


                ParseUser.logInInBackground(producer_username, producer_password, new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            exists = true;
                            if (exists && emailVerified) {

                                Toast.makeText(getApplicationContext(), "Successfully Loged in as producer", Toast.LENGTH_SHORT).show();
                                Constants.IS_PRODUCER = true;
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("producerId", producer_username);
                                startActivity(intent);
                                finish();
                            }
                            else
                            if(exists && !emailVerified)
                                Toast.makeText(getApplicationContext(), "verify email" , Toast.LENGTH_SHORT).show();
                            else
                            if(!exists)
                                Toast.makeText(getApplicationContext(), "Please Check Your Login Details" , Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getApplicationContext(), "Please Check Your Login Details" , Toast.LENGTH_SHORT).show();

                        }
                    }
                });

                break;
            }
        }


    }

    private void checkEmail(ParseUser user) {

        user.setEmail(producer_usernameET.getText().toString());
        Toast.makeText (this, "Sent", Toast.LENGTH_SHORT).show();


    }


    public void customerLogin(View v) {
        Toast.makeText (this, "Successfully Loged in as customer", Toast.LENGTH_SHORT).show ();
        Intent intent = new Intent (this, MainActivity.class);
        if (isGuest.equals ("true")) {
            intent.putExtra ("chat_id", "1234");
        } else {
            intent.putExtra ("chat_id", customer_id);
        }

        startActivity(intent);
        finish();

    }

    private String readFromFile(String file) {
       String s = "";
        try {
            InputStream inputStream = openFileInput (file + ".txt");
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader (inputStream);
                BufferedReader bufferedReader = new BufferedReader (inputStreamReader);
                String receiveString = "";
                while ((receiveString = bufferedReader.readLine ()) != null) {
                    s = receiveString;
                }
                inputStream.close ();
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return s;
    }


    @Override
    public void onStart() {
        super.onStart();

        Branch branch = Branch.getInstance();
        branch.initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    // params are the deep linked params associated with the link that the user clicked before showing up
                    try {
                        x = referringParams.getString("objectId");
                        Toast.makeText (getApplicationContext(), "id = " + x, Toast.LENGTH_SHORT).show ();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                else
                    Toast.makeText (getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show ();

            }

        }, this.getIntent().getData(), this);


    }

    public void signUp(View view){

        Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
        startActivity(intent);

    }



    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }


}
