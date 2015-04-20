package edu.cs430x.fuschia.geosnap.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.software.shell.fab.ActionButton;

import java.util.ArrayList;
import java.util.List;

import edu.cs430x.fuschia.geocommons.location.LatLngHelper;
import edu.cs430x.fuschia.geosnap.R;
import edu.cs430x.fuschia.geosnap.camera.ImageBitmap;
import edu.cs430x.fuschia.geosnap.network.geocloud.Discoverability;
import edu.cs430x.fuschia.geosnap.network.geocloud.InsertPhoto;
import edu.cs430x.fuschia.geosnap.network.geocloud.Photo;
import edu.cs430x.fuschia.geosnap.network.imgur.model.ImageResponse;
import edu.cs430x.fuschia.geosnap.network.imgur.model.Upload;
import edu.cs430x.fuschia.geosnap.network.imgur.services.OnImgurResponseListener;
import edu.cs430x.fuschia.geosnap.network.imgur.services.UploadService;
import edu.cs430x.fuschia.geosnap.service.receivers.LocationReceiver;


/**
 * This is written as an activity because the amount of interaction between the fragment and activity was way stupid
 *
 * Imgur upload process taken from https://github.com/AKiniyalocts/imgur-android
 *
 */
public class PictureReviewActivity extends ActionBarActivity implements OnImgurResponseListener {
    public final static String TAG = "PictureReviewActivity";

    private InsertPhoto insertPhotoTask;

    private Handler mUiHandler = new Handler();
    private List<ActionButton> discoverOptionsButtons = new ArrayList<ActionButton>();
    private final int mAnimationDelayPerItem = 50;
    private boolean mMenuOpened = false;

    private String mDiscoverability;
    ActionButton fam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_review);

        // Get the bitmap we saved in our static class variable
        Bitmap bm = ImageBitmap.bm;

        // Put it in the image view
        ImageView imageView = (ImageView) findViewById(R.id.imageReview);
        imageView.setImageBitmap(bm);

        // Set up our geocloud server upload task
        insertPhotoTask = new InsertPhoto();

        fam = (ActionButton) findViewById(R.id.menu);
        ActionButton fab1 = (ActionButton) findViewById(R.id.fab1);
        ActionButton fab2 = (ActionButton) findViewById(R.id.fab2);
        ActionButton fab3 = (ActionButton) findViewById(R.id.fab3);

        discoverOptionsButtons.add(fab1);
        discoverOptionsButtons.add(fab2);
        discoverOptionsButtons.add(fab3);

        fam.setOnClickListener(menuToggle);
        fab1.setOnClickListener(optionClick);
        fab2.setOnClickListener(optionClick);
        fab3.setOnClickListener(optionClick);


    }

    public View.OnClickListener menuToggle = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            toggleMenu();
        }
    };

    public void toggleMenu(){
        int delay = 0;
        if (!mMenuOpened){
            for(int i = 0; i < discoverOptionsButtons.size(); i++){
                final ActionButton option = discoverOptionsButtons.get(i);
                mUiHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        option.show();
                    }
                }, delay);
                delay += mAnimationDelayPerItem;
            }
        }
        else{
            for(int i = discoverOptionsButtons.size()-1; i >= 0; i--){
                final ActionButton option = discoverOptionsButtons.get(i);
                mUiHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        option.hide();
                    }
                }, delay);
                delay += mAnimationDelayPerItem;
            }
        }
        mMenuOpened = !mMenuOpened;
    }

    public View.OnClickListener optionClick = new View.OnClickListener(){

        //TODO: get icons for secret near and far and change the menu button on click
        // for now, we change the color
        @Override
        public void onClick(View view) {
            toggleMenu();
            switch (view.getId()){
                case R.id.fab1:
                    mDiscoverability = "SECRET";
                    fam.setImageDrawable(getResources().getDrawable(R.drawable.secret_icon_white));
                    mDiscoverability = Discoverability.DISC_SECRET;
                    break;
                case R.id.fab2:
                    fam.setImageDrawable(getResources().getDrawable(R.drawable.medium_icon_white));
                    mDiscoverability = Discoverability.DISC_MEDIUM;
                    break;
                case R.id.fab3:
                    fam.setImageDrawable(getResources().getDrawable(R.drawable.far_icon_white));
                    mDiscoverability = Discoverability.DISC_FAR;
                    break;
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_picture_preview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                return true;

            case android.R.id.home:
                onReject(null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        onReject(null);
    }

    /** Whe the accept button is pressed, start trying to upload the image to the servers */
    public void onAccept(View v) {
        /*     Start upload     */
        new UploadService(createUpload(), this).execute();
        NavUtils.navigateUpFromSameTask(this);
        Log.i(TAG, "Started upload to imgur");
    }

    /** When the reject button is pressed, the activity backs out to previous and
     * no image is sent to any server  */
    public void onReject(View v){
        NavUtils.navigateUpFromSameTask(this);
        Toast toast = Toast.makeText(this, "Image rejected", Toast.LENGTH_SHORT);
        toast.show();
    }


    @Override
    protected void onDestroy() {
        // Free the image on exit
        ImageBitmap.bm.recycle();
        super.onDestroy();
    }


    /** Package up the current picture to be uploaded to imgur */
    private Upload createUpload(){
        Upload imgur_upload = new Upload();
        imgur_upload.bm = ImageBitmap.bm;

        // TODO set these strings to real values... something useful like GPS? time?
        imgur_upload.title = "Anonymous title";
        imgur_upload.description = "poop";
        return imgur_upload;
    }



    @Override
    public void onImgurResponse(ImageResponse response) {
        // This is called when we get a successful response from imgur.
        if(!response.success) {
            // If uploaded to imgur failed, try again! forever!
            Toast toast = Toast.makeText(this, "Imgur upload failed. Trying again...", Toast.LENGTH_SHORT);
            toast.show();
            this.onAccept(null);
        }

        String imgur_image_id = response.data.id;
        Log.i(TAG,imgur_image_id);

        // Send crap to the GeoCloud server
        // TODO send crap to the GeoCloud Server...
        Photo photo = new Photo(imgur_image_id,
                (float)LocationReceiver.location_latitude,
                (float)LocationReceiver.location_longitude,
                this.mDiscoverability);
        Pair<Context,Photo> args = new Pair<Context,Photo>(getApplicationContext(),photo);
        insertPhotoTask.execute(args);
        // TODO parcel up Lat + Long for GeoCloud too
        Intent i = getIntent();
        float latitude = i.getFloatExtra(MainActivity.INTENT_LATITUDE, 0);
        float longitude = i.getFloatExtra(MainActivity.INTENT_LONGITUDE, 0);
        //TODO use this bucket with the Long/Lat... or calculate it server side with the same-ish call
        String bucket_name = LatLngHelper.calcBucketName(
                LocationReceiver.location_latitude,
                LocationReceiver.location_longitude);

    }

}
