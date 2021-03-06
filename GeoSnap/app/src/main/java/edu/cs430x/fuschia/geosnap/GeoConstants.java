package edu.cs430x.fuschia.geosnap;

import com.google.android.gms.location.LocationRequest;

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
                PREF_ACTIVITY_INTERVAL = "pref_activity_interval";
    }


    public interface GeoReceivers {
        static final String BROADCAST_NEW_LOCATION = "edu.cs430x.fuschia.geosnap.LOCATION_UPDATE",
                BROADCAST_NEW_ACTIVITY = "edu.cs430x.fuschia.geosnap.ACTIVITY_UPDATE";

    }

    public interface Intents {
        static final String INTENT_KEY_LOCATION = "com.google.android.location.LOCATION";
    }


    public interface LocationDefaults{
        public static final String ACTIVITY_INTERVAL = "5",/* seconds between activity updates*/
                LOCATION_INTERVAL = "10", /* seconds between official GPS location requests  */
                FASTEST_INTERVAL = "2", /* seconds between accepting location updates */
                SMALLEST_DISPLACEMENT = "0.0", /* meters */
                REQUEST_PRIORITY = String.valueOf(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    public static final String HELP_URL = "https://github.com/Robrowski/GeoSnap/wiki/Using-Geosnap!",
            SURVEY_URL = "https://docs.google.com/forms/d/17NKWX2r4qtQKNzInEsYFxZ6--GR_xVXSVUEVeUHMwgw/viewform" ;


    public class Discoverability {
        public static final String DISC_SECRET = "secret";
        public static final String DISC_MEDIUM = "medium";
        public static final String DISC_FAR    = "far";
    }

    public class DiscoverRadius {
        public static final int RAD_SECRET = 20;
        public static final int RAD_MEDIUM = 75;
        public static final int RAD_FAR    = 200;
    }


}
