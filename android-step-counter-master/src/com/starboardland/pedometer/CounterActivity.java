package com.starboardland.pedometer;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.starboardland.pedometer.StepContract.StepEntry;

import java.util.Timer;
import java.util.TimerTask;

public class CounterActivity extends Activity implements SensorEventListener{

    // App state
    boolean activityRunning;
    boolean startLogging = false;
    private int stepsSinceStartUp;

    // Gui state
    private int segment = 0;
    private View currentSegmentView;
    private TextView currentSegmentValueTextView;

    // Timer Related
    private Timer timer = new Timer();
    private TimerTask timerTask;
    public static Handler mHandler;

    // Database Helper
    private StepDbHelper mDbHelper;

    // Location and Pedometer
    private SensorManager sensorManager;
    private MapFragment mMapFragment;
    private GoogleMap googleMap;

    // Constants
    private final int MAKEVISIBLE = 1;
    private final int NUMSEGMENTS = 8;
    private final int SEGMENTDURATION = 60000; // in ms
    private final int TIMERDELAY = 100; // in ms

    private int[] SegmentViewIds = {
            R.id.segment1,
            R.id.segment2,
            R.id.segment3,
            R.id.segment4,
            R.id.segment5,
            R.id.segment6,
            R.id.segment7,
            R.id.segment8
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        populateSegmentLabels();

        // get the first segment text view an mark it as current
        currentSegmentView = findViewById(R.id.segment1);
        currentSegmentView.setVisibility(View.VISIBLE);
        currentSegmentValueTextView = (TextView) currentSegmentView.findViewById(R.id.value);


        // get the sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Create a handler in charge of updating the UI whenever the timertask sends
        // an update on the steps so far
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.arg2 == MAKEVISIBLE){
                    currentSegmentView.setVisibility(View.VISIBLE);
                }
                currentSegmentValueTextView.setText(Integer.toString(msg.arg1));
            }
        };

        /**
         * Initialize the map and have it update every ~1 second
         */

        mMapFragment = ((MapFragment)getFragmentManager().findFragmentById(R.id.map));
        // Getting GoogleMap object from the fragment
        googleMap = mMapFragment.getMap();

        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // set our location to be updated every second
        locationManager.requestLocationUpdates(provider,1000,0, new LocationUpdater(googleMap));


        /**
         * Initialize Database
         */
        // Create the database helper for storing and accessing steps at each segment
        mDbHelper = new StepDbHelper(this);

        // Clear the database of all records in order to ensure only data from the current
        // session are used when calculating total
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        mDbHelper.deleteAllRecords(db);
    }

    public void populateSegmentLabels(){
        // We have to manually set the labels for each Segment Text View Label since we
        // are using the same included xml layout for each segment
        for (int i = 0; i < SegmentViewIds.length; i++){
            View segmentView = findViewById(SegmentViewIds[i]);
            TextView segmentTextView = (TextView) segmentView.findViewById(R.id.label);
            segmentTextView.setText("Segment " + Integer.toString(i+1) + " steps:");
        }
    }

    public void finishSegment(int segmentTotal){
        Log.d("steps","current Segment: " + segment + " steps: " + segmentTotal);

        // Set the segment steps one last time to make sure it definitley
        // reflects what will be in the database
        Message m = new Message();
        m.arg1 = segmentTotal;
        mHandler.sendMessageAtFrontOfQueue(m);

        addToDatabase(segment,segmentTotal);

        // We increment our segment, and stop if we have reached the maximum segment length
        segment++;
        if (segment >= NUMSEGMENTS){
            stopCounting();
        }
        else{
            // get the segment label_value view, which contains a value TextView that we now mark as
            // current for proper updating
            View segmentView = findViewById(SegmentViewIds[segment]);
            currentSegmentView = segmentView;
            currentSegmentValueTextView = (TextView) segmentView.findViewById(R.id.value);

            // Send a message to make the new segment visible, and initialized with 0 steps
            Message visible = new Message();
            visible.arg1 = 0;
            visible.arg2 = MAKEVISIBLE;
            mHandler.sendMessage(visible);
        }

    }

    /**
     * Kills the timer and totals all the steps taken
     */
    public void stopCounting(){
        // kill the timer, we are done step counting
        timer.cancel();

        // get the database for reading, and query for all columns and rows
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor results = db.query(StepEntry.TABLE_NAME, null, null, null, null, null, null);

        // calculate the total steps taken from all of the segments in the database
        int totalSteps = 0;
        while (results.moveToNext()) {
            int segmentTotal = results.getInt(results.getColumnIndex(StepEntry.COLUMN_SEGMENT_STEPS));
            totalSteps += segmentTotal;
        }

        // Get the total views so the handler can properly update them
        currentSegmentView = findViewById(R.id.totalview);
        currentSegmentValueTextView = (TextView) currentSegmentView.findViewById(R.id.valueTotal);


        // Send the last message to the UI to make the Total visible
        // and set the total in the text view
        Message m = new Message();
        m.arg1 = totalSteps;
        m.arg2 = MAKEVISIBLE;
        mHandler.sendMessageAtFrontOfQueue(m);

        Log.d("stepper", "total steps: " + totalSteps);
    }

    /**
     * Adds the total for the given segment's steps to the sqLite database
     * @param segment
     * @param segmentTotal
     */
    public void addToDatabase(int segment,int segmentTotal){
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(StepEntry.COLUMN_SEGMENT, segment);
        values.put(StepEntry.COLUMN_SEGMENT_STEPS, segmentTotal);

        // Insert the new row
        db.insert(StepEntry.TABLE_NAME, null, values);
    }

    /**
     * Set the step counter to be active again on Resume
     */
    @Override
    protected void onResume() {
        super.onResume();
        activityRunning = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * mark that our app is not currently running so our app has time to react accordingly
     */
    @Override
    protected void onPause() {
        super.onPause();
        activityRunning = false;
        // if you unregister the last listener, the hardware will stop detecting step events
//        sensorManager.unregisterListener(this); 
    }

    /**
     * Called whenever the pedometer has a new total step value, it stores the new step
     * value for later use.  We also start the timer here on first sensor reading.  We wait until
     * now to start the timer so we know and have an initial stepscount to compare against.
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (activityRunning) {
            // If this is our first sensor reading, we can start the timertask to begin
            if (startLogging == false){
                startLogging = true;
                // initialize the timer task with the initial steps count
                timerTask = new stepCountTimer((int) event.values[0]);
                // run the timer immediatley every 100ms
                timer.schedule(timerTask, 0, TIMERDELAY);
                Log.d("sensorchanged","startlogging");

            }
            // reset our count to be the new total steps from our sensor
            stepsSinceStartUp = (int) event.values[0];
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public class stepCountTimer extends TimerTask {
        int ms = 0;
        int initSteps;

        public stepCountTimer(int firstSteps){
            initSteps = firstSteps;
        }

        @Override
        public void run() {
            if (ms >= SEGMENTDURATION){
                ms = 0;
                int finalSteps = stepsSinceStartUp - initSteps;
                initSteps = stepsSinceStartUp;
                finishSegment(finalSteps);
            }
            else{
                ms += TIMERDELAY;
                int segmentSteps = stepsSinceStartUp - initSteps;
                Message m = new Message();
                m.arg1 = segmentSteps;
                mHandler.sendMessage(m);
            }
        }
    }
}
