package com.starboardland.pedometer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.starboardland.pedometer.StepContract.*;

/**
 * Created by Matt on 4/4/2015.
 */
public class StepDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "steps.db";

    public StepDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_STEP_TABLE = "CREATE TABLE " + StepEntry.TABLE_NAME + " (" +
                StepEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                StepEntry.COLUMN_SEGMENT + " INTEGER NOT NULL, " +
                StepEntry.COLUMN_SEGMENT_STEPS + " INTEGER NOT NULL )";

        sqLiteDatabase.execSQL(SQL_CREATE_STEP_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + StepEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void deleteAllRecords(SQLiteDatabase sqLiteDatabase){
        sqLiteDatabase.execSQL("delete from "+ StepEntry.TABLE_NAME);
    }
}
