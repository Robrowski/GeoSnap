package edu.cs430x.fuschia.geocloud;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.List;

/**
 * Created by Matt on 4/10/2015.
 */
@Entity
public class ImageEntity {
    @Id
    public Long id;

    public String imageUrl;
    public float latitude;
    public float longitude;
    public String discoverability;
    public String timestamp;

    @Index
    public List<String> geocells;

    public ImageEntity(){

    }

    public ImageEntity(String imageUrl,float lat, float lon, String discoverability, List<String> geocells, String timestamp){
        this.imageUrl = imageUrl;
        this.latitude = lat;
        this.longitude = lon;
        this.discoverability = discoverability;
        this.timestamp = timestamp;
        this.geocells = geocells;
    }

}
