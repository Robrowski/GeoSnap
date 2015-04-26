package edu.cs430x.fuschia.geosnap.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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

                // the ID of the location entry associated with this snap data
                DiscoveredEntry.COLUMN_PHOTO_LOC + " TEXT NOT NULL, " +
                DiscoveredEntry.COLUMN_PHOTO_URL + " TEXT NOT NULL, " +
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


    /**
     * Static function to handle easy inserting into the database. Call
     *  from anywhere in the code to insert an entry in the database.
     */
    public static long InsertSnapIntoDatabase(Context currentContext,
                                              String photoLoc,
                                              String photoURL,
                                              double lat,
                                              double lon,
                                              int discovery,
                                              String timestamp)
    {

        DiscoveredSnapsDBHelper mDbHelper = new DiscoveredSnapsDBHelper(currentContext);

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DiscoveredContract.DiscoveredEntry.COLUMN_PHOTO_LOC, photoLoc);
        values.put(DiscoveredContract.DiscoveredEntry.COLUMN_PHOTO_URL, photoLoc);
        values.put(DiscoveredContract.DiscoveredEntry.COLUMN_COORD_LAT, lat);
        values.put(DiscoveredContract.DiscoveredEntry.COLUMN_COORD_LON, lon);
        values.put(DiscoveredContract.DiscoveredEntry.COLUMN_DISCOVER, discovery);
        values.put(DiscoveredContract.DiscoveredEntry.COLUMN_TIMESTAMP, timestamp);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                DiscoveredContract.DiscoveredEntry.TABLE_NAME,
                null,
                values);

        //Return the key of the snap, if needed.
        return newRowId;
    }


    /**
     * Check if a snap exists in the database, presumably before adding it.
     * @param imageURL The URL of the image
     * @return Whether it is already obtained or not.
     */
    public static boolean SnapExists(Context currentContext, String imageURL)
    {
        DiscoveredSnapsDBHelper mDbHelper = new DiscoveredSnapsDBHelper(currentContext);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //We don't really care about the columns, we just want to see if ANY come back.
        String[] projection = {
                DiscoveredContract.DiscoveredEntry._ID
        };
        //Select where the url equals the imageURL
        String selection = DiscoveredContract.DiscoveredEntry.COLUMN_PHOTO_URL;
        String[] selectionArgs = { imageURL };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = DiscoveredContract.DiscoveredEntry._ID;

        Cursor c = db.query(
                DiscoveredContract.DiscoveredEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        //At the end, we just want to know if anything was found. If it was, the snap exists.
        return (c.getCount() > 0);
    }
}
