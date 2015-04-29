package edu.cs430x.fuschia.geosnap;

/**
 * Created by Robrowski on 4/28/2015.
 */
public interface GeoConstants {

    public static final boolean DEBUG_TOASTS = false;

    public static final int SECONDS = 60, MILLISECONDS = 1000;


    public interface GeoPrefs {
        public final static String PREF_ACTIVITY_MOVING = "pref_activity_moving",
                PREF_INTERVAL = "pref_interval",
                PREF_FASTEST_INTERVAL = "pref_fastest_interval",
                PREF_SMALLEST_DISPLACEMENT = "pref_smallest_displacement",
                PREF_REQUEST_PRIORITY = "pref_request_priority",
                PREF_ALLOW_ACTIVITY_RECOGNITION = "pref_allow_activity_recognition",
                PREF_ACTIVITY_INTERVAL = "pref_activity_interval",
                PREF_ALLOW_LOCATION_SERVICE = "pref_allow_location_service";
    }


    public interface GeoReceivers {
        static final String BROADCAST_NEW_LOCATION = "edu.cs430x.fuschia.geosnap.LOCATION_UPDATE",
                BROADCAST_NEW_ACTIVITY = "edu.cs430x.fuschia.geosnap.ACTIVITY_UPDATE";

    }


}
