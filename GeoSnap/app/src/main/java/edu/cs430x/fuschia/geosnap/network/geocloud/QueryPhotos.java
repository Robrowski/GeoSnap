package edu.cs430x.fuschia.geosnap.network.geocloud;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.IOException;

import edu.cs430x.fuschia.geocloud.geoCloud.GeoCloud;
import edu.cs430x.fuschia.geocloud.geoCloud.model.GeoQueryResponseBean;
import edu.cs430x.fuschia.geocloud.geoCloud.model.ImageEntity;
import edu.cs430x.fuschia.geosnap.GeoConstants;
import edu.cs430x.fuschia.geosnap.R;
import edu.cs430x.fuschia.geosnap.activity.MainActivity;
import edu.cs430x.fuschia.geosnap.data.DateHelper;
import edu.cs430x.fuschia.geosnap.data.DiscoveredSnapsDBHelper;

/**
 * Created by Matt on 4/21/2015.
 */
public class QueryPhotos extends IntentService {
    private static final String TAG = "queryPhotosService";
    private static GeoCloud myApiService = null;

    public static final int NOTIFICATION_ID = 98234;
    public static final String SNAPS_DISCOVERED = "SNAPS_DISCOVERED";


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

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Location location = (Location) intent.getExtras().get(GeoConstants.Intents.INTENT_KEY_LOCATION);

        try {
            GeoQueryResponseBean response = myApiService.queryPhotoByLocation(
                    (float)location.getLatitude(),(float)location.getLongitude()).execute();

            // Check to see if we found images!
            if (response.getFoundImages()){
                int images_ready = 0;
                Log.i(TAG,"discovered photos!");
                for (ImageEntity i: response.getImages()){
                    String string_url = "http://i.imgur.com/" + i.getImageUrl() + ".png";
                    Log.i(TAG,string_url);
                    if(DiscoveredSnapsDBHelper.SnapExists(getApplicationContext(),
                                                        string_url))
                    {
                        Log.i(TAG,"already found photo" + i.getImageUrl());
                        continue;
                    }

                    //       2: if not, add it and then download the image
                    ImageLoader.getInstance().loadImage(string_url, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            // nothing, we are just caching it for later
                        }
                    });


                    // Commit to phone's DB AFTER downloading image from imgur JUST IN CASE imgur
                    // fails...

                    String newDiscoverability = i.getDiscoverability();
                    DiscoveredSnapsDBHelper.InsertSnapIntoDatabase( getApplicationContext(),
                                                                    "dur", /* TODO dur */
                                                                    string_url,
                                                                    location.getLatitude(),
                                                                    location.getLongitude(),
                                                                    newDiscoverability,
                                                                    i.getTimestamp());


                    // Made it here, therefore the snap is fully downloaded and ready to view
                    images_ready++;
                }
                if (images_ready >0 && intent.getBooleanExtra("NOTIFICATION",true)){
                    if (sharedPref.contains("NOTIFY_COUNT")){
                        int currentCount = sharedPref.getInt("NOTIFY_COUNT",0);
                        int newCount = currentCount+images_ready;
                        Log.v(TAG,"already had count: " + currentCount);
                        sharedPref.edit().putInt("NOTIFY_COUNT",newCount).commit();
                        notifyNewSnaps(newCount);
                    }
                    else{
                        Log.v(TAG,"putting into shared pref:" + images_ready);
                        sharedPref.edit().putInt("NOTIFY_COUNT",images_ready).commit();
                        notifyNewSnaps(images_ready);
                    }
                }
            }
            else{
                Log.i(TAG,"no photos discovered");
            }
            Intent refreshIntent = new Intent();
            refreshIntent.setAction("edu.cs430x.fuschia.geosnap.REFRESH_SNAPS");
            sendBroadcast(refreshIntent);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

    }

    /** Makes or updates a notification that states the number of snaps ready to be viewed. Clicking
     * this notification opens the discovered tab in the main activity
     *
     * @param images_ready The number of images ready (from the last download)
     */
    private void notifyNewSnaps(int images_ready) {
        Log.i(TAG, "Building discovered snaps notification");
        NotificationCompat.Builder nBuild =
                new NotificationCompat.Builder(this) // TODO material design notifications
                        .setCategory(Notification.CATEGORY_SOCIAL)
                        .setSmallIcon(R.drawable.fab_plus_icon)
                        .setAutoCancel(true)
                        .setContentTitle("GeoSnaps Discovered!")
                        .setContentText("Click to view " + String.valueOf(images_ready) + " new snaps!");

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra(SNAPS_DISCOVERED, true);

        // Set up intents so that the app is launched when the notification is clicked
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this)
                .addParentStack(MainActivity.class)
                .addNextIntent(resultIntent);

        nBuild.setContentIntent(stackBuilder.getPendingIntent( 0, PendingIntent.FLAG_UPDATE_CURRENT));

        // Launch notification
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, nBuild.build());
    }

}
