package com.dabrowski.robert.screen3;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import 	android.webkit.WebView;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView myWebView = (WebView) findViewById(R.id.webView);
        myWebView.loadUrl("https://www.google.com");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
