package com.example.FundigoApp.Verifications;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.FundigoApp.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class CreateNewProducerActivity extends Activity {

    EditText mailET;
    EditText usernameET;
    EditText passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_signup_new_producer);
        mailET = (EditText) findViewById (R.id.emailET);
        usernameET = (EditText) findViewById (R.id.usernameET);
        passwordET = (EditText) findViewById (R.id.passwordET);
    }

    public void createAccount(View view) {
        ParseUser currentUser = ParseUser.getCurrentUser ();
        currentUser.logOut ();
        ParseUser user = new ParseUser ();
        user.setUsername (usernameET.getText ().toString ());
        user.setPassword (passwordET.getText ().toString ());
        user.setEmail (mailET.getText ().toString ());

        user.signUpInBackground (new SignUpCallback () {
            public void done(ParseException e) {
                if (e == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder (CreateNewProducerActivity.this);
                    builder.setMessage (R.string.account_created_successfully_verification_mail)
                            .setCancelable (false)
                            .setPositiveButton (R.string.ok, new DialogInterface.OnClickListener () {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish ();
                                }
                            })
                            .setCancelable (true);
                    AlertDialog alert = builder.create ();
                    alert.show ();
                    Toast.makeText (getApplicationContext (), R.string.successfully_signed_up, Toast.LENGTH_SHORT).show ();
                } else {
                    Toast.makeText (getApplicationContext (), "error = " + e.getMessage (), Toast.LENGTH_SHORT).show ();
                    Log.e ("CreateNewProducer", e.getMessage ());
                }
            }
        });
    }
}
