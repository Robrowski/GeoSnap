package edu.cs430x.fuschia.geocommons.location;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.Collection;
import java.util.LinkedList;



/**
 * Methods to take a LatLng or latitude and longitude and place it in a bucket
 * or generate a list of buckets that the given location is close to.
 *
 */
public class LatLngHelper {

    // These two increments are used because latitude and longitude
    // do not correlate to the same real world distance.
    // ALSO - in calcBucketName, make sure there are enough decimal places
    // to reflect the precision of the increment
    protected static final double LAT_INC = 0.0075, LON_INC = 0.01;
    private static final String BUCKET_FORMAT = "%.3f;%.2f";

    /**
     * Gives the string name of the bucket that the given coordinates reside in
     *
     * @return
     */
    public static String calcBucketName(double lat, double lon) {
        LatLng ll = calcBucket(lat, lon);
        return String.format( BUCKET_FORMAT, ll.latitude, ll.longitude);
        // TODO could return a hashed version, or compressed string for space
        // This string is used for nothing more than a bucket name
    }

    /**
     * Gives the string name of the bucket that the given coordinates reside in
     *
     * @param l
     * @return
     */
    public static String calcBucketName(LatLng l) {
        return calcBucketName(l.latitude, l.longitude);
    }

    /**
     * Gives the string name of the bucket that the given coordinates reside in
     *
     * @param l
     * @return
     */
    public static String calcBucketName(Location l) {
        return calcBucketName(l.getLatitude(), l.getLongitude());
    }

    /**
     * Calculates what bucket the given coordinates belong in. BASICALLY it
     * rounds down each coordinate to the nearest smaller number that is a
     * multiple of the appropriate increment
     *
     * @param lat
     *            the latitude
     * @param lon
     *            the longitude
     * @return The bucket that the given coordinates reside in
     */
    protected static LatLng calcBucket(double lat, double lon) {
        return new LatLng(round(lat, LAT_INC), round(lon, LON_INC));
    }

    /**
     * Calls calcBucket(double, double).
     *
     * @param ll
     *            LatLng object
     * @return the bucket as a LatLng
     */
    protected static LatLng calcBucket(LatLng ll) {
        return calcBucket(ll.latitude, ll.longitude);
    }

    /**
     * Calls calcBucket(double, double).
     *
     * @param l
     *            Location object
     * @return the bucket as a LatLng
     */
    protected static LatLng calcBucket(Location l) {
        return calcBucket(l.getLatitude(), l.getLongitude());
    }


    /**
     * Takes a value and rounds it down to the nearest increment ROUNDS TOWARDS
     * zero
     */
    private static double round(double x, double inc) {
        // Basically rounds down to the nearest multiple of an increment
        return ((int) (x / inc)) * inc;
        // There are some edge case bugs where it rounds down and stuff when
        // you think it shouldn't round at all... this shouldn't be an issue
        // because of the function that decides when to take multiple buckets
        // AND the fact that the buckets are plenty large.
    }

    protected static Collection<String> getBuckets(LatLng ll) {
        return getBuckets(ll.latitude, ll.longitude);
    }

    private static double QUARTER = 0.25;

    public static double FAR_PERCENT = QUARTER;

    protected static Collection<String> getBuckets(double lat, double lon) {
        Collection<String> c = new LinkedList<String>();
        c.add(calcBucketName(lat, lon)); // Actually in this bucket

        // TODO I assumed that "far = 25% of a bucket"
        final double lat_rem = lat % LAT_INC, lon_rem = lon % LON_INC;

        // Figure out which parts are out of bounds
        final boolean lat_q = Math.abs(lat_rem) <= FAR_PERCENT * LAT_INC;
        final boolean lat_tq = Math.abs(lat_rem) >= (1 - FAR_PERCENT) * LAT_INC;
        final boolean lon_q = Math.abs(lon_rem) <= FAR_PERCENT * LON_INC;
        final boolean lon_tq = Math.abs(lon_rem) >= (1 - FAR_PERCENT) * LON_INC;

        // Top/Bottom
        if (lat_tq) { // TOP
            c.add(calcBucketName(lat + 0.9 * LAT_INC, lon));
        } else if (lat_q) {
            c.add(calcBucketName(lat - 0.9 * LAT_INC, lon));
        }

        // Left/Right
        if (lon_tq) { // Left
            c.add(calcBucketName(lat, lon - 0.9 * LON_INC));
        } else if (lon_q) { // Right
            c.add(calcBucketName(lat, lon + 0.9 * LON_INC));
        }

        // Diagonals -- Can only have one
        if (lon_tq && lat_tq) { // Left top
            c.add(calcBucketName(lat + 0.9 * LAT_INC, lon - 0.9 * LON_INC));
        } else if (lon_q && lat_tq) { // Right top
            c.add(calcBucketName(lat + 0.9 * LAT_INC, lon + 0.9 * LON_INC));
        } else if (lon_q && lat_q) { // Right // Bot
            c.add(calcBucketName(lat - 0.9 * LAT_INC, lon + 0.9 * LON_INC));
        } else if (lon_tq && lat_q) { // left Bot
            c.add(calcBucketName(lat - 0.9 * LAT_INC, lon - 0.9 * LON_INC));
        }

        return c;
    }

    public static double distance(LatLng l1, LatLng l2) {
        float[] results = new float[1];
        Location.distanceBetween(l1.latitude, l1.longitude, l2.latitude, l2.longitude, results);
        return results[0];
    }

    public static double distance(Location l1, Location l2) {
        float[] results = new float[1];
        Location.distanceBetween(l1.getLatitude(), l1.getLongitude(), l2.getLatitude(), l2.getLongitude(), results);
        return results[0];
    }

    public static double distance(double lat1, double lng1, double lat2, double lng2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lng1, lat2, lng2, results);
        return results[0];
    }

}
