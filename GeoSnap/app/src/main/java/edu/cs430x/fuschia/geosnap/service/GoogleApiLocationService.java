package edu.cs430x.fuschia.geosnap.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import edu.cs430x.fuschia.geosnap.data.LocationReceiver;

public class GoogleApiLocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "GoogleLocationService";
    private static final int REQUEST_CODE = 8934230;
    static final String BROADCAST_NEW_LOCATION = "edu.cs430x.fuschia.geosnap.LOCATION_UPDATE";

    private PendingIntent locationPendingIntent;
    private GoogleApiClient mGoogleLocationClient;

    private static final int SECONDS = 60, MILLISECONDS = 1000;

    public GoogleApiLocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("GoogleApiLocationService not for binding");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        // Called EVERY time the service is "started"
        super.onStartCommand(intent, flags, startId);
        buildGoogleApiClient();
        if(!mGoogleLocationClient.isConnected() || !mGoogleLocationClient.isConnecting())
            mGoogleLocationClient.connect();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        Log.i(TAG, "Google API location service started");
        return START_STICKY;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Google location services connected. Requesting location updates");
        requestLocationUpdates();
    }

    /** Make all the intents and final request to request location updates */
    private void requestLocationUpdates(){
        Intent newLocation = new Intent(this, LocationReceiver.class).setAction(BROADCAST_NEW_LOCATION);
        locationPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), REQUEST_CODE, newLocation, PendingIntent.FLAG_CANCEL_CURRENT);

        // Remove previous request for location updates to be safe
        LocationServices.FusedLocationApi.removeLocationUpdates( mGoogleLocationClient, locationPendingIntent);

        // Make the request
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleLocationClient, buildLocRequest(), locationPendingIntent);
    }

    /* Build the request based on settings in the shared preference manager */
    private LocationRequest buildLocRequest(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        return LocationRequest.create()
                .setInterval(sharedPref.getInt("Interval", 30)*SECONDS*MILLISECONDS)      // 2 minutes
                .setFastestInterval(sharedPref.getInt("Fastest Interval", 60) * MILLISECONDS) // 30 seconds
                .setSmallestDisplacement(sharedPref.getFloat("Smallest Displacement", 0))
                .setPriority(getPreferredPriority(sharedPref.getInt("Request Priority", 0)));
    }

    /** Translate a given preference value to a LocationRequest flag.
     * See arrays.xml for the two arrays*/
    private int getPreferredPriority(int val){
        switch (val){
            default:
            case 0:
                return LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
            case 1:
                return LocationRequest.PRIORITY_HIGH_ACCURACY;
            case 2:
                return LocationRequest.PRIORITY_LOW_POWER;
            case 3:
                return LocationRequest.PRIORITY_NO_POWER;
        }
    }


    @Override
    public void onDestroy() {
        LocationServices.FusedLocationApi.removeLocationUpdates( mGoogleLocationClient, locationPendingIntent);
        mGoogleLocationClient.disconnect();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        Log.w(TAG, "Google location service destroyed");
        super.onDestroy();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "Google location client API connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Google location client API connection failed");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key){
            // If any of these settings change, restart the location service with new settings
            case "pref_interval":
            case "pref_fastest_interval":
            case "pref_smallest_displacement":
            case "pref_request_priority":
                Log.i(TAG, "Location polling preferences changed");
                mGoogleLocationClient.reconnect();
                return;
            default:
                Log.i(TAG, "This preference changed: " + key);
                return;
        }
    }
}
