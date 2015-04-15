package edu.cs430x.fuschia.geosnap.service.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

public class LocationReceiver extends BroadcastReceiver {
    public static final String TAG = "LocationReceiver";

    public static double location_latitude = -1;
    public static double location_longitude = -1;

    public LocationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Got a location update");
        Location location = (Location) intent.getExtras().get("com.google.android.location.LOCATION");

        location_latitude = location.getLatitude();
        location_longitude = location.getLongitude();

        Toast t = Toast.makeText(context,"lat: " + location_latitude + " lon: " + location_longitude,Toast.LENGTH_SHORT);
        t.show();
        Log.w(TAG,"lat: " + location_latitude + " lon: " + location_longitude);


        // TODO ping geocloud for discovery?
    }

    public static void setLastLocation(Location l){
        if (l != null){
            location_latitude = l.getLatitude();
            location_longitude = l.getLongitude();
        }
    }

}
