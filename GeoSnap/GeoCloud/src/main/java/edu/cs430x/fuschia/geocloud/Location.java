package edu.cs430x.fuschia.geocloud;

/**
 * Created by Matt on 4/21/2015.
 */

/**
 * Created by Matt on 4/21/2015.
 */
public class Location {
    float lat;
    float lon;

    private double earthRadius = 3958.75;

    public Location(){

    }
    public Location(float lat, float lon){
        this.lat = lat;
        this.lon = lon;
    }

    /**
     * Calculate the distance from this location object, to another, in meters
     * @param to
     * @return
     */
    public double distFrom(Location to){
        return distFrom(this.lat,this.lon,to.lat,to.lon);
    }

    /**
     * Caclulate the distance between two location objects, in meters
     * @param loc1
     * @param loc2
     * @return
     */
    public double distFrom(Location loc1, Location loc2){
        return distFrom(loc1.lat,loc1.lon,loc2.lat,loc2.lon);
    }

    /**
     * Calculate the distance between two lat lon pairs, in meters
     *
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return   earthRadius * c * 1609.34; // multiply by 1609.34, # meters to 1 mile
    }
}