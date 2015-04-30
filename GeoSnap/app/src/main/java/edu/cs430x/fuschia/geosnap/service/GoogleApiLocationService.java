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

import edu.cs430x.fuschia.geosnap.GeoConstants;
import edu.cs430x.fuschia.geosnap.service.receivers.ActivityReceiver;
import edu.cs430x.fuschia.geosnap.service.receivers.LocationReceiver;

public class GoogleApiLocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "GoogleLocationService";
    private static final int REQUEST_CODE = 8934230;

    private SharedPreferences sharedPref;
    private PendingIntent locationPendingIntent, activityPendingIntent;
    private static GoogleApiClient mGoogleLocationClient;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("GoogleApiLocationService not for binding!");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        // Called EVERY time the service is "started"
        super.onStartCommand(intent, flags, startId);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        buildGoogleApiClient();
        buildPendingIntents();

        if(!mGoogleLocationClient.isConnected() || !mGoogleLocationClient.isConnecting())
            mGoogleLocationClient.connect();
        sharedPref.unregisterOnSharedPreferenceChangeListener(this);
        sharedPref.registerOnSharedPreferenceChangeListener(this);
        Log.i(TAG, "Google API location service started");
        return START_STICKY;
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.w(TAG, "Google API services connected. Requesting activity and location updates");
        updateCurrentLocation();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // Case 1: activity disabled in pref
        if (!sharedPref.getBoolean(GeoConstants.GeoPrefs.PREF_ALLOW_ACTIVITY_RECOGNITION, true)){
            Log.d(TAG, "Enabling location, disabling activity updates");
            requestLocationUpdates(); // default
            cancelActivityUpdates();
            return;
        }

        // Else enable activity to control state of location services
        requestActivityUpdates();

        // Case 2&3: activity receiver disable location - request location if moving
        if (sharedPref.getBoolean(GeoConstants.GeoPrefs.PREF_ACTIVITY_MOVING, true)) {
            Log.d(TAG, "Enabling location, enabling activity updates");
            requestLocationUpdates();
        } else {
            Log.d(TAG, "Disabling location, enabling activity updates");
            cancelLocationUpdates();
        }
    }

    /** Requests the last location */
    public static void updateCurrentLocation() {
        Location l = LocationServices.FusedLocationApi.getLastLocation(mGoogleLocationClient);
        LocationReceiver.setLastLocation(l);
    }

    @Override
    public void onDestroy() {
        if(mGoogleLocationClient.isConnected()) { // Can't cancel if not connected
            cancelActivityUpdates();
            cancelLocationUpdates();
        }
        mGoogleLocationClient.disconnect();
        sharedPref.unregisterOnSharedPreferenceChangeListener(this);
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
        Intent newActivity = new Intent(this, ActivityReceiver.class)
                .setAction(GeoConstants.GeoReceivers.BROADCAST_NEW_ACTIVITY);
        activityPendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                REQUEST_CODE,
                newActivity,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Intent newLocation = new Intent(this, LocationReceiver.class)
                .setAction(GeoConstants.GeoReceivers.BROADCAST_NEW_LOCATION);
        locationPendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                REQUEST_CODE,
                newLocation,
                PendingIntent.FLAG_CANCEL_CURRENT);
    }


    private void requestActivityUpdates() {
        cancelActivityUpdates();

        long ACTIVITY_DETECTION_MILLIS = Integer.parseInt(
                sharedPref.getString(
                        GeoConstants.GeoPrefs.PREF_ACTIVITY_INTERVAL,
                        GeoConstants.LocationDefaults.ACTIVITY_INTERVAL))
                * GeoConstants.MILLISECONDS;
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleLocationClient,
                ACTIVITY_DETECTION_MILLIS,
                activityPendingIntent);
    }

    /** request to request location updates */
    private void requestLocationUpdates(){
        // Remove previous request for location updates to be safe
        cancelLocationUpdates();
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleLocationClient,
                buildLocRequest(),
                locationPendingIntent);


        LocationRequest lr = buildLocRequest();
        Log.w(TAG, lr.toString());
    }

    private void cancelActivityUpdates(){
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                mGoogleLocationClient,
                activityPendingIntent);
    }

    private void cancelLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleLocationClient,
                locationPendingIntent);
    }

    /* Build the request based on settings in the shared preference manager */
    private LocationRequest buildLocRequest(){

        return LocationRequest.create()
                .setInterval( Integer.parseInt(
                        sharedPref.getString(GeoConstants.GeoPrefs.PREF_INTERVAL,
                                GeoConstants.LocationDefaults.LOCATION_INTERVAL))
                        * GeoConstants.MILLISECONDS )
                .setFastestInterval(Integer.parseInt(
                        sharedPref.getString(
                                GeoConstants.GeoPrefs.PREF_FASTEST_INTERVAL,
                                GeoConstants.LocationDefaults.FASTEST_INTERVAL))
                        * GeoConstants.MILLISECONDS)
                .setSmallestDisplacement(Float.parseFloat(
                        sharedPref.getString(
                                GeoConstants.GeoPrefs.PREF_SMALLEST_DISPLACEMENT,
                                GeoConstants.LocationDefaults.SMALLEST_DISPLACEMENT)))
                .setPriority(getPreferredPriority(Integer.parseInt(
                        sharedPref.getString(
                                GeoConstants.GeoPrefs.PREF_REQUEST_PRIORITY,
                                GeoConstants.LocationDefaults.REQUEST_PRIORITY))));
    }

    /** Translate a given preference value to a LocationRequest flag.
     * See arrays.xml for the two arrays*/
    private static int getPreferredPriority(int val){
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
        Log.e(TAG, connectionResult.describeContents() + " " + connectionResult.getErrorCode());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key){
            case GeoConstants.GeoPrefs.PREF_INTERVAL:
            case GeoConstants.GeoPrefs.PREF_FASTEST_INTERVAL:
            case GeoConstants.GeoPrefs.PREF_SMALLEST_DISPLACEMENT:
            case GeoConstants.GeoPrefs.PREF_REQUEST_PRIORITY:
            case GeoConstants.GeoPrefs.PREF_ACTIVITY_INTERVAL:
                Log.i(TAG, "Location polling preferences changed");
                mGoogleLocationClient.reconnect();
                return;
            case GeoConstants.GeoPrefs.PREF_ACTIVITY_MOVING:
            case GeoConstants.GeoPrefs.PREF_ALLOW_ACTIVITY_RECOGNITION:
                Log.i(TAG, "Updating location services based on movement");
                mGoogleLocationClient.reconnect();
                return;

            default:
                Log.i(TAG, "This preference changed: " + key);
                return;
        }
    }
}
