package edu.cs430x.fuschia.geosnap.network.imgur.services;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import edu.cs430x.fuschia.geosnap.network.imgur.model.ImageResponse;
import edu.cs430x.fuschia.geosnap.network.imgur.utils.ImgurUtils;

/**
 *
 * Our get service. This creates our restadapter, requests our image, and notifies us of the response.
 *
 *
 */
public class GetService extends AsyncTask<Void, Void, Void> {
    public final static String TAG = GetService.class.getSimpleName();


    public String id;
    private ImageResponse response;
    private Activity activity;
    private OnImgurResponseListener mListener;
    private String returnedFile = null;


    public GetService(String id, OnImgurResponseListener listener, Activity activity){
        this.activity = activity;
        this.id = id;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.w(TAG, "Preparing to execute imgur request");
    }

    @Override
    protected Void doInBackground(Void... params) {
        returnedFile = ImgurUtils.downloadPhoto(id, activity);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        // TODO update UI that we have loaded the snaps as per
        // http://developer.android.com/reference/android/os/AsyncTask.html

        if(returnedFile != null) {
            // TODO put together a real response
            mListener.onImgurResponse(response);
        } else {
            Log.e(TAG, "Failed to get the image");
        }
    }
}
