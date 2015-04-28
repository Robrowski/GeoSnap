/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package edu.cs430x.fuschia.geocloud;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.BoundingBox;
import com.beoui.geocell.model.Point;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.googlecode.objectify.Key;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import edu.cs430x.fuschia.geocloud.Constants.DiscoverRadius;
import edu.cs430x.fuschia.geocloud.Constants.Discoverability;
import edu.cs430x.fuschia.geocloud.Constants.StatusCodes;

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
                                           @Named("discoverability") String discoverability,
                                           @Named("timestamp") String timestamp) {

        double lat = locLat;
        double lon = locLon;

        // Transform it to a point
        Point p = new Point(lat, lon);

        // Generates the list of GeoCells
        List<String> cells = GeocellManager.generateGeoCell(p);

        // Create our Image Entity and save it to the datastore
        ImageEntity toInsert = new ImageEntity(photoUrl,locLat,locLon,discoverability,cells,timestamp);
        Key<ImageEntity> insertedImage = ofy().save().entity(toInsert).now();

        // Send back the response with an OK status and the ID of the inserted Image
        InsertResponseBean response= new InsertResponseBean();
        response.setID(insertedImage.getId());
        response.setStatus(StatusCodes.OK);

        return response;
    }

    @ApiMethod(name = "queryPhotoByLocation")
    public GeoQueryResponseBean queryPhotoByLocation(@Named("locLat") float locLat,
                                                   @Named("locLon") float locLon){
        /**
         * To save on distance calculations, we get all of the images within a +/- mile box
         *
         * mile = 0.01666 degrees
         * meter = mile/1609.34; // 1 mile = 1609.34 meters
         * max_bounds = meter*DiscoverRadius.RAD_FAR; // set max bounds to be meters of our furthest discover radius
         *
         * For RAD_FAR = 200m, max bounds is 0.00207
         * Avoid computation every time, just use this pre-computation
         */
        double max_bounds = 0.00207;

        double latS = locLat - max_bounds;
        double latN = locLat + max_bounds;

        double lonW = locLon - max_bounds;
        double lonE = locLon + max_bounds;

        // Transform this to a bounding box (Never Eat Soggy Waffles)
        BoundingBox bb = new BoundingBox(latN, lonE, latS, lonW);

        // Calculate the geocells list to be used in the queries
        List<String> cells = GeocellManager.bestBboxSearchCells(bb, null);

        // check to see if any there are any images located in the same geocells as our user
        List<ImageEntity> nearbyImages = new ArrayList<ImageEntity>();
        nearbyImages = ofy().load().type(ImageEntity.class).filter("geocells in", cells).list();
        System.out.println("nearby images size: " + nearbyImages.size());

        // We have all pictures within ~max radius of user, now we check to see if user is in bounds
        // of the individual radii
        Location user = new Location(locLat,locLon);
        ArrayList<ImageEntity> foundImages = new ArrayList<ImageEntity>();
        for (ImageEntity img : nearbyImages){
            Location pt = new Location(img.latitude,img.longitude);
            double distance = user.distFrom(pt);
            System.out.println("distance: " + distance);
            switch(img.discoverability){
                case Discoverability.DISC_FAR:
                    if (distance < DiscoverRadius.RAD_FAR){
                        foundImages.add(img);
                        System.out.println("far image found: " + img.imageUrl);
                    }
                    break;
                case Discoverability.DISC_MEDIUM:
                    if (distance < DiscoverRadius.RAD_MEDIUM){
                        foundImages.add(img);
                        System.out.println("medium image found: " + img.imageUrl);
                    }
                    break;
                case Discoverability.DISC_SECRET:
                    if (distance < DiscoverRadius.RAD_SECRET){
                        foundImages.add(img);
                        System.out.println("secret image found: " + img.imageUrl);
                    }
                    break;
            }
        }

        // Send back a response with all of the photos found
        GeoQueryResponseBean response = new GeoQueryResponseBean();
        if (foundImages.size() > 0){
            System.out.println("sending response with images");
            response.setImages(foundImages);
            response.setFoundImages(true);
        }
        else{
            System.out.println("sending response with no images");
            response.setFoundImages(false);
        }

        return response;
    }
}
