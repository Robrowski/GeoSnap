package edu.cs430x.fuschia.geosnap.network.imgur.services;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

import edu.cs430x.fuschia.geosnap.network.imgur.Constants;
import edu.cs430x.fuschia.geosnap.network.imgur.model.ImageResponse;
import edu.cs430x.fuschia.geosnap.network.imgur.model.ImgurAPI;
import edu.cs430x.fuschia.geosnap.network.imgur.model.Upload;
import edu.cs430x.fuschia.geosnap.network.imgur.utils.NetworkUtils;
import retrofit.RestAdapter;
import retrofit.mime.TypedFile;

/**
 * Created by AKiniyalocts on 1/12/15. https://github.com/AKiniyalocts/imgur-android
 *
 * Our upload service. This creates our restadapter, uploads our image, and notifies us of the response.
 *
 *
 */
public class UploadService extends AsyncTask<Void, Void, Void> {
    public final static String TAG = UploadService.class.getSimpleName();


    public String title, description, albumId;
    private ImageResponse response;
    private Activity activity;
    private OnImageUploadedListener mUploaded;
    private File image;


    public UploadService(Upload upload, Activity activity){
        this.image = upload.image;
        this.title = upload.title;
        this.description = upload.description;
        this.albumId = upload.albumId;
        this.activity = activity;
        mUploaded = (OnImageUploadedListener) activity;


    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.i(TAG, "Preparing to execute imgur upload");
    }

    @Override
    protected Void doInBackground(Void... params) {
        if(NetworkUtils.isConnected(activity)) {
            if (NetworkUtils.connectionReachable()) {

        /*
          Create rest adapter using our imgur API
         */
                RestAdapter imgurAdapter = new RestAdapter.Builder()
                        .setEndpoint(ImgurAPI.server)
                        .build();

        /*
          Set rest adapter logging if we're already logging
         */

                if(Constants.LOGGING)
                    imgurAdapter.setLogLevel(RestAdapter.LogLevel.FULL);

        /*
          Upload image, get response for image
         */

                response = imgurAdapter.create(ImgurAPI.class)
                        .postImage(
                                Constants.getClientAuth(), title, description, albumId, null, new TypedFile("image/*", image)
                        );

            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if(response != null) {
            mUploaded.onImageUploaded(response);
        }
        Log.e(TAG, "Null response from imgur server");
    }
}
