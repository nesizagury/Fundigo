package com.example.FundigoApp.Verifications;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import com.example.FundigoApp.R;

/**
 * Created by מנהל on 30/01/2016.
 */
public class WebRequest extends Activity {

    WebView myBrowser;
    EditText Msg;
    Button btnSendMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activitiy_webrequest);
        myBrowser = (WebView)findViewById(R.id.webView);
        myBrowser.loadUrl("http://fundigo.weebly.com/");


    }
}