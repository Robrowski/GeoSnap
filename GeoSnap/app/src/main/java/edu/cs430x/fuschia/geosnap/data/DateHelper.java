package edu.cs430x.fuschia.geosnap.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by EP on 4/26/2015.
 */
public class DateHelper {

    /**
     * Formatter to keep dates consistent and convert timestamps to strings.
     * @return The timestamp in a consistent format.
     */
    public static String GetCurrentTimestamp()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
