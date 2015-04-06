package com.starboardland.pedometer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class CounterActivity extends Activity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int TOTAL = 9, TOTAL_SO_FAR = 10;
    private static final boolean DEBUG = false;
    private static final String TAG = "StepCounterDebugTag";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    GoogleApiClient mGoogleApiClient;
    private SensorManager sensorManager;
    private TextView count;
    boolean activityRunning;
    private Location mLastLocation;
    private CountDownTimer cdt;

    private SegmentDatabaseHelper sdh;

    private int current_segment = 0, SECONDS_PER_SEGMENT = 60;
    private Sensor countSensor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        sdh = new SegmentDatabaseHelper(this);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        setUpMapIfNeeded();

        buildGoogleApiClient();

        if (DEBUG) {
            SECONDS_PER_SEGMENT = 10;
        }
    }

    /**
     * There is a view for each segment, numbered 1 to 8, with TOTAL = 9
     *
     * @param i The index of the view
     * @return TextView for the given segment or null if it didn't exist
     */
    private TextView getCountView(int i) {
        switch (i) {
            case 1:
                return (TextView) findViewById(R.id.seg1_count);
            case 2:
                findViewById(R.id.seg2_txt).setVisibility(TextView.VISIBLE);
                return (TextView) findViewById(R.id.seg2_count);
            case 3:
                findViewById(R.id.seg3_txt).setVisibility(TextView.VISIBLE);
                return (TextView) findViewById(R.id.seg3_count);
            case 4:
                findViewById(R.id.seg4_txt).setVisibility(TextView.VISIBLE);
                return (TextView) findViewById(R.id.seg4_count);
            case 5:
                findViewById(R.id.seg5_txt).setVisibility(TextView.VISIBLE);
                return (TextView) findViewById(R.id.seg5_count);
            case 6:
                findViewById(R.id.seg6_txt).setVisibility(TextView.VISIBLE);
                return (TextView) findViewById(R.id.seg6_count);
            case 7:
                findViewById(R.id.seg7_txt).setVisibility(TextView.VISIBLE);
                return (TextView) findViewById(R.id.seg7_count);
            case 8:
                findViewById(R.id.seg8_txt).setVisibility(TextView.VISIBLE);
                return (TextView) findViewById(R.id.seg8_count);
            case TOTAL:
                return (TextView) findViewById(R.id.total_counts);
            default:
                Log.e(TAG, "Invalid segment count view requested");
                return null;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        activityRunning = true;
        countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
        }
        setUpMapIfNeeded();
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityRunning = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        current_segment = 1;
        sdh.insertRun(TOTAL_SO_FAR, 0);
        sdh.init();

        // Set up the 8 minute count down timer
        cdt = new CountDownTimer(8 * SECONDS_PER_SEGMENT * 1000, 1000) {
            private int ticks = 0;

            // Tick once every second
            public void onTick(long millisUntilFinished) {
                if (DEBUG) {
                    ((TextView) findViewById(R.id.count_down)).setText("Seconds remaining: " + (millisUntilFinished / 1000) % SECONDS_PER_SEGMENT);
                }
                ticks = (ticks + 1) % SECONDS_PER_SEGMENT;

                // After 60 ticks, increment the segment
                if (ticks == 0) {
                    // Set the previous line to black
                    TextView current = getCountView(current_segment);
                    current.setTextColor(Color.WHITE);
                    sdh.insertRun(TOTAL_SO_FAR, sdh.queryRun(TOTAL));

                    toast_segment(current_segment, sdh.queryRun(current_segment));

                    // Set the next segment index
                    current_segment++;

                    // Set the next line to a different color
                    current = getCountView(current_segment);
                    current.setText("0");
                    if (DEBUG)
                        current.setTextColor(Color.MAGENTA);
                }
            }

            public void onFinish() {
                activityRunning = false;

                // Correct the color of the last segment
                getCountView(8).setTextColor(Color.WHITE);

                int total = 0;
                for (int i = 1; i <= 8; i++) {
                    total += sdh.queryRun(i);
                    Log.i(TAG, "Summing up:" + String.valueOf(total));
                }
                ((TextView) findViewById(R.id.total_counts)).setText("Total Steps: " + String.valueOf(total));
                if (DEBUG) {
                    ((TextView) findViewById(R.id.count_down)).setText("Done");
                }
            }

            public void start(int segment) {
                this.start();
            }

        }.start();
    }

    private void toast_segment(int current_segment, int steps) {
        Toast.makeText(this, "You took " + String.valueOf(steps) + " steps in segment " + String.valueOf(current_segment), Toast.LENGTH_LONG).show();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (activityRunning) {
            // Max of zero and comparison to last SQL db, in case device was restarted
            if (sdh.queryRun(TOTAL_SO_FAR) == 0) {
                sdh.insertRun(current_segment, 0);
                sdh.insertRun(TOTAL_SO_FAR, (int) event.values[0]);
            } else {
                sdh.insertRun(current_segment, Math.max(0, ((int) event.values[0]) - sdh.queryRun(TOTAL_SO_FAR)));
            }
            sdh.insertRun(TOTAL, (int) event.values[0]);

            // Update the text view
            getCountView(current_segment).setText(String.valueOf(sdh.queryRun(current_segment)));
        }
    }

    @Override
    protected void onStop() {
        sensorManager.unregisterListener(this);
        mGoogleApiClient.disconnect();
        cdt.cancel();
        super.onStop();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            LatLng ll = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
