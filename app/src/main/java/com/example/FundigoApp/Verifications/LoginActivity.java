package com.example.FundigoApp.Verifications;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.FundigoApp.Constants;
import com.example.FundigoApp.MainActivity;
import com.example.FundigoApp.R;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity {
    Button producer_loginButton;
    String producer_username;
    String producer_password;
    EditText producer_passwordET;
    EditText producer_usernameET;
    Button customer_loginButton;
    String isGuest = "";
    String customer_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.requestWindowFeature (Window.FEATURE_NO_TITLE);
        setContentView (R.layout.activity_login2);
        producer_usernameET = (EditText) findViewById (R.id.username_et);
        producer_passwordET = (EditText) findViewById (R.id.password_et);
        producer_loginButton = (Button) findViewById (R.id.button_login);
        customer_loginButton = (Button) findViewById (R.id.button_customer);

        customer_id = readFromFile ();
        if (customer_id.equals ("") || customer_id == null) {
            customer_loginButton.setText ("GUEST LOGIN");
            isGuest = "true";
            Constants.IS_GUEST = true;
        }
    }

    public void producerLogin(View v) {
        producer_username = producer_usernameET.getText ().toString ();
        producer_password = producer_passwordET.getText ().toString ();
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
                break;
            }
        }
        if (exists) {
            try {
                ParseUser.logIn (producer_username, producer_password);
                Toast.makeText (getApplicationContext (), "Successfully Loged in as producer", Toast.LENGTH_SHORT).show ();
                Constants.IS_PRODUCER = true;
                Intent intent = new Intent (this, MainActivity.class);
                intent.putExtra ("producerId", producer_username);
                startActivity (intent);
                finish ();
            } catch (ParseException e1) {
                Toast.makeText (getApplicationContext (), "Wrong Password, try again :)", Toast.LENGTH_SHORT).show ();
            }
        } else {
            Toast.makeText (getApplicationContext (), "This user does not exist, try again :)", Toast.LENGTH_SHORT).show ();
        }
    }

    public void customerLogin(View v) {
        Toast.makeText (this, "Successfully Loged in as customer", Toast.LENGTH_SHORT).show ();
        Intent intent = new Intent (this, MainActivity.class);
        if (isGuest.equals ("true")) {
            intent.putExtra ("chat_id", "Guest User");
        } else {
            intent.putExtra ("chat_id", customer_id);
        }
        startActivity (intent);
        finish ();
        ;
    }

    private String readFromFile() {
        String phone_number = "";
        try {
            InputStream inputStream = openFileInput ("verify.txt");
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader (inputStream);
                BufferedReader bufferedReader = new BufferedReader (inputStreamReader);
                String receiveString = "";
                while ((receiveString = bufferedReader.readLine ()) != null) {
                    phone_number = receiveString;
                }
                inputStream.close ();
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return phone_number;
    }
}
