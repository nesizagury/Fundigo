package com.example.events;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class Web1 extends AppCompatActivity {
    private WebView myWebView;
    private int amount;
    private int idorder;
    private String myPayHtml="<html><head><title>My ParseApp site</title><style>body { font-family: Helvetica, Arial, sans-serif; } div { width: 800px; height: 400px; margin: 40px auto; padding: 20px; border: 2px solid #5298fc; }h1 { font-size: 30px; margin: 0; }p { margin: 40px 0; em { font-family: monospace; }a { color: #5298fc; text-decoration: none; } </style><meta name=\"viewport\" content=\"width=device-width, user-scalable=no\" /></head><body><form name=\"pelepayform\" action=\"https://www.pelepay.co.il/pay/paypage.aspx\" method=\"post\"><input type=\"hidden\" value=\"nesizagury@gmail.com\" name=\"business\"><INPUT TYPE=\"hidden\" value=\"\" NAME=\"amount\"><INPUT TYPE=\"hidden\" value=\"\" NAME=\"orderid\"><INPUT TYPE=\"hidden\" value=\"_product\" NAME=\"description\"><input type=\"image\" src=\"http://www.pelepay.co.il/btn_images/pay_button_3.gif\" name=\"submit\" alt=\"Make payments with pelepay\"></form><script language=\"javascript\">document.pelepayform.amount.value= \""+amount+"\";document.pelepayform.orderid.value= \""+idorder+"\";</script></body></html>";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web1);
        myWebView=(WebView)findViewById(R.id.webView2);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        amount=122;
        idorder=111;


        Intent i = new Intent();

// MUST instantiate android browser, otherwise it won't work
        i.setComponent(new ComponentName("com.android.browser", "com.android.browser.BrowserActivity"));
        i.setAction(Intent.ACTION_VIEW);
       // String dataUri = "data:text/html," + URLEncoder.encode(myPayHtml).replaceAll("\\+","%20");
        i.setData(Uri.parse(myPayHtml));

        startActivity(i);

    }

}


/*
package com.example.events;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URL;

public class Web1 extends AppCompatActivity {
    private WebView myWebView;
    private String myPayHtml="<html><head><title>My ParseApp site</title><style>body { font-family: Helvetica, Arial, sans-serif; } div { width: 800px; height: 400px; margin: 40px auto; padding: 20px; border: 2px solid #5298fc; }h1 { font-size: 30px; margin: 0; }p { margin: 40px 0; em { font-family: monospace; }a { color: #5298fc; text-decoration: none; } </style></head><body><form name=\"pelepayform\" action=\"https://www.pelepay.co.il/pay/paypage.aspx\" method=\"post\"><input type=\"hidden\" value=\"nesizagury@gmail.com\" name=\"business\"><INPUT TYPE=\"hidden\" value=\"\" NAME=\"amount\"><INPUT TYPE=\"hidden\" value=\"\" NAME=\"orderid\"><INPUT TYPE=\"hidden\" value=\"_product\" NAME=\"description\"><input type=\"image\" src=\"http://www.pelepay.co.il/btn_images/pay_button_3.gif\" name=\"submit\" alt=\"Make payments with pelepay\"></form><script language=\"javascript\">document.pelepayform.amount.value= \"20\";document.pelepayform.orderid.value= \"20\";</script></body></html>";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web1);
        myWebView=(WebView)findViewById(R.id.webView2);
        WebSettings webSettings = myWebView.getSettings();

        webSettings.setJavaScriptEnabled(true);


        MyWebView view = new MyWebView(this);

        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setDomStorageEnabled(true);
        view.loadUrl("http://fundigo.parseapp.com");
        view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView v, String url) {
                //v.loadUrl(url);
                Log.d("me1234", url);

                try {
                    URL urlObj = new URL(url);
                    if (TextUtils.equals(urlObj.getHost(), "http://fundigo.parseapp.com")) {
                        //Allow the WebView in your application to do its thing
                        return false;

                    } else {
                        //Pass it to the system, doesn't match your domain
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        Log.d("me1234", Uri.parse(url).toString());
                        startActivity(intent);
                        //Tell the WebView you took care of it.
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView v, String url) {
                v.loadUrl("javascript:" +
                        "var y = document.getElementsByName('amount')[0].value='" + 2000 + "';" +
                        "var x = document.getElementsByName('orderid')[0].value='" + 2 + "';");


            }

        });
        setContentView(view);
    }

    class MyWebView extends WebView {
        Context context;
        public MyWebView(Context context) {
            super(context);
            this.context = context;


        }

    }


}
 */