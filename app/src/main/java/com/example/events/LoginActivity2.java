package com.example.events;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class LoginActivity2 extends Activity {

    Button producer_loginButton;
    Button signupButton;
    String producer_username;
    EditText producer_usernameET;

    Button customer_loginButton;

    String isGuest = "";

    String customer_id;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login2);

        producer_usernameET = (EditText) findViewById (R.id.username_et);
        producer_loginButton = (Button) findViewById (R.id.button_login);
        // signupButton = (Button) findViewById (R.id.button_signup);

        customer_loginButton = (Button) findViewById (R.id.button_customer);

        customer_id = readFromFile();

        if(customer_id.equals("") || customer_id == null)
        {
            customer_loginButton.setText("GUEST LOGIN");
            isGuest = "true";
        }


    }

    public void producerLogin(View v) {


        producer_username = producer_usernameET.getText ().toString ();
        LogIn(producer_username);


    }

    public void customerLogin(View v) {

        LogIn("");

    }




    public void Signup(View view) {
        producer_username = producer_usernameET.getText ().toString ();

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
            Toast.makeText (getApplicationContext (), "Username exists, try again :)", Toast.LENGTH_SHORT).show ();
        } else {
            ParseUser user = new ParseUser ();
            user.setUsername (producer_username);
            user.setPassword (producer_username);
            user.isNew();
            try {
                user.signUp ();
                Toast.makeText (getApplicationContext (), "Successfully Signed up", Toast.LENGTH_SHORT).show ();
                Intent intent = new Intent (this, MainActivity.class);
                startActivity (intent);
                finish ();
            } catch (ParseException e) {
                Toast.makeText (getApplicationContext (), "Error" + e, Toast.LENGTH_SHORT).show ();
            }
        }
    }


    private String readFromFile() {
        String phone_number = "";
        try {
            InputStream inputStream = openFileInput("verify.txt");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    phone_number = receiveString;
                    Toast.makeText(getApplicationContext(), phone_number , Toast.LENGTH_SHORT).show();
                }
                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return phone_number;
    }


    public void LogIn(String username){

        if(username.equals(""))
        {
            Toast.makeText(getApplicationContext(), "Successfully Loged in as customer", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            if(isGuest.equals("true"))
                intent.putExtra("is_guest",isGuest);
            else
                intent.putExtra("chat_id",customer_id);

            startActivity(intent);
            finish ();
        }
        else {
            List<ParseUser> list = new ArrayList<ParseUser>();
            ParseQuery<ParseUser> query1 = ParseUser.getQuery();
            try {
                list = query1.find();
            } catch (ParseException e) {
                Toast.makeText(this, "Error " + e, Toast.LENGTH_SHORT).show();
            }
            boolean exists = false;
            for (ParseUser user : list) {
                if (user.getUsername().equals(username)) {
                    exists = true;
                    break;
                }
            }
            if (exists) {
                try {
                    ParseUser.logIn(username, username);
                    Toast.makeText(getApplicationContext(), "Successfully Loged in", Toast.LENGTH_SHORT).show();
                    Constants.IS_PRODUCER = true;
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (ParseException e1) {
                    Toast.makeText(getApplicationContext(), "Wrong Password, try again :)", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "This user does not exist, try again :)", Toast.LENGTH_SHORT).show();
            }
        }


    }

}
