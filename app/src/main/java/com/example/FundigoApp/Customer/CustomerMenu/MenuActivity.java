package com.example.FundigoApp.Customer.CustomerMenu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.FundigoApp.Customer.CustomerDetails;
import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethods;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MenuActivity extends AppCompatActivity {
    LoginButton facebook_login_button;
    CallbackManager callbackManager;
    Button sms_login_button;
    Button user_profile_button;
    LoginButton facebook_logout_button;
    String currentUserName;
    String phoneNum;
    String userImage;
    TableLayout tableLayout; //table to prsent profile
    ImageView drawView; // profile picture
    TextView facebookUserNameView;
    ImageView profileFacebookPictureView;
    Context context;
    Button user_profile_update_button;
    Button user_evnets_tickets_button;
    Button save_credit_card_button;
    Button delete_credit_card_button;
    ImageLoader loader;

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
        user_profile_update_button = (Button) findViewById (R.id.buttonUserProfileUpdate);
        user_evnets_tickets_button = (Button) findViewById (R.id.eventsTicketsButton);
        facebook_logout_button = (LoginButton) findViewById (R.id.logout_button11);
        save_credit_card_button = (Button) findViewById (R.id.save_credit_card);
        delete_credit_card_button = (Button) findViewById (R.id.delete_credit_card);
        tableLayout = (TableLayout) findViewById (R.id.profileTable);
        drawView = (ImageView) findViewById (R.id.profileImg);

        String number = GlobalVariables.CUSTOMER_PHONE_NUM;
        if (!number.equals ("GUEST")) {
            sms_login_button.setText ("You logged in as " + number);
            sms_login_button.setOnClickListener (null);
            user_profile_button.setVisibility (View.VISIBLE);//if already registered then button is visible
            user_profile_update_button.setVisibility (View.VISIBLE);
            user_evnets_tickets_button.setVisibility (View.VISIBLE);
        }
        final AccessToken accessToken = AccessToken.getCurrentAccessToken ();
        if (accessToken != null) {
            facebook_login_button.setVisibility (View.GONE);
            profileFacebookPictureView.setVisibility (View.VISIBLE);
            facebookUserNameView.setVisibility (View.VISIBLE);
            facebook_logout_button.setVisibility (View.VISIBLE);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences (MenuActivity.this);
            String name = sp.getString (GlobalVariables.FB_NAME, null);
            String pic_url = sp.getString (GlobalVariables.FB_PIC_URL, null);
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
                                                           (MenuActivity.this,
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

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        callbackManager.onActivityResult (requestCode, resultCode, data);
    }

    public void smsLogin(View view) {
        Intent intent = new Intent (MenuActivity.this, SmsSignUpActivity.class);
        startActivity (intent);
    }

    @Override
    protected void onResume() {
        super.onResume ();
        String number = GlobalVariables.CUSTOMER_PHONE_NUM;
        if (!number.equals ("GUEST")) {
            sms_login_button.setText ("You logged in as " + number);
            sms_login_button.setOnClickListener (null);
            user_profile_button.setVisibility (View.VISIBLE);
            user_profile_update_button.setVisibility (View.VISIBLE);
            user_evnets_tickets_button.setVisibility (View.VISIBLE);
            ParseQuery<CreditCard> query = new ParseQuery ("creditCards");
            query.whereEqualTo ("IdCostumer", GlobalVariables.CUSTOMER_PHONE_NUM);
            query.getFirstInBackground (new GetCallback<CreditCard> () {
                public void done(CreditCard creditCard, ParseException e) {
                    if (e == null) {
                        save_credit_card_button.setVisibility (View.GONE);
                        delete_credit_card_button.setVisibility (View.VISIBLE);
                        String creditCardNumber = creditCard.getCreditCardNumber ();
                        String last4Digits = creditCardNumber.substring (creditCardNumber.length () - 4, creditCardNumber.length ());
                        delete_credit_card_button.setText ("Delete Credit Card XXXX-" + last4Digits);
                    } else if (e.getCode () == ParseException.OBJECT_NOT_FOUND) {
                        save_credit_card_button.setVisibility (View.VISIBLE);
                        delete_credit_card_button.setVisibility (View.GONE);
                    } else {
                        e.printStackTrace ();
                    }
                }
            });
        }
        tableLayout.setVisibility (View.GONE);
        drawView.setVisibility (View.GONE);
    }

    public void getUserProfile(View view) { //get onclick event for pulling the user profile
        /// verify if not Guest and set the Button to visible done in Oncreate function
        phoneNum = GlobalVariables.CUSTOMER_PHONE_NUM;
        CustomerDetails customerDetails = StaticMethods.getUserDetailsFromParseInMainThread (phoneNum);
        currentUserName = customerDetails.getCustomerName ();
        userImage = customerDetails.getCustomerImage ();
        userProfileDisplay ();
    }

    public void userProfileDisplay() {
        tableLayout.setVisibility (View.VISIBLE);
        TextView uRaw = (TextView) findViewById (R.id.userRow);
        TextView pRaw = (TextView) findViewById (R.id.phoneRow);
        uRaw.setText (currentUserName);
        pRaw.setText (phoneNum);
        if (userImage != null) {// for present User Picture
            drawView.setVisibility (View.VISIBLE);
            loader = StaticMethods.getImageLoader(this);
            loader.displayImage(userImage,drawView);
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
                                             SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences (MenuActivity.this);
                                             SharedPreferences.Editor editor = sp.edit ();
                                             editor.putString (GlobalVariables.FB_NAME, response.getJSONObject ().getString ("name"));
                                             editor.putString (GlobalVariables.FB_PIC_URL, data.getString ("url"));
                                             editor.putString (GlobalVariables.FB_ID, response.getJSONObject ().getString ("id"));
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

    public void updateUserProfile(View v) {
        try {
            Intent I = new Intent (this, CustomerProfileUpdate.class);
            startActivity (I);
        } catch (Exception e) {
            Log.e (e.toString (), "error in update flow");
        }
    }

    public void EventsTicketsDisplay(View v) { //Assaf: open tickets for Registered user. for Guest present a Dialog box
        if (!GlobalVariables.CUSTOMER_PHONE_NUM.equals ("GUEST")) {
            try {
                Intent I = new Intent (this, MyEventsTicketsActivity.class);
                startActivity (I);
            } catch (Exception e) {
                e.printStackTrace ();
            }
        } else {
            //show dialog in case  Guest want to see Tickets page. Tickets not saved for Guest
            final AlertDialog.Builder builder = new AlertDialog.Builder (this);
            builder.setMessage ("In Order To Save And View Tickets that Purchased You Have To Pass Registration First")
                    .setCancelable (true)
                    .setNeutralButton ("Register by SMS", new DialogInterface.OnClickListener () {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent smsRegister = new Intent (MenuActivity.this, SmsSignUpActivity.class);
                            startActivity (smsRegister);
                        }
                    });

            builder.setPositiveButton ("Cancel",new DialogInterface.OnClickListener () {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel ();
                }
            });
            AlertDialog smsAlert = builder.create ();
            smsAlert.show ();
        }
    }

    public void saveCreditCard(View v) {
        Intent intent = new Intent (this, SaveCreditCard.class);
        startActivity (intent);
    }

    public void deleteCreditCard(View v) {
        ParseQuery<CreditCard> query = new ParseQuery ("creditCards");
        query.whereEqualTo ("IdCostumer", GlobalVariables.CUSTOMER_PHONE_NUM);
        CreditCard creditCard;
        try {
            creditCard = query.getFirst ();
            creditCard.delete ();
            save_credit_card_button.setVisibility (View.VISIBLE);
            delete_credit_card_button.setVisibility (View.GONE);
        } catch (ParseException e) {
            e.printStackTrace ();
        }
    }
}
