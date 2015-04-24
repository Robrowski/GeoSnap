package edu.cs430x.fuschia.geosnap.network.geocloud;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

import edu.cs430x.fuschia.geocloud.geoCloud.GeoCloud;
import edu.cs430x.fuschia.geocloud.geoCloud.model.GeoQueryResponseBean;
import edu.cs430x.fuschia.geocloud.geoCloud.model.ImageEntity;

/**
 * Created by Matt on 4/21/2015.
 */
public class QueryPhotos extends IntentService {
    private static String TAG = "queryPhotosService";
    private static GeoCloud myApiService = null;

    public QueryPhotos(){
        super("queryPhotos");
    }
    public QueryPhotos(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(TAG,"query photos service started");
        if(myApiService == null) {  // Only do this once
            GeoCloud.Builder builder = new GeoCloud.Builder(
                    AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://geosnap-cloud.appspot.com/_ah/api/");
            // end options for devappserver
            myApiService = builder.build();
        }

        Location location = (Location) intent.getExtras().get("com.google.android.location.LOCATION");
        try {
            GeoQueryResponseBean response = myApiService.queryPhotoByLocation(
                    (float)location.getLatitude(),(float)location.getLongitude()).execute();

            // Check to see if we found images!
            if (response.getFoundImages()){
                Log.i(TAG,"discovered photos!");
                for (ImageEntity i: response.getImages()){
                    Log.i(TAG,i.getImageUrl());
                    // TODO: 1: check if already found in our local db.
                    //       2: if not, add it and then download the image
                    // TODO: potentially alert receiver here, who might update UI to say we are downloading?
                }
                // TODO: call receiver here, who could make sure listview is updated + send
                // system notification that we've discovered a snap that is ready to view.
            }
            else{
                Log.i(TAG,"no photos discovered");
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

    }
}
