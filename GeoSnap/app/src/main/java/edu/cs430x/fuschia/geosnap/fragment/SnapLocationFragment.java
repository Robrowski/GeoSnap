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

import edu.cs430x.fuschia.geosnap.data.ImageParcelable;
import edu.cs430x.fuschia.geosnap.service.receivers.LocationReceiver;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SnapLocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SnapLocationFragment extends SupportMapFragment implements OnMapReadyCallback {

    private static final String TAG = "SnapLocationFragment";

    public static final String INTENT_IMG_URL = "IMAGE_URL";
    private ImageParcelable mImage;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SnapLocationFragment.
     */
    public static SnapLocationFragment newInstance(ImageParcelable image) {
        SnapLocationFragment fragment = new SnapLocationFragment();
        Bundle args = new Bundle();
        args.putParcelable(INTENT_IMG_URL,image);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImage = getArguments().getParcelable(INTENT_IMG_URL);
        }
        getMapAsync(this); // ons onMapReady when the map is ready :D
        Log.i(TAG, "Waiting for map to be ready");
    }


    @Override
    public void onMapReady(GoogleMap mMap) {
        Log.i(TAG, "Map is ready! Adding markers now.");
        LatLng snap_ll = new LatLng(mImage.getLat(), mImage.getLon());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(snap_ll, 16));
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

        mMap.addCircle(new CircleOptions()
                .center(snap_ll)
                .fillColor(Color.argb(120,200, 0, 100))
                .radius(mImage.getDiscoverabilityRadius())); // Measured in meters
    }
}

