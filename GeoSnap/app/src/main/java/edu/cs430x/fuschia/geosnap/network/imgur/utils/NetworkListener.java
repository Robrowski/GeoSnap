package edu.cs430x.fuschia.geosnap.network.imgur.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class NetworkListener extends ConnectivityManager.NetworkCallback {
    private static final String TAG = "NetworkConnectivity";
    public static final String  PREF_NETWORK_STATE = "pref_network_state";
    private static final NetworkRequest nr = new NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build();
    private ConnectivityManager cm;
    private SharedPreferences sharedPref;


    public NetworkListener(Context c) {
        cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
    }

    public void start(){
        cm.registerNetworkCallback( nr, this);
    }

    public void stop(){
        cm.unregisterNetworkCallback(this);
    }

    @Override
    public void onAvailable(Network network) {
        super.onAvailable(network);
        Log.i(TAG, "Internet is OK");
        setNetworkStatePref(true);
    }

    @Override
    public void onLost(Network network) {
        super.onLost(network);
        Log.i(TAG, "No internet connection");
        setNetworkStatePref(false);
    }

    private void setNetworkStatePref(boolean val){
        Log.i(TAG, "Updating status of network connectivity");
        SharedPreferences.Editor e = sharedPref.edit();
        e.putBoolean(PREF_NETWORK_STATE, val);
        e.commit();
    }
}
