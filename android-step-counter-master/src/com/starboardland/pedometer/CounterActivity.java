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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.starboardland.pedometer.StepContract.StepEntry;

import java.util.Timer;
import java.util.TimerTask;

public class CounterActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private TextView currentViewSegment;
    private TextView count;
    boolean activityRunning;
    boolean startLogging = false;
    private Timer timer = new Timer();
    private int stepsSinceStartUp;

    TimerTask timerTask;
    private int segment = 0;

    StepDbHelper mDbHelper;

    public Handler mHandler;

    private int[] segmentTextViewIds = {
            R.id.value1,
            R.id.value2,
            R.id.value3,
            R.id.value4,
            R.id.value5,
            R.id.value6,
            R.id.value7,
            R.id.value8
    };

    public class stepCountTimer extends TimerTask {
        int ms = 0;
        int initSteps;

        public stepCountTimer(int firstSteps){
            initSteps = firstSteps;
        }

        @Override
        public void run() {
            if (ms >= 5000){
                ms = 0;
                int finalSteps = stepsSinceStartUp - initSteps;
                initSteps = stepsSinceStartUp;
                finishSegment(finalSteps);
            }
            else{
                ms += 100;
                int segmentSteps = stepsSinceStartUp - initSteps;
                Message m = new Message();
                m.arg1 = segmentSteps;
                mHandler.sendMessage(m);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        currentViewSegment = (TextView) findViewById(R.id.value1);
        count = (TextView) findViewById(R.id.count);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                currentViewSegment.setText(Integer.toString(msg.arg1));
            }
        };

        mDbHelper = new StepDbHelper(this);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        mDbHelper.deleteAllRecords(db);

    }

    public void finishSegment(int segmentTotal){
        // Set the segment steps one last time to make sure it definitley
        // reflects what will be in the database
        Message m = new Message();
        m.arg1 = segmentTotal;
        mHandler.sendMessageAtFrontOfQueue(m);

        addToDatabase(segment,segmentTotal);
        Log.v("steps","current Segment: " + segment + " steps: " + segmentTotal);
        segment++;
        if (segment >= 8){
            stopCounting();
        }
        else{
            currentViewSegment = (TextView) findViewById(segmentTextViewIds[segment]);
        }

    }

    public void stopCounting(){
        timer.cancel();

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor results = db.query(StepEntry.TABLE_NAME, null, null, null, null, null, null);

        int totalSteps = 0;
        while (results.moveToNext()) {
            int segmentTotal = results.getInt(results.getColumnIndex(StepEntry.COLUMN_SEGMENT_STEPS));
            totalSteps += segmentTotal;
        }

        currentViewSegment = (TextView) findViewById(R.id.valueTotal);
        Message m = new Message();
        m.arg1 = totalSteps;
        mHandler.sendMessageAtFrontOfQueue(m);

        Log.v("stepper","total steps: " + totalSteps);
    }

    public void addToDatabase(int segment,int segmentTotal){
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(StepEntry.COLUMN_SEGMENT, segment);
        values.put(StepEntry.COLUMN_SEGMENT_STEPS, segmentTotal);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                StepEntry.TABLE_NAME,
                null,
                values);

    }


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

    @Override
    protected void onPause() {
        super.onPause();
        activityRunning = false;
        // if you unregister the last listener, the hardware will stop detecting step events
//        sensorManager.unregisterListener(this); 
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (activityRunning) {
            if (startLogging == false){
                startLogging = true;
                timerTask = new stepCountTimer((int) event.values[0]);
                timer.schedule(timerTask, 0, 100);
                Log.v("sensorchanged","startlogging");

            }
            stepsSinceStartUp = (int) event.values[0];
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
