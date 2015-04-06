package com.starboardland.pedometer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SegmentDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "segments.sqlite";
    private static final int VERSION = 1;

    private static final String TABLE_SEGMENT = "seg";
    private static final String SEGMENT_ID = "_id";
    private static final String COLUMN_STEPS = "steps";


    public SegmentDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create the table
        db.execSQL("CREATE TABLE seg (" + SEGMENT_ID +" INTEGER PRIMARY KEY, " + COLUMN_STEPS + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // implement schema changes and data massage here when upgrading
    }

    public void insertRun(int id, int steps) {
        SQLiteDatabase db = getWritableDatabase();
        // Delete first as an easy hack
        db.delete(TABLE_SEGMENT, SEGMENT_ID + " = ?",
                new String[] { String.valueOf(id) });

        // Prepare the data
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_STEPS, steps);
        cv.put(SEGMENT_ID, id);

        // Save the data
        db.insert(TABLE_SEGMENT, null, cv);
        db.close();
    }


    public int queryRun(long id) {
        try {
            SQLiteDatabase db = getWritableDatabase();

            Cursor wrapped = db.query(TABLE_SEGMENT,
                    null, // all columns
                    SEGMENT_ID + " = ?", // look for a run ID
                    new String[]{String.valueOf(id)}, // with this value
                    null, // group by
                    null, // order by
                    null, // having
                    "1"); // limit 1 row

            if (wrapped != null)
                wrapped.moveToFirst();

            Log.e("DB_DEBUG", wrapped.getString(0) + " and " + wrapped.getString(1));

            return wrapped.getInt(wrapped.getColumnIndex(COLUMN_STEPS));
        } catch (Exception e){ // This is because I am super lazy
            return 0;
        }

    }

    public void init() {
        for (int i = 1; i <= 8; i++){
            this.insertRun(i, 0);
        }
    }
}
