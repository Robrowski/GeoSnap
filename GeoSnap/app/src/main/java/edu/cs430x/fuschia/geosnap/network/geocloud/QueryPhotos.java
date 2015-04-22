package edu.cs430x.fuschia.geosnap.network.geocloud;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.ArrayList;

import edu.cs430x.fuschia.geocloud.geoCloud.GeoCloud;
import edu.cs430x.fuschia.geocloud.geoCloud.model.GeoQueryResponseBean;
import edu.cs430x.fuschia.geocloud.geoCloud.model.ImageEntity;

/**
 * Created by Matt on 4/21/2015.
 */
public class QueryPhotos extends IntentService {
    private static String TAG = "InsertPhotoTask";
    private static GeoCloud myApiService = null;

    public QueryPhotos(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
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

            // example of extracting data from response.
            ArrayList<String> imgUrls = new ArrayList<String>();
            for (ImageEntity i: response.getImages()){
                imgUrls.add(i.getImageUrl());
            }
            // TODO: potentially call receiver here, who might update UI to say we are downloading?

            // TODO: We are already running in the background here, so now we should call to
            // download each of the photos we just discovered.

            // TODO: call receiver here, who could make sure listview is updated + send
            // system notification that we've discovered a snap that is ready to view.


        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

    }
}
