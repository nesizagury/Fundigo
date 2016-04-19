package com.example.FundigoApp.Tickets;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.FundigoApp.GlobalVariables;
import com.parse.ParseException;

public class WebBrowserActivity extends AppCompatActivity {
    private String amount;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        MyWebView view = new MyWebView (this);

        Intent i = getIntent ();
        amount = i.getStringExtra ("eventPrice");
        String isSeats = i.getStringExtra ("isChoose");
        if (isSeats.equals ("no")) {
            String eventObjectId = i.getStringExtra ("eventObjectId");
            EventsSeats eventsSeats = new EventsSeats ();
            eventsSeats.put ("price", Integer.parseInt (amount));
            eventsSeats.put ("eventObjectId", eventObjectId);
            eventsSeats.setCustomerPhone (GlobalVariables.CUSTOMER_PHONE_NUM);
            eventsSeats.setIsSold (false);
            try {
                eventsSeats.save ();
            } catch (ParseException e) {
                e.printStackTrace ();
            }
            orderId = eventsSeats.getObjectId ();
        } else {
            orderId = i.getStringExtra ("seatParseObjId");
        }

        view.getSettings ().setJavaScriptEnabled (true);
        view.getSettings ().setDomStorageEnabled (true);
        view.getSettings ().setLoadWithOverviewMode (true);
        view.getSettings ().setUseWideViewPort (true);
        view.loadUrl ("https://akimbomaster.parseapp.com/");
        view.setWebViewClient (new WebViewClient () {
            @Override
            public boolean shouldOverrideUrlLoading(WebView v, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView v, String url) {
                v.loadUrl ("javascript:" +
                                   "var y = document.getElementsByName('amount')[0].value='" + -1 + "';" +
                                   "var x = document.getElementsByName('orderid')[0].value='" + orderId + "';");

            }
        });
        setContentView (view);
    }

    class MyWebView extends WebView {
        Context context;

        public MyWebView(Context context) {
            super (context);
            this.context = context;
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
}
