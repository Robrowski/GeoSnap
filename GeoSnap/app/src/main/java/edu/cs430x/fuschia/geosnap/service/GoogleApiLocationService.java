package edu.cs430x.fuschia.geosnap.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import edu.cs430x.fuschia.geosnap.data.LocationReceiver;

public class GoogleApiLocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "GoogleLocationService";
    private static final int REQUEST_CODE = 8934230;
    static final String BROADCAST_NEW_LOCATION = "edu.cs430x.fuschia.geosnap.LOCATION_UPDATE";

    private PendingIntent locationPendingIntent;
    private GoogleApiClient mGoogleLocationClient;

    // TODO set the values via preferences
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(2*60*1000)      // 2 minutes
            .setFastestInterval(30 * 1000) // 30 seconds
            .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

    public GoogleApiLocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        // Called EVERY time the service is "started"
        super.onStartCommand(intent, flags, startId);
        buildGoogleApiClient();
        if(!mGoogleLocationClient.isConnected() || !mGoogleLocationClient.isConnecting())
            mGoogleLocationClient.connect();

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

        // Remove previous request for location updates to be safe // TODO test this
//        LocationServices.FusedLocationApi.removeLocationUpdates( mGoogleLocationClient, locationPendingIntent);

        // Make the request to get location updates
        Intent newLocation = new Intent(this, LocationReceiver.class).setAction(BROADCAST_NEW_LOCATION);
        locationPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), REQUEST_CODE, newLocation, PendingIntent.FLAG_CANCEL_CURRENT);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleLocationClient, REQUEST, locationPendingIntent);
    }

    @Override
    public void onDestroy() {
        LocationServices.FusedLocationApi.removeLocationUpdates( mGoogleLocationClient, locationPendingIntent);
        mGoogleLocationClient.disconnect();
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
}
