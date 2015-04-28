package edu.cs430x.fuschia.geosnap.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by EP on 4/26/2015.
 */
public class DateHelper {

    //The string to use to format dates consistently.
    static String dateFormatString = "yyyy/MM/dd HH:mm:ss";
    static int MILLISECONDS_PER_HOUR = 3600000;

    /**
     * Formatter to keep dates consistent and convert timestamps to strings.
     * @return The timestamp in a consistent format.
     */
    public static String GetCurrentTimestamp()
    {
        DateFormat dateFormat = new SimpleDateFormat(dateFormatString);
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * Get the difference between two timestamps. If it is negative, the second date is earlier than the first.
     * @param timestamp1
     * @param timestamp2
     * @return The difference rounded up to the nearest hour
     */
    public static int GetDifferenceBetweenTimestampsInHours(String timestamp1, String timestamp2)
    {
        Date date1 = ConvertStringToDate(timestamp1);
        Date date2 = ConvertStringToDate(timestamp2);

        //Calculate the difference between the times
        long time1 = date1.getTime();
        long time2 = date2.getTime();
        long differenceInMilliseconds = Math.abs(time1-time2);
        //Round up
        int differenceInHours = (int)Math.ceil(differenceInMilliseconds / MILLISECONDS_PER_HOUR);

        return differenceInHours;
    }


    /**
     * Determine if the specified date came before the second date, or not.
     * @param before
     * @param after
     * @return
     */
    public static boolean isBefore(String before, String after)
    {
        Date date1 = ConvertStringToDate(before);
        Date date2 = ConvertStringToDate(after);
        return date1.before(date2);
    }

    /**
     * Get the time in string format after adding some hours
     * @param hours
     * @return
     */
    public static String AddHoursToCurrentTime(int hours)
    {
        DateFormat dateFormat = new SimpleDateFormat(dateFormatString);

        Date current = new Date();
        current.setTime(current.getTime() + hours * MILLISECONDS_PER_HOUR);

        return dateFormat.format(current);
    }

    /**
     * Get the time in string format after adding some minutes
     * @param hours
     * @return
     */
    public static String AddMinutesToCurrentTime(int minutes)
    {
        DateFormat dateFormat = new SimpleDateFormat(dateFormatString);

        Date current = new Date();
        current.setTime(current.getTime() + minutes * MILLISECONDS_PER_HOUR / 60);

        return dateFormat.format(current);
    }

    /**
     * Convert a string timestamp to a date object.
     * @param timestamp
     * @return
     */
    private static Date ConvertStringToDate(String timestamp)
    {
        //Set up the formatter
        DateFormat dateFormat = new SimpleDateFormat(dateFormatString);

        //Parse the strings into date objects
        Date date;
        try {
            date = dateFormat.parse(timestamp);
        }catch (ParseException e)
        {
            //The string passed in was not a date.
            return null;
        }
        return date;
    }
}
