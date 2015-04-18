package edu.cs430x.fuschia.geosnap.service.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import edu.cs430x.fuschia.geosnap.service.GoogleApiLocationService;

/**
 * Receiver to catch broadcasts that the device has started. Launches the location
 * service.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, GoogleApiLocationService.class);
        context.startService(serviceIntent);
        Log.w("BootReceiver", "Starting location + activity service");
    }
}
