package edu.cs430x.fuschia.geosnap.service.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.Collection;

import edu.cs430x.fuschia.geocommons.location.LatLngHelper;

public class LocationReceiver extends BroadcastReceiver {
    public static final String TAG = "LocationReceiver";

    public static double location_latitude = -1, location_longitude = -1;
    public static Location location;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Got a location update");
        location = (Location) intent.getExtras().get("com.google.android.location.LOCATION");

        location_latitude = location.getLatitude();
        location_longitude = location.getLongitude();

        Toast t = Toast.makeText(context,"lat: " + location_latitude + " lon: " + location_longitude,Toast.LENGTH_SHORT);
        t.show();
        Log.w(TAG,"lat: " + location_latitude + " lon: " + location_longitude);


        // TODO ping geocloud for discovery?
        Collection<String> c = LatLngHelper.getBuckets(location);
        for (String s: c){
            // Send a query to the geo cloud
            // Primary query is in the S = bucket name
            //      All snaps within get checked against distance from the actual location
        }

    }

    /** only to be used when manually getting the last known location */
    public static void setLastLocation(Location l){
        if (l != null){
            location = l;
            location_latitude = l.getLatitude();
            location_longitude = l.getLongitude();
        }
    }

    public static LatLng getLatLng() {
        return new LatLng(
                LocationReceiver.location_latitude,
                LocationReceiver.location_longitude);
    }
}
