package edu.cs430x.fuschia.geosnap.network.geocloud;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

import edu.cs430x.fuschia.geocloud.geoCloud.GeoCloud;
import edu.cs430x.fuschia.geosnap.data.DateHelper;

/**
 * Created by Matt on 4/12/2015.
 */
public class InsertPhoto extends AsyncTask<Pair<Context, Photo>, Void, Long>{
    private static String TAG = "InsertPhotoTask";
    private static GeoCloud myApiService = null;
    private Context context;

    @Override
    protected Long doInBackground(Pair<Context, Photo>... params) {
        if(myApiService == null) {  // Only do this once
            GeoCloud.Builder builder = new GeoCloud.Builder(
                    AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://geosnap-cloud.appspot.com/_ah/api/");
            // end options for devappserver

            myApiService = builder.build();
        }

        context = params[0].first;
        Photo photo = params[0].second;

        try {
            return myApiService.insertPhoto(
                    photo.imageUrl,
                    photo.latitude,
                    photo.longitude,
                    photo.discoverability,
                    DateHelper.GetCurrentTimestamp()).execute().getInsertedID();
        } catch (IOException e) {
            Log.e(TAG,e.getMessage());
            Long l = -0l;
            return l;
        }
    }

    @Override
    protected void onPostExecute(Long result) {
        //TODO: this is where we will get a response, and can now put the data
        // in the database, using the returned key as our local primary key.
        Toast.makeText(context, "Photo successfully planted!", Toast.LENGTH_SHORT).show();
    }
}
