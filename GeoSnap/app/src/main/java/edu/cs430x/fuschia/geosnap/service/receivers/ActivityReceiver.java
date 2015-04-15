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

    public ActivityReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Got an activity update");
        arr = ActivityRecognitionResult.extractResult(intent);

        String m = "Activity: ";

        DetectedActivity da = arr.getMostProbableActivity();
        moving = da.getType() != DetectedActivity.STILL;
        if (moving){
            m += "probably moving";
        } else {
            m += "STILL";
        }

        Log.w(TAG,m);
        Toast t = Toast.makeText(context,m,Toast.LENGTH_SHORT);
        t.show();

        // TODO Is this really the best way?
        // Set a flag in the shared preferences, which the GoogleApiLocationService is
        // listening to... flag says to turn off or on based on activity
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPref.getBoolean(PREF_ACTIVITY_MOVING, true) != moving){
            Log.e(TAG, "Updated shared preferences probably...");
            SharedPreferences.Editor e = sharedPref.edit();
            e.putBoolean(PREF_ACTIVITY_MOVING, moving);
            e.commit();
        }
    }
}
