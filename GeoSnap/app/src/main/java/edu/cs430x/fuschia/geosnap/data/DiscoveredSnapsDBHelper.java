package edu.cs430x.fuschia.geosnap.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import edu.cs430x.fuschia.geosnap.data.DiscoveredContract.DiscoveredEntry;

/**
 * Created by Matt on 4/9/2015.
 */
public class DiscoveredSnapsDBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "discovered.db";

    public DiscoveredSnapsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
}
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_DISCOVERED_TABLE = "CREATE TABLE " + DiscoveredEntry.TABLE_NAME + " (" +

                // ID will be assigned by the server
                DiscoveredEntry._ID + " INTEGER PRIMARY KEY," +

                // the ID of the location entry associated with this weather data
                DiscoveredEntry.COLUMN_PHOTO_LOC + " TEXT NOT NULL, " +
                DiscoveredEntry.COLUMN_COORD_LAT + " REAL NOT NULL, " +
                DiscoveredEntry.COLUMN_COORD_LON + " REAL NOT NULL, " +
                DiscoveredEntry.COLUMN_DISCOVER + " INTEGER NOT NULL," +

                // Have to agree on timestamp format. Assuming UTC formatted String
                DiscoveredEntry.COLUMN_TIMESTAMP + " TEXT NOT NULL); ";

        sqLiteDatabase.execSQL(SQL_CREATE_DISCOVERED_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}
