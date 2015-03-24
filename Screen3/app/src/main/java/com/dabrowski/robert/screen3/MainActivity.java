package com.dabrowski.robert.screen3;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        website1(null);
    }

    public void website1(View button){
        WebView myWebView = (WebView) findViewById(R.id.webView);
        myWebView.loadUrl("https://www.google.com");
    }

    public void website2(View button){
        WebView myWebView = (WebView) findViewById(R.id.webView);
        myWebView.loadUrl("http://developer.android.com/guide/webapps/webview.html");
    }

    public void website3(View button){
        WebView myWebView = (WebView) findViewById(R.id.webView);
        myWebView.loadUrl("http://web.cs.wpi.edu/~emmanuel/");
    }

}
