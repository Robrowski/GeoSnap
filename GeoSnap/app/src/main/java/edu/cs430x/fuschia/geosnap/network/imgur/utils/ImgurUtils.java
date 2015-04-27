package edu.cs430x.fuschia.geosnap.network.imgur.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Robrowski on 4/24/2015.
 */
public class ImgurUtils {
    private static final String TAG = "ImgurUtils";

    public static String downloadPhoto(String imgur_id, Context c) {
        if (!NetworkUtils.isConnected(c) || !NetworkUtils.connectionReachable()) {
            Log.w(TAG, "Network unavailable to request images from imgur");
            return null;
        }

        String string_url = "http://i.imgur.com/" + imgur_id + ".png";
        Bitmap bitmap;
        try {
            Log.i(TAG, "Requesting image " + imgur_id + " from imgur.");
            bitmap = BitmapFactory.decodeStream((InputStream) new URL(string_url).getContent());
        } catch (Exception e) {
            Log.e(TAG, "Couldn't get the image from imgur...");
            Log.e(TAG, Log.getStackTraceString(e));
            return null;
        }
        try {
            //Convert bitmap to byte array
            Log.i(TAG, "Converting image " + imgur_id + " from a bitmap to a file");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmap_data = bos.toByteArray();

            //write the bytes in file
            String fileName = imgur_id + ".png";
            FileOutputStream fos = c.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(bitmap_data);
            fos.flush();
            fos.close();
            return fileName;

        } catch (Exception e) {
            Log.e(TAG, "Couldn't save the image to file...");
            Log.e(TAG, Log.getStackTraceString(e));
            return null;
        }
    }
}
