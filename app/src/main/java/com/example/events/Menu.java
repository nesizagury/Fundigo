package com.example.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

/**
 * Created by benjamin on 01/01/2016.
 */
public class Menu extends AppCompatActivity {
    private final static String TAG = "Menu";
    LoginButton login_button;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_menu);

        login_button = (LoginButton) findViewById (R.id.login_button11);
        AccessToken accessToken = AccessToken.getCurrentAccessToken ();
        if (accessToken != null) {
            login_button.setVisibility (View.INVISIBLE);
        }

        callbackManager = CallbackManager.Factory.create ();
        login_button.registerCallback (callbackManager, new FacebookCallback<LoginResult> () {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText (getApplicationContext (), "error", Toast.LENGTH_SHORT).show ();
                Log.e (TAG, "" + exception.toString ());
            }
        });
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        login_button.setVisibility (View.INVISIBLE);
        callbackManager.onActivityResult (requestCode, resultCode, data);
    }
}
