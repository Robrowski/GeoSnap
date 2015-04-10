package edu.cs430x.fuschia.geosnap.network.imgur.services;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import edu.cs430x.fuschia.geosnap.network.imgur.model.ImageResponse;
import edu.cs430x.fuschia.geosnap.network.imgur.utils.NetworkUtils;

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
    private OnImageResponseListener mListener;
    private File image;
    private boolean success = false;


    public GetService(String id, OnImageResponseListener listener, Activity activity){
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
        if(NetworkUtils.isConnected(activity)) {
            if (NetworkUtils.connectionReachable()) {
                String string_url = "http://i.imgur.com/" + id + ".png";
                Bitmap bitmap;
                try {
                    bitmap = BitmapFactory.decodeStream((InputStream) new URL(string_url).getContent());
                } catch (Exception e) {
                    Log.e(TAG, "Couldn't get the image from imgur...");
                    Log.e(TAG, Log.getStackTraceString(e));
                    return null;
                }
                //create a file to write bitmap data
                File f;
                try {
                    //Convert bitmap to byte array
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                    byte[] bitmap_data = bos.toByteArray();

                    //write the bytes in file
                    FileOutputStream fos = activity.openFileOutput(id + ".png", Context.MODE_PRIVATE);
                    fos.write(bitmap_data);
                    fos.flush();
                    fos.close();
                    success = true;
                } catch (Exception e){
                    Log.e(TAG, "Couldn't save the image to file...");
                    Log.e(TAG, Log.getStackTraceString(e));
                    return null;
                }

            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if(success) {
            mListener.onImageResponse(response);
        }
        Log.e(TAG, "Failed to get the image");
    }
}
