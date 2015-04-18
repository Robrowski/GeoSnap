package edu.cs430x.fuschia.geosnap.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.cs430x.fuschia.geosnap.service.receivers.LocationReceiver;

/**
 * // TODO fragments can't have fragments in their layouts?!?! Very strange API thing...
 *
 *
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SnapLocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SnapLocationFragment extends SupportMapFragment implements OnMapReadyCallback {

    private static final String TAG = "SnapLocationFragment",
            ARG_LATITUDE = "arg_latitude",
            ARG_LONGITUDE = "arg_longitude";

    private double latitude = 0, longitude = 0;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SnapLocationFragment.
     */
    public static SnapLocationFragment newInstance(double lat, double lon) {
        // TODO how about making this take a reference to the DB on the phone...
        // can get discoverability, lat lon, building...

        SnapLocationFragment fragment = new SnapLocationFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_LATITUDE, lat);
        args.putDouble(ARG_LONGITUDE, lon);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            latitude = getArguments().getDouble(ARG_LATITUDE);
            longitude = getArguments().getDouble(ARG_LONGITUDE);
        }
        getMapAsync(this); // ons onMapReady when the map is ready :D
        Log.i(TAG, "Waiting for map to be ready");
    }


    @Override
    public void onMapReady(GoogleMap mMap) {
        Log.i(TAG, "Map is ready! Adding markers now.");
        LatLng snap_ll = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(snap_ll, 15));
        mMap.setMyLocationEnabled(false);

        /* place markers
        * https://developers.google.com/maps/documentation/android/marker        */
        mMap.addMarker(new MarkerOptions()
                .position(snap_ll)
                .draggable(false)
                .title("THE SNAP")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        // Place marker of self
        mMap.addMarker(new MarkerOptions()
                .position(LocationReceiver.getLatLng())
                .draggable(false)
                .title("You")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        // TODO add a better shape that shows discoverability radius
        mMap.addCircle(new CircleOptions()
                .center(snap_ll)
                .fillColor(Color.argb(120,200, 0, 100))
                .radius(50)); // Measured in meters
    }
}

