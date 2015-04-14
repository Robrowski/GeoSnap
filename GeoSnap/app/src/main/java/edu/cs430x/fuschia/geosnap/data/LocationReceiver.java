package edu.cs430x.fuschia.geosnap.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class LocationReceiver extends BroadcastReceiver {
    public static double location_latitude;
    public static double location_longitude;

    public LocationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        location_latitude = intent.getDoubleExtra("latitude",0);
        location_longitude = intent.getDoubleExtra("longitude",0);
        Toast t = Toast.makeText(context,"lat: " + location_latitude + " lon: " + location_longitude,Toast.LENGTH_SHORT);
        t.show();

        Log.v("Receiver","lat: " + location_latitude + " lon: " + location_longitude);
    }
//    <!--<receiver-->
//    <!--android:name=".data.LocationReceiver"-->
//    <!--android:enabled="true"-->
//    <!--android:exported="true" >-->
//    <!--<intent-filter>-->
//    <!--<action android:name="edu.cs430x.fuschia.geosnap.LOCATION_UPDATE" />-->
//    <!--</intent-filter>-->
//    <!--</receiver>-->
}
