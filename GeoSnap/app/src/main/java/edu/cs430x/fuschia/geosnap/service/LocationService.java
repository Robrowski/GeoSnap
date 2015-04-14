package edu.cs430x.fuschia.geosnap.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Matt on 4/13/2015.
 */
public class LocationService extends Service implements LocationListener{

    static final int LOCATION_UPDATE_INTERVAL_MILLIS = 500;
    static final int LOCATION_UPDATE_INTERVAL_METERS = 1;
    static final String BROADCAST_ACTION = "edu.cs430x.fuschia.geosnap.LOCATION_UPDATE";
    public static double location_latitude;
    public static double location_longitude;

    LocationManager mLocationManager;


    public LocationService() {
        super();
    }

    public void onCreate() {
        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);

        String provider = mLocationManager.getBestProvider(criteria, true);
        mLocationManager.requestLocationUpdates(provider, LOCATION_UPDATE_INTERVAL_MILLIS, LOCATION_UPDATE_INTERVAL_METERS, this);
        Log.v("service","service created");

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return START_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {
        // Send the updated location out as a broadcast so we can save the updated location
        // for use when taking a new photo and sending to our server.
        Intent newLocation = new Intent();
        newLocation.setAction(BROADCAST_ACTION);
        newLocation.putExtra("latitude",location.getLatitude());
        newLocation.putExtra("longitude", location.getLongitude());
        sendBroadcast(newLocation);

        location_latitude = location.getLatitude();
        location_longitude = location.getLongitude();

        //TODO: this is where we will also ping our server to check for new snaps

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
