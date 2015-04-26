package edu.cs430x.fuschia.geosnap.network.geocloud;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

import edu.cs430x.fuschia.geocloud.geoCloud.GeoCloud;
import edu.cs430x.fuschia.geocloud.geoCloud.model.GeoQueryResponseBean;
import edu.cs430x.fuschia.geocloud.geoCloud.model.ImageEntity;
import edu.cs430x.fuschia.geosnap.R;
import edu.cs430x.fuschia.geosnap.activity.MainActivity;
import edu.cs430x.fuschia.geosnap.data.DateHelper;
import edu.cs430x.fuschia.geosnap.data.DiscoveredSnapsDBHelper;
import edu.cs430x.fuschia.geosnap.network.imgur.utils.ImgurUtils;

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
        // TODO Remove (Matt wants this until he finishes the material design of the notifications)
//        if (intent.getBooleanExtra("DEBUG", false)){
//            notifyNewSnaps(11);
//            return;
//        }

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
                int images_ready = 0;
                Log.i(TAG,"discovered photos!");
                for (ImageEntity i: response.getImages()){
                    Log.i(TAG,i.getImageUrl());
                    // TODO: 1: check if already found in our local db.
                    // IF in DB already, then it is downloaded already, => break;
                    if(DiscoveredSnapsDBHelper.SnapExists(getApplicationContext(),
                                                        i.getImageUrl()))
                    {
                        Log.i(TAG,"already found photo" + i.getImageUrl());
                        continue;
                    }

                    //       2: if not, add it and then download the image
                    if (!ImgurUtils.downloadPhoto(i.getImageUrl(), this)){
                        Log.e(TAG, "IMGUR DOWNLOAD FAILED");
                        continue;
                    }

                    // TODO NOW that the image is viewable, commit to phone's DB as a final step
                    // that triggers UI updates, etc.
                    // Commit to phone's DB AFTER downloading image from imgur JUST IN CASE imgur
                    // fails...
                    String newPhotoLoc = "idkwhatthisvariableis";//The location of the photo?
                    String newDiscoverability = i.getDiscoverability();
                    String newTimestamp = DateHelper.GetCurrentTimestamp();
                    DiscoveredSnapsDBHelper.InsertSnapIntoDatabase( getApplicationContext(),
                                                                    newPhotoLoc,
                                                                    i.getImageUrl(),
                                                                    location.getLatitude(),
                                                                    location.getLongitude(),
                                                                    newDiscoverability,
                                                                    newTimestamp);


                    // Made it here, therefore the snap is fully downloaded and ready to view
                    images_ready++; // TODO Keep track of number between queries?!?!
                    notifyNewSnaps(images_ready);

                }
                // TODO: call receiver here, who could make sure listview is updated + send
            }
            else{
                Log.i(TAG,"no photos discovered");
            }
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
