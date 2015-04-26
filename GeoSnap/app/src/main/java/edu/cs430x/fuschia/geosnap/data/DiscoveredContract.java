package edu.cs430x.fuschia.geosnap.data;

import android.provider.BaseColumns;

/**
 * Created by Matt on 4/9/2015.
 * <p/>
 * Defines table and column names for the weather database.
 */
public class DiscoveredContract {

    /* Inner class that defines the table contents */
    public static abstract class DiscoveredEntry implements BaseColumns {
        public static final String TABLE_NAME = "discovered";
        public static final String COLUMN_PHOTO_LOC = "photo_loc";
        public static final String COLUMN_PHOTO_URL = "photo_url";
        public static final String COLUMN_COORD_LAT = "latitude";
        public static final String COLUMN_COORD_LON = "longitude";
        public static final String COLUMN_DISCOVER = "discoverability";
        public static final String COLUMN_TIMESTAMP = "time_stamp";
    }
}
