package com.example.events;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by מנהל on 13/02/2016.
 */
public class SignUpActivity extends Activity {


    EditText mailET;
    EditText usernameET;
    EditText passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mailET = (EditText) findViewById(R.id.emailET);
        usernameET = (EditText) findViewById(R.id.usernameET);
        passwordET = (EditText) findViewById(R.id.passwordET);

    }


public void createAccount(View view){

    ParseUser currentUser = ParseUser.getCurrentUser();
    currentUser.logOut();

    ParseUser user = new ParseUser();
    user.setUsername(usernameET.getText ().toString ());
    user.setPassword(passwordET.getText().toString());
    user.setEmail(mailET.getText ().toString ());


    user.signUpInBackground(new SignUpCallback() {
        public void done(ParseException e) {
            if (e == null) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                builder.setMessage("Account Created Successfully!\n Verification mail sent to your mailbox \n please verify your account and log in.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                             finish();

                            }
                        })

                        .setCancelable(true);
                AlertDialog alert = builder.create();
                alert.show();

                Toast.makeText(getApplicationContext(), "Successfully Signed Up", Toast.LENGTH_SHORT).show ();
            } else {
                Toast.makeText (getApplicationContext(), "error = " + e.getMessage(), Toast.LENGTH_SHORT).show ();

            }
        }
    });

}

}
