package edu.cs430x.fuschia.geosnap.network.imgur.services;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.google.common.net.MediaType;

import java.io.ByteArrayOutputStream;
import java.io.File;

import edu.cs430x.fuschia.geosnap.network.imgur.Constants;
import edu.cs430x.fuschia.geosnap.network.imgur.model.ImageResponse;
import edu.cs430x.fuschia.geosnap.network.imgur.model.ImgurAPI;
import edu.cs430x.fuschia.geosnap.network.imgur.model.Upload;
import edu.cs430x.fuschia.geosnap.network.imgur.utils.NetworkUtils;
import retrofit.RestAdapter;
import retrofit.mime.TypedByteArray;

/**
 * Created by AKiniyalocts on 1/12/15. https://github.com/AKiniyalocts/imgur-android
 * <p/>
 * Our upload service. This creates our restadapter, uploads our image, and notifies us of the response.
 */
public class UploadService extends AsyncTask<Void, Void, Void> {
    public final static String TAG = UploadService.class.getSimpleName();


    public String title, description, albumId;
    private ImageResponse response;
    private Activity activity;
    private OnImgurResponseListener mUploaded;
    private File image;
    private Bitmap bm;


    public UploadService(Upload upload, Activity activity) {
        this.image = upload.image;
        this.title = upload.title;
        this.description = upload.description;
        this.albumId = upload.albumId;
        this.activity = activity;
        this.bm = upload.bm;
        mUploaded = (OnImgurResponseListener) activity;


    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.i(TAG, "Preparing to execute imgur upload");
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (NetworkUtils.isConnected(activity)) {
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

                if (Constants.LOGGING)
                    imgurAdapter.setLogLevel(RestAdapter.LogLevel.FULL);

        /*
          Upload image, get response for image
         */
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                TypedByteArray typedByteArray = new TypedByteArray(MediaType.OCTET_STREAM.toString(), byteArray);
                response = imgurAdapter.create(ImgurAPI.class)
                        .postImage(
                                Constants.getClientAuth(), title, description, albumId, null, typedByteArray
                        );

            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (response != null) {
            mUploaded.onImgurResponse(response);
        } else {
            Log.e(TAG, "Null response from imgur server");
        }
    }
}
