package edu.cs430x.fuschia.geosnap.service.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import edu.cs430x.fuschia.geosnap.network.geocloud.QueryPhotos;
import edu.cs430x.fuschia.geosnap.service.GoogleApiLocationService;

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

        Intent newIntent = new Intent(context, QueryPhotos.class);
        newIntent.putExtra("com.google.android.location.LOCATION",location);
        context.startService(newIntent);
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


    public static void forceLocationUpdate(){
        GoogleApiLocationService.updateCurrentLocation();
    }
}
