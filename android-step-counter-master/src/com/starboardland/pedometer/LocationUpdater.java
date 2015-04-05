package com.starboardland.pedometer;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Matt on 4/5/2015.
 */
public class LocationUpdater implements LocationListener {
    GoogleMap mMap;
    public LocationUpdater(GoogleMap map){
        mMap = map;
    }
    @Override
    public void onLocationChanged(Location location) {
        // Showing the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(
                new LatLng(location.getLatitude(), location.getLongitude())
        ));

        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
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
