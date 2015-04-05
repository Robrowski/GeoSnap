package com.starboardland.pedometer;

import android.provider.BaseColumns;

/**
 * Created by Matt on 4/4/2015.
 */
public class StepContract {

    public StepContract(){

    }

    /* Inner class that defines the table contents */
    public static abstract class StepEntry implements BaseColumns {
        public static final String TABLE_NAME = "steps";
        public static final String COLUMN_SEGMENT = "segment";
        public static final String COLUMN_SEGMENT_STEPS = "segmentsteps";
    }
}
