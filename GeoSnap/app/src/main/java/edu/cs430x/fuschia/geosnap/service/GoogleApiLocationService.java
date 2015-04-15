package edu.cs430x.fuschia.geosnap.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import edu.cs430x.fuschia.geosnap.service.receivers.ActivityReceiver;
import edu.cs430x.fuschia.geosnap.service.receivers.LocationReceiver;

public class GoogleApiLocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "GoogleLocationService";
    private static final int REQUEST_CODE = 8934230;
    static final String BROADCAST_NEW_LOCATION = "edu.cs430x.fuschia.geosnap.LOCATION_UPDATE",
            BROADCAST_NEW_ACTIVITY = "edu.cs430x.fuschia.geosnap.ACTIVITY_UPDATE";
    private static final String PREF_INTERVAL = "pref_interval",
            PREF_FASTEST_INTERVAL = "pref_fastest_interval",
            PREF_SMALLEST_DISPLACEMENT = "pref_smallest_displacement",
            PREF_REQUEST_PRIORITY = "pref_request_priority",
            PREF_ALLOW_ACTIVITY_RECOGNITION = "pref_allow_activity_recognition",
            PREF_ACTIVITY_INTERVAL = "pref_activity_interval";

    private PendingIntent locationPendingIntent, activityPendingIntent;
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
        buildPendingIntents();
        if(!mGoogleLocationClient.isConnected() || !mGoogleLocationClient.isConnecting())
            mGoogleLocationClient.connect();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        Log.i(TAG, "Google API location service started");
        return START_STICKY;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.w(TAG, "Google API services connected. Requesting activity and location updates");
        Location l = LocationServices.FusedLocationApi.getLastLocation(mGoogleLocationClient);
        LocationReceiver.setLastLocation(l);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // Case 1: activity disabled in pref
        if (!sharedPref.getBoolean(PREF_ALLOW_ACTIVITY_RECOGNITION, true)){
            Log.d(TAG, "Enabling location, disabling activity updates");
            requestLocationUpdates(); // default
            cancelActivityUpdates();
            return;
        }

        // Else enable activity to control state of location services
        requestActivityUpdates();

        // Case 2&3: activity receiver disable location - request location if moving
        if (sharedPref.getBoolean(ActivityReceiver.PREF_ACTIVITY_MOVING, true)) {
            Log.d(TAG, "Enabling location, enabling activity updates");
            requestLocationUpdates();
        } else {
            Log.d(TAG, "Disabling location, enabling activity updates");
            cancelLocationUpdates();
        }
    }

    @Override
    public void onDestroy() {
        cancelActivityUpdates();
        cancelLocationUpdates();
        mGoogleLocationClient.disconnect();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        Log.w(TAG, "Google location service destroyed");
        super.onDestroy();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .build();
    }

    private void buildPendingIntents(){
        Intent newActivity = new Intent(this, ActivityReceiver.class).setAction(BROADCAST_NEW_ACTIVITY);
        activityPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), REQUEST_CODE, newActivity, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent newLocation = new Intent(this, LocationReceiver.class).setAction(BROADCAST_NEW_LOCATION);
        locationPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), REQUEST_CODE, newLocation, PendingIntent.FLAG_CANCEL_CURRENT);
    }


    private void requestActivityUpdates() {
        cancelActivityUpdates();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        long ACTIVITY_DETECTION_MILLIS = Integer.parseInt(sharedPref.getString(PREF_ACTIVITY_INTERVAL, "10")) * MILLISECONDS;
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleLocationClient, ACTIVITY_DETECTION_MILLIS, activityPendingIntent);
    }

    /** request to request location updates */
    private void requestLocationUpdates(){
        // Remove previous request for location updates to be safe
        cancelLocationUpdates();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleLocationClient, buildLocRequest(), locationPendingIntent);
    }

    private void cancelActivityUpdates(){
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mGoogleLocationClient, activityPendingIntent);
    }

    private void cancelLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleLocationClient, locationPendingIntent);
    }

    /* Build the request based on settings in the shared preference manager */
    private LocationRequest buildLocRequest(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        return LocationRequest.create()
                .setInterval(Integer.parseInt(sharedPref.getString(PREF_INTERVAL, "31")) * SECONDS * MILLISECONDS)      // 2 minutes
                .setFastestInterval(Integer.parseInt(sharedPref.getString(PREF_FASTEST_INTERVAL, "61")) * MILLISECONDS) // 30 seconds
                .setSmallestDisplacement(Float.parseFloat(sharedPref.getString(PREF_SMALLEST_DISPLACEMENT, "0.1")))
                .setPriority(getPreferredPriority(Integer.parseInt(sharedPref.getString(PREF_REQUEST_PRIORITY, "0"))));
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
            // TODO is it optimal to simply restart any/all API services when settings change?
            // If any of these settings change, RESTART the location and activity service with new settings
            case PREF_INTERVAL:
            case PREF_FASTEST_INTERVAL:
            case PREF_SMALLEST_DISPLACEMENT:
            case PREF_REQUEST_PRIORITY:
            case PREF_ACTIVITY_INTERVAL:
                Log.i(TAG, "Location polling preferences changed");
                mGoogleLocationClient.reconnect();
                return;
            case ActivityReceiver.PREF_ACTIVITY_MOVING:
            case PREF_ALLOW_ACTIVITY_RECOGNITION:
                Log.i(TAG, "Updating location services based on movement");
                mGoogleLocationClient.reconnect();
                return;
            default:
                Log.i(TAG, "This preference changed: " + key);
                return;
        }
    }
}
