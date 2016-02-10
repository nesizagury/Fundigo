package com.example.events;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by benjamin on 01/01/2016.
 */
public class Menu extends AppCompatActivity {
    LoginButton facebook_login_button;
    CallbackManager callbackManager;
    Button sms_login_button;
    Button user_profile_button;
    Button user_profile_update_button;
    Button user_evnets_tickets_button;

    protected String currentUser;
    protected String phoneNum;
    protected InputStream picStream;
    protected TableLayout tableLayout; //table to prsent profile
    protected ImageView drawView; // profile picture

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        facebook_login_button = (LoginButton) findViewById (R.id.login_button11);
        sms_login_button = (Button) findViewById (R.id.button3);
        user_profile_button = (Button) findViewById (R.id.buttonUserProfile);
        user_profile_update_button = (Button) findViewById (R.id.buttonUserProfileUpdate);
        user_evnets_tickets_button = (Button)findViewById (R.id.eventsTicketsButton);

                String number = readFromFile ();
        if (!number.isEmpty ()) {
            sms_login_button.setText("You logged in as " + number);
            sms_login_button.setOnClickListener(null);
            user_profile_button.setVisibility(View.VISIBLE);//if already registered then button is visible
            user_profile_update_button.setVisibility(View.VISIBLE);
            user_evnets_tickets_button.setVisibility(View.VISIBLE);
        }
        AccessToken accessToken = AccessToken.getCurrentAccessToken ();
        if (accessToken != null) {
            facebook_login_button.setVisibility (View.INVISIBLE);
        }

        callbackManager = CallbackManager.Factory.create ();
        facebook_login_button.registerCallback (callbackManager, new FacebookCallback<LoginResult> () {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText (getApplicationContext (), "error", Toast.LENGTH_SHORT).show ();
            }
        });
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
        //return "1-555-521-5554";
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        facebook_login_button.setVisibility (View.INVISIBLE);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void smsLogin(View view) {
        Bundle b = new Bundle ();
        Intent intent = new Intent (Menu.this, SmsSignUpActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume ();
        String number = readFromFile ();
        if (!number.isEmpty ()) {
            sms_login_button.setText ("You logged in as " + number);
            sms_login_button.setOnClickListener (null);
            user_profile_button.setVisibility(View.VISIBLE);
            user_profile_update_button.setVisibility(View.VISIBLE);
            user_evnets_tickets_button.setVisibility(View.VISIBLE);
        }
        tableLayout = (TableLayout) findViewById (R.id.profileTable);
        tableLayout.setVisibility (View.INVISIBLE);
        drawView = (ImageView) findViewById (R.id.profileImg);
        drawView.setVisibility(View.INVISIBLE);
    }

    public void getUserProfile(View view) { //get onclick event for pulling the user profile
        /// verify if not Guest and set the Button to visible done in Oncreate function
        List<ParseObject> list;
        String _userPhoneNumber = this.readFromFile ();
        if (!_userPhoneNumber.isEmpty ()) {
            try {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Numbers");
                query.whereEqualTo("number", _userPhoneNumber);
                list = query.find();
                for (ParseObject obj : list) {
                    currentUser = obj.getString("name");
                    phoneNum = obj.getString("number");
                    ParseFile parseFile = (ParseFile) obj.get("ImageFile");
                    picStream = parseFile.getDataStream();
                }
            } catch (ParseException e) {
                Log.e("Exception catch", e.toString());
            } catch (Exception e) {
                Log.e("Exception catch", e.toString());
            }
            try {
                if (picStream != null || !phoneNum.isEmpty() || !currentUser.isEmpty())
                    this.UserProfileDisplay();
               } catch (Exception e) {
                Toast.makeText (getApplicationContext (), "User may not Registered or not Exist", Toast.LENGTH_SHORT).show();
                Log.e("Exception catch", e.toString());
               }
        }
        else {
            //throw new Exception("Error Occured in getUserProfile method. User is not Exist or Null");
            Toast.makeText (getApplicationContext (), "User may not Registered or not Exist", Toast.LENGTH_SHORT).show();
           }
     }

    public void UserProfileDisplay() {
        tableLayout = (TableLayout) findViewById (R.id.profileTable);
        tableLayout.setVisibility (View.VISIBLE);

        TextView uRaw = (TextView) findViewById (R.id.userRow);
        TextView pRaw = (TextView) findViewById (R.id.phoneRow);
        uRaw.setText (currentUser);
        pRaw.setText (phoneNum);
        if (picStream != null) {// for present User Picture
            this.ImageStreamforProfileDisplay();
        }
    }

    public void ImageStreamforProfileDisplay() {
        drawView = (ImageView) findViewById (R.id.profileImg);
        drawView.setVisibility (View.VISIBLE);

        try {
            Drawable _draw = Drawable.createFromStream (picStream, null);// Assaf:Stream the Picture to the ImageView
            drawView.setImageDrawable (_draw);
        } catch (Exception e) {
            Log.e (e.toString (), "Image Stream Exception");
        } finally {
            try {
                picStream.close ();
            } catch (IOException e) {
                Log.e (e.toString (), "IO Exception occured");
            }
        }
    }

    public void updateUserProfile (View v)
    {
        try {
            Intent I = new Intent(this, CustomerProfileUpdate.class);
            startActivity(I);
        }
        catch (Exception e)
        {
            Log.e(e.toString(),"error in update flow");
        }
    }
    public void EventsTicketsDisplay (View v)
    {
        try {
            Intent I = new Intent(this, EventsTickets.class);
            startActivity(I);
        }
        catch (Exception e)
        {
            Log.e(e.toString(),"error in events tickets flow");
        }
    }
}
