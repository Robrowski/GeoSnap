/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package edu.cs430x.fuschia.geocloud;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.googlecode.objectify.Key;

import javax.inject.Named;

import static edu.cs430x.fuschia.geocloud.OfyService.ofy;

/**
 * An endpoint class we are exposing
 */
@Api(name = "geoCloud", version = "v1", namespace = @ApiNamespace(ownerDomain = "geocloud.fuschia.cs430x.edu", ownerName = "geocloud.fuschia.cs430x.edu", packagePath = ""))
public class GeoCloudEndpoint {
    /**
     * A simple endpoint method that takes a name and says Hi back
     */
    @ApiMethod(name = "insertPhoto")
     public InsertResponseBean insertPhoto(@Named("photoUrl") String photoUrl,
                                           @Named("locLat") float locLat,
                                           @Named("locLon") float locLon,
                                           @Named("discoverability") String discoverability) {

        ImageEntity toInsert = new ImageEntity(photoUrl,locLat,locLon,discoverability);
        Key<ImageEntity> insertedImage = ofy().save().entity(toInsert).now();

        InsertResponseBean response= new InsertResponseBean();
        response.setID(insertedImage.getId());
        response.setStatus(StatusCodes.OK);

        return response;
    }
}
