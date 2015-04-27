package edu.cs430x.fuschia.geosnap.service.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

public class ActivityReceiver extends BroadcastReceiver {
    public static final String TAG = "ActivityReceiver", PREF_ACTIVITY_MOVING = "pref_activity_moving";

    public static ActivityRecognitionResult arr = null;
    public static boolean moving = true;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Got an activity update");
        arr = ActivityRecognitionResult.extractResult(intent);
        moving = isMoving(arr);
        String m = "";

        for (DetectedActivity da: arr.getProbableActivities()){
            if (!m.equals("")) {
                m += "\n"; // New line character if adding a new line
            }
            m += activityToString(da.getType()) + ": " + String.valueOf(da.getConfidence());
        }

        Log.i(TAG,m);
        Toast t = Toast.makeText(context,m,Toast.LENGTH_SHORT);
        t.show();

        // TODO Is this really the best way?
        // Set a flag in the shared preferences, which the GoogleApiLocationService is
        // listening to... flag says to turn off or on based on activity
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPref.getBoolean(PREF_ACTIVITY_MOVING, true) != moving){
            Log.w(TAG, "Updating status of location services");
            SharedPreferences.Editor e = sharedPref.edit();
            e.putBoolean(PREF_ACTIVITY_MOVING, moving);
            e.commit();
        }
    }

    /** These numbers are estimated */
    private static int THRESHOLD_STILL = 55, THRESHOLD_MOVING = 45;


    /** Decide whether the device is moving enough to warrant turning the location
     * services back on or not.
     *
     * @param arr the latest activity recognition readings
     * @return true for moving enough, false otherwise
     */
    private static boolean isMoving(ActivityRecognitionResult arr) {
        // This list is automatically sorted by confidence level
        for (DetectedActivity da : arr.getProbableActivities()) {
            switch (da.getType()){
                case DetectedActivity.IN_VEHICLE:
                case DetectedActivity.ON_BICYCLE:
                case DetectedActivity.ON_FOOT:
                case DetectedActivity.RUNNING:
                case DetectedActivity.WALKING:
                    if (da.getConfidence() >= THRESHOLD_MOVING)
                        return true; // Only return if really sure
                    break;

                case DetectedActivity.STILL:
                    // TODO Consider the case where STILL is technically first, but running and
                    // TODO walking and on foot are also very high probability...
                    if (da.getConfidence() >= THRESHOLD_STILL)
                        return false; // Only return if really sure about being still
                    break;

                case DetectedActivity.TILTING:
                case DetectedActivity.UNKNOWN:
                default:
                    break;// Check next state if there is one
            }
        }

        // Default case is false because when TILT is present, it is usually alone...
        return false;
    }


    /** Convert types to printable strings */
    private static String activityToString(int act){
        switch (act){
            case DetectedActivity.STILL:
                return "STILL";
            case DetectedActivity.IN_VEHICLE:
                return "IN_VEHICLE";
            case DetectedActivity.ON_BICYCLE:
                return "ON_BICYCLE";
            case DetectedActivity.ON_FOOT:
                return "ON_FOOT";
            case DetectedActivity.RUNNING:
                return "RUNNING";
            case DetectedActivity.TILTING:
                return "TILTING";
            case DetectedActivity.UNKNOWN:
                return "UNKNOWN";
            case DetectedActivity.WALKING:
                return "WALKING";
            default:
                return "Pooping";
        }
    }

}
