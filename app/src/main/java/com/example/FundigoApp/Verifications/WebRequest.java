package com.example.FundigoApp.Verifications;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.example.FundigoApp.R;

public class WebRequest extends Activity {
    WebView myBrowser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        setContentView (R.layout.activitiy_webrequest);
        myBrowser = (WebView) findViewById (R.id.webView);
        myBrowser.loadUrl ("http://fundigo.weebly.com/");
    }
}