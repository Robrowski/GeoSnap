package edu.cs430x.fuschia.geosnap.network.geocloud;

/**
 * Created by Matt on 4/16/2015.
 */
public class Photo {
    String imageUrl;
    float latitude;
    float longitude;
    String discoverability;


    public Photo(String imageUrl,float lat, float lon, String discoverability){
        this.imageUrl = imageUrl;
        this.latitude = lat;
        this.longitude = lon;
        this.discoverability = discoverability;
    }
}
