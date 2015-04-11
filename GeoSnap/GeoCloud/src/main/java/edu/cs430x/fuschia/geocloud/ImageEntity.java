package edu.cs430x.fuschia.geocloud;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by Matt on 4/10/2015.
 */
@Entity
public class ImageEntity {
    @Id
    Long id;

    String imageUrl;
    float latitude;
    float longitude;
    String discoverability;
    String lifetime;


    public ImageEntity(String imageUrl,float lat, float lon, String discoverability){
        this.imageUrl = imageUrl;
        this.latitude = lat;
        this.longitude = lon;
        this.discoverability = discoverability;
    }

}
