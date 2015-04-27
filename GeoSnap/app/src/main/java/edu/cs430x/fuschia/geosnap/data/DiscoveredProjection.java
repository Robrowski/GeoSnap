package edu.cs430x.fuschia.geosnap.data;

/**
 * Created by Matt on 4/24/2015.
 */
public class DiscoveredProjection {
    public static final String[] DISCOVERED_COLUMNS = {
            DiscoveredContract.DiscoveredEntry._ID,
            DiscoveredContract.DiscoveredEntry.COLUMN_PHOTO_LOC,
            DiscoveredContract.DiscoveredEntry.COLUMN_PHOTO_URL,
            DiscoveredContract.DiscoveredEntry.COLUMN_DISCOVER,
            DiscoveredContract.DiscoveredEntry.COLUMN_COORD_LAT,
            DiscoveredContract.DiscoveredEntry.COLUMN_COORD_LON,
            DiscoveredContract.DiscoveredEntry.COLUMN_TIMESTAMP
    };

    // These indices are tied to DISCOVERED_COLUMNS  If DISCOVERED_COLUMNS changes, these
    // must change.
    public static final int COL_ID = 0;
    public static final int COL_PHOTO_LOC = 1;
    public static final int COL_PHOTO_URL = 2;
    public static final int COL_DISCOVER = 3;
    public static final int COL_COORD_LAT = 4;
    public static final int COL_COORD_LON = 5;
    public static final int COL_TIMESTAMP = 6;

}
