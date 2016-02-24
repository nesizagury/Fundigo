package com.example.FundigoApp.Tickets;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebBrowserActivity extends AppCompatActivity {
    private String amount;
    static int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        MyWebView view = new MyWebView (this);

        Intent i = getIntent ();
        amount = i.getStringExtra ("eventPrice");
        amount = amount.substring (0, amount.length () - 1);

        orderId++;

        view.getSettings ().setJavaScriptEnabled (true);
        view.getSettings ().setDomStorageEnabled (true);
        view.getSettings ().setLoadWithOverviewMode (true);
        view.getSettings ().setUseWideViewPort (true);
        view.loadUrl ("https://fundigo.parseapp.com/");
        view.setWebViewClient (new WebViewClient () {
            @Override
            public boolean shouldOverrideUrlLoading(WebView v, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView v, String url) {
                v.loadUrl ("javascript:" +
                                   "var y = document.getElementsByName('amount')[0].value='" + amount + "';" +
                                   "var x = document.getElementsByName('orderid')[0].value='" + orderId + "';");

                String url1 = "https://www.pelepay.co.il/pay/defaults/success.aspx";
                if (url.length () > 46) {
                    if (url.substring (0, 46).equals (url1)) {
                        Intent intentQR = new Intent (WebBrowserActivity.this, GetTicketQRCodeActivity.class);
                        Bundle b = new Bundle ();
                        Intent intentHere = getIntent ();
                        intentQR.putExtra ("eventName", intentHere.getStringExtra ("eventName"));
                        intentQR.putExtra ("eventPrice", intentHere.getStringExtra ("eventPrice"));
                        intentQR.putExtra ("phone", intentHere.getStringExtra ("phone"));
                        intentQR.putExtras (b);
                    } else if (url.startsWith ("https://www.pelepay.co.il/pay/defaults/fail.aspx")) {
                        try {
                            wait (3000);
                            finish ();
                        } catch (InterruptedException e) {
                            e.printStackTrace ();
                        }
                    }
                    if (url.startsWith ("https://www.pelepay.co.il/pay/defaults/cancel.aspx")) {
                        finish ();
                    }
                }
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
