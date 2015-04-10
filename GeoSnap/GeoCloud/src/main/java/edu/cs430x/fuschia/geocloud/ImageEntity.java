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

    String imagerUrl;
    String discoverability;
    String lifetime;
    

}
