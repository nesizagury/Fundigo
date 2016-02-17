package com.example.FundigoApp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.FundigoApp.Verifications.SmsSignUpActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by benjamin on 01/01/2016.
 */
public class Menu extends AppCompatActivity {
    LoginButton facebook_login_button;
    CallbackManager callbackManager;
    Button sms_login_button;
    Button user_profile_button;
    LoginButton facebook_logout_button;
    protected String currentUser;
    protected String phoneNum;
    protected InputStream picStream;
    protected TableLayout tableLayout; //table to prsent profile
    protected ImageView drawView; // profile picture
    TextView facebookUserNameView;
    ImageView profileFacebookPictureView;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_menu);

        context = this;
        facebook_login_button = (LoginButton) findViewById (R.id.login_button11);
        facebookUserNameView = (TextView) findViewById (R.id.profileUserName);
        profileFacebookPictureView = (ImageView) findViewById (R.id.faebook_profile);
        sms_login_button = (Button) findViewById (R.id.button3);
        user_profile_button = (Button) findViewById (R.id.buttonUserProfile);
        facebook_logout_button = (LoginButton) findViewById (R.id.logout_button11);
        String number = readFromFile ();
        if (!number.isEmpty ()) {
            sms_login_button.setText ("You logged in as " + number);
            sms_login_button.setOnClickListener (null);
            user_profile_button.setVisibility (View.VISIBLE);//if already registered then button is visible
        }
        final AccessToken accessToken = AccessToken.getCurrentAccessToken ();
        if (accessToken != null) {
            facebook_login_button.setVisibility (View.GONE);
            profileFacebookPictureView.setVisibility (View.VISIBLE);
            facebookUserNameView.setVisibility (View.VISIBLE);
            facebook_logout_button.setVisibility (View.VISIBLE);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences (Menu.this);
            String name = sp.getString (Constants.FB_NAME, null);
            String pic_url = sp.getString (Constants.FB_PIC_URL, null);
            Picasso.with (context).load (pic_url).into (profileFacebookPictureView);
            facebookUserNameView.setText (name);
        } else {
            facebook_login_button.setVisibility (View.VISIBLE);
            facebook_logout_button.setVisibility (View.GONE);
            profileFacebookPictureView.setVisibility (View.GONE);
            facebookUserNameView.setVisibility (View.GONE);
        }

        callbackManager = CallbackManager.Factory.create ();
        facebook_login_button.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance ().
                                                   logInWithReadPermissions
                                                           (Menu.this,
                                                                   Arrays.asList
                                                                                  ("public_profile",
                                                                                          "user_friends",
                                                                                          "email"));
            }
        });
        // Callback registration
        facebook_login_button.registerCallback (callbackManager, new FacebookCallback<LoginResult> () {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                accessToken.setCurrentAccessToken (loginResult.getAccessToken ());
                getUserDetailsFromFB ();
                facebook_login_button.setVisibility (View.GONE);
                facebook_logout_button.setVisibility (View.VISIBLE);
                profileFacebookPictureView.setVisibility (View.VISIBLE);
                facebookUserNameView.setVisibility (View.VISIBLE);
            }

            @Override
            public void onCancel() {
                Toast.makeText (context, "Canceled logging facebook", Toast.LENGTH_SHORT).show ();

            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText (context, "Error logging facebook", Toast.LENGTH_SHORT).show ();
                exception.printStackTrace ();
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
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        callbackManager.onActivityResult (requestCode, resultCode, data);
    }

    public void smsLogin(View view) {
        Intent intent = new Intent (Menu.this, SmsSignUpActivity.class);
        startActivity (intent);
    }

    @Override
    protected void onResume() {
        super.onResume ();
        String number = readFromFile ();
        if (!number.isEmpty ()) {
            sms_login_button.setText ("You logged in as " + number);
            sms_login_button.setOnClickListener (null);
            user_profile_button.setVisibility (View.VISIBLE);
        }
        tableLayout = (TableLayout) findViewById (R.id.profileTable);
        tableLayout.setVisibility (View.INVISIBLE);
        drawView = (ImageView) findViewById (R.id.profileImg);
        drawView.setVisibility (View.INVISIBLE);
    }

    public void getUserProfile(View view) { //get onclick event for pulling the user profile
        /// verify if not Guest and set the Button to visible done in Oncreate function
        List<ParseObject> list;
        String _userPhoneNumber = this.readFromFile ();
        if (!_userPhoneNumber.isEmpty ()) {
            try {
                ParseQuery<ParseObject> query = ParseQuery.getQuery ("Numbers");
                query.whereEqualTo ("number", _userPhoneNumber);
                list = query.find ();
                for (ParseObject obj : list) {
                    currentUser = obj.getString ("name");
                    phoneNum = obj.getString ("number");
                    ParseFile parseFile = (ParseFile) obj.get ("ImageFile");
                    picStream = parseFile.getDataStream ();
                }
            } catch (ParseException e) {
                Log.e ("Exception catch", e.toString ());
            } catch (Exception e) {
                Log.e ("Exception catch", e.toString ());
            }
            this.UserProfileDisplay ();
        } else {
            //throw new Exception("Error Occured in getUserProfile method. User is not Exist or Null");
            Toast.makeText (getApplicationContext (), "User may not Registered or not Exist", Toast.LENGTH_SHORT).show ();
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
            this.ImageStreamforProfileDisplay ();
        }
    }

    public void ImageStreamforProfileDisplay() {
        drawView = (ImageView) findViewById (R.id.profileImg);
        drawView.setVisibility (View.VISIBLE);
        try {
            Drawable _draw = Drawable.createFromStream (picStream, null);// Stream the Picture to the ImageView
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

    public void sendPush(View v) {
        SimpleDateFormat sdf = new SimpleDateFormat ("dd/MM/yyyy_HH:mm:ss");
        String currentDateandTime = sdf.format (new Date ());
        ParsePush push = new ParsePush ();
        push.setMessage ("Hey Come To See Events Near You (" + currentDateandTime + ")");
        try {
            push.send ();
        } catch (ParseException e) {
            e.printStackTrace ();
        }
    }

    private void getUserDetailsFromFB() {
        Bundle parameters = new Bundle ();
        parameters.putString ("fields", "email,name,picture,link");
        new GraphRequest (
                                 AccessToken.getCurrentAccessToken (),
                                 "/me",
                                 parameters,
                                 HttpMethod.GET,
                                 new GraphRequest.Callback () {
                                     public void onCompleted(GraphResponse response) {
                                         try {
                                             JSONObject picture = response.getJSONObject ().getJSONObject ("picture");
                                             JSONObject data = picture.getJSONObject ("data");
                                             SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences (Menu.this);
                                             SharedPreferences.Editor editor = sp.edit ();
                                             editor.putString (Constants.FB_NAME, response.getJSONObject ().getString ("name"));
                                             editor.putString (Constants.FB_PIC_URL, data.getString ("url"));
                                             editor.putString (Constants.FB_ID, response.getJSONObject ().getString ("id"));
                                             editor.apply ();
                                             Picasso.with (context).load (data.getString ("url")).into (profileFacebookPictureView);
                                             facebookUserNameView.setText (response.getJSONObject ().getString ("name"));
                                         } catch (JSONException e) {
                                             e.printStackTrace ();
                                         }
                                     }
                                 }
        ).executeAsync ();
    }

    public void logOutFacebook(View view) {
        new GraphRequest (AccessToken.getCurrentAccessToken (), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest.Callback () {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                LoginManager.getInstance ().logOut ();
                Toast.makeText (context, "Loged Out of facebook", Toast.LENGTH_SHORT).show ();
                facebook_login_button.setVisibility (View.VISIBLE);
                facebook_logout_button.setVisibility (View.GONE);
                profileFacebookPictureView.setVisibility (View.GONE);
                facebookUserNameView.setVisibility (View.GONE);
            }
        }).executeAsync ();
    }
}
